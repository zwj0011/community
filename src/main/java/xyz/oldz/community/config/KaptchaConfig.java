package xyz.oldz.community.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KaptchaConfig {

  @Bean
  public Producer kaptchaProducer() {
    Properties properties = new Properties();
    properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, "100");
    properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, "40");
    properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "32");
    properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "0,0,0");
    properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
    properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING,
        "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    properties.setProperty(Constants.KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
    DefaultKaptcha kaptcha = new DefaultKaptcha();
    kaptcha.setConfig(new Config(properties));
    return kaptcha;
  }
}
