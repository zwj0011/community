package xyz.oldz.community.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import xyz.oldz.community.entity.DiscussPost;

public interface DiscussPostMapper extends Mapper<DiscussPost> {

  List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId,
      @Param("offset") Integer offset, @Param("limit") Integer limit);

  int selectDiscussPostRows(@Param("userId") int userId);
}
