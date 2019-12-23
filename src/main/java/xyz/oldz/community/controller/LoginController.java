package xyz.oldz.community.controller;


import com.google.code.kaptcha.Producer;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.oldz.community.entity.User;
import xyz.oldz.community.service.UserService;
import xyz.oldz.community.util.CommunityConstant;

@Slf4j
@Controller
public class LoginController implements CommunityConstant {

  @Autowired
  private UserService userService;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Autowired
  private Producer kaptchaProducer;

  @GetMapping("/register")
  public String getRegisterPage() {
    return "/site/register";
  }

  @GetMapping("/login")
  public String getLoginPage() {
    return "/site/login";
  }

  @PostMapping("/register")
  public String register(Model model, User user) {
    Map<String, Object> map = userService.register(user);
    if (CollectionUtils.isEmpty(map)) {
      model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一份激活邮件，请尽快激活");
      model.addAttribute("target", contextPath + "/index");
      return "/site/operate-result";
    } else {
      model.addAttribute("usernameMsg", map.get("usernameMsg"));
      model.addAttribute("passwordMsg", map.get("passwordMsg"));
      model.addAttribute("emailMsg", map.get("emailMsg"));
      return "/site/register";
    }
  }

  @GetMapping(value = "/activation/{userId}/{code}")
  public String activation(Model model, @PathVariable("userId") int userId,
      @PathVariable("code") String code) {
    int result = userService.activation(userId, code);
    if (result == ACTIVATION_SUCCESS) {
      model.addAttribute("msg", "激活成功，您的账号可以正常使用了！");
      model.addAttribute("target", contextPath + "/login");
    } else if (result == ACTIVATION_REPEAT) {
      model.addAttribute("msg", "无效操作，该账号已经激活过了！");
      model.addAttribute("target", contextPath + "/index");
    } else if (result == ACTIVATION_FAILURE) {
      model.addAttribute("msg", "激活失败！");
      model.addAttribute("target", contextPath + "/index");
    }
    return "/site/operate-result";
  }

  @GetMapping("/kaptcha")
  public void kaptcha(HttpServletResponse response, HttpSession session) {
    String text = kaptchaProducer.createText();
    BufferedImage image = kaptchaProducer.createImage(text);
    session.setAttribute("kaptcha", text);
    response.setContentType("image/png");
    try {
      OutputStream os = response.getOutputStream();
      ImageIO.write(image, "png", os);
    } catch (IOException e) {
      log.error("验证码生成错误", e);
    }
  }

  @PostMapping("/login")
  public String login(@RequestParam("username") String username,
      @RequestParam("password") String password,
      @RequestParam("code") String code,
      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
      Model model, HttpSession session, HttpServletResponse response) {
    String kaptchaCode = (String) session.getAttribute("kaptcha");
    //验证码校验
    if (!StringUtils.equalsIgnoreCase(kaptchaCode, code)) {
      model.addAttribute("codeMsg", "验证码不正确！");
      return "/site/login";
    }
    int expiredSeconds = rememberme ? REMEMBER_ME_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
    //账号密码校验
    Map<String, Object> map = userService.login(username, password, expiredSeconds);
    if (map.containsKey("ticket")) {
      String s = map.get("ticket").toString();
      Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
      cookie.setPath(contextPath);
      cookie.setMaxAge(expiredSeconds);
      response.addCookie(cookie);
      return "redirect:/index";
    } else {
      model.addAttribute("usernameMsg", map.get("usernameMsg"));
      model.addAttribute("passwordMsg", map.get("passwordMsg"));
      return "/site/login";
    }
  }

  @GetMapping("/logout")
  public String logout(@CookieValue("ticket") String ticket) {
    userService.logout(ticket);
    return "redirect:/login";
  }
}
