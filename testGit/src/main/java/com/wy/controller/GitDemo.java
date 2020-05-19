/*
package com.wy.controller;

import com.wy.git.GitService2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(value="GitDemo测试用",
        tags={"GitDemo测试"})
@RestController
@RequestMapping("GitDemo")
public class GitDemo {
    @Autowired
    private GitService2 gitService;
    @ApiOperation("保存配置文件到git")
    @PostMapping("saveFileToGit")
    public String addFile(@ApiParam("组件类型") @RequestParam("moduleType") String moduleType,
                                     @ApiParam("集群id") @RequestParam("musterId") long musterId,
                                     @ApiParam("角色类型") @RequestParam("roleType") String roleType,
                                     @ApiParam("配置组名称") @RequestParam("configGroupName") String configGroupName,
                                     @ApiParam("提交message") @RequestParam("message") String message,
                                     @ApiParam(value = "上传的文件") @RequestParam(value = "file", name = "file", required = false) MultipartFile file,
                                     HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            gitService.addFile(moduleType, musterId, roleType, configGroupName,message, file);
            // }
            return "add,commit,push to remote git is ok !";
        } catch (Throwable t) {
            //logger.error("saveFileToGit", t);
            return t.getMessage();
        }
    }
    @ApiOperation("获取指定目录下的文件名列表")
    @GetMapping("getFiles")
    public Map<Integer, String>getFiles(@ApiParam("组件类型") @RequestParam("moduleType") String moduleType,
                           @ApiParam("集群id") @RequestParam("musterId") long musterId,
                           @ApiParam("角色类型") @RequestParam("roleType") String roleType,
                           @ApiParam("配置组名称") @RequestParam("configGroupName") String configGroupName){
        try {
            String path=moduleType+"/"+ musterId+"/"+roleType+"/"+configGroupName;
            Map<Integer, String> fileMap = gitService.getFileNames(path);
            // }
            return fileMap;
        } catch (Throwable t) {
            //logger.error("saveFileToGit", t);
            return null;
        }
    }
    @ApiOperation("获取单个文件内容")
    @GetMapping("getContent")
    public String getContents(@ApiParam("组件类型") @RequestParam("moduleType") String moduleType,
                               @ApiParam("集群id") @RequestParam("musterId") long musterId,
                               @ApiParam("角色类型") @RequestParam("roleType") String roleType,
                               @ApiParam("配置组名称") @RequestParam("configGroupName") String configGroupName,
                               @ApiParam("文件数字代码")@RequestParam("number")int number){
        try {
            String path=moduleType+"/"+ musterId+"/"+roleType+"/"+configGroupName;
            return gitService.getContent(path, number);
        } catch (Throwable t) {
            //logger.error("saveFileToGit", t);
            return null;
        }
    }
    @ApiOperation("删除单个文件")
    @PostMapping("deleteFile")
    public String deleteFile(@ApiParam("组件类型") @RequestParam("moduleType") String moduleType,
                               @ApiParam("集群id") @RequestParam("musterId") long musterId,
                               @ApiParam("角色类型") @RequestParam("roleType") String roleType,
                               @ApiParam("配置组名称") @RequestParam("configGroupName") String configGroupName,
                               @ApiParam("文件数字代码")@RequestParam("number")int number){
        try {
            String path=moduleType+"/"+ musterId+"/"+roleType+"/"+configGroupName;
            gitService.deleteFile(path, number);
            return "ok";
        } catch (Throwable t) {
            //logger.error("saveFileToGit", t);
            return "no";
        }
    }
}
*/
