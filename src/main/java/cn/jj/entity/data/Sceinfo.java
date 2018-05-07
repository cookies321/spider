package cn.jj.entity.data;

import java.io.Serializable;
import java.util.Date;

public class Sceinfo implements Serializable{
    private static final long serialVersionUID = 1L;

	private String id;

    private String urlid;

    private String name;

    private String address;

    private String longitude;

    private String latitude;

    private String starlevel;

    private String type;

    private String advicetime;

    private String opentime;

    private String servicecommitment;

    private String referprice;

    private String grade;

    private String gradenum;

    private String beennum;

    private String wanttonum;

    private String datasource;

    private Date createdate;

    private String creator;

    private String creatorid;
    
    private String introduction;

    private String otherinformation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUrlid() {
        return urlid;
    }

    public void setUrlid(String urlid) {
        this.urlid = urlid == null ? null : urlid.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    public String getStarlevel() {
        return starlevel;
    }

    public void setStarlevel(String starlevel) {
        this.starlevel = starlevel == null ? null : starlevel.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getAdvicetime() {
        return advicetime;
    }

    public void setAdvicetime(String advicetime) {
        this.advicetime = advicetime == null ? null : advicetime.trim();
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime == null ? null : opentime.trim();
    }

    public String getServicecommitment() {
        return servicecommitment;
    }

    public void setServicecommitment(String servicecommitment) {
        this.servicecommitment = servicecommitment == null ? null : servicecommitment.trim();
    }

    public String getReferprice() {
        return referprice;
    }

    public void setReferprice(String referprice) {
        this.referprice = referprice == null ? null : referprice.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getGradenum() {
        return gradenum;
    }

    public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getOtherinformation() {
		return otherinformation;
	}

	public void setOtherinformation(String otherinformation) {
		this.otherinformation = otherinformation;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setGradenum(String gradenum) {
        this.gradenum = gradenum == null ? null : gradenum.trim();
    }

    public String getBeennum() {
        return beennum;
    }

    public void setBeennum(String beennum) {
        this.beennum = beennum == null ? null : beennum.trim();
    }

    public String getWanttonum() {
        return wanttonum;
    }

    public void setWanttonum(String wanttonum) {
        this.wanttonum = wanttonum == null ? null : wanttonum.trim();
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource == null ? null : datasource.trim();
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid == null ? null : creatorid.trim();
    }

	@Override
	public String toString() {
		return "HolyrobotSceinfo [id=" + id + ", urlid=" + urlid + ", name=" + name + ", address=" + address
				+ ", longitude=" + longitude + ", latitude=" + latitude + ", starlevel=" + starlevel + ", type=" + type
				+ ", advicetime=" + advicetime + ", opentime=" + opentime + ", servicecommitment=" + servicecommitment
				+ ", referprice=" + referprice + ", grade=" + grade + ", gradenum=" + gradenum + ", beennum=" + beennum
				+ ", wanttonum=" + wanttonum + ", datasource=" + datasource + ", createdate=" + createdate
				+ ", creator=" + creator + ", creatorid=" + creatorid + "]";
	}
    
    
}