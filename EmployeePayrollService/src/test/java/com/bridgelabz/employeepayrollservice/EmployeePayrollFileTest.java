package com.bridgelabz.employeepayrollservice;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bridgelabz.employeepayrollservice.IoService.IOService;

public class EmployeePayrollFileTest {
	static int  size = 3;
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(1, "Jeff Bezos", 10000),
				new EmployeePayrollData(2, "Bill Gates", 20000),
				new EmployeePayrollData(3, "Mark Zuckerberg", 30000)
		};
		
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
		//read and count the entries
		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}
	
	@Test
	public void givenFileOnReadingFromMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> entries = employeePayrollService.readEmployeePayrollData(IOService.FILE_IO);
		Assert.assertEquals(3,entries);
	}
}
