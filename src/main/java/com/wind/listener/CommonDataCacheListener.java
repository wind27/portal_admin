package com.wind.listener;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import com.wind.commons.ServiceResult;
import com.wind.mongo.service.IdsService;

/**
 * 常用数据缓存
 * 
 * @author qianchun
 * @date 2016年2月25日 下午12:13:05
 */
public class CommonDataCacheListener implements InitializingBean, ServletContextAware{
	private final static Logger logger = LoggerFactory.getLogger(CommonDataCacheListener.class);
    @Override
    public void afterPropertiesSet() throws Exception {
        
    }
    
    @Override
    public void setServletContext(ServletContext servletContext) {
    	ServiceResult result = idsService.initMongodbIds();
    	if(result.isSuccess()==true) {
    		logger.info("mongodb 初始化 id 生成器完成");
    	} else {
    		logger.info("mongodb 初始化 id 生成器失败");
    	}
    	
        
        
    }
    //-------------------------------------------
    private IdsService idsService;
    
	public IdsService getIdsService() {
		return idsService;
	}
	public void setIdsService(IdsService idsService) {
		this.idsService = idsService;
	}
}

