package xyz.oldz.community.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SensitiveFilterTest {

  @Autowired
  private SensitiveFilter sensitiveFilter;

  @Test
  void filter() {
    String text=sensitiveFilter.filter("赌博，吸毒是好习惯");
    System.out.println(text);
    text=sensitiveFilter.filter("长***者热爱@吸@@毒@和赌%博");
    System.out.println(text);
    text=sensitiveFilter.filter("StringsssStr");
    System.out.println(text);
  }
}
