package com.atguigu.gmall.order;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallOrderServiceApplicationTests {

	@Test
	public void contextLoads() {
		String v = UUID.randomUUID().toString();
		System.out.println(v);
	}

}
