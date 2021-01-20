package com.linqibin.mall.product;

import com.linqibin.mall.product.service.BrandService;
import com.linqibin.mall.product.entity.BrandEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 阿里云OSS快速上手指南
 * 1)引入oss-starter
 * 2)在配置文件中配置好ak、as、ep相关信息即可
 * 3)使用OSSClient 自动注入.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CloudProductApplicationTests {

	@Autowired
    BrandService brandService;

	@Test
	public void contextLoads() {
		BrandEntity entity = new BrandEntity();
		entity.setName("苹果");
		brandService.save(entity);
		System.out.println("============================");
	}
}
