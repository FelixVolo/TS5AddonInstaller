package com.github.felixvolo.ts5ai.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final char[] HEX_ALPHABET = "0123456789ABCDEF".toCharArray();
	
	public static String validate(String input, String regex) throws Exception {
		if(!input.matches(regex)) {
			throw new Exception(input + " does not match pattern " + regex);
		}
		return input;
	}
	
	public static String md5sum(File file) throws FileNotFoundException, IOException {
		try {
			MessageDigest md5Digest = MessageDigest.getInstance("MD5");
			try(FileInputStream input = new FileInputStream(file)) {
				byte[] buffer = new byte[8192];
				int length = 0;
				while((length = input.read(buffer)) != -1) {
					md5Digest.update(buffer, 0, length);
				}
			}
			byte[] digest = md5Digest.digest();
			return bytesToHex(digest).toLowerCase();
		} catch(NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not get md5 algorithm");
		}
	}
	
	private static String bytesToHex(byte[] bytes) {
		StringBuilder builder = new StringBuilder(bytes.length * 2);
		for(byte b : bytes) {
			builder.append(HEX_ALPHABET[(b >> 4) & 0xF]);
			builder.append(HEX_ALPHABET[(b & 0xF)]);
		}
		return builder.toString();
	}
}
