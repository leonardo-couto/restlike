package com.github.leonardocouto.restlike.parameter;

import javax.servlet.http.HttpServletRequest;

public class PathParameter extends AbstractParameter {
	
	private final int index;
	
	public PathParameter(Class<?> type, String name, int index) {
		super(type, name);
		this.index = index;
	}

	@Override
	protected String extractString(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		return pathInfo.split("/")[this.index];
	}

}
