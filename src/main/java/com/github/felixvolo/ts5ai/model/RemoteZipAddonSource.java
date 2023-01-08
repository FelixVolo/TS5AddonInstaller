package com.github.felixvolo.ts5ai.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

import com.github.felixvolo.ts5ai.util.IOUtils;

public class RemoteZipAddonSource extends AbstractZipAddonSource {
	private final URL url;
	
	public RemoteZipAddonSource(URL url) {
		this.url = url;
	}
	
	@Override
	public void open() throws IOException {
		try {
			URLConnection connection = this.url.openConnection();
			InputStream inputStream = connection.getInputStream();
			File zipPathFile = Files.createTempFile("", "").toFile();
			FileOutputStream outputStream = new FileOutputStream(zipPathFile);
			IOUtils.copy(inputStream, outputStream);
			this.zipFile = new ZipFile(zipPathFile);
		} catch(IOException e) {
			throw new IOException("Could not download or open addon file");
		}
	}
	
	public URL getUrl() {
		return this.url;
	}
	
	@Override
	public void close() throws Exception {
		if(this.zipFile != null) {
			try {
				this.zipFile.close();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				Files.deleteIfExists(Paths.get(this.zipFile.getName()));
			}
		}
	}
}
