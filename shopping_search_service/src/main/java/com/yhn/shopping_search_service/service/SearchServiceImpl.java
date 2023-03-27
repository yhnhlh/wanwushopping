package com.yhn.shopping_search_service.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhn.shopping_common.pojo.*;
import com.yhn.shopping_common.service.SearchService;
import com.yhn.shopping_search_service.repository.GoodsESRepository;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@DubboService
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private GoodsESRepository goodsESRepository;
    @Autowired
    private ElasticsearchRestTemplate template;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 监听同步商品队列
    @RabbitListener(queues = "sync_goods_queue")
    public void listenSyncQueue(GoodsDesc goodsDesc){
        syncGoodsToES(goodsDesc);
    }

    // 监听删除商品队列
    @RabbitListener(queues = "del_goods_queue")
    public void listenDelQueue(Long id){
        delete(id);
    }

    /**
     * 分词
     * @param text 被分词的文本
     * @param analyzer 分词器
     * @return 分词结果
     */
    @SneakyThrows // 抛出已检查异常
    public List<String> analyze(String text, String analyzer){
        // 分词请求
        AnalyzeRequest request = AnalyzeRequest.withIndexAnalyzer("goods",analyzer, text);
        // 分词响应
        AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);
        // 分词结果集合
        List<String> words = new ArrayList<>();
        // 处理响应
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            String term = token.getTerm(); // 分出的词
            words.add(term);
        }
        return words;
    }

    @Override
    public List<String> autoSuggest(String keyword) {
        // 1.创建补全条件
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        SuggestionBuilder suggestionBuilder = SuggestBuilders
                .completionSuggestion("tags")
                .prefix(keyword)
                .skipDuplicates(true)
                .size(10);

        suggestBuilder.addSuggestion("prefix_suggestion", suggestionBuilder);

        // 2.发送请求
        SearchResponse response = template.suggest(suggestBuilder, IndexCoordinates.of("goods"));

        // 3.处理结果
        List<String> result = response
                .getSuggest()
                .getSuggestion("prefix_suggestion")
                .getEntries()
                .get(0)
                .getOptions()
                .stream()
                .map(Suggest.Suggestion.Entry.Option::getText)
                .map(Text::toString)
                .collect(Collectors.toList());

        return result;
    }

    @Override
    public GoodsSearchResult search(GoodsSearchParam goodsSearchParam) {
        // 1.构造ES搜索条件
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        // 2.搜索
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        // 3.将查询结果封装为Page对象
        // 3.1 将SearchHits转为List
        List<GoodsES> content = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }
        // 3.2 将List转为MP的Page对象
        Page<GoodsES> page = new Page();
        page.setCurrent(goodsSearchParam.getPage()) // 当前页
                .setSize(goodsSearchParam.getSize()) // 每页条数
                .setTotal(search.getTotalHits()) // 总条数
                .setRecords(content); // 结果集

        // 4.封装结果对象
        // 4.1 查询结果
        GoodsSearchResult result = new GoodsSearchResult();
        result.setGoodsPage(page);
        // 4.2 查询参数
        result.setGoodsSearchParam(goodsSearchParam);
        // 4.3 查询面板
        buildSearchPanel(goodsSearchParam,result);
        return result;
    }
    /**
     * 构造搜索条件
     * @param goodsSearchParam 查询条件对象
     * @return 搜索条件对象
     */
    public NativeSearchQuery buildQuery(GoodsSearchParam goodsSearchParam){
        // 1.创建复杂查询条件对象
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // 2.如果查询条件有关键词，关键词可以匹配商品名、副标题、品牌字段；否则查询所有商品
        if (!StringUtils.hasText(goodsSearchParam.getKeyword())){
            MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
            builder.must(matchAllQueryBuilder);
        }else {
            String keyword = goodsSearchParam.getKeyword();
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "goodsName", "caption", "brand");
            builder.must(multiMatchQueryBuilder);
        }

        // 3.如果查询条件有品牌，则精准匹配品牌
        String brand = goodsSearchParam.getBrand();
        if (StringUtils.hasText(brand)){
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brand", brand);
            builder.must(termQueryBuilder);
        }

        // 4.如果查询条件有价格，则匹配价格
        Double highPrice = goodsSearchParam.getHighPrice();
        Double lowPrice = goodsSearchParam.getLowPrice();
        if (highPrice != null && highPrice != 0){
            RangeQueryBuilder lte = QueryBuilders.rangeQuery("price").lte(highPrice);
            builder.must(lte);
        }
        if (lowPrice != null && lowPrice != 0){
            RangeQueryBuilder gte = QueryBuilders.rangeQuery("price").gte(lowPrice);
            builder.must(gte);
        }

        // 5.如果查询条件有规格项，则精准匹配规格项
        Map<String, String> specificationOptions = goodsSearchParam.getSpecificationOption();
        if (specificationOptions != null && specificationOptions.size() > 0){
            Set<Map.Entry<String, String>> entries = specificationOptions.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.hasText(key)){
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("specification." + key + ".keyword", value);
                    builder.must(termQuery);
                }
            }
        }

        // 6.添加分页条件
        PageRequest pageable = PageRequest.of(goodsSearchParam.getPage() - 1, goodsSearchParam.getSize());
        // 查询构造器，添加条件和分页
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(builder).withPageable(pageable);

        // 7.如果查询条件有排序，则添加排序条件
        String sortFiled = goodsSearchParam.getSortFiled();
        String sort = goodsSearchParam.getSort();
        SortBuilder sortBuilder = null;
        if (StringUtils.hasText(sort) && StringUtils.hasText(sortFiled)){
            // 新品的正序是id的倒序
            if (sortFiled.equals("NEW")){
                sortBuilder = SortBuilders.fieldSort("id");
                if (sort.equals("ASC")){
                    sortBuilder.order(SortOrder.DESC);
                }
                if (sort.equals("DESC")){
                    sortBuilder.order(SortOrder.ASC);
                }
            }
            if (sortFiled.equals("PRICE")){
                sortBuilder = SortBuilders.fieldSort("price");
                if (sort.equals("ASC")){
                    sortBuilder.order(SortOrder.ASC);
                }
                if (sort.equals("DESC")){
                    sortBuilder.order(SortOrder.DESC);
                }
            }
            nativeSearchQueryBuilder.withSorts(sortBuilder);
        }

        // 8.返回查询条件对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        return query;
    }
    /**
     * 封装查询面板，即根据查询条件，找到查询结果关联度前20名的商品进行封装
     * @param goodsSearchParam
     * @param goodsSearchResult
     */
    public void buildSearchPanel(GoodsSearchParam goodsSearchParam,GoodsSearchResult goodsSearchResult){
        // 1.构造搜索条件
        goodsSearchParam.setPage(1);
        goodsSearchParam.setSize(20);
        goodsSearchParam.setSort(null);
        goodsSearchParam.setSortFiled(null);
        NativeSearchQuery query = buildQuery(goodsSearchParam);
        // 2.搜索
        SearchHits<GoodsES> search = template.search(query, GoodsES.class);
        // 3.将结果封装为List对象
        List<GoodsES> content = new ArrayList();
        for (SearchHit<GoodsES> goodsESSearchHit : search) {
            GoodsES goodsES = goodsESSearchHit.getContent();
            content.add(goodsES);
        }
        // 4.遍历集合，封装查询面板
        // 商品相关的品牌列表
        Set<String> brands = new HashSet();
        // 商品相关的类型列表
        Set<String> productTypes = new HashSet();
        // 商品相关的规格列表
        Map<String, Set<String>> specifications = new HashMap();
        for (GoodsES goodsES : content) {
            // 获取品牌
            brands.add(goodsES.getBrand());
            // 获取类型
            List<String> productType = goodsES.getProductType();
            productTypes.addAll(productType);
            // 获取规格
            Map<String, List<String>> specification = goodsES.getSpecification();
            Set<Map.Entry<String, List<String>>> entries = specification.entrySet();
            for (Map.Entry<String, List<String>> entry : entries) {
                // 规格名
                String key = entry.getKey();
                // 规格值
                List<String> value = entry.getValue();
                // 如果没有遍历出该规格，新增键值对，如果已经遍历出该规格，则向规格中添加规格项
                if (!specifications.containsKey(key)){
                    specifications.put(key,new HashSet(value));
                }else{
                    specifications.get(key).addAll(value);
                }
            }
        }
        goodsSearchResult.setBrands(brands);
        goodsSearchResult.setProductType(productTypes);
        goodsSearchResult.setSpecifications(specifications);
    }


    @Override
    public void syncGoodsToES(GoodsDesc goodsDesc) {
        // 将商品详情对象转为GoodsES对象
        GoodsES goodsES = new GoodsES();
        goodsES.setId(goodsDesc.getId());
        goodsES.setGoodsName(goodsDesc.getGoodsName());
        goodsES.setCaption(goodsDesc.getCaption());
        goodsES.setPrice(goodsDesc.getPrice());
        goodsES.setHeaderPic(goodsDesc.getHeaderPic());
        goodsES.setBrand(goodsDesc.getBrand().getName());
        // 类型集合
        List<String> productType = new ArrayList();
        productType.add(goodsDesc.getProductType1().getName());
        productType.add(goodsDesc.getProductType2().getName());
        productType.add(goodsDesc.getProductType3().getName());
        goodsES.setProductType(productType);
        // 规格集合
        Map<String,List<String>> map = new HashMap();
        List<Specification> specifications = goodsDesc.getSpecifications();
        // 遍历规格
        for (Specification specification : specifications) {
            // 规格项集合
            List<SpecificationOption> options = specification.getSpecificationOptions();
            // 规格项名集合
            List<String> optionStrList = new ArrayList();
            for (SpecificationOption option : options) {
                optionStrList.add(option.getOptionName());
            }
            map.put(specification.getSpecName(),optionStrList);
        }
        goodsES.setSpecification(map);
        // 关键字
        List<String> tags = new ArrayList();
        tags.add(goodsDesc.getBrand().getName()); //品牌名是关键字
        tags.addAll(analyze(goodsDesc.getGoodsName(),"ik_smart"));//商品名分词后为关键词
        tags.addAll(analyze(goodsDesc.getCaption(),"ik_smart"));//副标题分词后为关键词
        goodsES.setTags(tags);
        goodsESRepository.save(goodsES);
    }

    @Override
    public void delete(Long id) {
        goodsESRepository.deleteById(id);
    }
}
