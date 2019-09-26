package com.gkx.cti.caas.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.pagehelper.PageInterceptor;
import com.ql.util.mybatis.MybatisSqlPrintInterceptor;


/**
 * <p>Mybatis配置</p>
 * @author 杨雪令
 * @time 2018年9月7日上午11:06:22
 * @version 1.0
 */
@Configuration
@MapperScan(basePackages = {"com.gkx.cti.caas.mapper"}, sqlSessionFactoryRef = "sqlSessionFactory")
public class MybatisConfig {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	/**
	 * <p>默认数据源</p>
	 * @author 杨雪令
	 * @time 2018年9月7日上午11:06:38
	 * @version 1.0
	 */
	@Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "datasource")
    public DataSource dataSource() {
		
		logger.info("创建默认数据源...");
        return DataSourceBuilder.create().build();
    }
	

	
	
	
	/**
	 * <p>默认sqlSessionFactory</p>
	 * @param dataSource
	 * @return SqlSessionFactory 
	 * @author 杨雪令
	 * @time 2018年9月7日上午11:06:54
	 * @version 1.0
	 */
	@Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) {
		
		logger.info("创建默认sqlSessionFactory...");
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource); 
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath*:mybatis/*.xml"));
            PageInterceptor pageInterceptor = new PageInterceptor();
            Properties pageProperties = new Properties();
            pageProperties.setProperty("reasonable", "true");
            pageInterceptor.setProperties(pageProperties);
            bean.setPlugins(new Interceptor[] {pageInterceptor, new MybatisSqlPrintInterceptor()});
            return bean.getObject();
        } catch (Exception e) {
        	logger.error("创建默认sqlSessionFactory出错", e);
        }
		return null; 
    }
	
}