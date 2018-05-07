package cn.jj.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import cn.jj.entity.Route;
import cn.jj.service.IDownLoadService;
import cn.jj.service.IRouteParse;
import cn.jj.service.impl.QueueRepositoryService;
import cn.jj.service.impl.RouteDownLoad;
import cn.jj.service.impl.RouteParse;
import cn.jj.utils.RedisUtil;
import cn.jj.utils.ValidateUtil;

public class RouteStart {

	private IDownLoadService downLoad;
	
	private IRouteParse routeParse;
	
	private QueueRepositoryService queue = new QueueRepositoryService();
	
	private ExecutorService pool = Executors.newFixedThreadPool(2);
	
	//redis工具类
	public static RedisUtil jedis=RedisUtil.getInstance();
	//redis中存放分页链接的key
	public static final String KEY_URL = "queueUrl";
	//redis中存放分页链接的key
	public static final String KEY_ROUTE = "route";
	
	public IDownLoadService getDownLoad() {
		return downLoad;
	}

	public void setDownLoad(IDownLoadService downLoad) {
		this.downLoad = downLoad;
	}
	
	
	public IRouteParse getRouteParse() {
		return routeParse;
	}

	public void setRouteParse(IRouteParse routeParse) {
		this.routeParse = routeParse;
	}

	public RouteStart() {
		super();
	}
	
	public void start(){
		for (int i=0;i<10;i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true){
						//String url = queue.poll();
						//从redis队列中取链接
						String url = jedis.pollStr("queueUrl");
						if(StringUtils.isNotBlank(url)){
							Map<String ,String > map=new HashMap<>();
							if(url.startsWith("https://gny.ly.com/list")){
								map.put("Cookie", "td_sid=MTUxNDI1ODUxOCw4YjNkZTljMzM4MWM4M2U5ZjEyY2ZmYWJiNWZhYjc5MDliODE3YjBhNTU1ODU5YmUyOTNmMzVjODJkZDMzNjVlLGY4NmU5MGRlZmM0NjFmMjIzYTc2YmMyYzNmYmU1MmE1MzBjMmEwMDcyOGIxYWY4NDI3NTIwMTUwZDNkMzZkYTA=;td_did=kAql6EmJPVPv4AVUaUUPxLBRJk%2B%2F4LFG%2FxVdBDEpanAYOTR7%2Be51aDQScpFPtidUkD5H6DNi2yuoX2k8xiAb%2FrMkn%2FaHFLBumKe8ONEgds0B%2FzH3KQSw9EENOn9bLtBUmfPQJp8VNMESA18SUzRdxPzQfcSTxLagJDLKwXMLHlca3IndjIQ2%2BUY0jHLRVn%2FOr%2B004iC8U5p0%2BhIjQ3qXZ6fbgmLqqcUrCjIN%2FvIciAMMT5JULti8LvC59xgV1jY8;");
								
								map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");
								
							}
							Route route=(Route) downLoad.download(url,map);
							String content = route.getContent();
							if(ValidateUtil.valid(content)){
								jedis.addStr(KEY_URL, url);
								System.err.println("失败"+url);
								continue;
							}
							try {
								routeParse.routeParse(route);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								jedis.addStr(KEY_URL, url);
								System.err.println("失败"+url);
								continue;
							}
						}else{
							System.out.println("没有链接");
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}
 
	public static void main(String[] args) {
		RouteStart start = new RouteStart();
		start.setDownLoad(new RouteDownLoad());
		start.setRouteParse(new RouteParse());
		//加入驴妈妈行程主页连接
		//添加主页url到redis队列中
		//RouteStart.jedis.addStr(KEY_URL, "http://s.lvmama.com/route/H8?keyword=上海&k=0#list");
		//RouteStart.jedis.addStr(KEY_URL, "http://s.lvmama.com/route/H8?keyword=海南&k=0#list");
		//加入携程行程主页连接
		//RouteStart.jedis.addStr(KEY_URL,"http://vacations.ctrip.com/tours/d-shanghai-2");
		//RouteStart.jedis.addStr(KEY_URL,"http://vacations.ctrip.com/tours/d-hainan-100001");
		//加入途牛行程主页连接
		//RouteStart.jedis.addStr(KEY_URL,"http://s.tuniu.com/search_complex/whole-sh-0-上海/");
		//RouteStart.jedis.addStr(KEY_URL,"http://s.tuniu.com/search_complex/whole-sh-0-海南/");
		//加入同城行程主页链接
		//RouteStart.jedis.addStr(KEY_URL, "https://gny.ly.com/list?src=三亚&dest=上海");
		//RouteStart.jedis.addStr(KEY_URL, "https://gny.ly.com/list?src=上海&dest=三亚");
		//加入去哪儿行程主页链接
		//三亚到上海
		RouteStart.jedis.addStr(KEY_URL, "https://dujia.qunar.com/golfz/routeList/adaptors/pcTop?lm=0%2C60&fhLimit=0%2C60&d=三亚&q=上海&m=l%2CbookingInfo%2CbrowsingInfo%2Clm&userId=O5cv5lol8oii3YQOkCSjAg%3D%3D");
		//上海到海南
		RouteStart.jedis.addStr(KEY_URL, "https://dujia.qunar.com/golfz/routeList/adaptors/pcTop?lm=0%2C60&fhLimit=0%2C60&d=上海&q=海南&m=l%2CbookingInfo%2CbrowsingInfo%2Clm&userId=O5cv5lol8oii3YQOkCSjAg%3D%3D");
		
		//加入飞猪行程主页链接
		//全国到海南的链接
		//RouteStart.jedis.addStr(KEY_URL, "https://s.fliggy.com/vacation/list.htm?cq=全国&mq=海南&format=json");
		//RouteStart.jedis.addStr(KEY_URL, "https://s.fliggy.com/vacation/list.htm?cq=全国&mq=上海&format=json");
		
		start.start();
	}
}
