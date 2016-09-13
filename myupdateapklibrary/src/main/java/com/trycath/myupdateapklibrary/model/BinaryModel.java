package com.trycath.myupdateapklibrary.model;

import java.io.Serializable;

/**
 * 在此写用途
 *
 * @author: guoyoujin
 * @mail: guoyoujin123@gmail.com
 * @date: 2016-08-26 14:11
 * @version: V1.0 <描述当前版本功能>
 */

public class BinaryModel implements Serializable{
    private  long fsize;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BinaryModel{");
        sb.append("fsize=").append(fsize);
        sb.append('}');
        return sb.toString();
    }

    public long getFsize() {
        return fsize;
    }

    public void setFsize(long fsize) {
        this.fsize = fsize;
    }
}