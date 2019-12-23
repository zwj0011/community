package xyz.oldz.community.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Table(name = "login_ticket")
public class LoginTicket {

  @Id
  @KeySql(useGeneratedKeys = true)
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  private String ticket;

  /**
   * 0-有效; 1-无效;
   */
  private Integer status;

  private Date expired;

  @Override
  public String toString() {
    return "LoginTicket{" +
        "id=" + id +
        "&userId=" + userId +
        "&ticket='" + ticket + '\'' +
        "&status=" + status +
        "&expired=" + expired +
        '}';
  }
}
