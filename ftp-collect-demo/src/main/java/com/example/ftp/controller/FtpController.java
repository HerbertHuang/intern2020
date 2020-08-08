package com.example.ftp.controller;

import com.example.ftp.utils.DownloadFileTime;
import com.example.ftp.utils.FtpUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
public class FtpController {

    @PostMapping("upLoad")
    @ApiOperation(value="上传文件")
    public Object upLoad(@RequestParam("headPic") MultipartFile file ) throws IOException {
        //调用自定义的FTP工具类 上传文件
        String filename = FtpUtil.uploadFile(file);
        return "上传成功";
    }
    @ResponseBody
    @RequestMapping("downLoad/{fileName}")
    @ApiOperation(value="下载文件",httpMethod = "GET")
    public Object downLoad(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response ) {
        //调用自定义的FTP工具类 上传文件
        FtpUtil.downloadFile(fileName);
        return "下载成功";
    }

    @ResponseBody
    @RequestMapping("delete/{fileName}")
    @ApiOperation(value="删除指定文件",httpMethod = "GET")
    public Object delete(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response ) {
        //调用自定义的FTP工具类 上传文件
        FtpUtil. deleteFile(fileName);
        return "删除 "+fileName+" 成功";
    }

    @ResponseBody
    @RequestMapping("downloadTime/{fileName}")
    @ApiOperation(value="定时下载文件",httpMethod = "GET")
    public Object downloadTime(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response ) {
        //调用自定义的FTP工具类 上传文件
        DownloadFileTime. downloadArea(fileName);
        return "下载 "+fileName+" 成功";
    }

}
