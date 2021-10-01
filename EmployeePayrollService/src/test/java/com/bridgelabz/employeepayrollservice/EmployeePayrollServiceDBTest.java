package com.bridgelabz.employeepayrollservice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bridgelabz.employeepayrollservice.IoService.IOService;

public class EmployeePayrollServiceDBTest {
	public int numOfEntries = 0;
	@Before
	public void initialise() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		numOfEntries = employeePayrollData.size();
	}
	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount(){
		try {
			EmployeePayrollService employeePayrollService = new EmployeePayrollService();
			List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
			Assert.assertEquals(numOfEntries, employeePayrollData.size());
		}catch(EmployeePayrollException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void givenEmployeePayrollDetails_WhenAdded_ShouldSyncWithDB() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
		String date = "11-02-2017";
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mini","9880906789","RR Nagar","F",400000.00,LocalDate.parse(date,formatter),2,"d2");
		boolean result=employeePayrollService.checkEmployeePayrollInSyncWithDB("Mini");
		Assert.assertTrue(result);
		
	}
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Andy",23000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Andy");
		Assert.assertTrue(result);
	}
	@Test
	public void givenDateRange_WhenQueried_ShouldReturnEmployeeCount(){
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2019,01,01);
		LocalDate endDate = LocalDate.now(); 
		List<EmployeePayrollData> employeeList = employeePayrollService.getEmployeesInADateRange(startDate,endDate);
		Assert.assertEquals(1, employeeList.size());
	}
	@Test
	public void givenEmployeePayrollDB_WhenAvgSalaryRetrievedByGender_ShouldReturnAvgOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(266666.6666666667)&&
		averageSalaryByGender.get("F").equals(400000.00));
	}
	@Test
	public void givenEmployeePayrollDB_WhenSumSalaryRetrievedByGender_ShouldReturnSumOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> sumSalaryByGender = employeePayrollService.readSumSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(sumSalaryByGender.get("M").equals(423000.00)&&
		sumSalaryByGender.get("F").equals(400000.00));
	}
	@Test
	public void givenEmployeePayrollDB_WhenMaxSalaryRetrievedByGender_ShouldReturnMaxOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> maxSalaryByGender = employeePayrollService.readMaxSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(maxSalaryByGender.get("M").equals(400000.00)&&
		maxSalaryByGender.get("F").equals(400000.00));
	}
	@Test
	public void givenEmployeePayrollDB_WhenMinSalaryRetrievedByGender_ShouldReturnMinOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> minSalaryByGender = employeePayrollService.readMinSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(minSalaryByGender.get("M").equals(200000.00)&&
		minSalaryByGender.get("F").equals(400000.00));
	}
	@Test
	public void givenEmployeeName_WhenDeleted_ShouldBecomeInactive() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(new ArrayList<>()); 
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		int result = employeePayrollService.deleteEmployee("Mini");
		Assert.assertTrue(result>0);
	}
	@Test
	public void givenEmployeeWithWrongCompany_WhenInserted_ShouldThrowException() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		String date = "16/08/2019";
		LocalDate startDate = LocalDate.parse(date, formatter);
		EmployeePayrollService employeePayrollService = new EmployeePayrollService(new ArrayList<>()); 
		try {
			employeePayrollService.addEmployeeToPayroll("Mary","9866906789","RT Nagar","F",500000.00,LocalDate.parse(date,formatter),15,"d1");
			ExpectedException exceptionRule = ExpectedException.none();
			exceptionRule.expect(EmployeePayrollException.class);
		}
		catch(EmployeePayrollException e) {
			System.out.println(e.getMessage());
		}
	}
}
