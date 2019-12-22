package xyz.oldz.community.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.oldz.community.entity.DiscussPost;
import xyz.oldz.community.entity.Page;
import xyz.oldz.community.entity.User;
import xyz.oldz.community.service.DiscussPostService;
import xyz.oldz.community.service.UserService;

/**
 * @author lz
 */
@Controller
public class HomeController {

  @Autowired
  private DiscussPostService discussPostService;

  @Autowired
  private UserService userService;

  @GetMapping("/index")
  public String getIndexPage(Model model, Page page) {
    page.setRows(discussPostService.findDiscussPostRows(0));
    page.setPath("/index");

    List<DiscussPost> list = discussPostService
        .findDiscussPosts(0, page.getOffset(), page.getLimit());
    List<Map<String, Object>> discussPosts = new ArrayList<>();
    if (list != null) {
      list.forEach(o -> {
        Map<String, Object> map = new HashMap<>();
        map.put("post", o);
        User user = userService.findUserById(o.getUserId());
        map.put("user", user);
        discussPosts.add(map);
      });
    }
    model.addAttribute("discussPosts", discussPosts);
    return "/index";
  }
}
