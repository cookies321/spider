package cn.jj.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import cn.jj.entity.Hotel;
import cn.jj.service.IDownLoadService;
import cn.jj.service.IHotelParse;
import cn.jj.service.impl.HotelDownLoad;
import cn.jj.service.impl.HotelParse;
import cn.jj.service.impl.QueueRepositoryService;
import cn.jj.utils.RedisUtil;

public class HotelStart {
	private IDownLoadService downLoad;
	
	private IHotelParse hotelParse;
	
	private QueueRepositoryService queue=new QueueRepositoryService();
	
	//redis工具类
	private static RedisUtil jedis=RedisUtil.getInstance();
	
	//redis中存放分页链接的key
	public static final String KEY_URL = "queueUrl";
	

	public IDownLoadService getDownLoad() {
		return downLoad;
	}

	public void setDownLoad(IDownLoadService downLoad) {
		this.downLoad = downLoad;
	}

	public IHotelParse getHotelParse() {
		return hotelParse;
	}

	public void setHotelParse(IHotelParse hotelParse) {
		this.hotelParse = hotelParse;
	}
	
	public HotelStart() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void start(){
		while(true){
			//String url = queue.poll();
			//从redis队列中取链接信息
			String url=jedis.pollStr(KEY_URL);
			if(StringUtils.isNotBlank(url)){
				Map<String,String> header=new HashMap<>();
				if(url.contains("https://www.ly.com")){
					header.put("Referer", "https://www.ly.com");
					header.put("Cookie", "wangba=1511314396467");
					header.put("Content-type", "application/x-www-form-urlencoded");
				}
				Hotel hotel=(Hotel)this.downLoad.download(url,header);
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				String content = hotel.getContent();
				if(StringUtils.isBlank(content)){
					jedis.addStr(KEY_URL, url);
					System.err.println("失败"+url);
					continue;
				}
				try {
					this.hotelParse.hotelParse(hotel);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					jedis.addStr(KEY_URL, url);
					System.err.println("失败"+url);
					continue;
				}
			}else{
				System.out.println("没有链接了");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
		
	public static void main(String[] args) {
		HotelStart start=new HotelStart();
		start.setDownLoad(new HotelDownLoad());
		start.setHotelParse(new HotelParse());
		String[] cityNames={"上海","三亚","海口","琼海","文昌","万宁","儋州","东方","五指山"};
		//加入驴妈妈酒店主页连接
		/*for(String string :cityNames){
			//String url=start.getLvmamaURL(string);
			String url=start.getxiechengURL(string);
			//添加主页链接到redis中
			HotelStart.jedis.addStr(KEY_URL, url);
		}*/
		//加入携程酒店主页链接
		/*for (String string : cityNames) {
			String url=start.getxiechengURL(string);
			//添加主页链接到redis中
			HotelStart.jedis.addStr(KEY_URL, url);
		}*/
		//加入途牛酒店主页链接
		/*for(String string:cityNames){
			String url = start.gettuniuURL(string);
			HotelStart.jedis.addStr(KEY_URL, url);
		}*/
		//加入同程酒店主页链接
		/*for(String string:cityNames){
			String url = start.getTongChengUrl(string);
			HotelStart.jedis.addStr(KEY_URL, url);
		}*/
		//加入去哪儿酒店主页链接
		/*String []strs={"海南","上海"};
		for (String string : strs) {
			String url="https://touch.qunar.com/api/hotel/suggest/c?city="+string;
			HotelStart.jedis.addStr(KEY_URL, url);
		}*/
		start.start();
	}
	
	/**
	 * 驴妈妈根据不同的城市名获取相关的信息
	 * @author 赵乐
	 * @param cityName 城市名
	 * @return map
	 */
	public String getLvmamaURL(String cityName){
		String url="";
		if(cityName.equals("上海")){
			url="http://s.lvmama.com/hotel/U9C20171012O20171013?mdd=上海";
		} 
		if(cityName.equals("三亚")){
			url="http://s.lvmama.com/hotel/U257C20171012O20171013?mdd=三亚";
		} 
		if(cityName.equals("海口")){
			url="http://s.lvmama.com/hotel/U221C20171012O20171013?mdd=海口";
		} 
		if(cityName.equals("琼海")){
			url="http://s.lvmama.com/hotel/U2298C20171012O20171013?mdd=琼海";
		}
		if(cityName.equals("文昌")){
			url="http://s.lvmama.com/hotel/U2300C20171012O20171013?mdd=文昌";
		}
		if(cityName.equals("万宁")){
			url="http://s.lvmama.com/hotel/U2301C20171012O20171013?mdd=万宁";
		} 
		if(cityName.equals("儋州")){
			url="http://s.lvmama.com/hotel/U2299C20171012O20171013?mdd=儋州";
		} 
		if(cityName.equals("东方")){
			url="http://s.lvmama.com/hotel/U2302C20171012O20171013?mdd=东方";
		} 
		if(cityName.equals("五指山")){
			url="http://s.lvmama.com/hotel/U2297C20171012O20171013?mdd=五指山";
		} 
		return url;
	}
	
	/**
	 * 携程    根据不同的城市名获取相关的信息
	 * @author 赵乐
	 * @param cityName 城市名
	 * @return map
	 */
	public String getxiechengURL(String cityName){
		String url="";
		if(cityName.equals("上海")){
			url="http://hotels.ctrip.com/hotel/shanghai2";
		} 
		if(cityName.equals("三亚")){
			url="http://hotels.ctrip.com/hotel/sanya43";
		} 
		if(cityName.equals("海口")){
			url="http://hotels.ctrip.com/hotel/haikou42";
		} 
		if(cityName.equals("琼海")){
			url="http://hotels.ctrip.com/hotel/qionghai52";
		}
		if(cityName.equals("文昌")){
			url="http://hotels.ctrip.com/hotel/wenchang44";
		}
		if(cityName.equals("万宁")){
			url="http://hotels.ctrip.com/hotel/wanning45";
		} 
		if(cityName.equals("儋州")){
			url="http://hotels.ctrip.com/hotel/danzhou57";
		} 
		if(cityName.equals("东方")){
			url="http://hotels.ctrip.com/hotel/dongfang48";
		} 
		if(cityName.equals("五指山")){
			url="http://hotels.ctrip.com/hotel/wuzhishan46";
		} 
		return url;
	}
	
	/**
	 * 
	 * @Description 途牛 根据不同的城市名获取相关的信息
	 * @author 赵乐
	 * @date 2017年12月1日 下午2:18:46
	 * @action getURL
	 * @param @param cityName
	 * @param @return
	 * @return String
	 */
	public String gettuniuURL(String cityName){
		String url="";
		if(cityName.equals("上海")){
			url="http://hotel.tuniu.com/list?city=2500";
		} 
		if(cityName.equals("三亚")){
			url="http://hotel.tuniu.com/list?city=906";
		} 
		if(cityName.equals("海口")){
			url="http://hotel.tuniu.com/list?city=902";
		} 
		if(cityName.equals("琼海")){
			url="http://hotel.tuniu.com/list?city=905";
		}
		if(cityName.equals("文昌")){
			url="http://hotel.tuniu.com/list?city=909";
		}
		if(cityName.equals("万宁")){
			url="http://hotel.tuniu.com/list?city=908";
		} 
		if(cityName.equals("儋州")){
			url="http://hotel.tuniu.com/list?city=903";
		} 
		if(cityName.equals("东方")){
			url="http://hotel.tuniu.com/list?city=911";
		} 
		if(cityName.equals("五指山")){
			url="http://hotel.tuniu.com/list?city=910";
		} 
		return url;
	}

	
	/**
	 * 根据不同的城市名获取相关的信息
	 * @author 汤玉林
	 * @param cityName 城市名
	 * @return map
	 */
	public String getTongChengUrl(String cityName){
		String url="";
		if(cityName.equals("上海")){
			url="https://www.ly.com/searchlist.html?cityid=321";
		} 
		if(cityName.equals("三亚")){
			url="https://www.ly.com/searchlist.html?cityid=133";
		} 
		if(cityName.equals("海口")){
			url="https://www.ly.com/searchlist.html?cityid=127";
		} 
		if(cityName.equals("琼海")){
			url="https://www.ly.com/searchlist.html?cityid=131";
		}
		if(cityName.equals("文昌")){
			url="https://www.ly.com/searchlist.html?cityid=136";
		}
		if(cityName.equals("万宁")){
			url="https://www.ly.com/searchlist.html?cityid=135";
		} 
		if(cityName.equals("儋州")){
			url="https://www.ly.com/searchlist.html?cityid=138";
		} 
		if(cityName.equals("东方")){
			url="https://www.ly.com/searchlist.html?cityid=126";
		} 
		if(cityName.equals("五指山")){
			url="https://www.ly.com/searchlist.html?cityid=137";
		} 
		return url;
	}

}
