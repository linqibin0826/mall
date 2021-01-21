package com.linqibin.mall.thirdparty;


import com.aliyun.oss.OSSClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudThirdPartyApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Autowired
	private OSSClient ossClient;

	@Test
	public void test1() throws FileNotFoundException {
		FileInputStream stream = new FileInputStream("C:\\Users\\Administrator\\Pictures\\电子照片\\beauty_20201012174823.jpg");
		ossClient.putObject("mall-hugh", "beauty_20201012174823.jpg", stream);

		ossClient.shutdown();

		System.out.println("上传成功");
	}

}
