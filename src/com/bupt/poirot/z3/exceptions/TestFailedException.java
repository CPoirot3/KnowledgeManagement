package com.bupt.poirot.z3.exceptions;

@SuppressWarnings("serial")
public class TestFailedException extends Exception {
	public TestFailedException() {
		super("Check FAILED");
	}
};