package cn.jj.service.impl;

import java.io.Serializable;
import java.util.Map;

import cn.jj.entity.Scenic;
import cn.jj.service.IDownLoadService;
import cn.jj.utils.PageDownLoadUtil;
/**
 * 
 * @Description: 下载页面
 * @author 赵乐
 * @date 2017年11月30日 上午11:27:47
 */
public class ScenicDownLoad implements IDownLoadService {
	
	@Override
	public Serializable download(String url,Map<?, ?>... maps) {
		Scenic ticket=new Scenic();
		String content = PageDownLoadUtil.httpClientGet(url, maps);
		ticket.setContent(content);
		ticket.setUrl(url);
		return ticket;
	}

	

}
