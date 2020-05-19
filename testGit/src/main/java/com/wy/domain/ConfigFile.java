package com.wy.domain;

public class ConfigFile {
    private String cluster;//集群
    private String role;//角色
    private String configGroup;//配置组
    private String name;//文件名
    private byte[] content;//文件内同

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
