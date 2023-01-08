package com.github.felixvolo.ts5ai.model;

import java.io.IOException;
import java.util.Iterator;

public interface IAddonSource extends AutoCloseable {
	void open() throws IOException;
	
	String read(String path) throws IOException;
	
	Iterator<String> entries() throws IOException;
}
