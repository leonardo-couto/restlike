package com.github.leonardocouto.restlike;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public interface Response {
	
	public void commit(HttpServletResponse response) throws IOException;

}
