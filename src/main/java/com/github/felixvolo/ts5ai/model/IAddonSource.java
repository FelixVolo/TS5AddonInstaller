package com.github.felixvolo.ts5ai.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public interface IAddonSource {
	Path setupZipPath(String zipPath) throws IOException;
	
	ZipFile openZipFile(Path zipPath) throws IOException;
	
	void cleanupZipPath(Path zipPath);
	
	String getName();
}
