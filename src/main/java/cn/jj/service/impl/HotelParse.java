package cn.jj.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.jj.controller.HotelStart;
import cn.jj.entity.Hotel;
import cn.jj.entity.data.Addressinfo;
import cn.jj.entity.data.Hotelinfo;
import cn.jj.service.IHotelParse;
import cn.jj.utils.DateUtil;
import cn.jj.utils.NumUtils;
import cn.jj.utils.RedisUtil;

public class HotelParse implements IHotelParse {

	static SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");

	//redis工具类
	private static RedisUtil jedis=RedisUtil.getInstance();

	//redis中存放分页链接的key
	public static final String KEY_URL = "queueUrl";

	//redis中存放分页链接的key
	public static final String KEY_HOTEL = "hotel";


	@Override
	public void hotelParse(Hotel hotel) {
		//判断url链接为驴妈妈网站
		if(hotel.getUrl().startsWith("http://s.lvmama.com/hotel/")){
			lvmamaParse(hotel);
		}
		//判断url链接为携程网站
		if(hotel.getUrl().startsWith("http://hotels.ctrip.com/")){
			xiechengParse(hotel);
		}
		//判断url链接为途牛网站
		if(hotel.getUrl().startsWith("http://hotel.tuniu.com")){
			tuniuParse(hotel);
		}
		//判断url链接为同程网站
		if(hotel.getUrl().startsWith("https://www.ly.com")){
			tongChengParse(hotel);
		}
		//判断url链接是去哪儿的网站
		if(hotel.getUrl().startsWith("https://touch.qunar.com")){
			//获取城市名称，拼装url
			qunarParse(hotel);
		}
	}


