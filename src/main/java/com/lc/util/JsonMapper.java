package com.lc.util;

import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

@Slf4j
public class JsonMapper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public static <T> String obj2String(T t){
        if(t==null) return null;

        try {
            return t instanceof String ? (String) t:objectMapper.writeValueAsString(t);
        }catch (Exception e){
            log.warn("parse object to String exception  error:{}",e.getMessage());
            return null;
        }
    }

    public static <T> T string2Obj(String t, TypeReference<T> typeReference){
        if(t==null || typeReference ==null) return null;
        try {
            return (T) ((typeReference.getType().equals(String.class))?t:objectMapper.readValue(t,typeReference));
        }catch (Exception e){
            log.warn("parse String to object  exception, String:{} , TypeReference<T>:{}, error:{}",t,typeReference.getType(),e.getMessage());
            return null;
        }

    }
}
