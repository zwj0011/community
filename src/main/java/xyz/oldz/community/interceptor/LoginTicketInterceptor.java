package xyz.oldz.community.interceptor;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import xyz.oldz.community.entity.LoginTicket;
import xyz.oldz.community.entity.User;
import xyz.oldz.community.service.UserService;
import xyz.oldz.community.util.CookieUtil;
import xyz.oldz.community.util.HostHolder;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

  @Autowired
  private UserService userService;

  @Autowired
  private HostHolder hostHolder;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    //从cookie中获取凭证
    String ticket = CookieUtil.getValue(request, "ticket");
    if (StringUtils.isNotBlank(ticket)) {
      LoginTicket loginTicket = userService.findLoginTicket(ticket);
      //检查凭证是否有效
      if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired()
          .after(new Date())) {
        //根据凭证查询用户
        User user = userService.findUserById(loginTicket.getUserId());
        //在本次请求中持有user
        hostHolder.setUser(user);
      }
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    User user = hostHolder.getUser();
    if (user != null && modelAndView != null) {
      modelAndView.addObject("loginUser", user);
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    hostHolder.clear();
  }
}
