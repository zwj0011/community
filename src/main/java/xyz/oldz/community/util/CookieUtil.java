package xyz.oldz.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class CookieUtil {

  /**
   * 获取request中cookie的value
   * @param request
   * @param name
   * @return
   */
  public static String getValue(HttpServletRequest request, String name) {
    if (request == null || StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("request或者name为空");
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(name)) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

}
