package com.github.felixvolo.ts5ai.model;

public class InstalledAddon {
	private final String id;
	private final String name;
	private final String version;
	private final int startIndex;
	private final int endIndex;
	
	public InstalledAddon(String id, String name, String version, int startIndex, int endIndex) {
		this.name = name;
		this.id = id;
		this.version = version;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public int getStartIndex() {
		return this.startIndex;
	}
	
	public int getEndIndex() {
		return this.endIndex;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
