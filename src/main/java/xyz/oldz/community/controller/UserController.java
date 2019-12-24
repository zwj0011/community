package xyz.oldz.community.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import xyz.oldz.community.annotation.LoginRequired;
import xyz.oldz.community.entity.User;
import xyz.oldz.community.service.UserService;
import xyz.oldz.community.util.CommunityUtil;
import xyz.oldz.community.util.HostHolder;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

  @Value("${community.path.domain}")
  private String domain;

  @Value("${community.path.upload}")
  private String uploadPath;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Autowired
  private UserService userService;

  @Autowired
  private HostHolder hostHolder;

  @LoginRequired
  @GetMapping("/setting")
  public String getSettingPage() {
    return "/site/setting";
  }

  @LoginRequired
  @PostMapping("/uploadHeader")
  public String uploadHeader(MultipartFile headerImage, Model model) {
    if (headerImage == null) {
      model.addAttribute("error", "您还没有选择图片！");
      return "site/setting";
    }
    String fileName = headerImage.getOriginalFilename();
    //后缀名
    String suffix = fileName.substring(fileName.lastIndexOf("."));
    if (StringUtils.isBlank(suffix)) {
      model.addAttribute("error", "文件格式不正确");
    }

    //生成随机文件名
    fileName = CommunityUtil.generateUUID() + suffix;
    // 确定文件存放路径
    File dext = new File(uploadPath + "/" + fileName);
    try {
      headerImage.transferTo(dext);
    } catch (IOException e) {
      log.error("上传文件失败！", e);
      throw new RuntimeException("上传文件失败，服务器发生异常！", e);
    }

    //更新当前用户的头像路径，web访问路径
    User user = hostHolder.getUser();
    String headerUrl = domain + contextPath + "/user/header/" + fileName;
    userService.updateHeader(user.getId(), headerUrl);

    return "redirect:/index";
  }

  @GetMapping("/header/{fileName}")
  public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
    fileName = uploadPath + "/" + fileName;
    String suffix = fileName.substring(fileName.lastIndexOf("."));
    //响应图片
    response.setContentType("image/" + suffix);
    try (FileInputStream inputStream = new FileInputStream(fileName)) {
      OutputStream outputStream = response.getOutputStream();
      byte[] buffer = new byte[1024];
      int b = 0;
      while ((b = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, b);
      }
    } catch (IOException e) {
      log.error("读取头像失败：" + e.getMessage());
    }
  }

  @LoginRequired
  @PostMapping("/changePassword")
  public String changePassword(String oldPassword, String newPassword, String confirmPassword,
      @CookieValue("ticket") String ticket, Model model) {
    if (!StringUtils.equals(newPassword, confirmPassword)) {
      model.addAttribute("confirmPasswordMsg", "密码不一致！");
      model.addAttribute("newPassword", newPassword);
      model.addAttribute("confirmPassword", confirmPassword);
      return "/site/setting";
    }
    User user = hostHolder.getUser();
    Map<String, Object> map = userService.changPassword(user.getId(), oldPassword, newPassword);
    if (map.get("msg") != null) {
      model.addAttribute("msg", map.get("msg"));
      return "/site/operate-result";
    }

    if (map.get("oldPasswordMsg") != null) {
      model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
      return "/site/setting";
    }
    userService.logout(ticket);
    model.addAttribute("target", contextPath + "/login");
    model.addAttribute("msg", "密码已修改，请重新登陆！");
    return "/site/operate-result";
  }
}
