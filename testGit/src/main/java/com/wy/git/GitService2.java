package com.wy.git;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitService2 {
    public String userName = "prettyYong";
    public String password = "XXzj1314";
    public String defaultBranch = "master";
    public String remotePath = "https://github.com/prettyYong/config.git";//远程库路径
    public String localPath = "D:/config";//下载已有仓库到本地路径
    //git仓库地址
    Git git = new Git(new FileRepository(localPath + "/.git"));
    private boolean exists = false;

    public GitService2() throws IOException {
    }

    /**
     * 克隆远程库，只需执行一次
     *
     * @throws IOException
     * @throws GitAPIException
     */
    @Test
    public void initAndClone() throws IOException, GitAPIException {
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new
                UsernamePasswordCredentialsProvider(userName, password);
        //克隆库
        CloneCommand cloneCommand = Git.cloneRepository();
        Git git = cloneCommand.setURI(remotePath) //设置远程URI
                .setBranch(defaultBranch) //设置clone下来的分支
                .setDirectory(new File(localPath)) //设置下载存放路径
                .setCredentialsProvider(usernamePasswordCredentialsProvider) //设置权限验证
                .call();
        System.out.print(git.tag());
        System.out.println("cloned");
    }

    /**
     * 判断是否已经初始化
     */
    public boolean flag() {
        File file = new File(localPath);
        boolean exists = file.exists();
        return exists;
    }

    /**
     * \
     * 读取指定目录文件名列表
     * 从本地读取，读取之前，先update本地仓库
     */
    public Map<Integer, String> getFileNames(String path) throws Exception {
        String filePath = localPath + "/" + path;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("指定目录不存在");
        }
        Map<Integer, String> data = new HashMap<>();
        String[] fileNames = file.list();
        for (int i = 0; i < fileNames.length; i++) {
            data.put(i + 1, fileNames[i]);
        }
        return data;
    }

    public List<File> getFilePath(String path) throws Exception {
        String filePath = localPath + "/" + path;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("指定目录不存在");
        }
        File[] files = file.listFiles();
        return Arrays.asList(files);
    }

    /**
     * 获取单个文件内容
     *
     * @return
     */
    public String getContent(String path, int number) throws Exception {
        String filePath = localPath + "/" + path;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception("指定目录不存在");
        }
        //目标文件路径
        String fileRealPath = "";
        File[] files = file.listFiles();
        Map<Integer, String> fileMap = getFileNames(path);
        if (fileMap.get(number) != null) {
            for (File f : files) {
                if (f.getName().contains(fileMap.get(number))) {
                    fileRealPath = f.getAbsolutePath();
                    break;
                }
            }
        }
        if (fileRealPath.trim().equals("")) {
            throw new Exception("目标文件不存在");
        }
        File tempFile = new File(fileRealPath);
        FileInputStream fis = new FileInputStream(tempFile);
        Reader reader = new InputStreamReader(fis, "gb2312");
        int len = 0;
        char[] buf = new char[4096];
        while ((len = reader.read(buf)) != -1) {
            System.out.println(new String(buf, 0, len));
        }
        //最后记得，关闭流
        fis.close();
        return new String(buf);
    }

    /**
     * 仓库新增文件
     */
    public void addFile(String moduleType, long musterId, String roleType, String configGroupName, String message, MultipartFile file) throws IOException, GitAPIException {
        if (!flag()) {
            initAndClone();
        }
        String originalFilename = file.getOriginalFilename();
        File directory = new File(localPath + "/" + moduleType + "/" + musterId + "/" + roleType + "/" + configGroupName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File f = new File(directory + "/" + originalFilename);
        if (!f.exists()) {
            f.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(f);
        byte[] bytes = file.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            int data = bytes[i];
            fos.write(data);
        }
        fos.close();
        //添加文件
        git.add().addFilepattern(".").call();
        System.out.println(".  add");
        commitFile(message);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path, int number) throws Exception {
        Map<Integer, String> fileMap = this.getFileNames(path);
        String dirFilePath="";
        if (fileMap.get(number) != null) {
            for (File file : this.getFilePath(path)) {
                if (file.getName().contains(fileMap.get(number))) {
                    dirFilePath=file.getAbsolutePath();
                }
            }
        }
        File file=new File(dirFilePath);
        file.delete();
        git.add().setUpdate(true).addFilepattern(".").call();
       // this.commitFile("update after delete");
    }

    /**
     * 提交本地文件
     */
    @Test
    public void commitFile(String message) throws IOException, GitAPIException,
            JGitInternalException {
        //提交代码
        git.commit().setMessage(message).call();
        System.out.println("commit");
        pushFile();
    }

    /**
     * push本地代码到远程仓库地址
     */
    @Test
    public void pushFile() throws IOException, JGitInternalException,
            GitAPIException {
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new
                UsernamePasswordCredentialsProvider(userName, password);
        git.push().setRemote("origin").setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        System.out.println("push");
    }
}
