package xyz.oldz.community.entity;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;

/**
 * @author lz
 */
@Data
@Table(name = "discuss_post")
public class DiscussPost {

  @Id
  private Integer id;

  @Column(name = "user_id")
  private Integer userId;

  private String title;

  /**
   * 0-普通; 1-置顶;
   */
  private Integer type;

  /**
   * 0-正常; 1-精华; 2-拉黑;
   */
  private Integer status;

  @Column(name = "create_time")
  private Date createTime;

  @Column(name = "comment_count")
  private Integer commentCount;

  private Double score;

  private String content;

}
