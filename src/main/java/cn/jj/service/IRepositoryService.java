package cn.jj.service;
/**
 * 存储url仓库接口
 * @author dajiangtai
 *
 */
public interface IRepositoryService {

	public String poll();
	
	public void addHighLevel(Object obj);
	
	public void addLowLevel(Object obj);
}
