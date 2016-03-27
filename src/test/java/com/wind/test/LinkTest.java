package com.wind.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wind.commons.Constant;
import com.wind.commons.ServiceResult;
import com.wind.entity.Link;
import com.wind.mongo.service.LinkService;
import com.wind.thread.CSNDArticleThread;
import com.wind.utils.CSDNLinkUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:target/classes/applicationContext*.xml"})
public class LinkTest {
	@Resource
	LinkService linkService;
	
	int threadNum = 0;
	
	ExecutorService pool = Executors.newFixedThreadPool(20);

	@Test
	public void main() {
		int start = 0;
		int limit = 20;
		while(start < 10000) {
			add(start, limit);
			start += limit;
		}
//		Link link = new Link();
//		link.setUrl("http://blog.csdn.net");
//		batchAdd(link);
	}
	
	public void add(int start, int limit) {
    	Map<String, Object> params = new HashMap<>();
		params.put("is_parse", Constant.LinkIsParse.NO);
		params.put("pstart", start);
		params.put("plimit", limit);
		ServiceResult<Link> linkResult = linkService.find(params);
		
		if(linkResult.isSuccess()==true && linkResult.getList()!=null) {
			for(int i=0; i<linkResult.getList().size(); i++) {
				Link tmp = linkResult.getList().get(i);
				if(tmp!=null) {
					threadNum += 1;
					Thread t = new Thread(new CSNDArticleThread(linkService, tmp));
					try {
						pool.execute(t);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
    
    
}
