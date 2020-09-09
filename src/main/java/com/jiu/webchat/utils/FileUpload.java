package com.jiu.webchat.utils;

import com.jiu.webchat.config.PropertiesConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lhj
 */
public class FileUpload {

    private static String ftpIP;
    private static int ftpPost;
    private static String ftpUser;
    private static String ftpPwd;
    private static String ftpUrl;
    private static String ftpStart;
    private static String ftpBrowse;

    public static void setFileUpload(PropertiesConfig propertiesConfig) {
        ftpIP = propertiesConfig.getFtpIP();
        ftpPost = propertiesConfig.getFtpPost();
        ftpUser = propertiesConfig.getFtpUser();
        ftpPwd = propertiesConfig.getFtpPassword();
        ftpUrl = propertiesConfig.getFtpUrl();
        ftpStart = propertiesConfig.getFtpStart();
        ftpBrowse = propertiesConfig.getFtpBrowse();
    }

    /**
     * @param
     * @param
     * @param
     * @return 文件名
     */
    public static String fileUp(MultipartFile file, String filePath, String fileName) {
        // 扩展名格式：
        String extName = "";
        try {
            if (file.getOriginalFilename().lastIndexOf(".") >= 0) {
                extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }
            copyFile(file.getInputStream(), filePath, fileName + extName, file).replaceAll("-", "");
            String remoteFilePath = ftpUrl + "/" + filePath + "/" + fileName + extName;
            // 然后上传服务器
            upload(filePath + "/" + fileName + extName, remoteFilePath, file);
            return fileName + extName;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 写文件到当前目录的upload目录中
     *
     * @param in
     * @param
     * @throws IOException
     */
    private static String copyFile(InputStream in, String dir, String realName, MultipartFile oldfile) throws IOException {
        File file = new File(dir, realName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        oldfile.transferTo(file);
        return realName;
    }

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录
     * @param filename 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     */
    public static boolean uploadFile(String url, int port, String username, String password, String path, String filename, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            // 连接FTP服务器
            ftp.connect(url, port);
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            // 登录
            ftp.login(username, password);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.changeWorkingDirectory(path);
            ftp.storeFile(filename, input);
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    // 上传文件
    private static void upload(String local, String remote, MultipartFile file) throws IOException {
        FTPClient ftpClient = loginFTP();
        InputStream is = file.getInputStream();
        try {
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            ftpClient.connect(ftpIP, ftpPost);
            ftpClient.enterLocalPassiveMode();
            // 设置传输超时时间为60秒
            ftpClient.setDataTimeout(60000);
            // 连接超时为60秒
            ftpClient.setConnectTimeout(60000);
            ftpClient.login(ftpUser, ftpPwd);
            String url = remote.split(ftpUrl)[1].split("/")[1];
            if (!ftpClient.changeWorkingDirectory(url)) {
                ftpClient.changeWorkingDirectory(ftpUrl);
                ftpClient.makeDirectory(url);
            }
            ftpClient.changeWorkingDirectory(url);
            String remoteFileName = remote.substring(remote.lastIndexOf("/") + 1);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if (ftpClient.storeFile(remoteFileName, is)) {
                /*写入成功保存文件路径*/
            } else {
                /*寫入失敗*/
            }
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (Exception e) {

        } finally {
            is.close();
            ftpClient.disconnect();
        }
    }

    /**
     * 登陆FTP并获取FTPClient对象
     *
     * @return
     */
    public static FTPClient loginFTP() {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            // 连接FTP服务器
            ftpClient.connect(ftpIP, 23);
            // 登陆FTP服务器
            ftpClient.login("jsjb", "jsjb");
            // 中文支持
            ftpClient.setControlEncoding("UTF-8");
            // 设置文件类型为二进制（如果从FTP下载或上传的文件是压缩文件的时候，不进行该设置可能会导致获取的压缩文件解压失败）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
            } else {
            }
        } catch (Exception e) {
        }
        return ftpClient;
    }

    /**
     * 从FTP下载文件到本地
     *
     * @param ftpClient     已经登陆成功的FTPClient
     * @param ftpFilePath   FTP上的目标文件路径
     * @param localFilePath 下载到本地的文件路径
     */
    public static void downloadFileFromFTP(FTPClient ftpClient, String ftpFilePath, String localFilePath) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            // 获取ftp上的文件
            is = ftpClient.retrieveFileStream(ftpFilePath);
            fos = new FileOutputStream(new File(localFilePath));
            // 文件读取方式一
            int i;
            byte[] bytes = new byte[1024];
            while ((i = is.read(bytes)) != -1) {
                fos.write(bytes, 0, i);
            }
            // 文件读取方式二
            // ftpClient.retrieveFile(ftpFilePath, new FileOutputStream(new File(localFilePath)));
            ftpClient.completePendingCommand();
        } catch (Exception e) {
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从FTP下载文件到本地
     *
     * @param ftpClient   已经登陆成功的FTPClient
     * @param ftpFilePath FTP上的目标文件路径
     */
    public static InputStream downloadFile(FTPClient ftpClient, String ftpFilePath) {
        InputStream is = null;
        try {
            // 获取ftp上的文件
            is = ftpClient.retrieveFileStream(ftpFilePath);
            ftpClient.completePendingCommand();
        } catch (Exception e) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return is;
    }

    public static Map<String, String> uploadFile(MultipartFile uploadFile) {
        Map<String, String> resultmMap = new HashMap<String, String>();
        // 生成一个文件名
        // 获取旧的名字
        String oldName = uploadFile.getOriginalFilename();
        // 新名字
        String newName = UUIDGenerator.getUUID();
        String filePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
        // 端口号
        try {
            if (null != uploadFile && !uploadFile.isEmpty()) {
                //文件上传路径
                newName = fileUp(uploadFile, filePath, newName);
            }
            resultmMap.put("resultCode", "1");
            resultmMap.put("resultFilePath", filePath + "/" + newName);
            resultmMap.put("resultMessageOldname", oldName);
            return resultmMap;
        } catch (Exception e) {
            resultmMap.put("resultCode", "0");
            resultmMap.put("resultFilePath", filePath + "/" + newName);
            resultmMap.put("resultMessageOldname", oldName);
            return resultmMap;
        }
    }


    /**
     * 获取FTPClient对象
     */
    public static FTPClient getFTPClient() {
        FTPClient ftpClient = new FTPClient();
        try {
            //设置传输超时时间为60秒
            ftpClient.setDataTimeout(60000);
            ftpClient.setConnectTimeout(60000);
            // 连接FTP服务器
            ftpClient.connect(ftpIP, ftpPost);
            // 登陆FTP服务器
            ftpClient.login(ftpUser, ftpPwd);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                ftpClient.disconnect();
            } else {
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    public static boolean uploadFile(String filePath, String filename, InputStream input) {
        boolean result = false;
        FTPClient ftp = getFTPClient();
        try {
            int reply;
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return result;
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            // 设置传输超时时间为60秒
            ftp.setDataTimeout(60000);
            // 连接超时为60秒
            ftp.setConnectTimeout(60000);

            //切换到上传目录
            if (!ftp.changeWorkingDirectory(filePath)) {
                //如果目录不存在创建目录
                String[] dirs = filePath.split("/");
                String tempPath = "1";
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    tempPath += "/" + dir;
                    if (!ftp.changeWorkingDirectory(tempPath)) {
                        if (!ftp.makeDirectory(tempPath)) {
                            return result;
                        } else {
                            ftp.changeWorkingDirectory(tempPath);
                        }
                    }
                }
            }
            //设置上传文件的类型为二进制类型
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            //上传文件
            if (!ftp.storeFile(filename, input)) {
                return result;
            }
            input.close();
            ftp.logout();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return result;
    }

}