	/**
	 * 
	 * @Description 去哪儿获取输入省份的城市名称，如"海南省"
	 * @author 姚良良
	 * @date 2017年12月22日 上午10:15:07
	 * @action cityList
	 * @return List<String>
	 */
	public static List<String> getCityList(Hotel hotel){
		String url = hotel.getUrl();
		String content = hotel.getContent();
		try {
			List<String> cityList = new ArrayList<String>();
			if(StringUtils.isBlank(content)){
				System.out.println("请求失败!");
				return null;
			}
			Thread.sleep(1000);
			JSONObject proJSON = new JSONObject(content);
			String data = proJSON.get("data").toString();
			if(StringUtils.isBlank(data)){
				System.out.println("输入省份有误!");
				return null;
			}
			JSONObject cityJSON = new JSONObject(data);
			String result = cityJSON.get("result").toString();
			JSONArray cityArr = new JSONArray(result);
			for (int i = 0; i < cityArr.length(); i++) {		//循环取出输入省份里的城市名字
				JSONObject keyJSON = new JSONObject(cityArr.get(i).toString());
				String key = keyJSON.get("key").toString();
				String display = keyJSON.get("display").toString();
				if(!key.equals(display)){
					continue;
				}
				cityList.add(key);
			}
			if(cityList == null || cityList.size() == 0){		//若取出的城市集合为空，则输入的目的地为城市名字，如"上海，三亚"
				String cityName = cityJSON.get("userInput").toString();
				cityList.add(cityName);
			}
			return cityList;
		} catch (Exception e) {
			jedis.addStr(KEY_URL, url);
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @Description 去哪酒店解析
	 * @author 赵乐
	 * @date 2017年12月28日 上午9:55:19
	 * @action qunarParse
	 * @param @param hotel
	 * @return void
	 */
	private void qunarParse(Hotel hotel) {
		// TODO Auto-generated method stub
		String url = hotel.getUrl();
		String content = hotel.getContent();
		//判断url是获取城市名字链接
		if(url.startsWith("https://touch.qunar.com/api/hotel/suggest/c?city=")){
			List<String> cityList = HotelParse.getCityList(hotel);
			for (int i = 0; i < cityList.size(); i++) {	//循环城市
				String cityName = cityList.get(i);
				String cityUrl = "https://touch.qunar.com/api/hotel/hotellist?city="+cityName;
				jedis.addStr(KEY_URL, cityUrl);
			}
		}
		//判断首页链接，获取分页链接
		if(url.startsWith("https://touch.qunar.com/api/hotel/hotellist?city=") && !(url.contains("&page="))){
			int totalPage = 0;
			try {
				JSONObject cityJSON = new JSONObject(content);
				String data = cityJSON.get("data").toString();
				JSONObject dataJSON = new JSONObject(data);
				totalPage = Integer.valueOf(dataJSON.get("totalPage").toString());
			} catch (Exception e) {
				jedis.addStr(KEY_URL, url);
				System.out.println("酒店信息获取失败!"+url);
			}
			for (int j = 1; j <= totalPage; j++) {	
				//循环城市酒店分页信息
				String pageUrl = url+"&page="+j;
				jedis.addStr(KEY_URL, pageUrl);
				System.out.println("分页链接："+pageUrl);
			}
		}
		//判断分页链接，获取酒店详情信息
		if(url.startsWith("https://touch.qunar.com/api/hotel/hotellist?city=") && (url.contains("&page="))){
			try {
				//String destination=url.substring(url.indexOf("city=")+5, url.indexOf("&page="));
				JSONObject cityJSON = new JSONObject(content);
				String data = cityJSON.get("data").toString();
				JSONObject dataJSON = new JSONObject(data);
				String countryName = dataJSON.get("countryName").toString();	//国家名字
				String province = dataJSON.getString("province");
				String hotels = dataJSON.get("hotels").toString();
				JSONArray hotelsArr = new JSONArray(hotels);
				System.out.println("该页酒店总数为："+hotelsArr.length());
				for (int k = 0; k < hotelsArr.length(); k++) {		//循环每页酒店具体信息
					try {
						//取出酒店基础信息
						JSONObject hotelJS = new JSONObject(hotelsArr.get(k).toString());
						
						String price = hotelJS.get("price").toString();				//价格
						String hotelId = hotelJS.get("id").toString();	
						String hotelUrl = "http://touch.qunar.com/hotel/hoteldetail?seq="+hotelId;		//酒店详情URL
						//用于url去重
						String string = jedis.get(hotelUrl);
						if(StringUtils.isNotBlank(string)){
							continue;
						}else{
							jedis.set(hotelUrl, hotelUrl);
						}
						
						String city = hotelJS.get("cityName").toString();			//城市名字
						String attrs = hotelJS.get("attrs").toString();
						JSONObject attrsJS = new JSONObject(attrs);
						String name = attrsJS.get("hotelName").toString();			//酒店名字
						String address = attrsJS.get("hotelAddress").toString();	//酒店地址
						String gpoint = attrsJS.get("gpoint").toString();			//经纬度
						String [] gpointArr = gpoint.split(",");
						String longitude ="";							
						String latitude ="";
						try {
							longitude = gpointArr[1];	//经度						
							latitude = gpointArr[0];//纬度
						} catch (Exception e) {
							// TODO: handle exception
						}
						String grade = attrsJS.get("CommentScore").toString(); 		//评分
						String gradeNum = attrsJS.get("CommentCount").toString();	//评分个数							
						String star = attrsJS.getString("dangciText");
						//酒店ID(用于获得酒店介绍)						
						//酒店基础信息
						Hotelinfo hotelinfo = new Hotelinfo();

						hotelinfo.setName(name);
						hotelinfo.setAddress(address);
						hotelinfo.setLongitude(longitude);
						hotelinfo.setLatitude(latitude);
						hotelinfo.setPrice(price);
						hotelinfo.setGrade(grade);
						hotelinfo.setGradenum(gradeNum);
						hotelinfo.setStar(star);

						

						System.out.println(hotelUrl);
						//酒店四级地址
						Addressinfo addressInfo=new Addressinfo();
						addressInfo.setCity(city);
						addressInfo.setDetailaddress(address);
						addressInfo.setType(2);
						addressInfo.setCountry(countryName);
						addressInfo.setProvince(province);
						/*if(!destination.equals(city)){
							addressInfo.setProvince(destination);
						}
						 */
						hotel.setUrl(hotelUrl);
						hotel.setPageUrl(url);
						hotel.setHotelbasicInfo(hotelinfo);
						hotel.setAddressInfo(addressInfo);
						jedis.add(KEY_HOTEL, hotel);

					} catch (Exception e) {
						e.printStackTrace();
						jedis.addStr(KEY_URL, url);
					}
				}
			} catch (Exception e) {
				jedis.addStr(KEY_URL, url);
			}
		}
	}

	/**
	 * @Description 同程酒店解析
	 * @author 汤玉林
	 * @date 2017年12月22日 下午2:37:41
	 * @action tongChengParse
	 * @param hotel
	 */
	private void tongChengParse(Hotel hotel) {
		Document doc = Jsoup.parse(hotel.getContent());
		String url = hotel.getUrl();
		//解析城市url
		if(url.startsWith("https://www.ly.com/searchlist")){
			String cityId= url.substring(url.lastIndexOf("=")+1, url.length());

			Elements totalEle=doc.select("div#filter-result>p.fm-HSG>span");
			String totalNum=totalEle.text();
			int totalPage=Integer.valueOf(totalNum)%20==0?Integer.valueOf(totalNum)/20:Integer.valueOf(totalNum)/20+1;
			for(int i=1;i<=totalPage;i++){
				//下面的链接是post请求
				String pageUrl="https://www.ly.com/hotel/api/search/hotellist";
				String param="CityId="+cityId+"&BizSectionId=0"
						+ "CityId="+cityId+"&ComeDate="+df.format(new Date())+"&LeaveDate="+DateUtil.getEndDate(df.format(new Date()), 1)+"&PageSize=20"
						+ "&Page="+i+"&antitoken=ecb5fecb4609690676f6f2c6922861e8";
				jedis.addStr(KEY_URL, pageUrl+"?"+param);
				System.out.println("分页url为："+pageUrl+"?"+param);
			}
		}
		//解析分页url
		if(url.startsWith("https://www.ly.com/hotel/api/search/hotellist")){
			resolveTongChengPageUrl(hotel);
		}



	}
	/**
	 * @Description 解析同程分页url
	 * @author 汤玉林
	 * @date 2017年12月22日 下午3:42:32
	 * @action resolveTongChengPageUrl
	 * @param hotel
	 */
	private void resolveTongChengPageUrl(Hotel hotel) {
		String content = hotel.getContent();
		String pageUrl = hotel.getUrl();
		if(StringUtils.isNotBlank(content)){
			try{
				JSONObject jsonObject=new JSONObject(content);
				JSONArray jsonArray=jsonObject.getJSONArray("HotelInfo");

				for(int k=0;k<jsonArray.length();k++){
					JSONObject obj=jsonArray.getJSONObject(k);
					String url="https://www.ly.com/HotelInfo-"+obj.getInt("Id")+".html";
					//用于去重的url键值
					String string = jedis.get(url);
					if(StringUtils.isNotBlank(string)){
						continue;
					}else{
						jedis.set(url, url);
					}
					Hotel hoteinfo = new Hotel();
					//url和分页url都放入hotel中
					hoteinfo.setPageUrl(hotel.getUrl());
					hoteinfo.setUrl(url);
					

					System.out.println("酒店url："+url);
					String hotelName=obj.getString("Name");
					String cityName=obj.getString("CityName");
					String hotelAddress=obj.getString("Address");
					hotelAddress=hotelAddress.substring(hotelAddress.indexOf("\">")+2, hotelAddress.indexOf("</"));
					String star=obj.getString("Grade");
					String price=(String) obj.get("Price");
					String grade=obj.getString("Pf");
					Integer commentCount=obj.getInt("MixedCmtNum");

					JSONObject pcHotelInfo=obj.getJSONObject("PCHotelInfo");
					String lng=pcHotelInfo.getString("Lon");
					String lat=pcHotelInfo.getString("Lat");


					System.out.println("url:"+url);
					System.out.println("酒店名称："+hotelName+";酒店星级："+star+";酒店地址："+hotelAddress+";经纬度："+lat+","+lng+";评分："+grade+";评论人数："+commentCount+";价格："+price);
					Hotelinfo basicInfo=new Hotelinfo();
					basicInfo.setName(hotelName);
					basicInfo.setAddress(hotelAddress);
					basicInfo.setPrice(price);
					basicInfo.setGrade(grade);
					basicInfo.setStar(star);
					basicInfo.setDatasource("Tongcheng");
					basicInfo.setGradenum(commentCount.toString());
					basicInfo.setLatitude(lat);
					basicInfo.setLongitude(lng);
					hoteinfo.setHotelbasicInfo(basicInfo);

					Addressinfo addressInfo=new Addressinfo();
					addressInfo.setCity(cityName);
					addressInfo.setDetailaddress(hotelAddress);
					addressInfo.setProvince(cityName.equals("上海")?"上海":"海南");
					addressInfo.setType(2);
					addressInfo.setCountry("中国");
					hoteinfo.setAddressInfo(addressInfo);

					jedis.add(KEY_HOTEL, hoteinfo);

				}

			}catch(Exception e){
				jedis.addStr(KEY_URL, hotel.getUrl());
			}
		}else {
			jedis.addStr(KEY_URL, pageUrl);
		}
	}

	/**
	 * 
	 * @Description 解析驴妈妈酒店
	 * @author 赵乐
	 * @date 2017年12月1日 上午11:34:47
	 * @action lvmamaParse
	 * @param @param hotel
	 * @return void
	 */
	public void lvmamaParse(Hotel hotel){
		Document document = Jsoup.parse(hotel.getContent());
		//抓取酒店分页链接
		if(hotel.getUrl().startsWith("http://s.lvmama.com/hotel/U")){
			//http://s.lvmama.com/hotel/U9C20171201O20171202?keyword=&mdd=%E4%B8%8A%E6%B5%B7
			//取总条数标签
			Elements totalHotel=document.select("div.request.clearfix>p.result>b");
			//计算总页数
			int totalPage=Integer.parseInt(totalHotel.text())%20==0?Integer.parseInt(totalHotel.text())/20:(Integer.parseInt(totalHotel.text())/20)+1;
			String href=hotel.getUrl();
			//通过截取url，拼接分页url
			Integer lastindex=href.lastIndexOf("/")+1;
			String cityId=href.substring(lastindex,href.indexOf("C", lastindex));
			String mmd=href.substring(href.indexOf("mdd=")+4,href.length());
			//获取分页链接
			for(int j=1;j<=totalPage;j++){
				String url="http://s.lvmama.com/hotel/C20171011O20171012P"+j+""+cityId+"?keyword=&mdd="+mmd+"#list";
				jedis.addStr(KEY_URL,url);
				System.out.println(url+"分页url++++++");
			}
		}
		//抓取详情页面
		if(hotel.getUrl().startsWith("http://s.lvmama.com/hotel/C")){
			//获取分页链接
			String pageUrl=hotel.getUrl();
			//获取分页链接中部分信息
			String cityName=pageUrl.substring(pageUrl.indexOf("mdd=")+4,pageUrl.length()-5);
			//解析分页页面
			resolveLvmamaPageUrl(hotel, cityName, document);

		}
	}
	/**
	 * 
	 * @Description 解析lvmama酒店分页url,获取分页页面部分信息
	 * @author 赵乐
	 * @date 2017年12月4日 下午1:50:32
	 * @action resolvePageUrl
	 * @param @param pageUrl
	 * @param @param cityName
	 * @param @return
	 * @return List<HotelBasicInfoPojo>
	 */
	public void resolveLvmamaPageUrl(Hotel hotelInfo,String cityName,Document document){
		//获取分页链接
		String pageUrl=hotelInfo.getUrl();
		Integer lastindex=pageUrl.lastIndexOf("/")+1;
		//C20171012O20171013P1U9
		String strss=pageUrl.substring(lastindex,pageUrl.indexOf("?"));
		String str[]=strss.split("P");
		String str1=str[0];
		String str2=str[1];
		String cityId=str2.substring(str2.indexOf("U"),str2.length());
		//获取父类url
		String parentUrl="http://s.lvmama.com/hotel/"+cityId+str1+"?mmd="+cityName;
		//获取酒店url的a标签
		Elements hotelList=document.select("div.mainLeft.fl>div.prdLi");
		int count=0;
		//遍历url的a标签集合，获取酒店详情部分信息
		for(Element e:hotelList){
			//获取酒店详情url
			Elements element=e.select("a.proImg");
			String hotelUrl=element.attr("href");
			
			//存入url用于去重
			String string = jedis.get(hotelUrl);
			if(StringUtils.isNotBlank(string)){
				continue;
			}else{
				jedis.set(hotelUrl, hotelUrl);
			}
			
			System.out.println(hotelUrl+"酒店url-----");
			Hotel hotel=new Hotel();
			//设置一级url 主页url
			hotel.setParentUrl(parentUrl);
			//设置二级url 分页url
			hotel.setPageUrl(pageUrl);
			//设置酒店详情url
			hotel.setUrl(hotelUrl);
			
			try{
				String stardesc="";
				//获取酒店星级的信息
				Elements hotelStar=e.select("p.proTit>span.djjd_tagsclasses");
				if(hotelStar.size()>0){
					stardesc=hotelStar.attr("title");
				}else{
					hotelStar=e.select("dt.proTit>span.djjd_tagsclasses");
					stardesc=hotelStar.text();
					if(hotelStar.size()<=0){
						hotelStar=e.select("dt.proTit>span.hotel_stars");
						stardesc=hotelStar.attr("class");
						if(stardesc.contains("hotel_stars05")){
							stardesc="五星级";
						}
						else if(stardesc.contains("hotel_stars04")){
							stardesc="四星级";
						}
						else if(stardesc.contains("hotel_stars03")){
							stardesc="三星级";
						}
						else if(stardesc.contains("hotel_stars02")){
							stardesc="二星级";
						}
						else if(stardesc.contains("hotel_stars01")){
							stardesc="一星级";
						}
					}
				}
				//获取酒店名称
				String hotelName=e.attr("name").replace("．", "");
				String address="";
				//酒店地址
				Elements hotelAddress=e.select("div.proInfo>dl.prdDtl.address>dd");
				if(hotelAddress.size()>0){
					address=hotelAddress.get(0).ownText();
				}else {
					hotelAddress=e.select("dd.proInfo-address>i");
					address=hotelAddress.text();
				}
				//酒店价格
				Elements hotelPrice=e.select("div.price_inf>p.price>span.num");
				if(hotelPrice.size()<=0){
					hotelPrice=e.select("div.priceInfo-price>dfn>span.num");
				}
				String price=hotelPrice.text();
				//酒店评分
				Elements hotelGoodCommentRate=e.select("div.price_inf>p.evaluate>span.num");
				if(hotelGoodCommentRate.size()<=0){
					hotelGoodCommentRate=e.select("div.priceInfo>div>ul.product-number>li>a>b");
				}
				String score=hotelGoodCommentRate.text();
				//酒店的评分个数
				String commentsCount="";
				Elements hotelCommentsCount=e.select("div.price_inf>p.eval_count>a.num");
				if(hotelCommentsCount.size()<=0){
					Elements selects = e.select("div.priceInfo>div>ul.product-number>li>a");
					if(selects.size()>1){
						Element elementCommentsCount = selects.get(1);
						commentsCount=elementCommentsCount.text();
					}
				}
				//设置酒店实体类
				Hotelinfo basicInfo=new Hotelinfo();
				basicInfo.setName(hotelName);
				basicInfo.setAddress(address);
				basicInfo.setPrice(price);
				basicInfo.setGrade(score);
				basicInfo.setStar(stardesc);
				basicInfo.setDatasource("Lvmama");
				basicInfo.setGradenum(commentsCount);

				hotel.setHotelbasicInfo(basicInfo);
				//设置地址实体类
				Addressinfo addressInfo=new Addressinfo();
				addressInfo.setDetailaddress(address);
				addressInfo.setCity(cityName);
				addressInfo.setProvince(cityName.equals("上海")?"上海":"海南");
				addressInfo.setType(2);
				addressInfo.setCountry("中国");

				hotel.setAddressInfo(addressInfo);
				//存入list集合
				jedis.add(KEY_HOTEL, hotel);
			}catch(Exception x){
				jedis.add(KEY_URL, pageUrl);
			}
		}
	}
	/**
	 * 
	 * @Description 解析携程酒店信息
	 * @author 赵乐
	 * @date 2017年12月1日 上午11:35:52
	 * @action xiechengParse
	 * @param @param hotel
	 * @return void
	 */
	public void xiechengParse(Hotel hotel){
		//抓取酒店分页链接
		if(hotel.getUrl().startsWith("http://hotels.ctrip.com/hotel/")){
			Document document = Jsoup.parse(hotel.getContent());
			Elements elements = document.select("div#page_info>div.c_page_list.layoutfix>a");
			String url=hotel.getUrl();
			String strss=url.substring(url.lastIndexOf("/")+1, url.length());
			Integer cityId=NumUtils.getInteger(strss);
			String cityPY=strss.replace(cityId+"", "");
			Elements elementscityLat = document.select("input#cityLat");
			//经纬度
			String cityLat ="";
			String cityLng ="";
			for (Element element : elementscityLat) {
				cityLat = element.attr("value");
			}
			Elements elementscityLng = document.select("input#cityLng");
			for (Element element : elementscityLng) {
				cityLng = element.attr("value");
			}
			//总页数
			Integer pageNum=1;
			for (Element element : elements) {
				if("nofollow".equals(element.attr("rel"))){
					String pageNumstr = element.text();
					pageNum=Integer.parseInt(pageNumstr);
				}
			}

			for(int k=1;k<=pageNum;k++){
				String pageHref="http://hotels.ctrip.com/Domestic/Tool/AjaxHotelList.aspx?__VIEWSTATEGENERATOR=DB1FBB6D&cityName=%25E4%25B8%2589%25E4%25BA%259A&"
						+ "StartTime=2017-09-26&DepTime=2017-09-27"
						+ "&txtkeyword=&Resource=&Room=&Paymentterm=&BRev=&Minstate=&PromoteType=&PromoteDate=&operationtype=NEWHOTELORDER&PromoteStartDate=&PromoteEndDate=&OrderID=&RoomNum=&IsOnlyAirHotel=F&"
						+ "cityId="+cityId+""
						+ "&cityPY="+cityPY+"&cityCode=0899&"
						+ "cityLat="+cityLat+"&cityLng="+cityLng+""
						+ "&positionArea=&positionId=&keyword=&hotelId=&htlPageView=0&hotelType=F&hasPKGHotel=F&requestTravelMoney=F&isusergiftcard=F&useFG=F&HotelEquipment=&priceRange=-2&hotelBrandId=&promotion=F&prepay=F&IsCanReserve=F&OrderBy=99&OrderType=&k1=&k2=&CorpPayType=&viewType=&checkIn=2017-09-26&checkOut=2017-09-27&DealSale=&ulogin=&hidTestLat=0%257C0&AllHotelIds=386917%252C486003%252C1486685%252C1682691%252C3362163%252C1185042%252C436673%252C346766%252C346690%252C396523%252C345067%252C345522%252C345078%252C6007926%252C345071%252C345553%252C353871%252C345101%252C532834%252C1601597%252C470083%252C5296769%252C3735187%252C345074%252C430163&psid=&HideIsNoneLogin=T&isfromlist=T&ubt_price_key=htl_search_result_promotion&showwindow=&defaultcoupon=&isHuaZhu=False&hotelPriceLow=&htlFrom=hotellist&unBookHotelTraceCode=&showTipFlg=&hotelIds=386917_1_1%2C486003_2_1%2C1486685_3_1%2C1682691_4_1%2C3362163_5_1%2C1185042_6_1%2C436673_7_1%2C346766_8_1%2C346690_9_1%2C396523_10_1%2C345067_11_1%2C345522_12_1%2C345078_13_1%2C6007926_14_1%2C345071_15_1%2C345553_16_1%2C353871_17_1%2C345101_18_1%2C532834_19_1%2C1601597_20_1%2C470083_21_1%2C5296769_22_1%2C3735187_23_1%2C345074_24_1%2C430163_25_1"
						+ "&markType=0&zone=&location=&type=&brand=&group=&feature=&equip=&star=&sl="
						+ "&s=&l=&price=&a=0&keywordLat=&keywordLon=&contrast=0&page="+k+"&contyped=0&productcode=";
				System.out.println(pageHref+"分页url++++");
				jedis.addStr(KEY_URL, pageHref);
			}

		}
		//抓取详情页面url
		if(hotel.getUrl().startsWith("http://hotels.ctrip.com/Domestic/Tool/")){
			String url=hotel.getUrl();
			String cityId=url.substring(url.indexOf("cityId")+7, url.indexOf("cityPY")-1);
			String cityPY=url.substring(url.indexOf("cityPY")+7, url.indexOf("cityCode")-1);
			System.out.println("cityId:"+cityId+";cityPY:"+cityPY);
			String cityName="";
			if(cityPY.contains("shanghai")){
				cityName="上海";
			}else if(cityPY.contains("sanya")){
				cityName="三亚";
			}else if(cityPY.contains("haikou")){
				cityName="海口";
			}else if(cityPY.contains("qionghai")){
				cityName="琼海";
			}else if(cityPY.contains("wenchang")){
				cityName="文昌";
			}else if(cityPY.contains("wanning")){
				cityName="万宁";
			}else if(cityPY.contains("danzhou")){
				cityName="儋州";
			}else if(cityPY.contains("dongfang")){
				cityName="东方";
			}else if(cityPY.contains("wuzhishan")){
				cityName="五指山";
			}

			//解析分页url获取酒店详情信息
			resolveXiechengPageUrl(hotel,cityName);
		}

	}
	/**
	 * 
	 * @Description 解析携程酒店分页url，获取部分酒店详细信息
	 * @author 赵乐
	 * @date 2017年12月6日 上午9:44:05
	 * @action resolvePageUrl
	 * @param @return
	 * @return List<Hotel>	
	 */
	public void resolveXiechengPageUrl(Hotel hotel,String cityName){
		//获取分页链接
		String pageUrl = hotel.getUrl();
		//获取下载的内容
		System.out.println(pageUrl);
		String content = hotel.getContent();
		try {
			//解析json中的hotelList(为html)
			String hotelList="";
			if(content.indexOf("hotelList")>0){
				hotelList=content.replace("\\", "").substring(content.indexOf("hotelList")+12, content.indexOf("paging")-2);
			} 
			Document doc=Jsoup.parse(hotelList);
			//获得最低价格
			Elements element=doc.select("span.J_price_lowList");

			content=content.substring(content.indexOf("hotelMapStreetJSON")-1, content.indexOf("biRecord")-2);
			content="{"+content+"}";
			content=content.replace("\\", "");
			JSONObject json = new JSONObject(content);
			JSONArray array=json.getJSONArray("hotelPositionJSON");
			System.out.println("该页酒店url总数为："+array.length());
			for(int j=0;j<array.length();j++){
				
				JSONObject obj=array.getJSONObject(j);
				String url2=obj.get("url").toString().substring(0,obj.get("url").toString().indexOf("?"));
				String href="http://hotels.ctrip.com"+url2;
				
				//用于去重的url键值
				String string = jedis.get(href);
				if(StringUtils.isNotBlank(string)){
					continue;
				}else{
					jedis.set(href, href);
				}
				
				Hotel hotelInfo=new Hotel();
				hotelInfo.setPageUrl(pageUrl);
				hotelInfo.setUrl(href);
				
				
				//酒店价格
				String price=element.get(j).text();

				//酒店详情对象
				Hotelinfo basicInfo=new Hotelinfo();
				basicInfo.setName(obj.getString("name"));
				basicInfo.setAddress(obj.getString("address"));
				basicInfo.setLongitude(obj.getString("lon"));
				basicInfo.setLatitude(obj.getString("lat"));
				basicInfo.setPrice(price);
				basicInfo.setGrade(obj.getString("score"));
				basicInfo.setStar(obj.getString("stardesc"));
				basicInfo.setDatasource("Ctrip");
				basicInfo.setGradenum(obj.getString("dpcount"));

				hotelInfo.setHotelbasicInfo(basicInfo);

				//酒店四级地址表对象
				Addressinfo addressInfo=new Addressinfo();
				addressInfo.setCity(cityName);
				addressInfo.setDetailaddress(obj.getString("address"));
				addressInfo.setProvince(cityName.equals("上海")?"上海":"海南");
				addressInfo.setType(2);
				addressInfo.setCountry("中国");

				hotelInfo.setAddressInfo(addressInfo);
				//加入redis
				jedis.add(KEY_HOTEL, hotelInfo);
			}
		} catch (Exception e) {
			jedis.add(KEY_URL, pageUrl);
		}
	}

	/**
	 * 
	 * @Description 解析途牛酒店信息
	 * @author 赵乐
	 * @date 2017年12月1日 上午11:37:42
	 * @action tuniuParse
	 * @param @param hotel
	 * @return void
	 */
	public void tuniuParse(Hotel hotel){
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		String url = hotel.getUrl();
		String content = hotel.getContent();
		//抓取酒店总个数url，数据为异步
		if(url.startsWith("http://hotel.tuniu.com/ajax/destSuggestion")){
			if(StringUtils.isNotBlank(content)){
				try {
					content=content.substring(1, content.length()-1);//去除前后括号
					JSONObject jsonObject = new JSONObject(content);
					JSONArray suggestions = jsonObject.getJSONArray("suggestions");
					if(suggestions.length()>0){
						JSONObject obj = suggestions.getJSONObject(0);
						Integer cityCode = obj.getInt("cityCode");
						String cityName = obj.getString("cityName");
						String cityUrl = "http://hotel.tuniu.com/list?city="+cityCode+"&cityName="+cityName;
						jedis.addStr(KEY_URL, cityUrl);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}else if(hotel.getUrl().startsWith("http://hotel.tuniu.com/list")){
			
			Integer cityId=NumUtils.getInteger(url);
			String cityName = url.substring(url.indexOf("cityName")+9, url.length());
			String firstPageUrl="http://hotel.tuniu.com/ajax/list?search%5BcityCode%5D="+cityId+""
						+ "&search%5BcheckInDate%5D="+df.format(new Date())+"&search%5BcheckOutDate%5D="+DateUtil.getEndDate(df.format(new Date()), 1)+"&search%5Bkeyword%5D="
						+ "&suggest=&sort%5Bfirst%5D%5Bid%5D=recommend&sort%5Bfirst%5D%5Btype%5D=&sort%5Bsecond%5D="
						+ "&sort%5Bthird%5D=cash-back-after&page=1&returnFilter=0&cityName="+cityName;
			jedis.addStr(KEY_URL, firstPageUrl);
			
		}
		//获取第一页数据
		else if(hotel.getUrl().startsWith("http://hotel.tuniu.com/ajax/list?search%5BcityCode%5D=")&&hotel.getUrl().contains("suggest")){
			String cityName = url.substring(url.indexOf("cityName")+9, url.length());
			String cityId=url.substring(url.indexOf("cityCode")+12, url.indexOf("checkInDate")-10);
			System.out.println(cityId);
			int total = 0; 
			if(StringUtils.isNotBlank(content)){
				try {
					JSONObject json = new JSONObject(content);
					JSONObject data=json.getJSONObject("data");
					total=data.getInt("total");
				} catch (Exception e) {
					jedis.addStr(KEY_URL, url);
					return;
				}
				
			}
			Integer pageNum=total%20==0?total/20:(total/20)+1;
			for(int i=1;i<=pageNum;i++){
				String totalNumHref="http://hotel.tuniu.com/ajax/list?search%5BcityCode%5D="+cityId+""
						+ "&search%5BcheckInDate%5D="+df.format(new Date())+"&search%5BcheckOutDate%5D="+DateUtil.getEndDate(df.format(new Date()), 1)+"&search%5Bkeyword%5D="
						+ "&sort%5Bfirst%5D%5Bid%5D=recommend&sort%5Bfirst%5D%5Btype%5D=&sort%5Bsecond%5D="
						+ "&sort%5Bthird%5D=cash-back-after&page="+i+"&returnFilter=0&cityName="+cityName;
				System.out.println("分页链接："+totalNumHref);
				jedis.addStr(KEY_URL, totalNumHref);
			}
		}
		//获取分页链接
		else if(hotel.getUrl().startsWith("http://hotel.tuniu.com/ajax/list?search%5BcityCode%5D=")&&!hotel.getUrl().contains("suggest")){
			//解析分页url
			resolveTuniuPageurl(hotel);
		}
	}
	/**
	 * 
	 * @Description 解析途牛酒店分页url，获取酒店部分详情信息
	 * @author 赵乐
	 * @date 2017年12月6日 上午10:50:17
	 * @action resolveTuniuPageurl
	 * @param @param hotel
	 * @param @return
	 * @return List<Hotel>
	 */
	public void resolveTuniuPageurl(Hotel hotel){
		//获取分页url
		String pageUrl = hotel.getUrl();
		String cityName = pageUrl.substring(pageUrl.indexOf("cityName")+9, pageUrl.length());
		//获取下载的信息
		String content = hotel.getContent();
		try {
			//获取酒店详情url
			JSONObject jsonObject=new JSONObject(content);
			boolean success=jsonObject.getBoolean("success");
			if(success){
				JSONObject data=jsonObject.getJSONObject("data");
				JSONArray datalist=data.getJSONArray("list");
				System.out.println("本页酒店url总数为："+datalist.length());
				for(int j=0;j<datalist.length();j++){
					
					JSONObject obj=datalist.getJSONObject(j);
					Integer num=obj.getString("url").indexOf("?");
					//获取酒店url
					String url="";
					if(num!=-1){
						url="http://hotel.tuniu.com"+obj.getString("url").substring(0,num);  
						//System.out.println(url+"------途牛酒店url");
					}else{
						url="http://hotel.tuniu.com"+obj.getString("url");
						//System.out.println(url+"------途牛酒店url");
					}
					
					//存入url用于去重
					String string = jedis.get(url);
					if(StringUtils.isNotBlank(string)){
						continue;
					}else{
						jedis.set(url, url);
					}
					
					

					String hotelName=obj.getString("name");
					JSONObject levelInfo=obj.getJSONObject("levelInfo");
					String star=levelInfo.getString("title");
					String addr=obj.getString("address");
					JSONObject pos=obj.getJSONObject("pos");
					String lng=pos.getString("lng");
					String lat=pos.getString("lat");
					Double remarkScore=obj.getDouble("remarkScore");
					Integer remarkCount=obj.getInt("remarkCount");
					Integer price=obj.getInt("startPrice");
					
					Hotel hotelInfo=new Hotel();
					hotelInfo.setPageUrl(pageUrl);
					hotelInfo.setUrl(url);
					Hotelinfo basicInfo=new Hotelinfo();
					basicInfo.setName(hotelName);
					basicInfo.setAddress(addr);
					basicInfo.setLongitude(lng);
					basicInfo.setLatitude(lat);
					basicInfo.setPrice(price.toString());
					basicInfo.setGrade(remarkScore.toString());
					basicInfo.setStar(star);
					basicInfo.setDatasource("Tuniu");
					basicInfo.setGradenum(remarkCount.toString());

					hotelInfo.setHotelbasicInfo(basicInfo);

					Addressinfo addressinfo = new Addressinfo();
					addressinfo.setCity(cityName);
					addressinfo.setDetailaddress(addr);
					addressinfo.setProvince(StringUtils.isNotBlank(cityName)?(cityName.equals("上海")?"上海":"海南"):"");
					
					hotelInfo.setAddressInfo(addressinfo);
					
					
					jedis.add(KEY_HOTEL, hotelInfo);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			jedis.addStr(KEY_URL, pageUrl);
		}
	}

}
