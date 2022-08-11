package com.github.felixvolo.ts5ai.model;

public enum InjectionPoint {
	HEAD("head"),
	BODY("body");
	
	private final String tagName;
	
	private InjectionPoint(String tagName) {
		this.tagName = tagName;
	}
	
	public String getTagName() {
		return this.tagName;
	}
}
