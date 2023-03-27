package com.yhn.shopping_goods_service.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.exception.BusException;
import com.yhn.shopping_common.pojo.Brand;
import com.yhn.shopping_common.result.CodeEnum;
import com.yhn.shopping_common.service.BrandService;
import com.yhn.shopping_goods_service.mapper.BrandMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@DubboService
@Transactional
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @Override
    public Brand findById(Long id) {
        if (id == 0){
            int i = 1/0; // 模拟系统异常
        }else if (id == -1){
            throw new BusException(CodeEnum.PARAMETER_ERROR); // 模拟业务异常
        }
        return brandMapper.selectById(id);
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectList(null);
    }

    @Override
    public void add(Brand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateById(brand);
    }

    @Override
    public void delete(Long id) {
        brandMapper.deleteById(id);
    }

    @Override
    public Page<Brand> search(Brand brand, int page, int size) {
        QueryWrapper<Brand> queryWrapper = new QueryWrapper();
        // 判断品牌名不为空
        if (brand != null && StringUtils.hasText(brand.getName())){
            queryWrapper.like("name",brand.getName());
        }
        Page<Brand> page1 = brandMapper.selectPage(new Page(page, size), queryWrapper);
        return page1;
    }
}
