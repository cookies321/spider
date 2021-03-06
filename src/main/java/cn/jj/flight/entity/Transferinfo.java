package cn.jj.flight.entity;

import java.io.Serializable;

import cn.jj.entity.data.Flightinfo;
import cn.jj.entity.data.Flighttransferinfo;

public class Transferinfo implements Serializable{
  private static final long serialVersionUID = 1L;
  private Flightinfo beginFlightinfo;
  private Flightinfo endFlightinfo;
  private Flighttransferinfo flighttransferinfo;
public Flightinfo getBeginFlightinfo() {
	return beginFlightinfo;
}
public void setBeginFlightinfo(Flightinfo beginFlightinfo) {
	this.beginFlightinfo = beginFlightinfo;
}
public Flightinfo getEndFlightinfo() {
	return endFlightinfo;
}
public void setEndFlightinfo(Flightinfo endFlightinfo) {
	this.endFlightinfo = endFlightinfo;
}
public Flighttransferinfo getFlighttransferinfo() {
	return flighttransferinfo;
}
public void setFlighttransferinfo(Flighttransferinfo flighttransferinfo) {
	this.flighttransferinfo = flighttransferinfo;
}
}
