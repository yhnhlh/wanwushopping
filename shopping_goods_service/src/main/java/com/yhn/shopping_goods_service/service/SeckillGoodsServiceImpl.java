package com.yhn.shopping_goods_service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.pojo.SeckillGoods;
import com.yhn.shopping_common.service.SeckillGoodsService;
import com.yhn.shopping_goods_service.mapper.SeckillGoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    @Override
    public void add(SeckillGoods seckillGoods) {
        seckillGoodsMapper.insert(seckillGoods);
    }


    @Override
    public void update(SeckillGoods seckillGoods) {
        seckillGoodsMapper.updateById(seckillGoods);
    }


    @Override
    public Page<SeckillGoods> findPage(int page, int size) {
        return seckillGoodsMapper.selectPage(new Page(page, size), null);
    }
}

