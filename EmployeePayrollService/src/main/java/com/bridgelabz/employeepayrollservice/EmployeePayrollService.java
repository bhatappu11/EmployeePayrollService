package com.bridgelabz.employeepayrollservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO};
	
	private List<EmployeePayrollData> employeePayrollList;
	private EmployeePayrollDBService employeePayrollDBService;
	public EmployeePayrollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}
	public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public static void main(String[] args) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
		employeePayrollService.readEmployeePayrollData(IOService.CONSOLE_IO);
		employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
	}	
	public void writeEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.CONSOLE_IO))
			System.out.println("Writing employee payroll to console\n"+employeePayrollList);
		else if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().writeData(employeePayrollList);
		}
		else if(ioService.equals(IOService.DB_IO)) {
			employeePayrollDBService.writeData(employeePayrollList);
		}
	}
	
	public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) {
		if(ioService.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		else if(ioService.equals(IOService.FILE_IO)) {
			new EmployeePayrollFileIOService().printData();
		}
		else if(ioService.equals(IOService.CONSOLE_IO)) {
			Scanner consoleInputReader = new Scanner(System.in);
			System.out.println("Enter employee ID:");
			int id = consoleInputReader.nextInt();
			System.out.println("Enter employee name :");
			String name  = consoleInputReader.next();
			System.out.println("Enter employee salary: ");
			double salary = consoleInputReader.nextDouble();
			employeePayrollList.add(new EmployeePayrollData(id, name, salary));
		}
		return this.employeePayrollList;
	}
	public List<EmployeePayrollData> getEmployeesInADateRange(String date1, String date2){
		return employeePayrollDBService.getEmployeesBetweenDateRange(date1, date2);
	}
	public long countEntries(IOService ioService) {
		if(ioService.equals(IOService.FILE_IO)) {
			return new EmployeePayrollFileIOService().countEntries();
		}
		return 0;
	}
	public void updateEmployeeSalary(String name, double salary) {
		int result = employeePayrollDBService.updateEmployeeData(name,salary);
		if(result == 0) return;
		EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
		if(employeePayrollData != null) employeePayrollData.salary = salary;
		
	}
	private EmployeePayrollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.parallelStream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
				.findFirst()
				.orElse(null);
	}
	public boolean checkEmployeePayrollInSyncWithDB(String name) {
		List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
	public int getSumOfSalaryGenderWise(String gender) {
		return employeePayrollDBService.genderWiseSumOfEmployeeSalary(gender);
	}
	public int getMaxOfSalaryGenderWise(String gender) {
		return employeePayrollDBService.genderWiseMaxOfEmployeeSalary(gender);
	}
	public int getMinOfSalaryGenderWise(String gender) {
		return employeePayrollDBService.genderWiseMinOfEmployeeSalary(gender);
	}
	public int getAvgOfSalaryGenderWise(String gender) {
		return employeePayrollDBService.genderWiseAvgOfEmployeeSalary(gender);
	}

	
}
