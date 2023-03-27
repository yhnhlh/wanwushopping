package com.yhn.shopping_common.service;

import com.yhn.shopping_common.pojo.Address;
import com.yhn.shopping_common.pojo.Area;
import com.yhn.shopping_common.pojo.City;
import com.yhn.shopping_common.pojo.Province;

import java.util.List;

public interface AddressService {
    // 查询所有省份
    List<Province> findAllProvince();


    // 查询省份下的城市
    List<City> findCityByProvince(Long provinceId);


    // 查询城市下的区县
    List<Area> findAreaByCity(Long cityId);


    // 增加地址
    void add(Address address);


    // 修改地址
    void update(Address address);


    // 根据id获取地址
    Address findById(Long id);


    // 删除地址
    void delete(Long id);


    // 根据登录用户查询地址
    List<Address> findByUser(Long userId);
}
