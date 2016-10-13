package com.bupt.poirot.z3.exceptions;

@SuppressWarnings("serial")
public class RuntimeFailedException extends Exception {
	public RuntimeFailedException() {
		super("Runtime FAILED");
	}
	
	public RuntimeFailedException(String message) {
		super("Runtime FAILED " + message);
	}
	
};