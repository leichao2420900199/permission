package com.lc.util;

import java.util.Date;
import java.util.Random;

public class PasswordUtil {

    public final static  String[] WORD = {
            "a","b","c","d","e","f","g","h","j","k","m","n","p","q","r","s","t","u","v","w","x","y","z",
            "A","B","C","D","E","F","G","H","J","K","M","N","P","Q","R","S","T","U","V","W","X","Y","Z"
    };

    public final static  String[] NUM = {
            "2","3","4","5","6","7","8","9"
    };
    public static String randomPassword(){
        StringBuffer buffer = new StringBuffer();
        Random random = new Random(new Date().getTime());
        boolean flag = false;
        int length = random.nextInt(3)+8;
        for (int i = 0; i < length; i++) {
            if(flag){
                buffer.append(NUM[random.nextInt(NUM.length)]);
            }else {
                buffer.append(WORD[random.nextInt(WORD.length)]);
            }
            flag = !flag;
        }
        return buffer.toString();
    }

}
