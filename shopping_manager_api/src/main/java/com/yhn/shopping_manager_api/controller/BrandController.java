package com.yhn.shopping_manager_api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.pojo.Brand;
import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.BrandService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    //远程注入
    @DubboReference
    private BrandService brandService;

    @GetMapping("/findById")
    public BaseResult<Brand> findById(Long id){
        Brand brand=brandService.findById(id);
        return BaseResult.ok(brand);
    }

    @GetMapping("/all")
    BaseResult<List<Brand>> findAll() {
        List<Brand> brands = brandService.findAll();
        return BaseResult.ok(brands);
    }

    /**
     * 新增品牌
     *
     * @param brand 品牌对象
     * @return 执行结果
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody Brand brand) {
        brandService.add(brand);
        return BaseResult.ok();
    }

    /**
     * 修改品牌
     *
     * @param brand 品牌对象
     * @return 执行结果
     */
    @PutMapping("/update")
    public BaseResult update(@RequestBody Brand brand) {
        brandService.update(brand);
        return BaseResult.ok();
    }

    /**
     * 删除品牌
     *
     * @param id 品牌id
     * @return 执行结果
     */
    @DeleteMapping("/delete")
    public BaseResult delete(Long id) {
        brandService.delete(id);
        return BaseResult.ok();
    }

    /**
     * 分页查询品牌
     *
     * @param brand 查询条件对象
     * @param page  页码
     * @param size  每页条数
     * @return 查询结果
     */
    @GetMapping("/search")
    public BaseResult<Page<Brand>> search(Brand brand, int page, int size) {

        Page<Brand> page1 = brandService.search(brand, page, size);
        return BaseResult.ok(page1);
    }


}
