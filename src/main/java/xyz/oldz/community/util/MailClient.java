package xyz.oldz.community.util;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Slf4j
@Component
public class MailClient {

  @Autowired
  private JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String from;

  @Autowired
  private TemplateEngine templateEngine;

  public void sendMail(String to, String subject, String content) {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
    try {
      helper.setFrom(from);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(content, true);
      mailSender.send(mimeMessage);
    } catch (MessagingException e) {
      e.printStackTrace();
      log.error("邮件发送失败", e);
    }
  }

  public void sendHtmlMail(){

  }
}
