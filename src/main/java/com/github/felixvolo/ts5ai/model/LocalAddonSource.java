package com.github.felixvolo.ts5ai.model;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

public class LocalAddonSource implements IAddonSource {
	private final String name;
	
	public LocalAddonSource(String name) {
		this.name = name;
	}
	
	@Override
	public Path setupZipPath(String zipPath) throws IOException {
		try {
			return Paths.get(zipPath);
		} catch(InvalidPathException e) {
			throw new IOException("Invalid addon file path");
		}
	}
	
	@Override
	public ZipFile openZipFile(Path zipPath) throws IOException {
		try {
			return new ZipFile(zipPath.toFile());
		} catch(Exception e) {
			throw new IOException("Could not open selected addon file");
		}
	}
	
	@Override
	public void cleanupZipPath(Path zipPath) {}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
