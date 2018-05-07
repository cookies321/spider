package cn.jj.entity.data.pojo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
* Title: CommenPojo 目的地 景点 评论实体
* Description
* @author 赵乐
* @Date 2017年11月7日 上午11:37:37
 */
@JsonIgnoreProperties(ignoreUnknown = true)//vo中缺少json的某个字段属性
public class CommenPojo {
	private String  content;//评论内容
	private String  pinYinUrl;//图片链接
	private String formatterCreatedDate;//评论日期
	private List<CmtLatitudespojo> cmtLatitudes;//评分类
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPinYinUrl() {
		return pinYinUrl;
	}
	public void setPinYinUrl(String pinYinUrl) {
		this.pinYinUrl = pinYinUrl;
	}
	public String getFormatterCreatedDate() {
		return formatterCreatedDate;
	}
	public void setFormatterCreatedDate(String formatterCreatedDate) {
		this.formatterCreatedDate = formatterCreatedDate;
	}
	public List<CmtLatitudespojo> getCmtLatitudes() {
		return cmtLatitudes;
	}
	public void setCmtLatitudes(List<CmtLatitudespojo> cmtLatitudes) {
		this.cmtLatitudes = cmtLatitudes;
	}
	@Override
	public String toString() {
		return "CommenPojo [content=" + content + ", pinYinUrl=" + pinYinUrl + ", formatterCreatedDate="
				+ formatterCreatedDate + ", cmtLatitudes=" + cmtLatitudes + "]";
	}
	
	
	
	
	
}
