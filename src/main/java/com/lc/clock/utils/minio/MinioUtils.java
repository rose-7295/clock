package com.lc.clock.utils.minio;

import com.alibaba.fastjson.JSON;
import com.lc.clock.utils.RespBean;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-12  3:36 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@Component
public class MinioUtils {
    private static final Logger log = LoggerFactory.getLogger(MinioUtils.class);

    private static MinioClient minioClient;

    private MinioProp minioProp = new MinioProp();


    private static final String ENDPOINT = "http://47.108.228.4:9000";
    private static final String ACCESS_KEY = "chasing";
    private static final String SECRET_KEY = "chasing1874";
    private static final String MINIO_BUCKET = "img";


    static {
        try {


            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            minioClient = new MinioClient(ENDPOINT, ACCESS_KEY, SECRET_KEY);

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(MINIO_BUCKET);
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket(MINIO_BUCKET);
            }
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看桶列表
     * @param map
     */
    public List<Object> list(ModelMap map) throws Exception {
        Iterable<Result<Item>> myObjects = minioClient.listObjects(MINIO_BUCKET);
        Iterator<Result<Item>> iterator = myObjects.iterator();
        List<Object> items = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
        }
        return items;
    }

    public static RespBean upload(@RequestParam(name = "file", required = false) MultipartFile[] file) throws InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RespBean res = new RespBean();
        res.setStatus(500);

        if (file == null || file.length == 0) {
            res.setMsg("上传文件不能为空");
            return res;
        }

        List<String> orgfileNameList = new ArrayList<>(file.length);

        for (MultipartFile multipartFile : file) {
            String orgfileName = multipartFile.getOriginalFilename();
            orgfileNameList.add(orgfileName);

            try {
                InputStream in = multipartFile.getInputStream();
                minioClient.putObject(MINIO_BUCKET, orgfileName, in, new PutObjectOptions(in.available(), -1));
                in.close();
            } catch (Exception e) {
                log.error(e.getMessage());
                res.setMsg("上传失败");
                return res;
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bucketName", MINIO_BUCKET);
        data.put("fileName", orgfileNameList);
        res.setStatus(200);
        res.setMsg("上传成功");
        res.setData(data);
        return res;
    }

    public static void download(HttpServletResponse response, @PathVariable("fileName") String fileName) {
        InputStream in = null;
        try {
            ObjectStat stat = minioClient.statObject(MINIO_BUCKET, fileName);
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            in = minioClient.getObject(MINIO_BUCKET, fileName);
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public RespBean delete(@PathVariable("fileName") String fileName) {
        RespBean res = new RespBean();
        res.setStatus(200);
        try {
            minioClient.removeObject(MINIO_BUCKET, fileName);
        } catch (Exception e) {
            res.setStatus(500);
            log.error(e.getMessage());
        }
        return res;
    }


    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
        }
        return fileSizeString;
    }

}
