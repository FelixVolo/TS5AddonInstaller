package com.github.felixvolo.ts5ai.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class RemoteAddonSource implements IAddonSource {
	private final String name;
	private final URI url;
	
	public RemoteAddonSource(String name, URI url) {
		this.name = name;
		this.url = url;
	}
	
	@Override
	public Path setupZipPath(String zipPath) throws IOException {
		try {
			return Files.createTempFile("", "");
		} catch(IOException e) {
			throw new IOException("Could not initialize installation process");
		}
	}
	
	@Override
	public ZipFile openZipFile(Path zipPath) throws IOException {
		try {
			URLConnection connection = this.url.toURL().openConnection();
			InputStream inputStream = connection.getInputStream();
			File zipPathFile = zipPath.toFile();
			FileOutputStream outputStream = new FileOutputStream(zipPathFile);
			byte[] buffer = new byte[8192];
			int length;
			while((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			inputStream.close();
			outputStream.close();
			return new ZipFile(zipPathFile);
		} catch(IOException e) {
			throw new IOException("Could not download or open addon file");
		}
	}
	
	@Override
	public void cleanupZipPath(Path zipPath) {
		try {
			Files.deleteIfExists(zipPath);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public URI getUrl() {
		return this.url;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
