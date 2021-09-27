package com.bridgelabz.employeepayrollservice;


public class EmployeePayrollException extends RuntimeException{
	enum exceptionType{
		INVALID_QUERY
	}
	exceptionType etype;
	public EmployeePayrollException(exceptionType type, String message) {
		super(message);
		this.etype = type;
	}
}
