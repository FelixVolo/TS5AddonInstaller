package com.github.felixvolo.ts5ai.model;

public interface Injector {
	String inject(String targetString, String injectionString, InjectionPoint injectionPoint);
}
