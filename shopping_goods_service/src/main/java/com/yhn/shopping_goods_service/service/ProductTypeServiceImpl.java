package com.yhn.shopping_goods_service.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.exception.BusException;
import com.yhn.shopping_common.pojo.ProductType;
import com.yhn.shopping_common.result.CodeEnum;
import com.yhn.shopping_common.service.ProductTypeService;
import com.yhn.shopping_goods_service.mapper.ProductTypeMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

@DubboService
public class ProductTypeServiceImpl implements ProductTypeService {
    @Autowired
    private ProductTypeMapper productTypeMapper;


    @Override
    public void add(ProductType productType) {
        // 查询父类型
        ProductType productTypeParent = productTypeMapper.selectById(productType.getParentId());


        if (productTypeParent == null){ // 如果没有父类型，则为1级类型
            productType.setLevel(1);
        }else if(productTypeParent.getLevel() < 3){ // 如果父类型级别<3，则级别为父级别+1
            productType.setLevel(productTypeParent.getLevel()+1);
        }else if(productTypeParent.getLevel() >= 3){ // 如果父类型级别>=3，则不能添加子类型
            throw new BusException(CodeEnum.INSERT_PRODUCT_TYPE_ERROR);
        }
        productTypeMapper.insert(productType);
    }


    @Override
    public void update(ProductType productType) {
        // 查询父类型
        ProductType productTypeParent = productTypeMapper.selectById(productType.getParentId());


        if (productTypeParent == null){ // 如果没有父类型，则为1级类型
            productType.setLevel(1);
        }else if(productTypeParent.getLevel() < 3){ // 如果父类型级别<3，则级别为父级别+1
            productType.setLevel(productTypeParent.getLevel()+1);
        }else if(productTypeParent.getLevel() >= 3){ // 如果父类型级别>=3，则不能添加子类型
            throw new BusException(CodeEnum.INSERT_PRODUCT_TYPE_ERROR);
        }


        productTypeMapper.updateById(productType);
    }


    @Override
    public void delete(Long id) {
        // 查询该类型的子类型
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parentId",id);
        List<ProductType> productTypes = productTypeMapper.selectList(queryWrapper);
        // 如果该类型有子类型，删除失败
        if (productTypes != null && productTypes.size() > 0){
            throw new BusException(CodeEnum.DELETE_PRODUCT_TYPE_ERROR);
        }
        productTypeMapper.deleteById(id);
    }


    @Override
    public ProductType findById(Long id) {
        return productTypeMapper.selectById(id);
    }


    @Override
    public Page<ProductType> search(ProductType productType,int page, int size) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        if (productType != null){
            // 类型名不为空时
            if (StringUtils.hasText(productType.getName())){
                queryWrapper.like("name",productType.getName());
            }
            // 上级类型id不为空
            if (productType.getParentId()!= null){
                queryWrapper.eq("parentId",productType.getParentId());
            }
        }
        return productTypeMapper.selectPage(new Page(page,size),queryWrapper);
    }


    @Override
    public List<ProductType> findProductType(ProductType productType) {
        QueryWrapper<ProductType> queryWrapper = new QueryWrapper();
        if (productType != null){
            // 类型名不为空时
            if (StringUtils.hasText(productType.getName())){
                queryWrapper.like("name",productType.getName());
            }
            // 上级类型id不为空
            if (productType.getParentId()!= null){
                queryWrapper.eq("parentId",productType.getParentId());
            }
        }
        List<ProductType> productTypes = productTypeMapper.selectList(queryWrapper);
        return productTypes;
    }
}

