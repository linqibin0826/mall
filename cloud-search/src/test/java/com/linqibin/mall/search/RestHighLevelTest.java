package com.linqibin.mall.search;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.linqibin.mall.search.config.MallElasticSearchConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
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
     * 测试检索数据,空间和时间总是不能兼得
     */
    @Test
    public void testSearchData() throws IOException {
        SearchRequest request = new SearchRequest("newbank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 1. 分页条件
        sourceBuilder.from(0).size(10);
        // 2. 查询条件
        // sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // 3. 聚合
        sourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age"));
        sourceBuilder.aggregation(AggregationBuilders.avg("ageAvg").field("age"));
        sourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));

        request.source(sourceBuilder);
        System.out.println(sourceBuilder);
        SearchResponse response = client.search(request, MallElasticSearchConfig.COMMON_OPTIONS);
        /*
          {"took":2,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},
          "hits":{"total":{"value":1,"relation":"eq"},"max_score":1.0,
          "hits":[{"_index":"users","_type":"_doc","_id":"1","_score":1.0,"_source":{"age":18,"gender":"M","username":"linqibin"}}]}}
         */
        System.out.println(response);
        // 4. 解析命中的数据
        SearchHits searchHits = response.getHits();
        for (SearchHit hit : searchHits.getHits()) {
            Account account = JSON.parseObject(hit.getSourceAsString(), Account.class);
            System.out.println(account);
        }
        // 5. 解析聚合
        Aggregations aggregations = response.getAggregations();
        Terms ageAgg = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            System.out.println("年龄分布:" + bucket.getKeyAsString() + "===>" + bucket.getDocCount());
        }
        Avg ageAvg = aggregations.get("ageAvg");
        System.out.println("平均年龄: " + ageAvg.getValue());
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资: " + balanceAvg.getValue());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class User {
        private String username;
        private Integer age;
        private String gender;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Account {
        @JsonProperty("account_number")
        private Integer accountNumber;

        @JsonProperty("balance")
        private Integer balance;

        @JsonProperty("firstname")
        private String firstname;

        @JsonProperty("lastname")
        private String lastname;

        @JsonProperty("age")
        private Integer age;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("address")
        private String address;

        @JsonProperty("employer")
        private String employer;

        @JsonProperty("email")
        private String email;

        @JsonProperty("city")
        private String city;

        @JsonProperty("state")
        private String state;
    }
}

