package com.kk.pethome.coupon.service.impl;

import com.kk.pethome.coupon.mapper.*;
import com.kk.pethome.coupon.pojo.*;
import com.kk.pethome.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired private RedisTemplate<String, String> redis;
    @Autowired private CouponTemplateMapper templateMapper;
    @Autowired private UserCouponMapper userCouponMapper;
    @Autowired private CouponUsageLogMapper usageLogMapper;

    // ════════════════════════════════
    //  Redis + Lua 秒杀
    // ════════════════════════════════
    private static final String SECKILL_LUA =
        "local stockKey = KEYS[1]\n" +
        "local limitKey = KEYS[2]\n" +
        "local stock = tonumber(redis.call('get', stockKey) or '0')\n" +
        "if stock <= 0 then return -1 end\n" +
        "local count = tonumber(redis.call('incr', limitKey))\n" +
        "if count > tonumber(ARGV[1]) then redis.call('decr', limitKey); return -2 end\n" +
        "redis.call('decr', stockKey)\n" +
        "return stock - 1";

    @Override
    public int seckill(long activityId, long userId) {
        CouponSeckill act = templateMapper.getSeckillById(activityId);
        if (act == null || act.getStatus() != 1) return -3; // 活动不存在或已停用

        // 时间窗口校验
        Date now = new Date();
        if (act.getStartTime() != null && now.before(act.getStartTime())) return -4; // 活动未开始
        if (act.getEndTime() != null && now.after(act.getEndTime())) return -5;     // 活动已结束

        int perLimit = act.getPerUserLimit() == null ? 1 : act.getPerUserLimit();
        List<String> keys = List.of(
            "seckill:stock:" + activityId,
            "seckill:limit:" + activityId + ":" + userId
        );
        Long result = redis.execute(
            new DefaultRedisScript<>(SECKILL_LUA, Long.class),
            keys, String.valueOf(perLimit)
        );
        if (result == null) return -99;
        int r = result.intValue();
        if (r < 0) return r; // -1库存不足 -2超限

        // Redis扣减成功 → 同步写DB
        CouponTemplate tpl = templateMapper.getById(act.getTemplateId());
        if (tpl != null) {
            UserCoupon uc = new UserCoupon();
            uc.setUserId(userId); uc.setTemplateId(act.getTemplateId());
            uc.setSeckillId(activityId); uc.setStatus(0);
            uc.setExpireTime(new Date(System.currentTimeMillis() + 30L*24*3600*1000));
            userCouponMapper.insert(uc);
            templateMapper.incrIssued(act.getTemplateId());
            couponLog(uc.getId(), userId, "ISSUE", null, null, "秒杀领券");
        }
        return r;
    }

    // ═══════════════════════════════════════════════
    //  验券 + 预占（下单时调用）
    // ═══════════════════════════════════════════════
    @Override
    @Transactional
    public Map<String, Object> checkAndReserve(Long userId, Long couponId, BigDecimal orderAmount) {
        UserCoupon uc = userCouponMapper.getById(couponId);
        if (uc == null) return fail("券不存在");
        if (!uc.getUserId().equals(userId)) return fail("非本人券");
        if (uc.getStatus() != 0) return fail("券状态不可用");
        if (uc.getExpireTime().before(new Date())) {
            userCouponMapper.updateStatus(couponId, 2); // 过期
            return fail("券已过期");
        }
        CouponTemplate tpl = templateMapper.getById(uc.getTemplateId());
        if (tpl == null || tpl.getStatus() != 1) return fail("券模板已停用");
        if (tpl.getMinOrderAmount().compareTo(orderAmount) > 0)
            return fail("订单满" + tpl.getMinOrderAmount() + "元可用");

        // 预占
        int rows = userCouponMapper.lockCoupon(couponId);
        if (rows == 0) return fail("券被抢走了");

        BigDecimal discount = calcDiscount(tpl, orderAmount);
        couponLog(couponId, userId, "LOCK", null, discount, null);

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("success", true);
        m.put("couponId", couponId);
        m.put("templateName", tpl.getName());
        m.put("discountAmount", discount);
        return m;
    }

    // ═══════════════════════════════════════════════
    //  核销（支付成功回调）
    // ═══════════════════════════════════════════════
    @Override
    public void useCoupon(Long couponId, Long userId, Long orderId, BigDecimal actualDiscount) {
        userCouponMapper.useCoupon(couponId, orderId, new Date());
        couponLog(couponId, userId, "USE", orderId, actualDiscount, null);
    }

    // ═══════════════════════════════════════════════
    //  释放（订单取消/超时）
    // ═══════════════════════════════════════════════
    @Override
    public void releaseCoupon(Long couponId, Long userId) {
        userCouponMapper.releaseCoupon(couponId);
        couponLog(couponId, userId, "UNLOCK", null, null, "超时释放");
    }

    // ═══════════════════════════════════════════════
    //  预载秒杀库存到 Redis
    // ═══════════════════════════════════════════════
    @Override
    public void preloadStock(long activityId, int stock) {
        redis.opsForValue().set("seckill:stock:" + activityId, String.valueOf(stock));
    }

    // ═══════════════════════════════════════════════
    //  查用户可用券
    // ══════════════════════════════════════════════
    @Override
    public List<UserCoupon> myCoupons(Long userId) {
        return userCouponMapper.findByUser(userId);
    }

    // ══════════════════════════════════════════════
    //  管理端：券模板列表 & 新建
    // ═══════════════════════════════════════════════
    @Override
    public List<CouponTemplate> listTemplates() {
        return templateMapper.listAll();
    }

    @Override
    public CouponTemplate createTemplate(CouponTemplate tpl) {
        if (tpl.getIssuedCount() == null) tpl.setIssuedCount(0);
        if (tpl.getStatus() == null) tpl.setStatus(1);
        if (tpl.getValidStart() == null) tpl.setValidStart(new Date());
        if (tpl.getValidEnd() == null) tpl.setValidEnd(new Date(System.currentTimeMillis() + 90L * 24 * 3600 * 1000));
        templateMapper.insert(tpl);
        return tpl;
    }

    @Override
    public void deleteTemplate(Long id) {
        templateMapper.deleteById(id);
    }

    // ═══════════════════════════════════════════════
    //  前端首页：活跃秒杀活动列表
    // ══════════════════════════════════════════════
    @Override
    public List<CouponSeckill> listActiveSeckills() {
        List<CouponSeckill> list = templateMapper.listActiveSeckills();
        // 补充 Redis 剩余库存
        for (CouponSeckill s : list) {
            Object stock = redis.opsForValue().get("seckill:stock:" + s.getId());
            s.setSeckillStock(stock != null ? Integer.parseInt(stock.toString()) : 0);
        }
        return list;
    }

    // ═══════════════════════════════════════════════
    //  管理端：创建秒杀活动并预热库存
    // ═══════════════════════════════════════════════
    @Override
    public CouponSeckill createSeckillAndPreload(Long templateId, int seckillStock, int perUserLimit, Date startTime, Date endTime) {
        // 查找是否已有该模板的活跃秒杀活动
        CouponSeckill existing = templateMapper.findActiveSeckillByTemplate(templateId);
        if (existing != null) {
            // 已有活动，直接预热
            redis.opsForValue().set("seckill:stock:" + existing.getId(), String.valueOf(seckillStock));
            return existing;
        }
        // 创建新秒杀活动
        CouponSeckill seckill = new CouponSeckill();
        seckill.setTemplateId(templateId);
        seckill.setStartTime(startTime != null ? startTime : new Date());
        seckill.setEndTime(endTime != null ? endTime : new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000));
        seckill.setSeckillStock(seckillStock);
        seckill.setPerUserLimit(perUserLimit > 0 ? perUserLimit : 1);
        seckill.setStatus(1);
        templateMapper.insertSeckill(seckill);
        // 预热库存到 Redis
        redis.opsForValue().set("seckill:stock:" + seckill.getId(), String.valueOf(seckillStock));
        return seckill;
    }

    // ═══════════════════════════════════════════════
    //  内部方法
    // ═══════════════════════════════════════════════
    private BigDecimal calcDiscount(CouponTemplate tpl, BigDecimal orderAmount) {
        return switch (tpl.getType()) {
            case 1, 3 -> tpl.getDiscountValue();                    // 满减/无门槛
            case 2 -> orderAmount.multiply(BigDecimal.ONE.subtract(tpl.getDiscountValue())); // 折扣
            default -> BigDecimal.ZERO;
        };
    }
    private void couponLog(Long couponId, Long userId, String event, Long orderId, BigDecimal amount, String remark) {
        CouponUsageLog log = new CouponUsageLog();
        log.setCouponId(couponId);
        log.setUserId(userId);
        log.setEventType(event);
        log.setOrderId(orderId);
        log.setAmountDeducted(amount);
        log.setRemark(remark);
        usageLogMapper.insert(log);
    }
    private Map<String, Object> fail(String msg) { return Map.of("success", false, "message", msg); }
}
