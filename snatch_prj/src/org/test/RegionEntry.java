package org.test;

import java.util.ArrayList;
import java.util.List;

/**
* 
* @author zzy 2017年8月29日
* @version
*/
public class RegionEntry {
	private String name;
	private String code;
	
	private List<RegionEntry> subList = new ArrayList<>();
	public List<RegionEntry> getSubList() {
		return subList;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
	public void setName(String text) {
		this.name = text;
	}
	  

}
