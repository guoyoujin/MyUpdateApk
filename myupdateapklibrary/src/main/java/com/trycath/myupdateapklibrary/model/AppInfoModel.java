package com.trycath.myupdateapklibrary.model;

import java.io.Serializable;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-08-26 14:09
 * @version: V1.0 <描述当前版本功能>
 */

public class AppInfoModel implements Serializable{
    private static final String TAG = "AppInfoModel";
    private String  name;
    private String version;
    private String changelog;
    private String versionShort;
    private String build;
    private String installUrl;
    private String install_url;
    private String direct_install_url;
    private String update_url;
    private BinaryModel binary;
    private long updated_at;

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AppInfoModel{");
        sb.append("name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", changelog='").append(changelog).append('\'');
        sb.append(", versionShort='").append(versionShort).append('\'');
        sb.append(", build='").append(build).append('\'');
        sb.append(", installUrl='").append(installUrl).append('\'');
        sb.append(", install_url='").append(install_url).append('\'');
        sb.append(", direct_install_url='").append(direct_install_url).append('\'');
        sb.append(", update_url='").append(update_url).append('\'');
        sb.append(", binary=").append(binary);
        sb.append(", updated_at=").append(updated_at);
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }



    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstall_url() {
        return install_url;
    }

    public void setInstall_url(String install_url) {
        this.install_url = install_url;
    }

    public String getDirect_install_url() {
        return direct_install_url;
    }

    public void setDirect_install_url(String direct_install_url) {
        this.direct_install_url = direct_install_url;
    }

    public String getUpdate_url() {
        return update_url;
    }

    public void setUpdate_url(String update_url) {
        this.update_url = update_url;
    }

    public BinaryModel getBinary() {
        return binary;
    }

    public void setBinary(BinaryModel binary) {
        this.binary = binary;
    }
}