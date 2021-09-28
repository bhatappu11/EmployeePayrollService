package com.bridgelabz.employeepayrollservice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bridgelabz.employeepayrollservice.EmployeePayrollException.ExceptionType;


public class EmployeePayrollDBService {
	private PreparedStatement employeePayrollDataStatement;
	private PreparedStatement employeePayrollDateStatement;
	private PreparedStatement employeePayrollUpdateDataStatement;
	private static EmployeePayrollDBService employeePayrollDBService;
	private EmployeePayrollDBService() {
	}
	
	public static EmployeePayrollDBService getInstance(){
		if (employeePayrollDBService == null)
			employeePayrollDBService=new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	public List<EmployeePayrollData> readData() {
		String sql = "select * from employee_payroll;";
		List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}
	public EmployeePayrollData addEmployeeToPayroll(String name, String phoneNumber, String address,
			String gender, double salary, LocalDate startDate) {
		int employee_id = -1;
		EmployeePayrollData employeePayrollData = null;
		String sql = String.format("insert into employee_payroll(name,phoneNumber,address,gender,salary,start) values ('%s','%s','%s','%s','%2f','%s')",
				name,phoneNumber,address,gender,salary,Date.valueOf(startDate));
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
			if(rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) employee_id = resultSet.getInt(1);				
			}
			employeePayrollData = new EmployeePayrollData(employee_id, name, phoneNumber, address, gender, salary, startDate);
		}catch(SQLException e) {
			throw new EmployeePayrollException(ExceptionType.INSERT_FAILED, "Insertion failed");
		}
		return employeePayrollData;
	}
	
	private Connection getConnection() {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "perfios";
		Connection connection;
		System.out.println("connecting to database: "+jdbcURL);
		try {
			connection = DriverManager.getConnection(jdbcURL, userName, password);
		} catch (SQLException e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.FAILED_TO_CONNECT, "Failed to connect to database");
		}
		System.out.println("connection is successful!"+connection);
		return connection;
	}
	public int updateEmployeeData(String name, double salary) throws EmployeePayrollException {
		return this.updateEmployeeDataUsingPreparedStatement(name,salary);
	}
	
	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) {
		if(this.employeePayrollUpdateDataStatement==null){
			this.preparedStatementForUpdateEmployeeData();
		}
		try{
			employeePayrollUpdateDataStatement.setString(2,name);
			employeePayrollUpdateDataStatement.setDouble(1,salary);
			return employeePayrollUpdateDataStatement.executeUpdate();
		}
		catch (SQLException e){
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.UPDATE_FAILED, "Failed to update the given data");
		}

	}

	private void preparedStatementForUpdateEmployeeData() {
		try{
			Connection connection = this.getConnection();
			String sql="UPDATE employee_payroll SET salary = ? WHERE name = ?;";

			employeePayrollUpdateDataStatement=connection.prepareStatement(sql);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}
		private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary = '%2f' where name = '%s';",salary,name);
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int result = statement.executeUpdate(sql);
		}catch(SQLException e) {
			e.printStackTrace();
		} 
		return 0;
	}
	public List<EmployeePayrollData> getEmployeePayrollData(String name) {
		List<EmployeePayrollData> employeePayrollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;	
	}

	private List<EmployeePayrollData> getEmployeePayrollData(ResultSet result) {
		List<EmployeePayrollData> employeePayrollList = new ArrayList();
		try {
			while(result.next ()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				String phoneNumber = result.getString("phoneNumber");
				String address = result.getString("address");
				String gender = result.getString("gender");
				double salary = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, phoneNumber, address, gender, salary, start));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql=	"SELECT	* FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public List<EmployeePayrollData> getEmployeesBetweenDateRange(LocalDate startDate, LocalDate endDate){
		List<EmployeePayrollData> employeePayrollList=null;
		if(this.employeePayrollDateStatement==null){
			this.preparedStatementForEmployeeInDateRange();
		}
		try{
			employeePayrollDateStatement.setDate(1,java.sql.Date.valueOf(startDate));
			employeePayrollDateStatement.setDate(2,java.sql.Date.valueOf(endDate));

			ResultSet resultSet= employeePayrollDateStatement.executeQuery();
			employeePayrollList=this.getEmployeePayrollData(resultSet);

		}catch (SQLException e){
			e.printStackTrace();
		}
		return employeePayrollList;

	}

	private void preparedStatementForEmployeeInDateRange() {
		try{
			Connection connection = this.getConnection();
			String sql="SELECT * FROM employee_payroll WHERE start BETWEEN ? AND ?";
			employeePayrollDateStatement=connection.prepareStatement(sql);
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

		public Map<String, Double> getAverageSalaryByGender() {
		String sql = "SELECT gender,avg(salary) as avg_salary FROM employee_payroll GROUP BY gender;";
		Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement. executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("avg_salary");
				genderToAverageSalaryMap.put(gender, salary);
			}
		}catch (SQLException e) {
			throw new EmployeePayrollException(ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute given query");
		}
		return genderToAverageSalaryMap;
	}

	public Map<String, Double> getSumSalaryByGender() {
		String sql = "SELECT gender,sum(salary) as sum_salary FROM employee_payroll GROUP BY gender;";
		Map<String, Double> genderToSumSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement. executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("sum_salary");
				genderToSumSalaryMap.put(gender, salary);
			}
		}catch (SQLException e) {
			throw new EmployeePayrollException(ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute given query");
		}
		return genderToSumSalaryMap;
	}

	public Map<String, Double> getMaxSalaryByGender() {
		String sql = "SELECT gender,max(salary) as max_salary FROM employee_payroll GROUP BY gender;";
		Map<String, Double> genderToMaxSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement. executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("max_salary");
				genderToMaxSalaryMap.put(gender, salary);
			}
		}catch (SQLException e) {
			throw new EmployeePayrollException(ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute given query");
		}
		return genderToMaxSalaryMap;
	}

	public Map<String, Double> getMinSalaryByGender() {
		String sql = "SELECT gender,min(salary) as min_salary FROM employee_payroll GROUP BY gender;";
		Map<String, Double> genderToMinSalaryMap = new HashMap<>();
		try(Connection connection = this.getConnection()){
			Statement statement = connection.createStatement();
			ResultSet result = statement. executeQuery(sql);
			while(result.next()) {
				String gender = result.getString("gender");
				double salary = result.getDouble("min_salary");
				genderToMinSalaryMap.put(gender, salary);
			}
		}catch (SQLException e) {
			throw new EmployeePayrollException(ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute given query");
		}
		return genderToMinSalaryMap;
	}

	
}
