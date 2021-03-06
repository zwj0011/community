package xyz.oldz.community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import xyz.oldz.community.interceptor.LoginRequiredInterceptor;
import xyz.oldz.community.interceptor.LoginTicketInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired
  private LoginRequiredInterceptor loginRequiredInterceptor;

  @Autowired
  private LoginTicketInterceptor loginTicketInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginTicketInterceptor)
        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
    registry.addInterceptor(loginRequiredInterceptor)
        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

  }
}
