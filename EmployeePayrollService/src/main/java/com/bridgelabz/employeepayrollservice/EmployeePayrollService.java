package com.bridgelabz.employeepayrollservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO};
	private List<EmployeePayrollData> employeePayrollList;
	
	public EmployeePayrollService() {}
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}
	
	public static void main(String[] args) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		Scanner consoleInputReader = new Scanner(System.in);
		employeePayrollService.readEmployeePayrollData(consoleInputReader);
		employeePayrollService.writeEmployeePayrollData();
	}
	
	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter employee ID:");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter employee name :");
		String name  = consoleInputReader.next();
		System.out.println("Enter employee salary: ");
		double salary = consoleInputReader.nextDouble();
		employeePayrollList.add(new EmployeePayrollData(id, name, salary));
	}
	
	private void writeEmployeePayrollData() {
		System.out.println("\nWriting Employee Payroll Roaster to console \n"+ employeePayrollList);
	}
}
