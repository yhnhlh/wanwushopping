package com.yhn.shopping_goods_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhn.shopping_common.pojo.Specification;

import java.util.List;

public interface SpecificationMapper extends BaseMapper<Specification> {
    Specification findById(Long id);
    List<Specification> findByProductTypeId(Long productTypeId);
}
