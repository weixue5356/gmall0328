package com.atguigu.gmall.manager;

import com.sun.demo.jvmti.hprof.Tracker;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManagerWebApplicationTests {

	@Test
	public void contextLoads() throws IOException, MyException {

		String path = GmallManagerWebApplicationTests.class.getClassLoader()
				.getResource("tracker.conf").getFile();
		     ClientGlobal.init(path);

		TrackerClient trackerClient = new TrackerClient();
		TrackerServer server = trackerClient.getConnection();
		StorageClient storageClient = new StorageClient(server,null);

		String[] jpgs = storageClient.upload_appender_file("C:/resource/aaa.jpg", "jpg", null);
		String url = "http://192.168.252.135";
		for (String jpg : jpgs) {
			url = url + "/" + jpg;
		}
		System.out.print(url);

	}

}
