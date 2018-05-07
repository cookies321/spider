package cn.jj.flight.service.impl;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import cn.jj.entity.data.Flightinfo;
import cn.jj.entity.data.Flightinputinfo;
import cn.jj.entity.data.Flightpriceinfo;
import cn.jj.entity.data.Flighttransferinfo;
import cn.jj.flight.entity.Flight;
import cn.jj.flight.entity.Transferinfo;
import cn.jj.flight.service.IFlightParse;
import cn.jj.service.IDownLoadService;
import cn.jj.utils.PhantomjsUtils;
import cn.jj.utils.RedisUtil;

public class FlightParse implements IFlightParse {
	// redis工具类
	private static RedisUtil jedis = RedisUtil.getInstance();
	// redis中存放分页链接的key
	public static final String KEY_URL = "queueUrl";

	// redis中存放分页链接的key
	public static final String KEY_FLIGHT = "flight";
	private String string;

	@Override
	public void flightparse(Flight flight) {
		String pageUrl = flight.getPageUrl();
		// System.out.println(pageUrl);
		if (pageUrl.startsWith("http://flight.lvmama.com/booking")) {
			this.lvmamaParse(flight);
		} else if (pageUrl.startsWith("http://flight-api.tuniu.com/open/flight")) {
			this.tuniuParse(flight);
		} else if (pageUrl.startsWith("https://flight.qunar.com/site/oneway_list.htm")) {
			this.QunaerParse(flight);
		} else if (pageUrl.startsWith("http://flights.ctrip.com/domesticsearch/search/SearchFirstRouteFlights")) {
			this.CtripParse(flight);
		} else if (pageUrl.startsWith("http://m.ly.com/flightnew/")) {
			this.TongchengParse(flight);
		}

	}

	/**
	 * 
	 * @Description 驴妈妈分页页面数据的解析,可以获取航班输入信息
	 * @author xdl
	 * @date 2017年12月26日 下午2:14:27
	 * @action lvmamaParse
	 * @return void
	 */
	public void lvmamaParse(Flight flight) {
		String pageUrl = flight.getPageUrl();
		String content = flight.getContent();
		if (StringUtils.isNotBlank(content)) {
			try {
				// 得到页面的document
				Document document = Jsoup.parse(content);
				// 获得航班输入表的数据
				Elements element1 = document.select("div#tickets>div.tl-item");
				// 得到当日的航班数目
				if(element1.isEmpty()){
					
					System.out.println("解析失败，放回队列");
					jedis.addStr(KEY_URL, flight.getPageUrl());
				}else{
					String flightNum = String.valueOf(element1.size());
					if ("0".equals(flightNum)) {
						System.out.println("返回数据出错！放回队列");
						jedis.addStr(KEY_URL, flight.getPageUrl());
					}else{
						// 得到航班日期
						String flightDate = pageUrl.split("=")[1].split("&")[0];
						String flightDateData = flightDate;
						// 出发城市
						String beginCityCode = pageUrl.split("/")[4].split("-")[0];
						// 到达城市
						String endCityCode = pageUrl.split("-")[1].split(".html")[0];

						Flightinputinfo flightinputinfo = new Flightinputinfo();
						flightinputinfo.setUrl(pageUrl);
						flightinputinfo.setFlightdate(flightDateData);
						flightinputinfo.setBegincity(beginCityCode);
						flightinputinfo.setEndcity(endCityCode);
						flightinputinfo.setFlightnum(flightNum);
						flightinputinfo.setCreator("xiedalei");
						flightinputinfo.setLastupdatetime(new Date());
						flightinputinfo.setStatus(1);
						flightinputinfo.setCreatorid("xiedalei-15255178580");
						flightinputinfo.setCreatedate(new Date());
						// 设置输入表信息的id
						String flightinputid = UUID.randomUUID().toString();
						flightinputinfo.setId(flightinputid);
						System.out.println("驴妈妈" + flightDateData + "航班数据获取成功！当天有" + flightNum + "个航班！");
						//
						Elements flightElements = document.select("div#tickets>div.tl-item");
						for (int i = 0; i < flightElements.size(); i++) {
							Flight flights = new Flight();
							Flightinfo flightinfo = new Flightinfo();
							// 首先获得机票的航班号
							String flightNoData = flightElements.get(i)
									.select("div.tl-detail.clearfix>div.tl-col.tl-info>div.tl-bottom>span").first().text();
							//// 拼接查询详细信息的链接
							String detailUrl = "http://flight.lvmama.com/booking/getYCFseats?departureAirportCode="
									+ beginCityCode + "&arrivalAirportCode=" + endCityCode + "&departureDate=" + flightDateData
									+ "&flightNo=" + flightNoData;
							// 设置某些属性
							flightinfo.setFlightno(flightNoData);
							flightinfo.setInputid(flightinputid);
							flightinfo.setDatasource("Lvmama");
							flights.setPageUrl(pageUrl);
							flights.setFlightinfo(flightinfo);
							flights.setUrl(detailUrl);
							flights.setFlightinputinfo(flightinputinfo);
							flights.setBeginCity(beginCityCode);
							flights.setEndCity(endCityCode);
							flights.setFlightDate(flightDateData);
							// 存入redis中
							System.out.println("驴妈妈部分基本信息获取成功!航班号:" + flightNoData);
							jedis.add(KEY_FLIGHT, flights);
						}
					}
				}
				
			} catch (Exception e) {
				System.out.println("解析失败，放回队列");
				jedis.addStr(KEY_URL, flight.getPageUrl());
			}

		}else{
			System.out.println("解析失败，放回队列");
			jedis.addStr(KEY_URL, flight.getPageUrl());
		}

	}

