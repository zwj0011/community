package xyz.oldz.community.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
class MailClientTest {

  @Autowired
  private MailClient mailClient;

  @Autowired
  private TemplateEngine templateEngine;

  @Test
  void sendMail() {
    mailClient.sendMail("1030010976@qq.com", "你好，世界！", "hello world!");
  }

  @Test
  void sendHtmlMail() {
    Context context = new Context();
    context.setVariable("username", "张三");
    String content = templateEngine.process("/mail/demo", context);
    System.out.println(content);
    mailClient.sendMail("1030010976@qq.com", "Html", content);
  }
}
