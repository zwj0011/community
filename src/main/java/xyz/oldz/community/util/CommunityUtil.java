package xyz.oldz.community.util;

import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

public class CommunityUtil {

  //生成UUID
  public static String generateUUID() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  // MD5加密
  public static String md5(String key) {
    if (StringUtils.isBlank(key)) {
      return null;
    }
    return DigestUtils.md5DigestAsHex(key.getBytes());
  }
}
