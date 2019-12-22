package xyz.oldz.community.entity;

import lombok.Data;

/**
 * 封装分页相关信息
 */
@Data
public class Page {

  //当前页码
  private int current = 1;
  //显示上限
  private int limit = 10;
  //数据总数，用于计算总的页数
  private int rows;
  //查询路径 用于复用分页链接
  private String path;

  public void setCurrent(int current) {
    if (current >= 1) {
      this.current = current;
    }
  }

  public void setLimit(int limit) {
    if (limit >= 1 && limit <= 100) {
      this.limit = limit;
    }
  }

  public void setRows(int rows) {
    if (rows >= 0) {
      this.rows = rows;
    }
  }

  /**
   * 获取当前页的起始行
   *
   * @return
   */
  public int getOffset() {
    return current * limit - limit;
  }

  /**
   * 获取总页数
   *
   * @return
   */
  public int getTotal() {
    if (rows % limit == 0) {
      return rows / limit;
    } else {
      return rows / limit + 1;
    }
  }

  /**
   * 获取起始页码
   *
   * @return
   */
  public int getFrom() {
    int from = current - 2;
    return Math.max(from, 1);
  }

  /**
   * 获取结束页码
   */
  public int getTo() {
    int to = current + 2;
    int total = getTotal();
    return Math.min(to, total);
  }

}