	/**
	 * 
	 * @Description 途牛机票解析
	 * @author xdl
	 * @date 2017年12月27日 上午11:10:50
	 * @action tuniuParse
	 * @return void
	 */
	public void tuniuParse(Flight flight) {
		String content = flight.getContent();
		String pageUrl = flight.getPageUrl();
		FlightDownLoad flightdownload = new FlightDownLoad();

		if (StringUtils.isNotBlank(content)) {
			try {
				String data = content.substring(content.indexOf("(") + 1, content.length() - 2);
				// 航班信息获取输入表的数据
				Flightinputinfo flightinputinfo = new Flightinputinfo();
				// id
				String flightinputinfoid = UUID.randomUUID().toString();
				flightinputinfo.setId(flightinputinfoid);
				// url
				flightinputinfo.setUrl(pageUrl);
				// 飞行日期
				String flightDate = pageUrl.split("=")[8];
				flightinputinfo.setFlightdate(flightDate);
				// 出发城市
				String beginCity = pageUrl.split("=")[6].split("&")[0];
				flightinputinfo.setBegincity(beginCity);
				// 到达城市
				String endCity = pageUrl.split("=")[7].split("&")[0];
				flightinputinfo.setEndcity(endCity);
				// 包含航班信息的jsonobject
				JSONObject flightdata = new JSONObject(data);
				// 包含航班信息的jsonarray
				JSONArray flightArray = flightdata.getJSONObject("data").getJSONArray("voyGroups");
				JSONObject flightinfoJson = flightdata.getJSONObject("data").getJSONObject("airBasic")
						.getJSONObject("flightMap");
				// 得到reponseid
				String responseId = (String) flightdata.getJSONObject("data").get("responseId");
				// 当天航班数
				String flightNum = String.valueOf(flightArray.length());
				flightinputinfo.setFlightnum(flightNum);
				// 封装其他参数
				flightinputinfo.setCreator("chenwenqi");
				flightinputinfo.setCreatorid("chenwenqi-13263625152");
				flightinputinfo.setCreatedate(new Date());
				System.out.println(flightDate + "航班输入数据获取成功！有" + flightNum + "个航班！");
				// 遍历航班信息的数组
				for (int i = 0; i < flightArray.length(); i++) {
					Flight flights = new Flight();
					Flightinfo fliInfo = new Flightinfo();

					// 得到某一个航班的jsonobject
					JSONObject flightObject = flightArray.getJSONObject(i);
					// 得到参数
					JSONArray voys = flightObject.getJSONArray("voys");
					JSONArray arr = voys.getJSONObject(0).getJSONArray("segFltKeys");
					// 拼接查询的keys
					String key = arr.toString();
					key = key.substring(2, key.length() - 2);
					JSONObject flightdetailJson = (JSONObject) flightinfoJson.get(key);
					// 获取航班号 fn
					String fn = (String) flightdetailJson.get("flightNo");
					fliInfo.setFlightno(fn);
					// 获取出行日期
					fliInfo.setFlightdate(flightDate);
					// 获取到达日期 at
					String at = (String) flightdetailJson.get("arrvDate");
					fliInfo.setFlightarrivedate(at);
					// 获取航班型号cf c
					String cfcc = "";
					Object cfc = flightdetailJson.get("name");
					if (!JSONObject.NULL.equals(cfc)) {
						cfcc = (String) flightdetailJson.get("name");
						fliInfo.setPlane(cfcc);
					}

					// 出发城市dcn
					String dcn = (String) flightdetailJson.get("orgCity");
					fliInfo.setBegincity(dcn);
					// 到达城市acn
					String acn = (String) flightdetailJson.get("dstCity");
					fliInfo.setEndcity(acn);
					// 出发时间dt
					String dt = (String) flightdetailJson.get("deptTime");
					fliInfo.setBegintime(dt);
					// 到达时间at
					String fat = (String) flightdetailJson.get("arrvTime");
					fliInfo.setEndtime(fat);
					// 实际用时pft 分钟
					int pft = (int) flightdetailJson.get("duration");
					fliInfo.setRealcosttime(Integer.toString(pft));
					// 经停sts
					Object sts = flightdetailJson.get("stops");

					if (!JSONObject.NULL.equals(sts)) {
						// 经停
						JSONArray stsarr = flightdetailJson.getJSONArray("stops");
						String cn = (String) stsarr.getJSONObject(0).get("cityCode");
						fliInfo.setStop(cn);
					}
					// 出发航站楼dsmsn
					String dsmsn = (String) flightdetailJson.get("deptTerm");
					fliInfo.setBeginterminal(dsmsn);
					// 到达航站楼asmsn
					String asmsn = (String) flightdetailJson.get("arrvTerm");
					fliInfo.setEndterminal(asmsn);
					// 出发机场dcc
					String dcc = (String) flightdetailJson.get("orgAirport");
					fliInfo.setBegincode(dcc);
					// 到达机场acc
					String acc = (String) flightdetailJson.get("dstAirport");
					fliInfo.setEndcode(acc);
					// 尺寸
					Object atcExt = flightdetailJson.get("atcExt");
					if (!JSONObject.NULL.equals(atcExt)) {
						// 尺寸
						JSONObject filters = flightdetailJson.getJSONObject("atcExt").getJSONObject("filters");
						String size = (String) filters.get("size");
						fliInfo.setSize(size);
					}

					// 最低价格arrayi
					JSONArray flightPrice = flightObject.getJSONArray("lowestPrices");
					int flightPriceSize = flightPrice.length() - 1;

					JSONArray lowestPrices = flightPrice.getJSONObject(flightPriceSize).getJSONArray("psgLowPrices");
					// System.out.println(jSONObject);
					// System.out.println(lowestPrices);
					for (int k = 0; k < lowestPrices.length(); k++) {
						String psgType = (String) lowestPrices.getJSONObject(k).get("psgType");
						if ("ADT".equals(psgType)) {
							// 价格
							int price = (int) lowestPrices.getJSONObject(k).get("price");
							fliInfo.setLowestprice(Integer.toString(price));
							// 折扣
							Object priceDiscount = lowestPrices.getJSONObject(k).get("discount");
							if (!JSONObject.NULL.equals(priceDiscount)) {
								fliInfo.setDiscount(priceDiscount.toString());
							}
							break;
						}

					}
					String flightinfoId = UUID.randomUUID().toString();
					fliInfo.setId(flightinfoId);
					fliInfo.setInputid(flightinputinfoid);
					fliInfo.setDatasource("Tuniu");
					fliInfo.setCreatedate(new Date());
					fliInfo.setCreator("chenwenqi");
					fliInfo.setCreatorid("chenwenqi-13263625152");
					System.out.println(fn + "信息获取完毕！");

					// 获取航班价格拼接参数
					String voyGroupId = flightObject.getString("voyGroupId");
					// 获取航班价格的url
					String priceUrl = "http://flight-api.tuniu.com/open/flight/v0/flightDetails?callback=jQuery&responseId="
							+ responseId + "&groupIds[]=" + voyGroupId + "&_=";
					// 请求航班价格的url
					// 线程睡眠
					// Thread.sleep((long) (Math.random() * 4000 + 4000));
					Map<String, String> map = new HashMap<>();
					map.put("Referer", "http://flight.tuniu.com/domest/list" + flightinputinfo.getBegincity() + "_"
							+ flightinputinfo.getEndcity() + "_ST_1_0_0/?deptDate=" + flightinputinfo.getFlightdate());
					//重复请求5次直到请求成功，5次都失败，直接放弃这个航班
					String result = flightdownload.downloadDetail(priceUrl, map);
					if (StringUtils.isBlank(result)) {
						for (int x = 0; x < 5; x++) {
							result = flightdownload.downloadDetail(priceUrl, map);
							if (StringUtils.isNotBlank(result)) {
								break;
							}
						}
					}
					if (!result.startsWith("/**/ typeof jQuery === 'function' && jQuery({\"success\":true")) {
						continue;
					}
					System.out.println(result);

					// 封装业务对象
					flights.setPageUrl(pageUrl);
					flights.setBeginCity(beginCity);
					flights.setFlightinfo(fliInfo);
					flights.setUrl(priceUrl);
					flights.setContent(result);
					flights.setEndCity(endCity);
					flights.setFlightDate(flightDate);
					flights.setFlightinputinfo(flightinputinfo);
					jedis.add(KEY_FLIGHT, flights);

				}

			} catch (Exception e) {
				jedis.addStr(KEY_URL, flight.getPageUrl());
			}
		}

	}

