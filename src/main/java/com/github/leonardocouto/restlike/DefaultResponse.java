package com.github.leonardocouto.restlike;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import flexjson.JSONSerializer;

public class DefaultResponse implements Response {
	
	private final Object object;
	
	public DefaultResponse() {
		this(null);
	}
	
	public DefaultResponse(Object object) {
		this.object = object;
	}
	
	public String serialize() {
		if (this.object == null) {
			return null;
		}
		
		JSONSerializer serializer = new JSONSerializer();
		serializer.exclude("class");
		return serializer.serialize(this.object);
	}
	
	public void commit(HttpServletResponse response) throws IOException {
		String serialized = this.serialize();
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		if (serialized != null) {
			PrintWriter writer = response.getWriter();
			writer.print(serialized);
			writer.close();
		}
	}

}
