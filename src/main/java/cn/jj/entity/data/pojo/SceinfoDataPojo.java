package cn.jj.entity.data.pojo;
/**
 * 
* Title: SceinfoDataPojo
* Description
* @author 赵乐
* @Date 2017年11月7日 上午11:27:48
 */
public class SceinfoDataPojo {
	private String count_been;
	private String count_want;
	
	public String getCount_been() {
		return count_been;
	}
	public void setCount_been(String count_been) {
		this.count_been = count_been;
	}
	public String getCount_want() {
		return count_want;
	}
	public void setCount_want(String count_want) {
		this.count_want = count_want;
	}
	
	@Override
	public String toString() {
		return "SceinfoDataPojo [count_been=" + count_been + ", count_want=" + count_want + "]";
	}
	
	

}
