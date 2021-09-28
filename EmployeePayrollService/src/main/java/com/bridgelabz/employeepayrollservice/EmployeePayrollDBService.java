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
	private PreparedStatement employeePayrollDataStatement;
	private PreparedStatement employeePayrollDateStatement;
	private PreparedStatement employeePayrollUpdateDataStatement;
	private PreparedStatement genderWiseSumDataStatement;
	private PreparedStatement genderWiseMaxDataStatement;
	private PreparedStatement genderWiseMinDataStatement;
	private PreparedStatement genderWiseAvgDataStatement;
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
	public int genderWiseSumOfEmployeeSalary(String gender) {
		return this.genderWiseSumUsingPreparedStatement(gender);
	}
	
	private int genderWiseSumUsingPreparedStatement(String gender) {
		if(this.genderWiseSumDataStatement==null){
			this.preparedStatementForGenderWiseSumEmployeeSalary();
		}
		try{
			genderWiseSumDataStatement.setString(1,gender);
			genderWiseSumDataStatement.executeQuery();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		return 0;
	}

	private void preparedStatementForGenderWiseSumEmployeeSalary() {
		try{
			Connection connection = this.getConnection();
			String sql="SELECT sum(salary) from employee_payroll where gender = ? group by gender;";

			genderWiseSumDataStatement=connection.prepareStatement(sql);
		}
		catch (SQLException e){
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute the query");
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
				String department = result.getString("department");
				String gender = result.getString("gender");
				double salary = result.getDouble("salary");
				LocalDate start = result.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayrollData(id, name, phoneNumber, address, department, gender, salary, start));
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
	public List<EmployeePayrollData> getEmployeesBetweenDateRange(String startDate, String endDate){
		List<EmployeePayrollData> employeePayrollList=null;
		if(this.employeePayrollDateStatement==null){
			this.preparedStatementForEmployeeJoinedInDateRange();
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

	private void preparedStatementForEmployeeJoinedInDateRange() {
		try{
			Connection connection = this.getConnection();
			String sql="SELECT * FROM employee_payroll WHERE start BETWEEN ? AND ?";
			employeePayrollDateStatement=connection.prepareStatement(sql);
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	public int genderWiseMaxOfEmployeeSalary(String gender) {
		return this.genderWiseMaxUsingPreparedStatement(gender);
	}
	
	private int genderWiseMaxUsingPreparedStatement(String gender) {
		if(this.genderWiseMaxDataStatement==null){
			this.preparedStatementForGenderWiseMaxEmployeeSalary();
		}
		try{
			genderWiseMaxDataStatement.setString(1,gender);
			genderWiseMaxDataStatement.executeQuery();
		}
		catch (SQLException e){
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute the query");
		}
		return 0;
	}

	private void preparedStatementForGenderWiseMaxEmployeeSalary() {
		try{
			Connection connection = this.getConnection();
			String sql="SELECT max(salary) from employee_payroll where gender = ? group by gender;";

			genderWiseMaxDataStatement=connection.prepareStatement(sql);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}

	public int genderWiseMinOfEmployeeSalary(String gender) {
		return this.genderWiseMinUsingPreparedStatement(gender);
	}
	
	private int genderWiseMinUsingPreparedStatement(String gender) {
		if(this.genderWiseMinDataStatement==null){
			this.preparedStatementForGenderWiseMinEmployeeSalary();
		}
		try{
			genderWiseMinDataStatement.setString(1,gender);
			genderWiseMinDataStatement.executeQuery();
		}
		catch (SQLException e){
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute the query");
		}
		return 0;
	}

	private void preparedStatementForGenderWiseMinEmployeeSalary() {
		try{
			Connection connection = this.getConnection();
			String sql="SELECT min(salary) from employee_payroll where gender = ? group by gender;";

			genderWiseMinDataStatement=connection.prepareStatement(sql);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}

	public int genderWiseAvgOfEmployeeSalary(String gender) {
		return this.genderWiseAvgUsingPreparedStatement(gender);
	}
	
	private int genderWiseAvgUsingPreparedStatement(String gender) {
		if(this.genderWiseAvgDataStatement==null){
			this.preparedStatementForGenderWiseAvgEmployeeSalary();
		}
		try{
			genderWiseAvgDataStatement.setString(1,gender);
			genderWiseAvgDataStatement.executeQuery();
		}
		catch (SQLException e){
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute the query");
		}
		return 0;
	}

	private void preparedStatementForGenderWiseAvgEmployeeSalary() {
		try{
			Connection connection = this.getConnection();
			String sql="SELECT avg(salary) from employee_payroll where gender = ? group by gender;";

			genderWiseAvgDataStatement=connection.prepareStatement(sql);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}




	

}