	/**
	 * 
	 * @Description 去哪儿解析
	 * @author xdl
	 * @date 2017年12月28日 下午4:03:47
	 * @action QunaerParse
	 * @return void
	 */
	public void QunaerParse(Flight flight) {
		WebDriver phantomjs = null;
		try {
			Flightinputinfo flightinputinfo = new Flightinputinfo();

			String pageUrl = flight.getPageUrl();
			String[] arrurl = pageUrl.split("&");
			// 获取出发地
			String[] dep = arrurl[0].split("=");
			String departureAirport = dep[1];
			departureAirport = URLDecoder.decode(departureAirport, "utf-8");
			flightinputinfo.setBegincity(departureAirport);
			// 获取目的地
			String[] arrarr = arrurl[1].split("=");
			String arrivalAirport = arrarr[1];
			arrivalAirport = URLDecoder.decode(arrivalAirport, "utf-8");
			flightinputinfo.setEndcity(arrivalAirport);
			// 获取出发时间
			// 起飞时间
			String[] datearr = arrurl[2].split("=");
			String flightDate = datearr[1];
			flightinputinfo.setFlightdate(flightDate);
			// 当天的航班数
			// 封装其他对象
			String flightinputinfoId = UUID.randomUUID().toString();
			flightinputinfo.setId(flightinputinfoId);
			flightinputinfo.setUrl(pageUrl);
			flightinputinfo.setLastupdatetime(new Date());
			flightinputinfo.setCreatedate(new Date());
			flightinputinfo.setCreator("chenwenqi");
			flightinputinfo.setCreatorid("chenwenqi-13263625152");
			System.out.println(
					"departureAirport" + departureAirport + "arrivalAirport" + arrivalAirport + "date" + flightDate);
			// 获取页面
		   phantomjs = PhantomjsUtils.getPhantomjs(false);
			phantomjs.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			phantomjs.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
			phantomjs.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
			phantomjs.get(pageUrl);
			Thread.sleep(3000);
			String pageSource = phantomjs.getPageSource();
			List<WebElement> findElements = phantomjs.findElements(By.className("b-airfly"));

			// 获取页数
			List<WebElement> linkElement = phantomjs.findElements(By.className("page"));
			int page = linkElement.size();
			List<Flight> flightList = new LinkedList<>();

			for (int pagei = 0; pagei <= page; pagei++) {
				// 点击价格
				for (int i = 0; i < findElements.size(); i++) {
					WebElement webElement = findElements.get(i);
					WebElement e_airfly = webElement.findElement(By.cssSelector("div.air"));
					e_airfly.click();
				}	
				//
					phantomjs.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
					phantomjs.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
					phantomjs.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
					Thread.sleep(3000);
					pageSource = phantomjs.getPageSource();
					Document doc = Jsoup.parse(pageSource);
					// 循环航班
					Elements aHrefElement = doc.select("div.b-airfly");
					for (Element element : aHrefElement) {
						Flight flights = new Flight();
						// 获取航班基本信息
						Flightinfo fliInfo = getQunaerFlightinfo(flightDate, departureAirport, arrivalAirport, element);
						String fliInfoId = UUID.randomUUID().toString();
						fliInfo.setId(fliInfoId);
						fliInfo.setInputid(flightinputinfoId);
						flights.setFlightinfo(fliInfo);
						flights.setBeginCity(departureAirport);
						flights.setEndCity(arrivalAirport);
						flights.setUrl(pageUrl);
						flights.setPageUrl(pageUrl);
						flights.setContent(pageSource);
						List<Flightpriceinfo> priceList = new ArrayList<>();
						// 循环价格
						Elements outerElement = element.select("div.e-ota-outer");
						for (Element oelement : outerElement) {
							Flightpriceinfo priceinfo = new Flightpriceinfo();
							// 价格 prc
							Elements prcElement = oelement.select("div.prc");

							priceinfo.setPrice(prcElement.text());
							// 航班号
							priceinfo.setFlightno(fliInfo.getFlightno());
							// 航班日期
							priceinfo.setFlightdate(flightDate);
							// 舱位类别
							Elements classtypeElement = oelement.select("span.a-sta");
							String classtype = classtypeElement.text();
							if (classtype != null && classtype.length() > 3) {
								String[] arr = classtype.split(" ");
								classtype = arr[0];
							} else if (classtype.equals("")) {
								classtype = "经济舱";
							}
							priceinfo.setId(UUID.randomUUID().toString());
							priceinfo.setFlightid(fliInfoId);
							priceinfo.setClasstype(classtype);
							priceinfo.setCreatedate(new Date());
							priceinfo.setDatasource("Qunar");
							priceinfo.setCreator("陈文奇");
							priceinfo.setCreatorid("chenwenqi-13263625152");
							priceList.add(priceinfo);
							System.out.println(fliInfo.getFlightno() + "价格信息获取完毕！");
						}

						flights.setFlightpriceinfoList(priceList);
						flightList.add(flights);
					}

				}
			
			flightinputinfo.setFlightnum(String.valueOf(flightList.size()));
			for (Flight flight2 : flightList) {
				flight2.setFlightinputinfo(flightinputinfo);
				jedis.add(KEY_FLIGHT, flight2);
			}

		} catch (Exception e) {
			jedis.addStr(KEY_URL, flight.getPageUrl());
		}finally{
			phantomjs.quit();
		}
		
	}

