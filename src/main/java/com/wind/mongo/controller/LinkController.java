package com.wind.mongo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wind.commons.Constant;
import com.wind.commons.ServiceResult;
import com.wind.entity.Link;
import com.wind.mongo.service.LinkService;
import com.wind.utils.CSDNLinkUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/link")
public class LinkController {
	@Resource
	LinkService linkService;
	
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
	public Object add() {
    	Map<String, Object> params = new HashMap<>();
		params.put("is_parse", Constant.LinkIsParse.NO);
		params.put("pstart", Constant.DEFAULT_PAGE_START);
		params.put("plimit", Constant.DEFAULT_PAGE_LIMIT);
		ServiceResult<Link> linkResult = linkService.find(params);
		
//		if(linkResult.isSuccess()==true && linkResult.getList()!=null) {
//			for(int i=0; i<linkResult.getList().size(); i++) {
//				Link tmp = linkResult.getList().get(i);
//				if(tmp!=null) {
//					ServiceResult<Link> linkResult = linkService.find(params);
//				}
//			}
//		}
    	
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		return resultMap;
	}
    
    
    
    public void batchAdd(Link link) {
    	if(link==null) {
    		return ;
    	}
    	List<Link> linkList = CSDNLinkUtil.getLinks(link.getUrl());
    	
		if(linkList==null || linkList.size()==0) {
			return ;
		}
		//如果以前入库，则从linkList中移除
		for(int i=0; i<linkList.size(); i++) {
			Link tmp = linkList.get(i);
			if(tmp==null) {
				linkList.remove(tmp);
				i--;
				continue;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("url", tmp.getUrl());
			ServiceResult<Link> tmpResult = linkService.find(params);
			if(tmpResult.getList()!=null && tmpResult.getList().size()==0) {
				linkList.remove(tmp);
				i--;
			}
		}
		
		if(linkList==null || linkList.size()==0) {
			return ;
		}
		
		linkService.batchCreate(linkList);
    }
    
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ResponseBody
    public Object find() {
    	Map<String, Object> params = new HashMap<>();
		params.put("is_parse", Constant.LinkIsParse.NO);
		params.put("pstart", Constant.DEFAULT_PAGE_START);
		params.put("plimit", Constant.DEFAULT_PAGE_LIMIT);
		ServiceResult<Link> linkResult = linkService.find(params);
		
		if(linkResult.isSuccess()) {
			return linkResult.getList();
		}
		return new JSONObject();
    }
}
