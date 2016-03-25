package com.wind.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wind.commons.Constant;
import com.wind.commons.ServiceResult;
import com.wind.entity.Link;
import com.wind.mongo.service.LinkService;
import com.wind.utils.CSDNLinkUtil;

import net.sf.json.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:target/classes/applicationContext*.xml"})
public class LinkTest {
	@Resource
	LinkService linkService;

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
					batchAdd(tmp);
				}
			}
		}
	}
    
    public void batchAdd(Link link) {
    	if(link==null) {
    		return ;
    	}
    	List<Link> tmpList = CSDNLinkUtil.getLinks(link.getUrl());
    	List<Link> linkList = new ArrayList<>();
    	
		if(tmpList==null || tmpList.size()==0) {
			return ;
		}
		//如果以前入库，则从linkList中移除
		for(int i=0; i<tmpList.size(); i++) {
			Link tmp = tmpList.get(i);
			if(tmp==null) continue;
			boolean contains = false;
			for(int k=0; k<linkList.size(); k++) {
				if(linkList.get(k).getUrl().equals(tmp.getUrl())) {
					contains = true;
				}
			}
			if(contains) continue;
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("url", tmp.getUrl());
			ServiceResult<Link> tmpResult = linkService.find(params);
			if(tmpResult.getList()==null || tmpResult.getList().size()==0) {
				linkList.add(tmp);
			}
		}
		
		if(linkList==null || linkList.size()==0) {
			return ;
		}
		
		linkService.batchCreate(linkList);
    }
}
