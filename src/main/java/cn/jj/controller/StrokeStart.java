package cn.jj.controller;

import org.apache.commons.lang3.StringUtils;

import cn.jj.entity.Stroke;
import cn.jj.service.IDownLoadService;
import cn.jj.service.IStrokeParse;
import cn.jj.service.impl.QueueRepositoryService;
import cn.jj.service.impl.StrokeDownLoad;
import cn.jj.service.impl.StrokeParse;
import cn.jj.utils.RedisUtil;
import cn.jj.utils.ValidateUtil;

public class StrokeStart {

	private IDownLoadService downLoad;
	
	private IStrokeParse scenicParse;
	//本地消息队列
	public static QueueRepositoryService queue = new QueueRepositoryService();
	
	//redis工具类
	public static RedisUtil jedis=RedisUtil.getInstance();
	
	//redis中存放分页链接的key
	public static final String KEY_URL = "queueUrl";
	//redis中存放分页链接的key
	public static final String KEY_STROKE = "stroke";
	
	public IDownLoadService getDownLoad() {
		return downLoad;
	}

	public void setDownLoad(IDownLoadService downLoad) {
		this.downLoad = downLoad;
	}

	public IStrokeParse getScenicParse() {
		return scenicParse;
	}

	public void setScenicParse(IStrokeParse scenicParse) {
		this.scenicParse = scenicParse;
	}

	public StrokeStart() {
		super();
	}
	
	public void start(){
		for(int i=0;i<10;i++){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true){
						String url = jedis.pollStr(KEY_URL);
						System.out.println(url+"pool取出来的");
						if(StringUtils.isNotBlank(url)){
							System.out.println("取出url开始解析");
							Stroke scenic = (Stroke) downLoad.download(url);
							System.out.println("解析成功");
							String content = scenic.getContent();
							if(ValidateUtil.valid(content)){
								jedis.addStr(KEY_URL, url);
								System.err.println("下载内容为空失败"+url);
								continue;
							}
							try {
								scenicParse.scenicParse(scenic);
							} catch (Exception e) {
								e.printStackTrace();
								jedis.addStr(KEY_URL, url);
								System.err.println("解析失败"+url);
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
			}).start();
		}
	}

	public static void main(String[] args) {
		StrokeStart start = new StrokeStart();
		start.setDownLoad(new StrokeDownLoad());
		start.setScenicParse(new StrokeParse());
		//添加链接到redis队列中
		//StrokeStart.jedis.addStr(KEY_URL, "http://www.lvmama.com/lvyou/scenery/d-shanghai79.html");
		//StrokeStart.jedis.addStr(KEY_URL, "http://www.lvmama.com/lvyou/scenery/d-hainan267.html");
		//加入携程景点主页连接
		//StrokeStart.jedis.addStr(KEY_URL,"http://you.ctrip.com/sight/shanghai2.html");
		//StrokeStart.jedis.addStr(KEY_URL,"http://you.ctrip.com/countrysightlist/hainan100001.html");
		//加入途牛景点主页连接  其中省和城市的链接相同，显示的内容不同，省份显示城市，城市直接显示景点
		/*String cityNames[]={"上海","三亚","海口","三沙市","儋州"};
		for(String cityName:cityNames){
			System.out.println(start.gettuniuUrl(cityName));
			StrokeStart.jedis.addStr(KEY_URL,start.gettuniuUrl(cityName));
		}*/
		//加入同城程景点主页链接
		//StrokeStart.jedis.addStr(KEY_URL,"https://www.ly.com/go/scenery/321.html");
		//StrokeStart.jedis.addStr(KEY_URL,"https://www.ly.com/go/countryProvince/9.html");
		
		
		//加入去哪网景点主页链接
		//海南
		StrokeStart.jedis.addStr(KEY_URL,"http://travel.qunar.com/p-sf299522-hainan");
		//上海
		StrokeStart.jedis.addStr(KEY_URL,"http://travel.qunar.com/p-cs299878-shanghai");
		
		start.start();
	}
	
	/**
	 * 
	 * @Description 获取途牛景点城市url
	 * @author 赵乐
	 * @date 2017年12月1日 上午11:02:02
	 * @action gettuniuUrl
	 * @param @param cityName
	 * @param @return
	 * @return String
	 */
	public String gettuniuUrl(String cityName){
		String url="";
		if("上海".equals(cityName)){
			url="http://www.tuniu.com/guide/d-shanghai-2500/jingdian/";
		}
		if("三亚".equals(cityName)){
			url="http://www.tuniu.com/guide/d-sanya-906/jingdian/";
		}
		
		if("海口".equals(cityName)){
			url="http://www.tuniu.com/guide/d-haikou-902/jingdian/";
		}
		if("三沙市".equals(cityName)){
			url="http://www.tuniu.com/guide/d-sanshashi-920/jingdian/";
		}
		if("儋州".equals(cityName)){
			url="http://www.tuniu.com/guide/d-danzhou-903/jingdian/";
		}
		return url;
	}
	
}
