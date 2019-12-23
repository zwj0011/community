package xyz.oldz.community.dao;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import xyz.oldz.community.entity.LoginTicket;

public interface LoginTicketMapper extends Mapper<LoginTicket> {

  LoginTicket selectByTicket(String ticket);

  int updateStatus(@Param("ticket") String ticket,@Param("status") int status);
}
