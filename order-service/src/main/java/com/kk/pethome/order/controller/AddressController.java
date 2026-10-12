package com.kk.pethome.order.controller;

import com.kk.pethome.basic.result.jsonresult;
import com.kk.pethome.order.mapper.AddressMapper;
import com.kk.pethome.order.pojo.UserAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/address")
public class AddressController {
    @Autowired
    private AddressMapper addressMapper;

    @GetMapping
    public jsonresult list(@RequestParam Long userId) {
        return jsonresult.success(addressMapper.findByUserId(userId));
    }

    @PostMapping
    public jsonresult save(@RequestBody Map<String, Object> body) {
        UserAddress addr = new UserAddress();
        addr.setUserId(Long.valueOf(body.get("userId").toString()));
        addr.setRecipient((String) body.get("recipient"));
        addr.setPhone((String) body.get("phone"));
        addr.setAddress((String) body.get("address"));
        addr.setIsDefault(Boolean.TRUE.equals(body.get("isDefault")));
        addressMapper.Add(addr);
        if (addr.getIsDefault()) addressMapper.setDefault(addr.getUserId(), addr.getId());
        return jsonresult.success(addr);
    }

    @PutMapping("/{id}")
    public jsonresult update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        UserAddress addr = addressMapper.lookById(id);
        if (addr == null) return jsonresult.fail().setMessage("地址不存在");
        if (body.containsKey("recipient")) addr.setRecipient((String) body.get("recipient"));
        if (body.containsKey("phone")) addr.setPhone((String) body.get("phone"));
        if (body.containsKey("address")) addr.setAddress((String) body.get("address"));
        addressMapper.Update(addr);
        return jsonresult.success(addr);
    }

    @PutMapping("/{id}/default")
    public jsonresult setDefault(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        addressMapper.setDefault(userId, id);
        return jsonresult.success("ok");
    }

    @DeleteMapping("/{id}")
    public jsonresult delete(@PathVariable Long id) {
        addressMapper.Delete(id);
        return jsonresult.success("ok");
    }
}
