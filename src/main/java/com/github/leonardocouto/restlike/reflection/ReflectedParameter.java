package com.github.leonardocouto.restlike.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class ReflectedParameter {
	
	private final Annotation[] annotations;
	private final Method method;
	private final Class<?> type;
	
	private ReflectedParameter(Method method, Class<?> type, Annotation[] annotations) {
		this.method = method;
		this.type = type;
		this.annotations = annotations;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAnnotation(Class<T> type) {
		for (Annotation a : this.annotations) {
			if (a.annotationType() == type) {
				return (T) a;
			}
		}
		return null;
	}
	
	public Annotation[] getAnnotations() {
		return annotations;
	}
	
	public Method getDeclaringExecutable() {
		return this.method;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public static ReflectedParameter[] extractParams(Method method) {
		Annotation[][] annotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();
		
		ReflectedParameter[] parameters = new ReflectedParameter[types.length];
		for (int i = 0; i < types.length; i++) {
			parameters[i] = new ReflectedParameter(method, types[i], annotations[i]);
		}
		
		return parameters;
	}

}