	/**
	 * 
	 * @Description 得到去哪儿航班基本信息
	 * @author 谢大磊
	 * @date 2017年12月28日 下午4:02:18
	 * @action getQunaerFlightinfo
	 * @return Flightinfo
	 */
	public Flightinfo getQunaerFlightinfo(String date, String departureAirport, String arrivalAirport,
			Element element) {
		Flightinfo fliInfo = new Flightinfo();
		try {
			// 获取航班号 fn
			Elements outerElement = element.select("span.n");
			String fn = outerElement.get(0).text();
			System.out.println("航班号 fn" + fn);
			fliInfo.setFlightno(fn);
			// 获取出行日期
			fliInfo.setFlightdate(date);
			System.out.println("出行日期" + date);
			// 获取到达日期 dd
			Elements atElement = element.select("g-tips");
			if (atElement != null && !"".equals(atElement.text())) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
				Date d = sdf.parse(date);
				date = getNextDay(d, 1);
				System.out.println("到达日期 at" + date);
			} else {
				fliInfo.setFlightarrivedate(date);
			}

			// 获取航班型号pt
			String cf = outerElement.get(1).text();
			;
			System.out.println("航班型号cf" + cf);
			fliInfo.setPlane(cf);
			// 出发城市da
			fliInfo.setBegincity(departureAirport);
			// 到达城市aa
			fliInfo.setEndcity(arrivalAirport);
			// 出发时间dt
			Elements dtElement = element.select("div.sep-lf>h2");
			System.out.println("出发时间d" + dtElement.text());
			String dt = dtElement.text();
			fliInfo.setBegintime(dt);
			// 到达时间at sep-rt
			Elements fatElement = element.select("div.sep-rt>h2");
			String fat = fatElement.text();
			System.out.println("到达时间at" + fat);
			fliInfo.setEndtime(fat);
			// 出发航站楼dsmsn airport
			Elements dsmsnElement = element.select("div.sep-lf>p.airport>span");
			String dsmsn = dsmsnElement.get(1).text();
			System.out.println("出发航站楼dsmsn" + dsmsn);
			fliInfo.setBeginterminal(dsmsn);
			// 到达航站楼asmsn
			Elements asmsnElement = element.select("div.sep-rt>p.airport>span");
			String asmsn = asmsnElement.get(1).text();
			System.out.println("到达航站楼asmsn" + asmsn);
			fliInfo.setEndterminal(asmsn);
			// 出发机场dcc
			String dcc = dsmsnElement.get(0).text();
			System.out.println("出发机场dcc" + dcc);
			fliInfo.setBegincode(dcc);
			// 到达机场acc
			String acc = asmsnElement.get(0).text();
			System.out.println("到达机场acc" + acc);
			fliInfo.setEndcode(acc);

			// 最低价格 prc
			Elements prcElement = element.select("span.prc_wp>em.rel>b");

			String prc = prcElement.text();
			if ("".equals(prc)) {

			} else {
				char[] price = prcElement.get(0).text().toCharArray();
				for (int i = 1; i < prcElement.size(); i++) {
					String style = prcElement.get(i).attr("style");
					style = style.substring(style.length() - 4, style.length() - 2);
					String c = prcElement.get(i).text();
					char[] carr = c.toCharArray();
					switch (style) {
					case "18":
						price[price.length - 1] = carr[0];
						break;
					case "36":
						price[price.length - 2] = carr[0];
						break;
					case "54":
						price[price.length - 3] = carr[0];
						break;
					case "72":
						price[0] = carr[0];
						break;
					}
				}
				prc = String.valueOf(price);
				fliInfo.setLowestprice(prc);
			}

			// 折扣 dis
			Elements disElement = element.select("span.dis");
			fliInfo.setDiscount(disElement.text());
			System.out.println("折扣" + disElement.text());
			fliInfo.setDatasource("Qunar");
			fliInfo.setCreatedate(new Date());
			fliInfo.setCreator("陈文奇");
			fliInfo.setCreatorid("chenwenqi-13263625152");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fliInfo;

	}

