package com.github.felixvolo.ts5ai.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.felixvolo.ts5ai.util.Util;

public class Addon {
	private final String name;
	private final String id;
	private final String version;
	private final String inject;
	private final InjectionPoint injectionPoint;
	private final InjectAt injectAt;
	private final String sources;
	
	public Addon(@JsonProperty("name") String name, @JsonProperty("id") String id, @JsonProperty("version") String version, @JsonProperty("inject") String inject, @JsonProperty("injection_point") InjectionPoint injectionPoint, @JsonProperty("inject_at") InjectAt injectAt, @JsonProperty("sources") String sources) throws Exception {
		this.name = Objects.requireNonNull(name, "Missing addon name");
		this.id = Util.validate(Objects.requireNonNull(id, "Missing addon id"), "[A-Za-z_0-9]+");
		this.version = Util.validate(Objects.requireNonNull(version, "Missing addon version"), "(?:\\d+\\.|\\d)*\\d");
		this.inject = Objects.requireNonNull(inject, "Missing addon patches");
		this.injectionPoint = injectionPoint != null ? injectionPoint : InjectionPoint.HEAD;
		this.injectAt = injectAt != null ? injectAt : InjectAt.TAIL;
		this.sources = Objects.requireNonNull(sources, "Missing addon sources");
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getInject() {
		return this.inject;
	}
	
	public InjectionPoint getInjectionPoint() {
		return this.injectionPoint;
	}
	
	public InjectAt getInjectAt() {
		return this.injectAt;
	}
	
	public String getSources() {
		return this.sources;
	}
}