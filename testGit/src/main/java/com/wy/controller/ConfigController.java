package com.wy.controller;

import com.wy.domain.ConfigFile;
import com.wy.git.GitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.eclipse.jgit.api.ShowNoteCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Api(value = "git",
        tags = {"git操作相关接口"})
@RestController
@RequestMapping("/gitSalter")
public class ConfigController {
    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
    @Autowired
    private GitService gitService;

    //1.读文件列表 2.提交文件（合并问题，替代）3.提交历史 4.回滚特定版本
    @ApiOperation("保存文件到git")
    @PostMapping("saveFileToGit")
    public String saveFileToGit(@ApiParam("集群id") @RequestParam("musterId") long musterId,
                                     @ApiParam("角色类型") @RequestParam("roleType") String roleType,
                                     @ApiParam("配置组名称") @RequestParam("configGroupName") String configGroupName,
                                     @ApiParam(value = "上传的文件") @RequestParam(value = "file", name = "file", required = false) MultipartFile file,
                                     HttpServletRequest request) {
        logger.info("saveFileToGit,moduleType={},musterId={},roleType={},configGroupName={},",
                musterId, roleType, configGroupName);
        try {
            String path=musterId+"/"+roleType+"/"+configGroupName;
            gitService.addFile(path, file);
            return "ok";
        } catch (Throwable t) {
            logger.error("saveFileToGit", t);
            return "no ok";
        }
    }
    @ApiOperation("从git删除文件")
    @GetMapping("deleteFile")
    public String deleteFile(@ApiParam("集群id") @RequestParam("musterId") long musterId,
                                     @ApiParam("角色类型") @RequestParam("roleType") String roleType,
                                     @ApiParam("配置组名称") @RequestParam("configGroupName") String configGroupName,
                                     @ApiParam("文件名") @RequestParam("fileName") String fileName) {
        logger.info("deleteFile,musterId={},roleType={},configGroupName={},fileName={}", musterId, roleType, configGroupName, fileName);
        try {
            String path=musterId+"/"+roleType+"/"+configGroupName;
            gitService.deleteFile(path, fileName);
            return "ok";
        } catch (Throwable t) {
            logger.error("sendToSalt", t);
            return "no ok";
        }
    }

    @ApiOperation("根据配置组获取文件列表")
    @GetMapping("getGroupFiles")
    public List<ConfigFile> getGroupFiles(@ApiParam("集群") @RequestParam("clusterName") String clusterName,
                                     @ApiParam("角色") @RequestParam("roleType") String roleType,
                                     @ApiParam("配置组") @RequestParam("configGroupId") String configGroupName) {
        logger.info("getGroupFiles,clusterName={},roleType={},configGroupName={}", clusterName, roleType,configGroupName);
        try {
            List<ConfigFile> groupFiles = gitService.getGroupFiles(clusterName,roleType,configGroupName);
            return groupFiles;
        } catch (Throwable t) {
            logger.error("sendToSalt", t);
            return null;
        }
    }
    @ApiOperation("根据主机获取文件列表")
    @GetMapping("getHostFiles")
    public List<ConfigFile> getHostFiles(@ApiParam("集群") @RequestParam("clusterName") String clusterName,
                                     @ApiParam("角色") @RequestParam("roleType") String roleType,
                                     @ApiParam("节点名") @RequestParam("hostName") String hostName) {
        logger.info("getHostFiles,clusterName={},roleType={},hostName={}", clusterName, roleType,hostName);
        try {
            List<ConfigFile> hostFiles = gitService.getHostFiles(clusterName, roleType, hostName);
            return hostFiles;
        } catch (Throwable t) {
            logger.error("sendToSalt", t);
            return null;
        }
    }
    @ApiOperation("回滚文件到上一版版本")
    @GetMapping("rollbackFile")
    public String rollbackFile(@ApiParam("日志id") @RequestParam("logId") long logId) {
        logger.info("rollbackFile,logId={}",logId);
        try {
            gitService.rollback(logId);
            return "ok";
        } catch (Throwable t) {
            logger.error("rollbackFile", t);
            return "no ok";
        }
    }
    @ApiOperation("获取某次提交日志")
    @GetMapping("getLog")
    public List<DiffEntry>  getLog(@ApiParam("日志id") @RequestParam("logId") long logId) {
        logger.info("getLog,logId={}", logId);
        try {
            List<DiffEntry> log = gitService.getLog(logId);
            return log;
        } catch (Throwable t) {
            logger.error("getLog", t);
            return null;
        }
    }
}
