package cn.jj.entity.data.pojo;

import java.util.List;

import cn.jj.entity.data.Pictureinfo;
import cn.jj.entity.data.Sceinfo;
import cn.jj.entity.data.Scepriceinfo;

/**
 * 驴妈妈门票封装对象
 * 
 * @author 徐仁杰
 *
 */
public class LvMaMaTicketPoJo {

	private Sceinfo sceInfo;

	private List<Pictureinfo> imgList;

	private List<Scepriceinfo> priceList;

	public Sceinfo getSceInfo() {
		return sceInfo;
	}

	public void setSceInfo(Sceinfo sceInfo) {
		this.sceInfo = sceInfo;
	}

	public List<Pictureinfo> getImgList() {
		return imgList;
	}

	public void setImgList(List<Pictureinfo> imgList) {
		this.imgList = imgList;
	}

	public List<Scepriceinfo> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Scepriceinfo> priceList) {
		this.priceList = priceList;
	}

	
}
