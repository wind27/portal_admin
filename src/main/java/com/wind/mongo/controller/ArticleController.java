package com.wind.mongo.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wind.commons.Constant;
import com.wind.commons.ServiceResult;
import com.wind.entity.Article;
import com.wind.mongo.service.ArticleService;
import com.wind.utils.CSDNBlogUtil;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/article")
public class ArticleController {
	private final static Logger logger = LoggerFactory.getLogger(ArticleController.class);
	
	@Resource
	ArticleService articleService;
	
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
	public Object add(String url) {
		Article article = CSDNBlogUtil.getArticle("http://blog.csdn.net/mr_tank_/article/details/17454315");
		ServiceResult<Article> serviceResult = articleService.create(article);
		if(serviceResult.isSuccess()==true) {
			System.out.println("创建成功");
		}
		List<Long> uidList = new ArrayList<>();
		uidList.add(1001l);
		ServiceResult<Article> articleResult = articleService.findByUids(uidList, 0, Constant.DEFAULT_PAGE_LIMIT);
		
		if(articleResult.isSuccess()) {
			return articleResult.getList();
		}
		return new JSONObject();
	}
    
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ResponseBody
    public Object find() {
    	List<Long> uidList = new ArrayList<>();
		uidList.add(1001l);
		ServiceResult<Article> articleResult = articleService.findByUids(uidList, 0, Constant.DEFAULT_PAGE_LIMIT);
		if(articleResult.isSuccess()) {
			return articleResult.getList();
		}
		return new JSONObject();
    }
}
