package com.atguigu.gmall.manager.controller.utils;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MyUploadUtils {

    public static String uploadImg(MultipartFile file) {
        String path = MyUploadUtils.class.getClassLoader()
                .getResource("tracker.conf").getFile();
        try {
            ClientGlobal.init(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

        TrackerClient trackerClient = new TrackerClient();
        TrackerServer server = null;
        try {
            server = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient = new StorageClient(server, null);
        String originalFilename = file.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");

        String substring = originalFilename.substring(i + 1);
        String[] strings = new String[0];
        ;
        try {
            strings = storageClient.upload_file(file.getBytes(), substring, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String url = "http://192.168.252.135";
        for (String string : strings) {
            url = url + "/" +string;
        }
        return url;
    }
}