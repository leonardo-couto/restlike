package com.github.leonardocouto.restlike.parameter;

import javax.servlet.http.HttpServletRequest;

public class InjectParamater extends AbstractParameter {
	
	public InjectParamater() {
		super(HttpServletRequest.class, "HttpServletRequest");
	}
	
	@Override
	public HttpServletRequest extract(HttpServletRequest request) {
		return request;
	}

}
