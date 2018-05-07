package cn.jj.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.jj.entity.Hotel;
import cn.jj.service.IDownLoadService;
import cn.jj.utils.PageDownLoadUtil;
/**
 * 
 * @Description: 下载页面
 * @author 赵乐
 * @date 2017年11月30日 上午11:27:47
 */
public class HotelDownLoad implements IDownLoadService {
	
	@Override
	public Serializable download(String url,Map<?, ?>... maps) {
		Hotel hotel=new Hotel();
		String content="";
		//判断是否是途牛的网站，途牛的网站请求需要header头并且是post
		if(url.contains("https://www.ly.com/hotel/api/search")){
			String param=url.substring(url.indexOf("?")+1, url.length());
			String postUrl=url.substring(0, url.indexOf("?"));
			content = PageDownLoadUtil.post(postUrl, param, maps);
		}else if(url.startsWith("https://touch.qunar.com/api/hotel/hotellist?city=")){
			Map<String,String> map=new HashMap<>();
			map.put("Cookie", "N99=8972; QunarGlobal=10.86.213.140_-403384e3_160c45cd02c_dc3|1515124225903; QN601=d5821be1ff2c82b39043e1aa4efd6c7f; QN269=B4E34791F06011E78EBFFA163E244083; QN48=tc_f372c7ef233ec0b2_160c470ffb0_8a66; QN235=2018-01-05; Qs_lvt_55613=1515124292; Qs_pv_55613=4194832815894949000; Hm_lvt_8fa710fe238aadb83847578e333d4309=1515124293; QN243=4; QN57=15156610331480.6540962676722188; QN58=1515661033147%7C1515661041570%7C3; QN73=3030-3031; QN668=51%2C55%2C51%2C55%2C59%2C58%2C57%2C52%2C54%2C59%2C53%2C56%2C51; QN621=fr%3Dtouch_index; csrfToken=XGs4aHxWFZ45LmTE5gydfJgHPxSVSViS; QN205=auto_4e0d874a; QN277=auto_4e0d874a; QN163=0; Hm_lvt_75154a8409c0f82ecd97d538ff0ab3f3=1516069404,1516071764,1516082154,1516099379; Hm_lpvt_75154a8409c0f82ecd97d538ff0ab3f3=1516099379; QN6=auto_4e0d874a; QN70=13a064252160fe90a272; _i=RBTjefyhUPexBHJR67qH1K8L8xqx; _vi=dvhpiZ8bVCQrKPn9hb2y6Or2I3SP1nAwyDzMCih1AQQDJ9rM9gpFyBnlALKSKEE9V3BqKEnbLUrxcTIbnHHDUv0260KJ8l8oRYcMo2s_XdNE4vvhKlcCHiH88j19A5TnHUZuQP9ms4G3icfgsm2b8902pSJeZmA2u75gWPuVt-es; __utma=183398822.1481523103.1515124235.1515979197.1516099382.3; __utmc=183398822; __utmz=183398822.1515124235.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _jzqa=1.2306119616918587400.1515124237.1515979199.1516099384.3; _jzqc=1; _jzqx=1.1515124237.1516099384.3.jzqsr=hotel%2Equnar%2Ecom|jzqct=/.jzqsr=hotel%2Equnar%2Ecom|jzqct=/; _jzqckmp=1; QN66=3w; QN300=3w; Hm_lvt_45585c4dc07dfd48a91539497071518d=1516083926; Hm_lpvt_45585c4dc07dfd48a91539497071518d=1516099413; QN25=7ee73fb5-49c7-4a7e-a457-30894960935a-9f992f90; QN1=dXrgjVpd11xGP+t6BF7nAg==; _RF1=180.154.197.128; _RSG=qI4eF_QAcRF5WCiF31Pl8A; _RDG=2899a69c7a3b652e91325384a7d6570622; _RGUID=f6e629af-cfa0-44b4-a22f-6410cfb0c805");
			content = PageDownLoadUtil.httpClientGet(url, map);
		}else{
			content = PageDownLoadUtil.httpClientGet(url, maps);
		}
		hotel.setContent(content);
		hotel.setUrl(url);
		return hotel;
	}

	

}
