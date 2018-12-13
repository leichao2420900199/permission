package com.lc.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class JsonData {
    private boolean ret;
    private String msg;
    private Object data;

    public JsonData(boolean ret){
        this.ret = ret;
    }

    public static JsonData success(){
       return new JsonData(true);
    }

    public static JsonData success(Object data){
        JsonData jsonData = new JsonData(true);
        jsonData.data = data;
        return jsonData;
    }

    public static JsonData success(Object data,String msg){
        JsonData jsonData = new JsonData(true);
        jsonData.data = data;
        jsonData.msg = msg;
        return jsonData;
    }

    public static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.msg = msg;
        return jsonData;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> map = new HashMap<>();
        map.put("ret",this.ret);
        map.put("msg",this.msg);
        map.put("data",this.data);
        return map;

    }
}
