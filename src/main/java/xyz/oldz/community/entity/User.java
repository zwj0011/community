package xyz.oldz.community.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;

/**
 * @author lz
 */
@Data
@Table(name = "users")
public class User {

  @Id
  private Integer id;

  private String username;

  private String password;

  private String salt;

  private String email;

  /**
   * 0-普通用户; 1-超级管理员; 2-版主;
   */
  private Integer type;

  /**
   * 0-未激活; 1-已激活;
   */
  private Integer status;

  @Column(name = "activation_code")
  private String activationCode;

  @Column(name = "header_url")
  private String headerUrl;

  @Column(name = "create_time")
  private Date createTime;

}
