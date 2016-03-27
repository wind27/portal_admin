package com.wind.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wind.commons.ServiceResult;
import com.wind.entity.Link;
import com.wind.mongo.service.LinkService;
import com.wind.utils.CSDNLinkUtil;

public class CSNDArticleThread implements Runnable{
	
	private Link link = null;
	private LinkService linkService;
	
	public CSNDArticleThread(LinkService linkService, Link link) {
		this.linkService = linkService;
		this.link = link;
	}
	
	@Override
	public void run() {
		if(link==null) {
    		return ;
    	}
		System.out.println(Thread.currentThread().getName()+"正处理");
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
