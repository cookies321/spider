package cn.jj.entity.data.pojo;

import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import cn.jj.entity.data.Pictureinfo;
import cn.jj.entity.data.Routeinfo;
import cn.jj.entity.data.Routepriceinfo;
import cn.jj.entity.data.Url;

/**
 * 携程行程控制实体类
 * @author 姚良良
 *
 */
public class RoutePojo {
	
	private String destination;			//目的地名字，如上海，海南
	
	private List<Document> docList;		//目的地行程的所有页面信息集合
	
	private List<Url> urlList;	//目的地行程的URL地址链接表对象集合

	private Map<String, Routeinfo> routeinfoMap;	//目的地行程信息表对象集合
	
	private Map<String, List<Routepriceinfo>> datePriceMap;		//行程价格表对象集合
	
	private List<Pictureinfo> pictureInfoList;		//行程图片表对象集合

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public List<Document> getDocList() {
		return docList;
	}

	public void setDocList(List<Document> docList) {
		this.docList = docList;
	}

	public List<Url> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<Url> urlList) {
		this.urlList = urlList;
	}

	public Map<String, Routeinfo> getRouteinfoMap() {
		return routeinfoMap;
	}

	public void setRouteinfoMap(Map<String, Routeinfo> routeinfoMap) {
		this.routeinfoMap = routeinfoMap;
	}

	public Map<String, List<Routepriceinfo>> getDatePriceMap() {
		return datePriceMap;
	}

	public void setDatePriceMap(Map<String, List<Routepriceinfo>> datePriceMap) {
		this.datePriceMap = datePriceMap;
	}

	public List<Pictureinfo> getPictureInfoList() {
		return pictureInfoList;
	}

	public void setPictureInfoList(List<Pictureinfo> pictureInfoList) {
		this.pictureInfoList = pictureInfoList;
	}
	
	
}
