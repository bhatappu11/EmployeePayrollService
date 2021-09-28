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

import com.bridgelabz.employeepayrollservice.EmployeePayrollService.IOService;

import static com.bridgelabz.employeepayrollservice.EmployeePayrollService.IOService.FILE_IO;


public class EmployeePayrollServiceTest {
	public int numOfEntries = 0;
	@Before
	public void initialise() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		numOfEntries = employeePayrollData.size();
	}
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(1, "Jeff Bezos", 10000),
				new EmployeePayrollData(2, "Bill Gates", 20000),
				new EmployeePayrollData(3, "Mark Zuckerberg", 30000)
		};
		
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(FILE_IO);
		employeePayrollService.readEmployeePayrollData(FILE_IO);
		//read and count the entries
		long entries = employeePayrollService.countEntries(FILE_IO);
		Assert.assertEquals(3, entries);
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
	public void givenEmployeePayrollDetails_ShouldInsertToTheTable() {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MM-yyyy");
			String date1 = "11-02-2017";
			String date2 = "16-04-2018";
			EmployeePayrollService employeePayrollService;
			EmployeePayrollData data = new EmployeePayrollData();
			data.setName("Jasmine");
			data.setPhoneNumber("8880909090");
			data.setAddress("Shimoga");
			data.setDepartment("Sales");
			data.setGender("F");
			data.setSalary(20300000);
			data.setStartDate(LocalDate.parse(date1,formatter));
			EmployeePayrollData data2 = new EmployeePayrollData();
			data2.setName("Alex");
			data2.setPhoneNumber("9099777090");
			data2.setAddress("Koramangala");
			data2.setDepartment("HR");
			data2.setGender("M");
			data2.setSalary(200000);
			data2.setStartDate(LocalDate.parse(date2,formatter));
			EmployeePayrollData[] arrayOfEmps = {
					data,data2
			};
			employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
			employeePayrollService.writeEmployeePayrollData(IOService.DB_IO);
			numOfEntries++;
			Assert.assertTrue(true);
			
		}catch(EmployeePayrollException e) {
			e.printStackTrace();
		}
		
	}
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDB() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Tanisha",2300000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Tanisha");
		Assert.assertTrue(result);
	}
	@Test
	public void givenDateRange_WhenQueried_ShouldReturnEmployeeCount(){
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2019,01,01);
		LocalDate endDate = LocalDate.now(); 
		List<EmployeePayrollData> employeeList = employeePayrollService.getEmployeesInADateRange(startDate,endDate);
		Assert.assertEquals(3, employeeList.size());
	}
	@Test
	public void givenEmployeePayrollDB_WhenAvgSalaryRetrievedByGender_ShouldReturnAvgOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(2190909.090909091)&&
		averageSalaryByGender.get("F").equals(17728571.42857143));
	}
	@Test
	public void givenEmployeePayrollDB_WhenSumSalaryRetrievedByGender_ShouldReturnSumOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> sumSalaryByGender = employeePayrollService.readSumSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(sumSalaryByGender.get("M").equals(24100000.00)&&
		sumSalaryByGender.get("F").equals(124100000.00));
	}
	@Test
	public void givenEmployeePayrollDB_WhenMaxSalaryRetrievedByGender_ShouldReturnMaxOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> maxSalaryByGender = employeePayrollService.readMaxSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(maxSalaryByGender.get("M").equals(20300000.00)&&
		maxSalaryByGender.get("F").equals(20300000.00));
	}
	@Test
	public void givenEmployeePayrollDB_WhenMinSalaryRetrievedByGender_ShouldReturnMinOfSalary() {
		EmployeePayrollService employeePayrollService= new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> minSalaryByGender = employeePayrollService.readMinSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(minSalaryByGender.get("M").equals(100000.00)&&
		minSalaryByGender.get("F").equals(2300000.00));
	}
}
