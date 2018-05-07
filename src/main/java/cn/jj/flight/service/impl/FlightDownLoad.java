package cn.jj.flight.service.impl;


import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;

import cn.jj.flight.entity.Flight;
import cn.jj.service.IDownLoadService;
import cn.jj.utils.PageDownLoadUtil;
import cn.jj.utils.PhantomjsUtils;

public class FlightDownLoad implements IDownLoadService {

	@Override
	public Serializable download(String url, Map<?, ?>... maps) {
		Flight flight =  new Flight();
		if(url.startsWith("https://flight.qunar.com/site/oneway_list.htm")){
			flight.setPageUrl(url);
			flight.setContent("OK!");
			return flight;
		}else if(url.startsWith("https://m.ly.com/flightnew/")){
			return httpClientDefultGet(url, maps);
		}else{
			String content =  PageDownLoadUtil.httpClientGet(url,maps);
			flight.setPageUrl(url);
			flight.setContent(content);
			return flight;
		}
	}
	public String  downloadDetail(String url,Map<?, ?>... maps){
		String content =  PageDownLoadUtil.httpClientGet(url,maps);
		return content;
	}
	public Serializable httpClientDefultGet(String url,Map<?, ?>... maps){
		Flight flight =  new Flight();
		String content = PageDownLoadUtil.httpClientDefultGet(url, maps);
		flight.setPageUrl(url);
		flight.setContent(content);
		return flight;
	}
}
