package com.wy.git;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class GitService1 {
    public String userName = "prettyYong";
    public String password = "XXzj1314";
    public String defaultBranch = "master";
    public String remotePath = "https://github.com/prettyYong/config.git";//远程库路径
    public String localPath = "E:/config";//下载已有仓库到本地路径
    //public String initPath = "D:\\test\\";//本地路径新建

    /**
     * 克隆远程库
     *
     * @throws IOException
     * @throws GitAPIException
     */
    @Test
    public void testClone() throws IOException, GitAPIException {
        //设置远程服务器上的用户名和密码
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new
                UsernamePasswordCredentialsProvider(userName, password);
        //克隆代码库命令
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
     * 本地新建仓库
     */
    /*@Test
    public void testCreate() throws IOException {
        //本地新建仓库地址
        Repository newRepo = FileRepositoryBuilder.create(new File(initPath + "/.git"));
        newRepo.create();
    }*/

    /**
     * 仓库新增文件
     */
    @Test
    public void testAdd() throws IOException, GitAPIException {
        // File myfile01 = new File(localPath + "/myfile01.txt");
        //myfile01.createNewFile();
        File dir01 = new File(localPath + "/dir01");
        if(!dir01.exists()){
            dir01.mkdir();
        }
        //git仓库地址
        Git git = new Git(new FileRepository(localPath + "/.git"));
        //添加文件
        git.add().addFilepattern(".").call();
        System.out.println(".  added");
    }

    /**
     * 提交本地文件
     */
    @Test
    public void testCommit() throws IOException, GitAPIException,
            JGitInternalException {
        //git仓库地址
        Git git = new Git(new FileRepository(localPath + "/.git"));
        //提交代码
        git.commit().setMessage("dir01 commited").call();
        System.out.println("commited");
    }


    /**
     * 拉取远程仓库内容到本地
     */
    /*@Test
    public void testPull() throws IOException, GitAPIException {

        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new
                UsernamePasswordCredentialsProvider("username", "password");
        //git仓库地址
        Git git = new Git(new FileRepository(localPath + "/.git"));
        git.pull().setRemoteBranchName("master").
                setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        System.out.println("pulled");
    }*/

    /**
     * push本地代码到远程仓库地址
     */
    @Test
    public void testPush() throws IOException, JGitInternalException,
            GitAPIException {
        UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider = new
                UsernamePasswordCredentialsProvider(userName, password);
        //git仓库地址
        Git git = new Git(new FileRepository(localPath + "/.git"));
        git.push().setRemote("origin").setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        System.out.println("pushed");

    }
}
