package com.lc.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtil {

    public final static String SEPARATOR = ".";
    public final static String ROOT = "0";

    public static String calculateLevel(String parentLevel,Integer parentId){
        if(StringUtils.isBlank(parentLevel)){
            return ROOT;
        }
        return StringUtils.join(parentLevel,SEPARATOR,parentId);
    }
}
