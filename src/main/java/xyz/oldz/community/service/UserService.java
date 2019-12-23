package xyz.oldz.community.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import xyz.oldz.community.dao.LoginTicketMapper;
import xyz.oldz.community.dao.UserMapper;
import xyz.oldz.community.entity.LoginTicket;
import xyz.oldz.community.entity.User;
import xyz.oldz.community.util.CommunityConstant;
import xyz.oldz.community.util.CommunityUtil;
import xyz.oldz.community.util.MailClient;

/**
 * @author lz
 */
@Service
@Transactional
public class UserService implements CommunityConstant {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private LoginTicketMapper loginTicketMapper;

  @Autowired
  private MailClient mailClient;

  @Autowired
  private TemplateEngine templateEngine;

  @Value("${community.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  public User findUserById(Integer id) {
    return userMapper.selectByPrimaryKey(id);
  }

  public Map<String, Object> register(User user) {
    Map<String, Object> map = new HashMap<>();

    if (user == null) {
      throw new IllegalArgumentException("参数不能为空");
    }
    if (StringUtils.isBlank(user.getUsername())) {
      map.put("usernameMap", "账号不能为空");
      return map;
    }
    if (StringUtils.isBlank(user.getPassword())) {
      map.put("passwordMsg", "密码不能为空");
      return map;
    }
    if (StringUtils.isBlank(user.getEmail())) {
      map.put("emailMsg", "邮箱不能为空");
      return map;
    }
    //验证账号是否被注册
    User u = userMapper.selectByUserName(user.getUsername());
    if (u != null) {
      map.put("usernameMsg", "该账号已存在");
      return map;
    }
    //验证邮箱是否被注册
    u = userMapper.selectByEmail(user.getEmail());
    if (u != null) {
      map.put("emailMsg", "该邮箱已被注册！");
      return map;
    }

    //注册用户
    //密码设置为md5加密+盐
    user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
    user.setPassword(CommunityUtil.md5(user.getPassword()) + user.getSalt());
    user.setType(0);
    user.setStatus(0);
    user.setActivationCode(CommunityUtil.generateUUID());
    user.setHeaderUrl(
        String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
    user.setCreateTime(new Date());

    userMapper.insertSelective(user);

    //激活邮件
    Context context = new Context();
    context.setVariable("email", user.getEmail());

    String url =
        domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
    context.setVariable("url", url);
    String content = templateEngine.process("/mail/activation", context);
    mailClient.sendMail(user.getEmail(), "激活账号", content);
    return map;
  }

  public int activation(int userId, String code) {
    User user = userMapper.selectByPrimaryKey(userId);
    if (user == null) {
      return ACTIVATION_FAILURE;
    }
    //已激活
    if (user.getStatus() == 1) {
      return ACTIVATION_REPEAT;
    } else if (StringUtils.equals(user.getActivationCode(), code)) {
      user.setStatus(1);
      userMapper.updateByPrimaryKeySelective(user);
      return ACTIVATION_SUCCESS;
    } else {
      return ACTIVATION_FAILURE;
    }
  }

  public Map<String, Object> login(String username, String password, int expiredSeconds) {
    Map<String, Object> map = new HashMap<>();
    if (StringUtils.isBlank(username)) {
      map.put("usernameMsg", "账号不能为空！");
      return map;
    }
    if (StringUtils.isBlank(password)) {
      map.put("passwordMsg", "密码不能为空！");
      return map;
    }
    //获取用户信息
    User user = userMapper.selectByUserName(username);
    if (user == null) {
      map.put("usernameMsg", "该用户不存在");
      return map;
    }
    //验证状态
    if (user.getStatus() == 0) {
      map.put("usernameMsg", "该用户未激活");
      return map;
    }
    //验证密码
    password = CommunityUtil.md5(password) + user.getSalt();
    if (!StringUtils.equals(password, user.getPassword())) {
      map.put("passwordMsg", "密码不存在");
    }

    LoginTicket loginTicket = new LoginTicket();
    loginTicket.setUserId(user.getId());
    loginTicket.setTicket(CommunityUtil.generateUUID());
    loginTicket.setStatus(0);
    loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
    loginTicketMapper.insertSelective(loginTicket);

    map.put("ticket", loginTicket.getTicket());
    return map;
  }

  public void logout(String ticket) {
    if (StringUtils.isBlank(ticket)) {
      return;
    }
    loginTicketMapper.updateStatus(ticket, 1);
  }

  public LoginTicket findLoginTicket(String ticket) {
    return loginTicketMapper.selectByTicket(ticket);
  }
}
