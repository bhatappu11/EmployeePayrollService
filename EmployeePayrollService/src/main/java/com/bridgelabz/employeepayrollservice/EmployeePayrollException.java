package com.bridgelabz.employeepayrollservice;


public class EmployeePayrollException extends RuntimeException{
	enum ExceptionType{
		FAILED_TO_CONNECT, CANNOT_EXECUTE_QUERY, UPDATE_FAILED, INSERT_FAILED,CONNECTION_CLOSE_FAILED
	}
	ExceptionType etype;
	public EmployeePayrollException(ExceptionType type, String message) {
		super(message);
		this.etype = type;
	}
}
