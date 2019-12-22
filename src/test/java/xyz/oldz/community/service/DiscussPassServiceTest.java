package xyz.oldz.community.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscussPassServiceTest {

  @Autowired
  private DiscussPostService discussPostService;

  @Test
  void findDiscussPosts() {
    discussPostService.findDiscussPosts(0,1,20);

  }

  @Test
  void getDiscussPostRows() {
    System.out.println(discussPostService.findDiscussPostRows(0));
  }
}
