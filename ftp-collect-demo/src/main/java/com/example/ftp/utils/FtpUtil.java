package com.example.ftp.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.annotation.Resource;
import java.io.*;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
/**
 * @author Nick
 */

public class FtpUtil {
    private final static Log logger = LogFactory.getLog(FtpUtil.class);

    private static final String FTP_ADDRESS = "49.123.92.88";

    private static final int FTP_PORT = 21;

    private static final String FTP_USERNAME = "user2";

    private static final String FTP_PASSWORD = "123";

    private static final String FTP_BASEPATH = "/home/user2/files";

    private static final String LOCAL_PATH = "C:/Users/Dell/Desktop";

    private static String LOCAL_CHARSET = "GBK";

    private static String SERVER_CHARSET = "ISO-8859-1";

    public static String uploadFile(MultipartFile file) throws IOException {
        //获取上传的文件流
        InputStream inputStream = file.getInputStream();
        //获取上传的文件名
        String fileName = file.getOriginalFilename();
        //截取文件名后缀
        //String suffix = filename.substring(filename.lastIndexOf("."));
        //使用UUID拼接后缀 避免文件重名
        //String finalName = UUID.randomUUID()+suffix;


        FTPClient ftp = new FTPClient();
        int reply;
        try {
            //客户端与ftp服务器的连接、登录
            ftp.connect(FTP_ADDRESS, FTP_PORT);
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.info("未连接到FTP 用户名或密码错误");
                ftp.disconnect();
                return null;
            }
            //设置文件传输格式
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(FTP_BASEPATH);
            ftp.changeWorkingDirectory(FTP_BASEPATH);
            //切换为被动支持模式
            ftp.enterLocalPassiveMode();
            //存文件
            ftp.storeFile(fileName, inputStream);
            inputStream.close();
            ftp.logout();
        } catch (SocketException e) {
            e.printStackTrace();
            logger.info("FTP的IP地址可能错误，请正确配置。");
            return null;
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    logger.info("FTP的端口错误,请正确配置。");
                }
            }
        }
        return fileName;
    }

    public static void downloadFile(String fileName) {

        FTPClient ftp = new FTPClient();
        int reply;
        try {
            ftp.connect(FTP_ADDRESS, FTP_PORT);
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.info("未连接到FTP 用户名或密码错误");
                ftp.disconnect();
            }

            if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }

            ftp.setControlEncoding(LOCAL_CHARSET);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

            ftp.changeWorkingDirectory(FTP_BASEPATH);

            InputStream retrieveFileStream = ftp.retrieveFileStream(fileName);

            byte[] input2byte = input2byte(retrieveFileStream);
            byte2File(input2byte, LOCAL_PATH, fileName);

            if (null != retrieveFileStream) {
                retrieveFileStream.close();
            }

        } catch (FileNotFoundException e) {
            logger.error("没有找到" + FTP_BASEPATH + "文件");
            e.printStackTrace();
        } catch (SocketException e) {
            logger.error("连接FTP失败.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("文件读取错误。");
            e.printStackTrace();
        } finally {

            if (ftp.isConnected()) {
                try {
                    //退出登录
                    ftp.logout();
                    //关闭连接
                    ftp.disconnect();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void deleteFile(String fileName) {
        FTPClient ftp = new FTPClient();
        int reply;
        try {
            ftp.connect(FTP_ADDRESS, FTP_PORT);
            ftp.login(FTP_USERNAME, FTP_PASSWORD);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                logger.info("未连接到FTP 用户名或密码错误");
                ftp.disconnect();
            }
            ftp.changeWorkingDirectory(FTP_BASEPATH);

            if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }

            ftp.setControlEncoding(LOCAL_CHARSET);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

            fileName = new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET);
            ftp.dele(fileName);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    //退出登录
                    ftp.logout();
                    //关闭连接
                    ftp.disconnect();
                } catch (IOException e) {
                }
            }
        }
    }
//    @Resource
//    private TAreaMapper areaMapper;
//    @Scheduled
//    public static void downloadFileTime(String fileName) {
//        FTPClient ftp = new FTPClient();
//        SimpleDateFormat fileDate = new SimpleDateFormat("yyyyMMdd");
//        String nowDate = fileDate.format(new Date());//当前日期
//        String newName="area_"+nowDate+".txt";//文件名
//        int reply;
//        try {
//            ftp.connect(FTP_ADDRESS, FTP_PORT);
//            ftp.login(FTP_USERNAME, FTP_PASSWORD);
//            reply = ftp.getReplyCode();
//            if (!FTPReply.isPositiveCompletion(reply)) {
//                logger.info("未连接到FTP 用户名或密码错误");
//                ftp.disconnect();
//            }
//            ftp.changeWorkingDirectory(FTP_BASEPATH);
//
//            FTPFile[] fs = ftp.listFiles();//ftp下的所有文件名称
//            for (FTPFile ftpfile : fs) { //遍历所有文件
//                fileName = ftpfile.getName();
//                if (fileName.equals(newName)) {
//                    logger.info("********" + nowDate + "：开始下载区域信息数据！");
//                    //logger.info("------------数据库操作---------");
//                    String userID = "admin";
//                    String read;//每一行数据
//                    int lineNo = 1;//行数
//                    String areaname = "";//区域名称
//                    String fareaname = "";//所属上级区域名
//                    TArea area = new TArea();
//                    fget = fc.retrieveFileStream(newName);// 读取ftp远程文件数据
//                    bufferedReader = new BufferedReader(new InputStreamReader(fget, "UTF-8"));
//                    bufferedReader.readLine();//先执行一次，除去标题的内容
//                    lineNo++;
//                    while ((read = bufferedReader.readLine()) != null) {
//                        //这个里面就写数据库操作步骤
//                        lineNo++;
//                    }
//                    logger.info("********" + nowDate + "：结束下载区域信息数据！");
//                }
//            }
//
//                } catch(Exception e){
//                    e.printStackTrace();
//                } finally{
//                    if (ftp.isConnected()) {
//                        try {
//                            //退出登录
//                            ftp.logout();
//                            //关闭连接
//                            ftp.disconnect();
//                        } catch (IOException e) {
//                        }
//                    }
//                }
//            }


    // 将字节数组转换为输入流
    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    // 将输入流转为byte[]
    public static final byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1000];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    // 将byte[]转为文件
    public static void byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}