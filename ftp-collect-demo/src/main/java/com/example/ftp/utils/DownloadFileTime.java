package com.example.ftp.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component("DownloadFileTime")
public class DownloadFileTime {

    private static FTPClient fc;
    private static String ftpIP;
    private static int ftpPort;
    private static String userName = "user2";;
    private static String userPwd = "123";
    private static InputStream fget;
    private static BufferedReader bufferedReader;


    @Scheduled(cron = "0 0 3 * * ?")
    public static boolean  downloadArea(String fileName) {
        boolean success = false;
        try {
            SimpleDateFormat fileDate = new SimpleDateFormat("yyyyMMdd");
            String nowDate = fileDate.format(new Date());//当前日期
            String newName="area_"+nowDate+".txt";//文件名
            int reply;
            fc = new FTPClient();// ftp客户端对象
            fc.connect(ftpIP,ftpPort);// 连接ftp服务器
            fc.login(userName, userPwd);// 登录ftp服务器
            //判断登陆是否成功
            reply = fc.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                fc.disconnect();
            }
            FTPFile[] fs = fc.listFiles();//ftp下的所有文件名称
            for (FTPFile ftpfile : fs) { //遍历所有文件
                fileName = ftpfile.getName();
                if (fileName.equals(newName)) {
                   //logger.info("********" + nowDate + "：开始下载区域信息数据！");
                    //logger.info("------------数据库操作---------");
                    String userID = "admin";
                    String read;//每一行数据
                    int lineNo = 1;//行数
                    String areaname = "";//区域名称
                    String fareaname = "";//所属上级区域名
                    //TArea area = new TArea();
                    fget = fc.retrieveFileStream(newName);// 读取ftp远程文件数据
                    bufferedReader = new BufferedReader(new InputStreamReader(fget, "UTF-8"));
                    bufferedReader.readLine();//先执行一次，除去标题的内容
                    lineNo++;
                    while ((read = bufferedReader.readLine()) != null) {
                        //这个里面就写数据库操作步骤
                        lineNo++;
                    }
                    //logger.info("********" + nowDate + "：结束下载区域信息数据！");
                    success=true;
                }
                success = false;
            }
            // 退出登陆
            fc.logout();
        } catch (IOException e) {
            try {
                bufferedReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return success;

    }

}
