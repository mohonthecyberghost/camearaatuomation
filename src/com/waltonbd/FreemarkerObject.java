package com.waltonbd;

public class FreemarkerObject {
	private String name;
	private String info;
	private String image;
	
	public FreemarkerObject(String name, String info, String image) {
	    this.name = name;
	    this.info = info;
	    this.image = image;
	}
	
	public String getName() {
	    return name;
	}
	
	public String getInfo() {
	    return info;
	}
	
	public String getImage() {
	    return image;
	}
}
