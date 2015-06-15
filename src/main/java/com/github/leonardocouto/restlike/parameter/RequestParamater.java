package com.github.leonardocouto.restlike.parameter;

import javax.servlet.http.HttpServletRequest;

public class RequestParamater extends AbstractParameter {
	
	public RequestParamater(Class<?> type, String name) {
		super(type, name);
	}

	@Override
	protected String extractString(HttpServletRequest request) {
		return request.getParameter(this.name);
	}

}
