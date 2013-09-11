package com.ytincl.scdp.module.simplejson.service;

public class SimpleJSONResponse {
	public SimpleJSONResponse() {
	}

	public SimpleJSONResponse(String s) {
		value = s;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String s) {
		value = s;
	}

	public String toString() {
		return value;
	}

	private String value;
}
