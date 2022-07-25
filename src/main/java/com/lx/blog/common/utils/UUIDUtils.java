package com.lx.blog.common.utils;

import java.util.UUID;

public class UUIDUtils {

    public static String getUUid(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
