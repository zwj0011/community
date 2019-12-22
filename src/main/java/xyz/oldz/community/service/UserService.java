package xyz.oldz.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.oldz.community.dao.UserMapper;
import xyz.oldz.community.entity.User;

/**
 * @author lz
 */
@Service
public class UserService {

  @Autowired
  private UserMapper userMapper;

  /**
   * 根据id查询用户
   *
   * @param id 用户id
   * @return
   */
  public User findUserById(Integer id) {
    return userMapper.selectByPrimaryKey(id);
  }
}
