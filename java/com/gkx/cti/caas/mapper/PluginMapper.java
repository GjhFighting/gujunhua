package com.gkx.cti.caas.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PluginMapper {

	int savePluginMap(@Param("server_id")String serverip, @Param("plugin")String plugin, @Param("update_user")String update_user, @Param("plugin_value")String plugin_value);

	String getFormValue(@Param("plugin")String plugin, @Param("server_id")String server_id);

}
