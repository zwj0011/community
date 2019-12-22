package xyz.oldz.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author lz
 */
@SpringBootApplication
@MapperScan("xyz.oldz.community.dao")
public class CommunityApplication {

  public static void main(String[] args) {
    SpringApplication.run(CommunityApplication.class, args);
  }

}
