package cn.corbinhu.community.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

/**
 * @author: CorbinHu
 * @date: 2021/1/19 15:35
 * @description:
 */
public class CommunityUtil {

    // 生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    // MD5加密
    // hello + 3e4a8 -> MD5(hello3e4a8)
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
