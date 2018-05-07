package cn.jj.entity.data.pojo;

/**
 * 
* Title: SceinfoCountPojo 目的地 景点 想去和去过实体
* Description
* @author 赵乐
* @Date 2017年11月7日 上午11:27:58
 */

public class SceinfoCountPojo {
	private String code;
	private String message;
	private SceinfoDataPojo data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public SceinfoDataPojo getData() {
		return data;
	}
	public void setData(SceinfoDataPojo data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "SceinfoCountPojo [code=" + code + ", message=" + message + ", data=" + data + "]";
	}
	
	
	
	
	
}
