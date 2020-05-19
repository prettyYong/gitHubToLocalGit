package com.wy.git;

import com.wy.domain.ConfigFile;
import com.wy.util.Util;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class GitService {
    public String userName = "prettyYong";
    public String password = "XXzj1314";
    public String defaultBranch = "master";
    public String remotePath = "https://github.com/prettyYong/config.git";//远程库路径
    public String localPath = "D:/config";//下载已有仓库到本地路径
    //git仓库地址
    Git git = new Git(new FileRepository(localPath + "/.git"));
    //设置远程服务器上的用户名和密码
    UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider =
            new UsernamePasswordCredentialsProvider(userName, password);

    public GitService() throws IOException {
    }

    /**
     * 克隆远程库，只需执行一次
     *
     * @throws IOException
     * @throws GitAPIException
     */
    @Test
    public void initAndClone() throws IOException, GitAPIException {
        File file = new File(localPath);
        if (!file.exists()) {
            //克隆库
            CloneCommand cloneCommand = Git.cloneRepository();
            git = cloneCommand.setURI(remotePath) //设置远程URI
                    .setBranch(defaultBranch) //设置clone下来的分支
                    .setDirectory(new File(localPath)) //设置下载存放路径
                    .setCredentialsProvider(usernamePasswordCredentialsProvider) //设置权限验证
                    .call();
            System.out.println("clone");
        } else {
            //更新仓库
            PullResult call = git.pull().setRemoteBranchName(defaultBranch).call();
            System.out.println("pull:" + call.isSuccessful());
        }

    }

    /**
     * 仓库新增文件
     */
   /* public String addFile(String path, MultipartFile mf)
            throws Exception {
        initAndClone();
        String originalFilename = mf.getOriginalFilename();
        List files=new ArrayList();
        files.add(path+"/"+originalFilename);
        //判断是否有被修改过的文件
        List<DiffEntry> diffEntries = git.diff()
                .setPathFilter(PathFilterGroup.createFromStrings(files))
                .setShowNameAndStatusOnly(true).call();
        if (diffEntries == null || diffEntries.size() == 0) {
            throw new Exception("提交的文件内容都没有被修改，不能提交");
        }
        //被修改过的文件
        List<String> updateFiles=new ArrayList<String>();
        DiffEntry.ChangeType changeType;
        for(DiffEntry entry : diffEntries){
            changeType = entry.getChangeType();
            switch (changeType) {
                case ADD:
                    updateFiles.add(entry.getNewPath());
                    break;
                case COPY:
                    updateFiles.add(entry.getNewPath());
                    break;
                case DELETE:
                    updateFiles.add(entry.getOldPath());
                    break;
                case MODIFY:
                    updateFiles.add(entry.getOldPath());
                    break;
                case RENAME:
                    updateFiles.add(entry.getNewPath());
                    break;
            }
        }
        //将文件提交到git仓库中，并返回本次提交的版本号
        AddCommand addCmd = git.add();
        for (String file : updateFiles) {
            addCmd.addFilepattern(file);
        }
        addCmd.call();

        CommitCommand commitCmd = git.commit();
        for (String file : updateFiles) {
            commitCmd.setOnly(file);
        }
        RevCommit revCommit = commitCmd.setMessage("publish").call();
        return revCommit.getName();
    }*/
    public void addFile(String path, MultipartFile mf)
            throws IOException, GitAPIException {
        initAndClone();
        String originalFilename = mf.getOriginalFilename();
        File directory = new File(localPath + "/" + path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory + "/" + originalFilename);
        if (file.exists()) {
            boolean delete = file.delete();
            file.createNewFile();
        } else {
            boolean createNewFile = file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = mf.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            int data = bytes[i];
            fos.write(data);
        }
        fos.close();
        //添加文件
        git.add().addFilepattern(".").call();
        System.out.println(".  add");
        commitFile("new file add");
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path, String fileName) throws Exception {
        File file = new File(localPath + "/" + path + "/" + fileName);
        if (file.exists()) {
            file.delete();
        } else {
            throw new Exception("文件不存在");
        }
        git.add().setUpdate(true).addFilepattern(".").call();
        commitFile("delete+" + fileName);
    }

    /**
     * 获取文件列表
     *
     * @param clusterName
     * @param roleType
     * @param configGroupName
     * @return
     */
    public List<ConfigFile> getGroupFiles(String clusterName, String roleType, String configGroupName) throws Exception {
        initAndClone();
        String path = clusterName + "/" + roleType + "/" + configGroupName;
        File dirDirectory = new File(localPath + "/" + path);
        if (dirDirectory.isFile()) {
            throw new Exception("该目录对应一个文件而不是目录");
        }
        List<ConfigFile> result = new ArrayList<>();
        ConfigFile configFile = new ConfigFile();
        File[] files = dirDirectory.listFiles();
        for (File f : files) {
            configFile.setName(f.getName());
            configFile.setCluster(clusterName);
            configFile.setRole(roleType);
            configFile.setConfigGroup(configGroupName);
            long length = f.length();
            //通过流获取具体内容
            byte[] contents = new byte[(int) f.length()];
            int data;
            FileInputStream fis = new FileInputStream(f);
            while ((data = fis.read(contents)) != -1) {

            }
            configFile.setContent(contents);
            result.add(configFile);
            //最后记得，关闭流
            fis.close();
        }
        return result;
    }

    public List<ConfigFile> getHostFiles(String clusterName, String roleType, String hostName) throws Exception {
        initAndClone();
        //根据主机名，获取所属配置组
        String configGroupName = "xxx";
        String path = clusterName + "/" + roleType + "/" + configGroupName;
        File dirDirectory = new File(localPath + "/" + path);
        if (dirDirectory.isFile()) {
            throw new Exception("该目录对应一个文件而不是目录");
        }
        List<ConfigFile> result = new ArrayList<>();
        ConfigFile configFile = new ConfigFile();
        File[] files = dirDirectory.listFiles();
        for (File f : files) {
            configFile.setName(f.getName());
            configFile.setCluster(clusterName);
            configFile.setRole(roleType);
            configFile.setConfigGroup(configGroupName);
            long length = f.length();
            //通过流获取具体内容
            byte[] contents = new byte[(int) f.length()];
            int data;
            FileInputStream fis = new FileInputStream(f);
            while ((data = fis.read(contents)) != -1) {

            }
            configFile.setContent(contents);
            result.add(configFile);
            //最后记得，关闭流
            fis.close();
        }
        return result;
    }

    /**
     * 提交本地文件
     */
    @Test
    public void commitFile(String commitMsg) throws IOException, GitAPIException, JGitInternalException {
        //提交代码
        RevCommit revCommit = git.commit().setMessage(commitMsg).call();
        System.out.println("commit+" + revCommit);
        pushFile();
    }

    /**
     * push本地代码到远程仓库地址
     */
    @Test
    public void pushFile() throws IOException, JGitInternalException, GitAPIException {
        git.push().setRemote("origin").setCredentialsProvider(usernamePasswordCredentialsProvider).call();
        System.out.println("push");
    }

    /**
     * 获取文件提交日志
     */
    @Test
    public List<DiffEntry> getLog(long id) throws IOException, JGitInternalException, GitAPIException, ParseException {
        //根据日志获取文件路径
        String filePath="";
        List<String> commitIdList = commitIds("1/r/p/hh.txt");//此处替换文件路径
        Repository repository = git.getRepository();
        ShowNoteCommand showNoteCommand = git.notesShow();
        ObjectId objId = repository.resolve(commitIdList.get(0));
        Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
        Iterator<RevCommit> iter = allCommitsLater.iterator();
        RevCommit commit = iter.next();
        TreeWalk tw = new TreeWalk(repository);
        tw.addTree(commit.getTree());

        commit = iter.next();
        if (commit != null) {
            tw.addTree(commit.getTree());
        } else {
            return null;
        }
        tw.setRecursive(true);
        RenameDetector rd = new RenameDetector(repository);
        rd.addAll(DiffEntry.scan(tw));
        return rd.compute();
    }

    /**
     * 回滚文件到上一版本
     */
    @Test
    public void rollback(long logId) throws
            IOException, JGitInternalException, GitAPIException {
        //根据日志获取文件路径
        String path="1/r/p/hh.txt";
        List<String> commitIdList = commitIds("1/r/p/hh.txt");//此处替换文件路径
        String revision = commitIdList.get(0);
        Repository repository = git.getRepository();
        RevWalk walk = new RevWalk(repository);
        ObjectId objId = repository.resolve(revision);
        RevCommit revCommit = walk.parseCommit(objId);
        String preVision = revCommit.getParent(0).getName();
        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(preVision).call();
        //this.commitFile("roleback");
        //git.checkout().addPath(path).setStartPoint(revision).call();
        git.commit().setMessage("lll").setOnly(path).call();
    }

    public List<String> commitIds(String filePath) throws GitAPIException {
        List<String> commitIds = new ArrayList<>();
        Iterable<RevCommit> revCommits = git.log().addPath(filePath).call();
        for (RevCommit revCommit : revCommits) {
            //name即是commitId,且最新提交的commitID即为第一个
            String name = revCommit.getName();
            //RevObject id = (RevObject) revCommit.getId();
            commitIds.add(name);
        }
        return commitIds;
    }
}
