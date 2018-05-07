package cn.jj.entity.data.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
* Title: CmtLatitudespojo 目的地 景点 评论中的评分
* Description
* @author 赵乐
* @Date 2017年11月7日 上午11:51:27
 */
@JsonIgnoreProperties(ignoreUnknown = true)//vo中缺少json的某个字段属性
public class CmtLatitudespojo {
	private String latitudeName;//评分类别
	private String score;//评分
	
	public String getLatitudeName() {
		return latitudeName;
	}
	public void setLatitudeName(String latitudeName) {
		this.latitudeName = latitudeName;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "CmtLatitudespojo [latitudeName=" + latitudeName + ", score=" + score + "]";
	}
	
	
	
}
