package com.bridgelabz.employeepayrollservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EmployeePayrollDBService {

	public List<EmployeePayrollData> readData() {
		String sql = "select * from employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				String phoneNumber = result.getString("phoneNumber");
				String address = result.getString("address");
				String department = result.getString("department");
				String gender = result.getString("gender");
				double salary = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, phoneNumber,address,department,gender,salary,start));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	public void writeData(List<EmployeePayrollData> employeePayrollList) {
		employeePayrollList.stream().forEach(employee -> {
		String sql = String.format("insert into employee_payroll(name,phoneNumber,address,department,gender,salary,start) values ('%s','%s','%s','%s','%s','%2f','%s')",
				employee.name,employee.phoneNumber,employee.address,employee.department,employee.gender,employee.salary,employee.startDate.toString());
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
		}catch(SQLException e) {
			e.printStackTrace();
		}
		});
	}

	private Connection getConnection() throws SQLException{
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
