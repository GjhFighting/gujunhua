package com.gkx.cti.caas.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.gkx.cti.caas.pojo.CallContent;

@Repository
public interface CallContentMapper {

	List<CallContent> getContentBySessionid(@Param("session_id")String session_id);

	List<CallContent> getHistoryContentList(@Param("first")int first, @Param("end")int end, @Param("search")String search);

	int getContentTotal(@Param("search")String string);

	List<CallContent> getContentBySearch(@Param("search")String search);

}
