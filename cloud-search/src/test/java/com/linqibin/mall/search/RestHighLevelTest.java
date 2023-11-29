package com.linqibin.mall.search;

import com.alibaba.fastjson.JSON;
import com.linqibin.mall.search.config.MallElasticSearchConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.4/java-rest-high.html">具体api参考官方文档</a>
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RestHighLevelTest {

    @Resource
    private RestHighLevelClient client;

    /**
     * 测试存储数据到es
     */
    @Test
    public void testIndexData() throws IOException {
        // 1. 创建保存操作的Request对象
        IndexRequest indexRequest = new IndexRequest("users");
        // 2. 指定数据的id， 如果没有指定则默认生成
        indexRequest.id("1");
        // 3. 将java对象转成json字符串直接保存
        String source = JSON.toJSONString(new User("linqibin", 18, "M"));
        indexRequest.source(source, XContentType.JSON);
        IndexResponse response = client.index(indexRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        // IndexResponse[index=users,type=_doc,id=1,version=1,result=created,seqNo=0,primaryTerm=1,shards={"total":2,"successful":1,"failed":0}]
        System.out.println(response);
    }

    /**
     * 测试检索数据
     */
    @Test
    public void testSearchData() throws IOException {
        SearchRequest request = new SearchRequest("users");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页条件
        sourceBuilder.from(0).size(10);
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, MallElasticSearchConfig.COMMON_OPTIONS);
        /*
          {"took":2,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},
          "hits":{"total":{"value":1,"relation":"eq"},"max_score":1.0,
          "hits":[{"_index":"users","_type":"_doc","_id":"1","_score":1.0,"_source":{"age":18,"gender":"M","username":"linqibin"}}]}}
         */
        System.out.println(response);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String username;
        private Integer age;
        private String gender;
    }
}

