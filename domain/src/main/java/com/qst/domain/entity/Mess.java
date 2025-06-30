package com.qst.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Mess implements Serializable {
    /*状态码*/
    private Integer code;
    /*响应消息*/
    private String message;
    /*响应是否成功标志*/
    private boolean success;
    /*响应的数据*/
    private Map<String, Object> data = new HashMap<>();
    /**
     * 响应成功函数
     * @return
     */
    public static Mess success() {
        Mess mess = new Mess();
        mess.setCode(20);
        mess.setMessage("响应成功");
        mess.setSuccess(true);
        return mess;
    }

    /**
     * 响应失败函数
     * @return
     */
    public static Mess fail() {
        Mess mess = new Mess();
        mess.setCode(50);
        mess.setMessage("响应失败");
        mess.setSuccess(false);
        return mess;
    }

    /**
     * 添加响应数据
     * @return
     */
    public Mess data(String key, Object value) {
        this.data.put(key,value);
        return this;
    }

    /**
     * 修改状态码
     * @return
     */
    public Mess code(Integer code) {
        this.setCode(code);
        return this;
    }

    /**
     * 修改消息
     * @return
     */
    public Mess mess(String str) {
        this.setMessage(str);
        return this;
    }


    public Integer getCode() {
        return code;
    }

    public Mess setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Mess setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public Mess setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Mess setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }
}