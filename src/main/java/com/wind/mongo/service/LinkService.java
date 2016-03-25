package com.wind.mongo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.Document;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoCollection;
import com.wind.commons.Constant.ServiceMsg;
import com.wind.commons.ServiceResult;
import com.wind.entity.Link;
import com.wind.mongo.doc.utils.DocumentLinkTransfer;
import com.wind.utils.MongodbUtil;

@Service
public class LinkService {
	@Resource
	MongodbUtil mongodbUtil;
	@Resource
	IdsService idsService;
	
	
	/**
	 * 获取连接
	 * 
	 * @author qianchun  @date 2016年3月3日 下午2:20:49
	 * @return
	 */
	public MongoCollection<Document> getColl() {
		return mongodbUtil.getMongoCollection("wind", "link");
	}
	//-----------------------------------------------------------
	/**
	 *  插入
	 * 
	 * @author qianchun  @date 2016年3月3日 下午2:20:25
	 * @param article
	 * @return
	 */
	public ServiceResult<Link> create(Link link) {
		ServiceResult<Link> result = new ServiceResult<Link>();
		if(link==null) {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.PARAMS_ERROR);
			return result;
		}
		
		//获取并插入自增主键id
		MongoCollection<Document> coll = getColl();
		long id = idsService.getNextIndex("link");
		if(id==0) {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.ID_INCREMENT_ERROR);
			return result;
		}
		link.setId(id);
		
		//插入
		Document doc = DocumentLinkTransfer.link2Document(link);
		boolean flag = mongodbUtil.insert(coll, doc);
		if(flag) {
			result.setSuccess(true);
			return result;
		} else {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.FAIL);
			return result;
		}
	}

	/**
	 *  批量插入
	 * 
	 * @author qianchun  @date 2016年3月3日 下午2:20:25
	 * @param article
	 * @return
	 */
	public ServiceResult<Link> batchCreate(List<Link> linkList) {
		ServiceResult<Link> result = new ServiceResult<Link>();
		
		if(linkList==null || linkList.size()==0) {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.PARAMS_ERROR);
			return result;
		}
		MongoCollection<Document> coll = getColl();
		for(int i=0; i<linkList.size(); i++) {
			Link link = linkList.get(i);
			if(link!=null) {
				long id = idsService.getNextIndex("link");
				if(id==0) {
					result.setSuccess(false);
					result.setMsg(ServiceMsg.ID_INCREMENT_ERROR);
					return result;
				}
				link.setId(id);
			}
		}
		
 		List<Document> docList = DocumentLinkTransfer.link2Document(linkList);
		if(docList==null || docList.size()==0) {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.PARAMS_ERROR);
			return result;
		}
		boolean flag = mongodbUtil.batchInsert(coll, docList);
		if(flag) {
			result.setSuccess(true);
			result.setMsg(ServiceMsg.SUCCESS);
		} else {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.FAIL);
		}
		return result;
	}
	
	/**
	 * 更新
	 * 
	 * @author qianchun  @date 2016年3月3日 下午2:32:23
	 * @param params
	 * @return
	 */
	public ServiceResult<Link> updateById(long id, Link link) {
		ServiceResult<Link> result = new ServiceResult<Link>();
		if(id==0 || link==null) {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.PARAMS_ERROR);
			return result;
		}
		MongoCollection<Document> coll = getColl();
		BsonDocument filter = new BsonDocument().append("id", new BsonInt64(id));
		Document document = DocumentLinkTransfer.link2Document(link);
		coll.findOneAndUpdate(filter, document);
		document = mongodbUtil.update(coll, filter, document);
		if(document==null) {
			result.setSuccess(false);
			result.setMsg(ServiceMsg.SUCCESS);
		} else {
			result.setSuccess(true);
			result.setMsg(ServiceMsg.FAIL);
		}
		return result;
	}
	
	//---------------------------- 查询数据 -----------------------------------
	/**
	 * 查询
	 * 
	 * @author qianchun  @date 2016年3月3日 下午2:21:06
	 * @param params
	 * @return
	 */
	public ServiceResult<Link> find(Map<String, Object> params) {
		ServiceResult<Link> result = new ServiceResult<Link>();
		List<Link> linkList = null;
		MongoCollection<Document> coll = getColl();
		Map<String, Object> filterParams = new HashMap<>();

		if(params!=null && params.get("is_parse")!=null) {
			BsonDocument filter = new BsonDocument().append("is_parse", 
					new BsonInt32((int) params.get("is_parse")));
			filterParams.put("filter", filter);
		}
		if(params!=null && params.get("url")!=null) {
			BsonDocument filter = new BsonDocument().append("url", 
					new BsonString(params.get("url").toString()));
			filterParams.put("filter", filter);
		}
		if(params!=null && params.get("pstart")!=null && params.get("plimit")!=null) {
			filterParams.put("pstart", params.get("pstart"));
			filterParams.put("plimit", params.get("plimit"));
		}
		List<Document> docList = mongodbUtil.find(coll, filterParams);
		if(docList!=null) {
			linkList = DocumentLinkTransfer.document2Link(docList);
		}
		result.setSuccess(true);
		result.setList(linkList);
		return result;
	}
	//-----------------------------------------------------------
}
