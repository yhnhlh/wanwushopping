package com.yhn.shopping_file_service.service;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.yhn.shopping_common.exception.BusException;
import com.yhn.shopping_common.result.CodeEnum;
import com.yhn.shopping_common.service.FileService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@DubboService
public class FileServiceImpl  implements FileService {


    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Value("${fdfs.fileUrl}")
    private String fileUrl;

    @Override
    public String uploadImage(byte[] fileBytes, String fileName) throws IOException {
        if (fileBytes.length != 0) {
            try {
                // 1.将文件字节数组转为输入流
                InputStream inputStream = new ByteArrayInputStream(fileBytes);
                // 2.获取文件的后缀名
                String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                // 3.上传文件
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileSuffix, null);
                // 4.返回图片路径
                String imageUrl = fileUrl + "/"+storePath.getFullPath();
                return imageUrl;
            } catch (IOException ioException) {
                throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
            }
        } else {
            throw new BusException(CodeEnum.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }
}
