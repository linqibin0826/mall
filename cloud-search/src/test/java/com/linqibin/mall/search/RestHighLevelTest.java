package com.linqibin.mall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RestHighLevelTest {

    @Resource
    private RestHighLevelClient client;

    @Test
    public void testSend() {
        System.out.println(client);
    }
}

