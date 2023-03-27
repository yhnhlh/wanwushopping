package com.yhn.shopping_order_customer_api.controller;

import com.yhn.shopping_common.pojo.Address;
import com.yhn.shopping_common.pojo.Area;
import com.yhn.shopping_common.pojo.City;
import com.yhn.shopping_common.pojo.Province;
import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.AddressService;
import com.yhn.shopping_common.util.JWTUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址
 */
@RestController
@RequestMapping("/user/address")
public class AddressController {
    @DubboReference
    private AddressService addressService;

    /**
     * 查询所有省份
     * @return 所有省份
     */
    @GetMapping("/findAllProvince")
    public BaseResult<List<Province>> findAllProvince() {
        List<Province> provinces = addressService.findAllProvince();
        return BaseResult.ok(provinces);
    }


    /**
     * 查询省份下的城市
     * @param provinceId 省份Id
     * @return 城市集合
     */
    @GetMapping("/findCityByProvince")
    public BaseResult<List<City>> findCityByProvince(Long provinceId) {
        List<City> cities = addressService.findCityByProvince(provinceId);
        return BaseResult.ok(cities);
    }




    /**
     * 查询城市下的区县
     * @param cityId 城市Id
     * @return 区县集合
     */
    @GetMapping("/findAreaByCity")
    public BaseResult<List<Area>> findAreaByCity(Long cityId) {
        List<Area> areas = addressService.findAreaByCity(cityId);
        return BaseResult.ok(areas);
    }


    /**
     * 增加地址
     * @param address 地址对象
     * @return
     */
    @PostMapping("/add")
    public BaseResult add(@RequestHeader String token, @RequestBody Address address) {
        Long userId = JWTUtil.getId(token); // 获取用户id
        address.setUserId(userId);
        addressService.add(address);
        return BaseResult.ok();
    }


    /**
     * 修改地址
     * @param address 地址对象
     * @return
     */
    @PutMapping("/update")
    public BaseResult update(@RequestHeader String token,@RequestBody Address address) {
        Long userId = JWTUtil.getId(token); // 获取用户id
        address.setUserId(userId);
        addressService.update(address);
        return BaseResult.ok();
    }


    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    @GetMapping("/findById")
    public BaseResult<Address> findById(Long id) {
        Address address = addressService.findById(id);
        return BaseResult.ok(address);
    }


    /**
     * 删除地址
     * @param id 地址id
     * @return
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long id) {
        addressService.delete(id);
        return BaseResult.ok();
    }


    /**
     * 根据登录用户查询地址
     * @return
     */
    @GetMapping("/findByUser")
    public BaseResult<List<Address>> findByUser(@RequestHeader String token) {
        Long userId = JWTUtil.getId(token); // 获取用户id
        List<Address> addresses = addressService.findByUser(userId);
        return BaseResult.ok(addresses);
    }
}

