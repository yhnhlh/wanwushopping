package com.yhn.shopping_goods_service;

import com.yhn.shopping_common.pojo.GoodsDesc;
import com.yhn.shopping_common.service.GoodsService;
import com.yhn.shopping_common.service.SearchService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShoppingGoodsServiceApplicationTests {

    @DubboReference
    private GoodsService goodsService;
    @DubboReference
    private SearchService searchService;

    @Test
    void testSyncGoodsToES(){
        List<GoodsDesc> goods = goodsService.findAll();
        for (GoodsDesc goodsDesc : goods) {
            // 如果商品是上架状态
            if (goodsDesc.getIsMarketable()){
                searchService.syncGoodsToES(goodsDesc);
            }
        }
    }


}
