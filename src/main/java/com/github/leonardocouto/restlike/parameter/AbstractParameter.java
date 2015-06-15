package com.github.leonardocouto.restlike.parameter;

import javax.servlet.http.HttpServletRequest;

import flexjson.JSONDeserializer;

abstract class AbstractParameter implements ParameterExtractor {
	
	private static final JSONDeserializer<Object> DESERIALIZER = new JSONDeserializer<Object>();
	
	protected final Class<?> type;
	protected final String name;
	
	public AbstractParameter(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}
	
	@Override
	public Object extract(HttpServletRequest request) {
		String str = this.extractString(request);
		return DESERIALIZER.deserialize(str, type);
	}
	
	protected String extractString(HttpServletRequest request) {
		return null;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
//	public static void main(String[] args) {
//		System.out.println(DESERIALIZER.deserialize("/aaaa", String.class));
//		
//	}

}
