package com.github.leonardocouto.restlike.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.github.leonardocouto.restlike.Context;
import com.github.leonardocouto.restlike.Param;
import com.github.leonardocouto.restlike.parameter.DataParamater;
import com.github.leonardocouto.restlike.parameter.InjectParamater;
import com.github.leonardocouto.restlike.parameter.ParameterExtractor;
import com.github.leonardocouto.restlike.parameter.PathParameter;
import com.github.leonardocouto.restlike.parameter.RequestParamater;
import com.github.leonardocouto.restlike.utils.StringUtils;

public class AddressableMethod implements Comparable<AddressableMethod> {
	
	private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{\\w+\\}");
	
	private final String path;
	private final Method method;
	private final Pattern pattern;
	private final HttpMethod type;
	private final ParameterExtractor[] parameters;
	
	public AddressableMethod(String path, Method method, HttpMethod type) {
		this.path = path;
		this.method = method;
		this.type = type;
		
		ProcessedPath processedPath = processPath(path);
		
		this.pattern = processedPath.pattern;
		this.parameters = processParameters(method, processedPath.pathParameters);
	}
	
	public boolean accept(HttpMethod type, HttpServletRequest request) {
		if (this.type != type) return false;
		String path = request.getPathInfo();
		if (path == null) return false;
		
		return pattern.matcher(path).matches();
	}
	
	public Object execute(HttpServlet servlet, HttpServletRequest request) throws ReflectiveOperationException {
		Object[] params = new Object[this.parameters.length];
		for (int i = 0; i < this.parameters.length; i++) {
			params[i] = this.parameters[i].extract(request);
		}
		
		return method.invoke(servlet, params);
	}

	@Override
	public int compareTo(AddressableMethod o) {
		return this.path.compareTo(o.path);
	}
	
	@Override
	public String toString() {
		return this.path;
	}
	
	private static ParameterExtractor[] processParameters(Method method, Map<String, Integer> pathParameters) {
		
		ReflectedParameter[] parameters = ReflectedParameter.extractParams(method);
		ParameterExtractor[] extractors = new ParameterExtractor[parameters.length];
		int dataParam = 0;
		
		for (int i = 0; i < parameters.length; i++) {
			ReflectedParameter p = parameters[i];
			ParameterExtractor extractor = processParameter(p, pathParameters);
			extractors[i] = extractor;
			
			if (extractor instanceof DataParamater) {
				dataParam++;
			}
		}
		
		assertSingleDataParam(method, dataParam);
		
		return extractors;
		
	}
	
	private static ParameterExtractor processParameter(ReflectedParameter p, Map<String, Integer> pathParameters) {
		
		Param annotation = p.getAnnotation(Param.class);
		
		if (annotation != null) {
			String name = annotation.value();
			if (pathParameters.containsKey(name)) {
				Integer index = pathParameters.get(name);
				return new PathParameter(p.getType(), name, index.intValue());
			}
			
			return new RequestParamater(p.getType(), name);
		}
		
		Context context = p.getAnnotation(Context.class);
		if (context != null) {
			// TODO: queria ter uma interface Injectable para nao usar diretamente o request/response
			//       dessa maneira fica mais simples de testar. O factory desse cara poderia ser
			//       enviado pelo servlet context e armazenado em um singleton
			
			if (p.getType() == HttpServletRequest.class) {
				return new InjectParamater();
			}
			
			String message = unsupportedParameter(p);
			throw new IllegalArgumentException(message);
		}
		
		return new DataParamater(p.getType());
	}
	
	private static ProcessedPath processPath(String path) {
		String[] tokens = path.split("/");
		
		Map<String, Integer> pathParameters = new HashMap<>();
		
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			Matcher matcher = PATH_PARAM_PATTERN.matcher(token);
			if (matcher.matches()) {
				String name = token.substring(1, token.length() - 1);
				pathParameters.put(name, i);
				tokens[i] = "\\w+";
			}
		}
		
		
		Pattern pattern = Pattern.compile(StringUtils.join("/", tokens));
		return new ProcessedPath(pathParameters, pattern);
	}
	
	private static class ProcessedPath {
		
		private final Map<String, Integer> pathParameters;
		private final Pattern pattern;
		
		public ProcessedPath(Map<String, Integer> pathParameters, Pattern pattern) {
			this.pathParameters = pathParameters;
			this.pattern = pattern;
		}
		
	}
	
	public static enum HttpMethod {
		DELETE, GET, POST, PUT;
	}

	private static String unsupportedParameter(ReflectedParameter parameter) {
		String error = "Method %s#%s have an unsupported parameter of type %s,"
				+ " use @Param annotation for request parameters";
		
		Method method = parameter.getDeclaringExecutable();
		String type = method.getDeclaringClass().getName();
		String name = method.getName();
		String parameterType = parameter.getType().getName();
		return String.format(error, type, name, parameterType);
	} 

	private static void assertSingleDataParam(Method method, int count) {
		if (count > 1) {
			String error = "Method %s#%s have two or more parameters without "
					+ "@Param or @Context annotation. Only one is allowed";

			String type = method.getDeclaringClass().getName();
			String name = method.getName();
			String message = String.format(error, type, name);
			throw new IllegalArgumentException(message);
		}
	}

}
