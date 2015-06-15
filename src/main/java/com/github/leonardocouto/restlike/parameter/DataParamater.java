package com.github.leonardocouto.restlike.parameter;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class DataParamater extends AbstractParameter {
	
	public DataParamater(Class<?> type) {
		super(type, "data");
	}

	@Override
	protected String extractString(HttpServletRequest request) {
		try {
			StringBuilder data = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				data.append(line);
			}
			return data.toString();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
