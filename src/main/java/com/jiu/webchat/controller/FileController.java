package com.jiu.webchat.controller;

import com.alibaba.fastjson.JSON;
import com.jiu.webchat.config.PropertiesConfig;
import com.jiu.webchat.entity.ChatRecordLogEntity;
import com.jiu.webchat.service.ChatRecordLogService;
import com.jiu.webchat.utils.FileUpload;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping(value = "file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private PropertiesConfig propertiesConfig;

    @Autowired
    private ChatRecordLogService chatRecordLogService;

    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username,
                         @RequestParam("groupId") String groupId, @RequestParam("userId") String userId) {
        Map<String, String> map;
        try {
            map = FileUpload.uploadFile(file);
            ChatRecordLogEntity chatRecordLogEntity = new ChatRecordLogEntity();
            chatRecordLogEntity.setGroupId(groupId);
            chatRecordLogEntity.setUserId(userId);
            chatRecordLogEntity.setName(username);
            chatRecordLogEntity.setText(map.get("resultMessageOldname"));
            chatRecordLogEntity.setType("2");
            chatRecordLogEntity.setFilePath(map.get("resultFilePath"));
            chatRecordLogEntity.setDistrict(propertiesConfig.getDistrict());
            chatRecordLogService.insert(chatRecordLogEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
        Map<String, Object> rt = new HashMap<>();
        rt.put("resultMessageOldname", map.get("resultMessageOldname"));
        rt.put("resultFilePath", map.get("resultFilePath"));
        return JSON.toJSONString(rt);
    }

    @RequestMapping(value = "downloadFtpFile")
    public void downloadFtpFile(@RequestParam("localPath") String localPath, @RequestParam("originalName") String originalName, HttpServletResponse response) {
        FTPClient ftpClient = null;
        try {
            ftpClient = FileUpload.getFTPClient();
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(propertiesConfig.getFtpUrl() + "/" + localPath.substring(0, 8));
            OutputStream out = response.getOutputStream();
            String filename = localPath.substring(9);
            InputStream is = ftpClient.retrieveFileStream(filename);
            if (is == null || ftpClient.getReplyCode() == FTPReply.FILE_UNAVAILABLE) {
                logger.error("ftpClient连接文件流不存在 ");
            }
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            /** 设置content-disposition响应头控制浏览器以下载的形式打开文件 */
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(originalName.getBytes(), StandardCharsets.UTF_8));
            int len = 0;
            byte[] buffer = new byte[1024];
            /** 将缓冲区的数据输出到客户端浏览器 */
            while ((len = is.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            is.close();
            out.close();
            ftpClient.logout();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实现文件打包压缩下载
     *
     * @param response 服务器响应对象HttpServletResponse
     */
    @RequestMapping(value = "packingDownload")
    public void zipDownloadFile(@RequestParam("last") String last, HttpServletResponse response) {
        String str = last;
        List<String> ids = Arrays.asList(str.split(","));
        /** 查询流转id 所有的附件 */
        List<ChatRecordLogEntity> userFile = chatRecordLogService.selectListByIdS(str);
        List<String> listname = new ArrayList<>();
        if (userFile.size() > 0) {
            for (int i = 0; i < userFile.size(); i++) {
                String string = userFile.get(i).getText();
                String[] strArr = string.split("\\/");
                listname.add(strArr[strArr.length - 1]);
            }
        }
        List<String> namelist = new ArrayList<String>();
        namelist.add("tupian");
        FTPClient ftp = FileUpload.getFTPClient();
        byte[] buf = new byte[1024];
        try {
            ftp.setControlEncoding("UTF-8");
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            ftp.changeWorkingDirectory(propertiesConfig.getFtpUrl());
            File zipFile = new File("123");
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            for (int i = 0; i < userFile.size(); i++) {
                ZipEntry entry = new ZipEntry(listname.get(i));
                zipOut.putNextEntry(entry);
                //获取数据库附件地址
                InputStream bis = ftp.retrieveFileStream("/" + userFile.get(i).getFilePath());
                if (bis != null) {
                    int readLen = -1;
                    while ((readLen = bis.read(buf, 0, 1024)) != -1) {
                        zipOut.write(buf, 0, readLen);
                    }
                    zipOut.closeEntry();
                    bis.close();
                    ftp.completePendingCommand();
                }
            }
            zipOut.close();
            ftp.logout();
            int len;
            /** 下载 */
            FileInputStream zipInput = new FileInputStream(zipFile);
            OutputStream out = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("压缩包", "UTF-8") + ".zip");
            while ((len = zipInput.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            zipInput.close();
            out.flush();
            out.close();
            /** 删除压缩包 */
            zipFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
