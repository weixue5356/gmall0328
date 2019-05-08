package com.atguigu.gmall.manage;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HTTP;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

	@Test
	public void contextLoads(HttpServletRequest request) {

		String servletPath = request.getServletPath();
		//http://localhost:8080/atcrowdfunding-main/user/index.htm
		System.out.println("servletPath" + servletPath);
	}

}