	/**
	 * 
	 * @Description 携程航班信息解析
	 * @author xdl
	 * @date 2017年12月29日 上午9:26:28
	 * @action CtripParse
	 * @return void
	 */
	public void CtripParse(Flight flight) {
		String pageUrl = flight.getPageUrl();
		String content = flight.getContent();
		String beginCityCode = flight.getPageUrl().split("=")[1].split("&")[0];
		String endCityCode = flight.getPageUrl().split("=")[2].split("&")[0];
		String flightDate = flight.getPageUrl().split("=")[4];
		
		if (StringUtils.isNotBlank(content) && content.startsWith("{")) {
			
			// 转成jsonobject对象
			JSONObject jsonObject=null;
			// 得到当天的航班转机数据
			JSONArray jsonTFArray = null;
			// 得到航班详细信息
			JSONArray jsonArray = null;
			try {
				jsonObject = new JSONObject(content);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(jsonObject!=null){
				
				// 得到航班输入数据
				Flightinputinfo flightinputinfo = new Flightinputinfo();
				String flightinputinfoId = UUID.randomUUID().toString();
				flightinputinfo.setId(flightinputinfoId);
				flightinputinfo.setBegincity(beginCityCode);
				flightinputinfo.setEndcity(endCityCode);
				flightinputinfo.setFlightdate(flightDate);
				flightinputinfo.setCreatedate(new Date());
				flightinputinfo.setCreator("chenwenqi");
				flightinputinfo.setCreatorid("chenwenqi-13263625152");
				flightinputinfo.setUrl(pageUrl);

				List<Transferinfo> transferList = new LinkedList<>();
				
				
				/*try {
					jsonTFArray = jsonObject.getJSONArray("tf");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
				}*/
				try {
					jsonArray = jsonObject.getJSONArray("fis");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(jsonArray.length()!=0){
					if (!Objects.isNull(jsonArray)) {
						flightinputinfo.setFlightnum(String.valueOf(jsonArray.length()));
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jSONObject = jsonArray.getJSONObject(i);
							// 得到每个航班信息和价格信息
							Flight flights=null;
							try {
								flights = getCtripFlightinfo(jSONObject, flightDate);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (flights != null && StringUtils.isNotBlank(flightinputinfoId)) {
								Flightinfo flightinfo = flights.getFlightinfo();
								flightinfo.setInputid(flightinputinfoId);
								// flightinfo
								flights.setFlightinfo(flightinfo);
								flights.setBeginCity(beginCityCode);
								flights.setEndCity(endCityCode);
								flights.setContent(content);
								flights.setUrl(pageUrl);
								flights.setPageUrl(pageUrl);
								// 转机
								flights.setFlighttransferList(transferList);
								flights.setFlightinputinfo(flightinputinfo);
								flights.setFlightDate(flightDate);
								// 机票flight存入缓存中
								jedis.add(KEY_FLIGHT, flights);
							} else {
								
							}
						}
					}
				}else{
					System.out.println("数据响应错误，放回队列");
					jedis.addStr(KEY_URL, flight.getPageUrl());
				}
			}else{
				System.out.println("数据响应错误，放回队列");
				jedis.addStr(KEY_URL, flight.getPageUrl());
			}
			
			// 转机表信息
			/*if (jsonTFArray != null) {
				for (int i = 0; i < jsonTFArray.length(); i++) {
					JSONArray routesArray = jsonTFArray.getJSONObject(i).getJSONArray("Routes");
					if (routesArray != null && routesArray.length() > 0) {
						JSONArray fisArray = routesArray.getJSONObject(0).getJSONArray("fis");
						if (fisArray != null && fisArray.length() > 0) {
							JSONObject dcjSONObject = fisArray.getJSONObject(0);
							Flight dcfliInfo = getCtripFlightinfo(dcjSONObject, flightDate);
							JSONObject acjSONObject = fisArray.getJSONObject(1);
							Flight acfliInfo = getCtripFlightinfo(acjSONObject, flightDate);
							// 定义转机业务对象
							Transferinfo transferInfo = new Transferinfo();
							transferInfo.setBeginFlightinfo(dcfliInfo.getFlightinfo());
							transferInfo.setEndFlightinfo(acfliInfo.getFlightinfo());
							// 得到其他信息
							Flighttransferinfo flighttransferinfo = new Flighttransferinfo();
							String flighttransferinfoId = UUID.randomUUID().toString();
							flighttransferinfo.setId(flighttransferinfoId);
							flighttransferinfo.setInputid(flightinputinfoId);
							// 价格
							int p = (int) jsonTFArray.getJSONObject(i).get("p");
							flighttransferinfo.setMinfare(Integer.toString(p));
							// 折扣
							Object rate = jsonTFArray.getJSONObject(i).get("rate");
							System.out.println(".toString()" + rate.toString());
							flighttransferinfo.setDiscount(rate.toString());
							// 出发日期
							flighttransferinfo.setFlightdate(dcfliInfo.getFlightinfo().getFlightdate());
							// 到达日期
							flighttransferinfo.setFlightarrivedate(acfliInfo.getFlightinfo().getFlightarrivedate());

							flighttransferinfo.setFlightid1(dcfliInfo.getFlightinfo().getId());
							flighttransferinfo.setFlightid2(acfliInfo.getFlightinfo().getId());
							flighttransferinfo.setCreatedate(new Date());
							flighttransferinfo.setCreator("chenwenqi");
							flighttransferinfo.setCreatorid("chenwenqi-13263625152");
							transferInfo.setFlighttransferinfo(flighttransferinfo);
							transferList.add(transferInfo);
						}
					}
				}

			}*/
		}else{
			System.out.println("数据错误，放回来队列");
			jedis.addStr(KEY_URL, flight.getPageUrl());
		}
	}

	/**
	 * 得到携程航班详细信息
	 * 
	 * @param jSONObject
	 * @param date
	 * @return
	 */
	public Flight getCtripFlightinfo(JSONObject jSONObject, String date) {
		
		Flight flight = new Flight();
		Flightinfo fliInfo = new Flightinfo();
		// 判断是否是转高铁/动车 axp
		Object axp = jSONObject.get("axp");
		if (!JSONObject.NULL.equals(axp)) {
			// 转
			return null;
		} else {
			// 非转
			// 获取航班号 fn
			String fn = (String) jSONObject.get("fn");
			System.out.println("航班号 fn" + fn);
			fliInfo.setFlightno(fn);
			// 获取出行日期
			fliInfo.setFlightdate(date);
			System.out.println("出行日期" + date);
			// 获取到达日期 at
			String at = (String) jSONObject.get("at");
			System.out.println("到达日期 at" + at);
			fliInfo.setFlightarrivedate(at);
			// 获取航班型号cf c
			JSONObject cf = jSONObject.getJSONObject("cf");
			String cfc = (String) cf.get("c");
			System.out.println("航班型号cf" + cfc);
			fliInfo.setPlane(cfc);
			// 出发城市dcn
			String dcn = (String) jSONObject.get("dcn");
			System.out.println("出发城市" + dcn);
			fliInfo.setBegincity(dcn);
			// 到达城市acn
			String acn = (String) jSONObject.get("acn");
			System.out.println("到达城市acn" + acn);
			fliInfo.setEndcity(acn);
			// 出发时间dt
			String dt = (String) jSONObject.get("dt");
			System.out.println("出发时间d" + dt);
			fliInfo.setBegintime(dt);
			// 到达时间at
			String fat = (String) jSONObject.get("at");
			System.out.println("到达时间at" + fat);
			fliInfo.setEndtime(fat);
			// 实际用时pft 分钟
			int pft = (int) jSONObject.get("pft");
			System.out.println("实际用时pft" + pft);
			fliInfo.setRealcosttime(Integer.toString(pft));
			// 经停sts
			Object sts = jSONObject.get("sts");
			if (!JSONObject.NULL.equals(sts)) {
				// 经停
				JSONArray stsarr = jSONObject.getJSONArray("sts");
				String cn = (String) stsarr.getJSONObject(0).get("cn");
				System.out.println("经停" + cn);
				fliInfo.setStop(cn);
			}
			// 出发航站楼dsmsn
			String dsmsn = (String) jSONObject.get("dsmsn");
			System.out.println("出发航站楼dsmsn" + dsmsn);
			fliInfo.setBeginterminal(dsmsn);
			// 到达航站楼asmsn
			String asmsn = (String) jSONObject.get("asmsn");
			System.out.println("到达航站楼asmsn" + asmsn);
			fliInfo.setEndterminal(asmsn);
			// 出发机场dcc
			String dcc = (String) jSONObject.get("dcc");
			System.out.println("出发机场dcc" + dcc);
			fliInfo.setBegincode(dcc);
			// 到达机场acc
			String acc = (String) jSONObject.get("acc");
			System.out.println("到达机场acc" + acc);
			fliInfo.setEndcode(acc);
			
			String flightInfoid = UUID.randomUUID().toString();
			// 最低价格scs salep
			JSONArray scs = jSONObject.getJSONArray("scs");
			if (scs != null && scs.length() > 0 && StringUtils.isNotBlank(flightInfoid)) {
				List<Flightpriceinfo> priceList=null;
				try {
					priceList = getCtripFlightPriceList(scs,flightInfoid, fn, date);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (priceList != null && priceList.size() > 0) {
					String salep = priceList.get(0).getPrice();
					fliInfo.setLowestprice(salep);
					// 折扣
					String rt = priceList.get(0).getDiscount();
					fliInfo.setDiscount(rt);
					flight.setFlightpriceinfoList(priceList);
				}
			}
			fliInfo.setId(flightInfoid);
			fliInfo.setDatasource("Ctrip");
			fliInfo.setCreatedate(new Date());
			fliInfo.setCreator("chenwenqi");
			fliInfo.setCreatorid("chenwenqi-13263625152");
			flight.setFlightinfo(fliInfo);
			
			return flight;
		}
	}

	/**
	 * 携程得到单个航班的价格信息
	 * 
	 * @param scs
	 * @param cfc
	 * @param date
	 * @return
	 */
	public List<Flightpriceinfo> getCtripFlightPriceList(JSONArray scs,String flightInfoid, String cfc, String date) {
		List<Flightpriceinfo> priceList = new ArrayList<>();
		try {
			// 价格表
			for (int j = 0; j < scs.length(); j++) {
				JSONObject jsonObject = scs.getJSONObject(j);
				if(jsonObject!=null){
					try {
						Flightpriceinfo priceinfo = new Flightpriceinfo();
						// 价格
						Object objectprice = jsonObject.get("salep");
						
						System.out.println("价格" + objectprice.toString());
						priceinfo.setPrice(objectprice.toString());
						// 折扣
						Object priceDiscount = jsonObject.get("rt");
						if (!priceDiscount.equals("") && priceDiscount != null && !priceDiscount.equals("null")) {
							System.out.println("折扣" + priceDiscount);
							priceinfo.setDiscount((String) priceDiscount);
						}

						// 航班号
						priceinfo.setFlightno(cfc);
						// 航班日期
						priceinfo.setFlightdate(date);
						// 舱位类别 经济舱是Y公务舱是C头等舱F
						String classtype = (String) jsonObject.get("c");
						System.out.println("舱位类别" + classtype);
						switch (classtype) {
						case "Y":
							priceinfo.setClasstype("经济舱");
							break;
						case "C":
							priceinfo.setClasstype("公务舱");
							break;
						case "F":
							priceinfo.setClasstype("头等舱");
							break;
						default:
							break;
						}
						String flightpriceId = UUID.randomUUID().toString();
						priceinfo.setId(flightpriceId);
						priceinfo.setFlightid(flightInfoid);
						priceinfo.setIdentitytype("成人");
						priceinfo.setCreatedate(new Date());
						priceinfo.setDatasource("Ctrip");
						priceinfo.setCreator("chenwenqi");
						priceinfo.setCreatorid("chenwenqi-13263625152");
						priceList.add(priceinfo);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return priceList;
	}

	/**
	 * 
	 * @Description 同程机票解析
	 * @author 谢大磊
	 * @date 2017年12月29日 下午2:25:09
	 * @action TongchengParse
	 * @return void
	 */
	public void TongchengParse(Flight flight) {
		String content = flight.getContent();
		String pageUrl = flight.getPageUrl();
		System.out.println(content);
		try {
			Thread.sleep((long) (Math.random() * 10000 + 10000));
			Flightinputinfo flightinputinfo = new Flightinputinfo();
			// 得到航班输入数据
			String beginCityCode = pageUrl.split("=")[3];
			flightinputinfo.setBegincity(beginCityCode);
			String endCityCode = pageUrl.split("=")[2].split("&")[0];
			flightinputinfo.setEndcity(endCityCode);
			String flightDate = pageUrl.split("=")[1].split("&")[0];
			flightinputinfo.setFlightdate(flightDate);
			String flightinputinfoId = UUID.randomUUID().toString();
			flightinputinfo.setId(flightinputinfoId);
			JSONObject jsonData = new JSONObject(content);
			Integer flightNum = jsonData.getJSONArray("flightinfo").length();
			flightinputinfo.setFlightnum(String.valueOf(flightNum));
			flightinputinfo.setUrl(pageUrl);
			flightinputinfo.setCreatedate(new Date());
			flightinputinfo.setCreator("xiedalei");
			flightinputinfo.setCreatorid("xiedalei-15255178580");

			JSONArray jsonArray = jsonData.getJSONArray("flightinfo");
			for (int i = 0; i < jsonArray.length(); i++) {
				Flightinfo flightinfo = new Flightinfo();
				Flight flights = new Flight();

				JSONObject json = jsonArray.getJSONObject(i);
				// 爬取航班号
				String flightNo = json.getString("flightNo");
				flightinfo.setFlightno(flightNo);
				// 飞行出发日期
				flightDate = json.getString("flyOffTime").split(" ")[0];
				flightinfo.setFlightdate(flightDate);
				// 飞行到达日期
				String flightArriveDate = json.getString("arrivalTime").split(" ")[0];
				flightinfo.setFlightarrivedate(flightArriveDate);
				// 飞机型号
				String plane = "";
				if (json.getString("CraftName") != null || "".equals(json.getString("CraftName"))) {
					plane = json.getString("CraftName");
				}
				flightinfo.setPlane(plane);
				// 飞行城市
				String beginCity = beginCityCode;
				flightinfo.setBegincity(beginCityCode);
				// 到达城市
				flightinfo.setEndcity(endCityCode);
				// 出发时间
				String beginTime = json.getString("flyOffTime").split(" ")[1];
				flightinfo.setBegintime(beginTime);
				// 到达时间
				String arrivalTime = json.getString("arrivalTime").split(" ")[1];
				flightinfo.setEndtime(arrivalTime);
				// 实际用时(计算出来)
				String endDateandTime = json.getString("arrivalTime");
				String beginDateandTime = json.getString("flyOffTime");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				Long seconds = (long) 0;
				try {
					seconds = sdf1.parse(endDateandTime).getTime() - sdf1.parse(beginDateandTime).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
				long nh = 1000 * 60 * 60;// 一小时的毫秒数
				long nm = 1000 * 60;// 一分钟的毫秒数
				long ns = 1000;// 一秒钟的毫秒数long diff;try {

				long day = seconds / nd;// 计算差多少天
				long hour = seconds % nd / nh;// 计算差多少小时
				long min = seconds % nd % nh / nm;// 计算差多少分钟
				String realCostTimeData = day + "d" + hour + "h" + min + "m";
				flightinfo.setRealcosttime(realCostTimeData);
				// 经停(无法爬取经停的城市名,只能爬取当前航班是否有经停)
				String stopData = String.valueOf(json.getInt("stopNum"));
				flightinfo.setStop(stopData);
				// 出发航站楼
				String beginTerminal = json.getString("originAirportShortName") + json.getString("boardPoint");
				flightinfo.setBeginterminal(beginTerminal);
				// 抵达航站楼
				String endTerminal = json.getString("arriveAirportShortName") + json.getString("offPoint");
				flightinfo.setEndterminal(endTerminal);
				// 出发机场代码
				String originAirportCode = json.getString("originAirportCode");
				flightinfo.setBegincode(originAirportCode);
				// 到达机场代码
				String arriveAirportCode = json.getString("arriveAirportCode");
				flightinfo.setEndcode(arriveAirportCode);
				// 尺寸
				String planeType = "";
				if (json.getString("ACPlaneType") != null || "".equals(json.getString("ACPlaneType"))) {
					plane = json.getString("ACPlaneType");
				}
				flightinfo.setSize(planeType);
				// 最低价格暂时无法获取，先把价格表遍历出来,得到最低价格
				List<Integer> priceList = new LinkedList<>();
				// 封装其他信息
				String flightinfoId = UUID.randomUUID().toString();
				flightinfo.setId(flightinfoId);
				flightinfo.setInputid(flightinputinfoId);
				flightinfo.setCreatedate(new Date());
				flightinfo.setCreator("xiedalei");
				flightinfo.setCreatorid("xiedalei-15255178580");
				flightinfo.setDatasource("Tongcheng");

				System.out.println(flightNo + "航班基本数据获取成功！");

				// 得到基建费
				String contructionFee = String.valueOf(json.getInt("pt"));
				// 得到燃油费
				String oilFee = String.valueOf(json.getInt("ot"));
				// 得到航班票价数据
				JSONArray priceArray = json.getJSONArray("cabins");
				List<Flightpriceinfo> priceinfoList = new LinkedList<>();
				for (int j = 0; j < priceArray.length(); j++) {

					JSONObject priceJson = priceArray.getJSONObject(j);
					// 得到航班号(已经有了)
					// 航班日期(已经有)
					// 舱位类别

					String classType = priceJson.getString("roomDes");
					// 基建费(已经有了)
					// 燃油费(已经有了)
					// 备注
					String comment = priceJson.getString("fProductName");
					// 以下是分情况的(根据身份类别的不同)
					// 身份类别(成人票，儿童票，婴儿票)
					String adultType = "成人";
					String childrenType = "儿童";
					String babyType = "婴儿";
					// 价格
					String adultPriceData = String.valueOf(priceJson.getInt("smtp"));
					String childrenPrice = String.valueOf(priceJson.getInt("sctp"));
					String babyPrice = String.valueOf(priceJson.getInt("sbtp"));
					// 折扣
					String discount = priceJson.getString("discountRate");
					// 开始寻找最低价格
					Integer adultPrice = priceJson.getInt("smtp");
					priceList.add(adultPrice);
					// 定义对象
					// 成人票
					Flightpriceinfo flightpriceinfo1 = new Flightpriceinfo();
					String flightpriceinfo1Id = UUID.randomUUID().toString();
					flightpriceinfo1.setId(flightpriceinfo1Id);
					flightpriceinfo1.setFlightid(flightinputinfoId);
					flightpriceinfo1.setFlightno(flightNo);
					flightpriceinfo1.setFlightdate(flightDate);
					flightpriceinfo1.setClasstype(classType);
					flightpriceinfo1.setIdentitytype(adultType);
					flightpriceinfo1.setPrice(adultPriceData);
					flightpriceinfo1.setDiscount(discount);
					flightpriceinfo1.setConstructionfee(contructionFee);
					flightpriceinfo1.setOilfee(oilFee);
					flightpriceinfo1.setComment(comment);
					flightpriceinfo1.setDatasource("Tongcheng");
					flightpriceinfo1.setCreator("xiedalei");
					flightpriceinfo1.setCreatorid("xiedalei-15255178580");
					flightpriceinfo1.setCreatedate(new Date());

					// //儿童票
					Flightpriceinfo flightpriceinfo2 = new Flightpriceinfo();
					String flightpriceinfo2Id = UUID.randomUUID().toString();
					flightpriceinfo2.setId(flightpriceinfo2Id);
					flightpriceinfo2.setFlightid(flightinfoId);
					flightpriceinfo2.setFlightno(flightNo);
					flightpriceinfo2.setFlightdate(flightDate);
					flightpriceinfo2.setClasstype(classType);
					flightpriceinfo2.setIdentitytype(childrenType);
					flightpriceinfo2.setPrice(childrenPrice);
					flightpriceinfo2.setDatasource("Tongcheng");
					flightpriceinfo2.setCreator("xiedalei");
					flightpriceinfo2.setCreatorid("xiedalei-15255178580");
					flightpriceinfo2.setCreatedate(new Date());

					priceinfoList.add(flightpriceinfo1);
					priceinfoList.add(flightpriceinfo2);

					System.out.println(flightNo + "的价格数据获取成功！");

				}
				Integer lowestPrice = priceList.get(0);
				for (int k = 0; k < priceList.size(); k++) {
					if (priceList.get(k) < lowestPrice) {
						lowestPrice = priceList.get(k);
					}
				}
				flightinfo.setLowestprice(String.valueOf(lowestPrice));
				// 封装业务对象
				flights.setBeginCity(beginCityCode);
				flights.setEndCity(endCityCode);
				flights.setContent(content);
				flights.setUrl(pageUrl);
				flights.setPageUrl(pageUrl);
				flights.setFlightinputinfo(flightinputinfo);
				flights.setFlightinfo(flightinfo);
				flights.setFlightpriceinfoList(priceinfoList);
				flights.setFlightDate(flightDate);
				// 存入缓存中
				jedis.add(KEY_FLIGHT, flights);
			}

		} catch (Exception e) {
			jedis.addStr(KEY_URL, pageUrl);
		}
	}

	// 返回某个日期下几天的日期
	public String getNextDay(Date date, int i) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(cal.getTime());
		return time;
	}

}
