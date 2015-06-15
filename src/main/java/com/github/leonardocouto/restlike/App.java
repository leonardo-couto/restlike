package com.github.leonardocouto.restlike;

import static com.github.leonardocouto.restlike.reflection.AddressableMethod.HttpMethod.DELETE;
import static com.github.leonardocouto.restlike.reflection.AddressableMethod.HttpMethod.GET;
import static com.github.leonardocouto.restlike.reflection.AddressableMethod.HttpMethod.POST;
import static com.github.leonardocouto.restlike.reflection.AddressableMethod.HttpMethod.PUT;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.leonardocouto.restlike.reflection.AddressableMethod;
import com.github.leonardocouto.restlike.reflection.AddressableMethod.HttpMethod;

public class App extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	private final List<AddressableMethod> methods;
	
	public App() {
		this.methods = new ArrayList<AddressableMethod>();
		for (Method m : this.getClass().getMethods()) {
		
			GET get = m.getAnnotation(GET.class);
			POST post = m.getAnnotation(POST.class);
			PUT put = m.getAnnotation(PUT.class);
			DELETE delete = m.getAnnotation(DELETE.class);
			
			if (get != null) {
				methods.add(new AddressableMethod(get.value(), m, GET));
			} else if (post != null) {
				methods.add(new AddressableMethod(post.value(), m, POST));
			} else if (put != null) {
				methods.add(new AddressableMethod(put.value(), m, PUT));
			} else if (delete != null) {
				methods.add(new AddressableMethod(delete.value(), m, DELETE));
			}
		}
		
		Collections.sort(this.methods);
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod type) throws ServletException {
		try {
			for (AddressableMethod method : this.methods) {
				if (method.accept(type, req)) {
					Object output = method.execute(this, req);
					Response response = (output instanceof Response) ? (Response) output : new DefaultResponse(output);
					response.commit(resp);
					return;
				}
			}
			
			resp.sendError(404);
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handleRequest(req, resp, DELETE);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handleRequest(req, resp, GET);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handleRequest(req, resp, POST);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.handleRequest(req, resp, PUT);
	}




}
