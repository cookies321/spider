package cn.jj.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import cn.jj.Config;
import cn.jj.quartz.ProxyJob;
import redis.clients.jedis.Jedis;

public class PageDownLoadUtil {

	private static RedisUtil redisUtil = RedisUtil.getInstance();

	/**
	 * @Description 发送get请求
	 * @author 徐仁杰
	 * @date 2017年11月24日 上午9:19:13
	 * @action httpClientGet
	 * @return String
	 */
	public static String httpClientGet(String url, Map<?, ?>... maps) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String key = "";
		try {
			HttpGet get = new HttpGet(url);
			/******************* 设置代理IP ******************/
			key=getProxyIpPort();
			RequestConfig config = getRequestConfig(key);
			/******************* 设置代理IP ******************/
			get.setConfig(config);
			setGetHeaders(get, maps);
			CloseableHttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			return result;
		} catch (Exception e) {
			//e.printStackTrace();
			try {
				redisUtil.del(key.toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		} finally {
			try {
				httpClient.close();
				httpClient.getConnectionManager().shutdown();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * @Description 本地连接get
	 * @author 徐仁杰
	 * @date 2017年12月13日 下午4:58:09
	 * @action httpClientDefultGet
	 * @return String
	 */
	public static String httpClientDefultGet(String url, Map<?, ?>... maps) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		StringBuffer key = new StringBuffer();
		try {
			HttpGet get = new HttpGet(url);
			setGetHeaders(get, maps);
			CloseableHttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			return result;
		} catch (Exception e) {
			redisUtil.del(key.toString());
			return null;
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @Description 代理ip链接post 参数为json
	 * @author 赵乐
	 * @date 2017年12月13日 下午4:58:09
	 * @action httpClientDefultGet
	 * @return String
	 */
	public static String sendPost(String url, String param,Map<?, ?>... maps) {
		String key = "";
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer();
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn =null;
			
			key = getProxyIpPort();
			if (StringUtils.isNotBlank(key)) {
				String replace = key.replace(Config.PROXY_IP_REDIS_KEY, "");
				String[] arr = replace.split(":");
				String proxy_ip = arr[0];
				int proxy_port = Integer.parseInt(arr[1]);
				InetSocketAddress addr = new InetSocketAddress(proxy_ip,proxy_port);  
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理  
				conn=realUrl.openConnection(proxy);
				// 设置通用的请求属性
			}else{
				conn=realUrl.openConnection();
			}
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36"
					+ " (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			for (Map<?, ?> map : maps) {
				Iterator<?> iterator = map.keySet().iterator();
				while (iterator.hasNext()) {
					String next = (String) iterator.next();
					String object = (String) map.get(next);
					conn.setRequestProperty(next, object);
				}
			}
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line=null;
			while ((line = in.readLine())!= null) {
				result.append(line);
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			System.out.println(url + "\n" + param);
			//e.printStackTrace();
			redisUtil.del(key.toString());
			return "";
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				//ex.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * @Description 发送post请求
	 * @author 徐仁杰
	 * @date 2017年12月13日 下午2:56:20
	 * @action post
	 * @return String
	 */
	public static String post(String url, String param, Map<?, ?>... maps) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String key = "";
		try {
			HttpPost post = new HttpPost(url);
			/******************* 设置代理IP ******************/
			key=getProxyIpPort();
			RequestConfig config = getRequestConfig(key);
			/******************* 设置代理IP ******************/
			post.setConfig(config);
			setPostHeaders(post, maps);
			StringEntity stringEntity = new StringEntity(param, "UTF-8");
			post.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			return result;
		} catch (Exception e) {
			redisUtil.del(key.toString());
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * @Description 获取httpclient上下文
	 * @author 徐仁杰
	 * @date 2017年12月13日 下午2:11:18
	 * @action getRequestConfig
	 * @return RequestConfig
	 */
	public static RequestConfig getRequestConfig(String key) {
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(2000).setConnectTimeout(2000)
				.setSocketTimeout(2000).build();
		if (StringUtils.isNotBlank(key)) {
			String replace = key.replace(Config.PROXY_IP_REDIS_KEY, "");
			String[] arr = replace.split(":");
			String proxy_ip = arr[0];
			int proxy_port = Integer.parseInt(arr[1]);
			HttpHost proxy = new HttpHost(proxy_ip, proxy_port);
			config = RequestConfig.custom().setProxy(proxy).setConnectionRequestTimeout(2000).setConnectTimeout(2000).setSocketTimeout(2000).build();
		}
		return config;
	}

	/**
	 * @Description 获取代理IP
	 * @author 徐仁杰
	 * @date 2017年12月15日 上午11:38:53
	 * @action getProxyIpPort
	 * @return String
	 */
	public static String getProxyIpPort() {
		Jedis jedis = RedisProxyUtil.getProxyJedisPool();
		Set<String> keys = jedis.keys(Config.PROXY_IP_REDIS_KEY + "*");
		jedis.close();
		String ip_port = null;
		List<String> list = new ArrayList<>();
		for (String string : keys) {
			list.add(string);
		}
		for (int i = 0; i < list.size();) {
			ip_port = list.get(NumUtils.getRandom(list.size(), 0));
			break;
		}
		return ip_port;
	}

	/**
	 * @Description 设置get请求头部信息
	 * @author 徐仁杰
	 * @date 2017年12月13日 下午3:16:36
	 * @action setGetHeaders
	 * @return void
	 */
	public static void setGetHeaders(HttpGet get, Map<?, ?>... maps) {
		get.setHeader("User-Agent", HeadersUtils.getUserAgent());
		for (Map<?, ?> map : maps) {
			Iterator<?> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String next = (String) iterator.next();
				String object = (String) map.get(next);
				get.setHeader(next, object);
			}
		}
	}

	/**
	 * @Description 设置post请求头部信息
	 * @author 徐仁杰
	 * @date 2017年12月13日 下午3:16:36
	 * @action setGetHeaders
	 * @return void
	 */
	public static void setPostHeaders(HttpPost post, Map<?, ?>... maps) {
		post.setHeader("User-Agent", HeadersUtils.getUserAgent());
		for (Map<?, ?> map : maps) {
			Iterator<?> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String next = (String) iterator.next();
				String object = (String) map.get(next);
				post.setHeader(next, object);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		
		String url = "http://api.trip258.com/hotelapi.ashx";
		String hotelAction="gethotellist";
		String markplaceAction="getmarkplace";
		
		String companycode="GX1222";
		String key="529d06f385e44976adf2db034f916db6";
		String cityid="102";
		
		String bdate="2018-01-31";
		String edate="2018-02-01";
		String pagecurrent="1";
		String pagesize="10";
		
		String count="10";
		String md5 = MD5Util.getMD5(hotelAction+companycode+key+cityid+bdate+edate+pagecurrent+pagesize);
		
		String markplaceMd5=MD5Util.getMD5(markplaceAction+companycode+key+cityid+count);
		System.out.println(markplaceMd5);
		String param ="param={\"action\":\"getmarkplace\",\"companycode\":\"GX1222\",\"key\":\"529d06f385e44976adf2db034f916db6\",\"sign\":\"85481a3fdb960b26e6310ddb1c6f1668\",\"cityid\":102,\"count\":\"10\"}";
		String str = sendPost(url,param);
		System.out.println(str);
		//String params ="param={"action":"gethotellist","companycode":"GX1222","key":"529d06f385e44976adf2db034f916db6","sign":"fab346a840b6490dec665018f57ae25c","cityid":102,"bdate":"2018-01-31","edate":"2018-02-01","pagecurrent":"1","pagesize":"10"}";
	}
}
