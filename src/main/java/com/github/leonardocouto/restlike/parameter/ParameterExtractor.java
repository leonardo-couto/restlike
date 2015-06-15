package com.github.leonardocouto.restlike.parameter;

import javax.servlet.http.HttpServletRequest;

public interface ParameterExtractor {
	
	public Object extract(HttpServletRequest request);
	public String getName();
	
}
