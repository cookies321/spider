package cn.jj.service.impl;

import java.io.Serializable;
import java.util.Map;

import cn.jj.entity.Stroke;
import cn.jj.service.IDownLoadService;
import cn.jj.utils.PageDownLoadUtil;
/**
 * 
 * @Description: 下载页面
 * @author 赵乐
 * @date 2017年11月30日 上午11:27:47
 */
public class StrokeDownLoad implements IDownLoadService {
	
	@Override
	public Serializable download(String url,Map<?, ?>... maps) {
		Stroke scenic = new Stroke();
		String content ="";
		if(url.startsWith("https://www.ly.com/go/RainbowClientAjax/GetAladdin")){
			content = PageDownLoadUtil.sendPost(url, "");
			System.out.println(content);
		}else{
			content = PageDownLoadUtil.httpClientGet(url, maps);
		}
		scenic.setContent(content);
		scenic.setUrl(url);
		return scenic;
	}

	

}
