package com.bridgelabz.employeepayrollservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {

	public List<EmployeePayrollData> readData() {
		String sql = "select * from employee;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String id = result.getString("emp_id");
				String name = result.getString("name");
				String phoneNumber = result.getString("phone_number");
				String gender = result.getString("gender");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, phoneNumber,gender,start));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "perfios";
		Connection connection;
		System.out.println("connecting to database: "+jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("connection is successful!"+connection);
		return connection;
	}
	
}