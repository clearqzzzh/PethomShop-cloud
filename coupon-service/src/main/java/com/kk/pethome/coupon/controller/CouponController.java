package com.kk.pethome.coupon.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.coupon.pojo.CouponSeckill;
import com.kk.pethome.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    @Autowired private CouponService couponService;

    /** 秒杀领券 */
    @PostMapping("/seckill")
    @Operation(summary = "秒杀领券")
    public jsonresult seckill(@RequestBody Map<String, Object> body) {
        long activityId = Long.parseLong(body.get("activityId").toString());
        long userId = Long.parseLong(body.get("userId").toString());
        int r = couponService.seckill(activityId, userId);
        return switch (r) {
            case -1 -> jsonresult.fail().setMessage("券已抢光");
            case -2 -> jsonresult.fail().setMessage("已达领取上限");
            case -3 -> jsonresult.fail().setMessage("活动无效");
            case -4 -> jsonresult.fail().setMessage("活动未开始");
            case -5 -> jsonresult.fail().setMessage("活动已结束");
            default -> jsonresult.success(Map.of("remaining", r));
        };
    }

    /** 我的优惠券 */
    @GetMapping("/my")
    public jsonresult myCoupons(@RequestParam Long userId) {
        return jsonresult.success(couponService.myCoupons(userId));
    }

    /** 验券（下单前校验+预占） */
    @PostMapping("/check")
    public jsonresult checkAndReserve(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long couponId = Long.valueOf(body.get("couponId").toString());
        BigDecimal amount = new BigDecimal(body.get("orderAmount").toString());
        Map<String, Object> r = couponService.checkAndReserve(userId, couponId, amount);
        if (Boolean.TRUE.equals(r.get("success")))
            return jsonresult.success(r);
        return jsonresult.fail().setMessage((String) r.get("message"));
    }

    /** 核销（支付回调） */
    @PostMapping("/use")
    public jsonresult use(@RequestBody Map<String, Object> body) {
        couponService.useCoupon(
            Long.valueOf(body.get("couponId").toString()),
            Long.valueOf(body.get("userId").toString()),
            Long.valueOf(body.get("orderId").toString()),
            new BigDecimal(body.getOrDefault("amount", "0").toString())
        );
        return jsonresult.success("ok");
    }

    /** 预载秒杀库存到 Redis */
    @PostMapping("/preload")
    public jsonresult preload(@RequestBody Map<String, Object> body) {
        long activityId = Long.parseLong(body.get("activityId").toString());
        int stock = Integer.parseInt(body.get("stock").toString());
        couponService.preloadStock(activityId, stock);
        return jsonresult.success(Map.of("activityId", activityId, "stock", stock));
    }

    /** 释放（订单取消） */
    @PostMapping("/release")
    public jsonresult release(@RequestBody Map<String, Object> body) {
        couponService.releaseCoupon(
            Long.valueOf(body.get("couponId").toString()),
            Long.valueOf(body.get("userId").toString())
        );
        return jsonresult.success("ok");
    }

    /** 管理端：券模板列表 */
    @GetMapping("/templates")
    @Operation(summary = "管理端-券模板列表")
    public jsonresult listTemplates() {
        return jsonresult.success(couponService.listTemplates());
    }

    /** 管理端：新建券模板 */
    @PostMapping("/templates")
    @Operation(summary = "管理端-新建券模板")
    public jsonresult createTemplate(@RequestBody com.kk.pethome.coupon.pojo.CouponTemplate tpl) {
        return jsonresult.success(couponService.createTemplate(tpl));
    }

    /** 管理端：删除券模板 */
    @DeleteMapping("/templates/{id}")
    @Operation(summary = "管理端-删除券模板")
    public jsonresult deleteTemplate(@PathVariable Long id) {
        couponService.deleteTemplate(id);
        return jsonresult.success("ok");
    }

    /** 管理端：创建秒杀活动并预热库存 */
    @PostMapping("/seckill/preload")
    @Operation(summary = "管理端-创建秒杀活动并预热")
    public jsonresult seckillPreload(@RequestBody Map<String, Object> body) {
        Long templateId = Long.valueOf(body.get("templateId").toString());
        int stock = Integer.parseInt(body.getOrDefault("stock", "100").toString());
        int perUserLimit = Integer.parseInt(body.getOrDefault("perUserLimit", "1").toString());
        Date startTime = null, endTime = null;
        try {
            if (body.containsKey("startTime") && body.get("startTime") != null)
                startTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(body.get("startTime").toString());
            if (body.containsKey("endTime") && body.get("endTime") != null)
                endTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(body.get("endTime").toString());
        } catch (Exception ignored) {}
        CouponSeckill seckill = couponService.createSeckillAndPreload(templateId, stock, perUserLimit, startTime, endTime);
        return jsonresult.success(seckill);
    }

    /** 前端首页：活跃秒杀活动列表 */
    @GetMapping("/seckill/active")
    @Operation(summary = "前端-活跃秒杀活动")
    public jsonresult activeSeckills() {
        return jsonresult.success(couponService.listActiveSeckills());
    }
}
