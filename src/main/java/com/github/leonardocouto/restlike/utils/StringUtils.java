package com.github.leonardocouto.restlike.utils;

public class StringUtils {
	
	public static String join(String ... tokens) {
		return join(",", tokens);
	}

	public static String join(String separator, String ... tokens) {
		if (tokens.length == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < tokens.length; i++) {
			builder.append(tokens[i-1]);
			builder.append(separator);
		}
		
		return builder.append(tokens[tokens.length - 1]).toString();
	}

}
