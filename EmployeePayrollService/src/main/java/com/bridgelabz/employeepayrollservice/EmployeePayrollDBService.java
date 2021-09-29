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
		String sql = "select * from employee e,payroll p where e.id = p.employee_id;";
		HashMap<Integer,ArrayList<Department>> departmentList = getDepartmentList();
		HashMap<Integer, Company> companyMap = getCompany();
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
	private HashMap<Integer, Company> getCompany() {
		HashMap<Integer, Company> companyMap = new HashMap<>();
		String sql = "select * from company";
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				int id  = result.getInt("company_id");
				String name  = result.getString("company_name");
				companyMap.put(id, new Company(id, name));
			}
		}
		catch(SQLException e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "query execution failed");
		}
		return companyMap;
	}

	private HashMap<Integer, ArrayList<Department>> getDepartmentList() {
		HashMap<Integer,ArrayList<Department>> departmentList = new HashMap<>();
		String sql = "select * from employee_department";
		HashMap<String,Department> departmentMap = getDepartment();
		try(Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				int employeeId = resultSet.getInt("employee_id");
				String departmentId = resultSet.getString("department_id");
				if(departmentList.get(employeeId) == null) departmentList.put(employeeId, new ArrayList<Department>());
				departmentList.get(employeeId).add(departmentMap.get(departmentId));
			}
		}
		catch(SQLException e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "query execution failed");
		}
		return departmentList;
	}

	private HashMap<String, Department> getDepartment() {
		String sql = "select * from department";
		HashMap<String,Department> dept = new HashMap<>();
		try(Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String id = result.getString("dept_id");
				String name  = result.getString("dept_name");
				dept.put(id,new Department(id, name));
			}
		}
		catch(SQLException e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "query execution failed");
		}
		return dept;
	}
	public EmployeePayrollData addEmployeeToPayroll(String name, String phoneNumber, String address,
			String gender, double salary, LocalDate startDate, int companyId) {
		int employeeId = -1;
		HashMap<Integer, Company> companyMap = getCompany();
		EmployeePayrollData employeePayrollData = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		}catch(Exception e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.FAILED_TO_CONNECT, "couldn't establish connection");
		}
	
		try (Statement statement = connection.createStatement()){
			String sql = String.format("select * from company where company_id = %d",companyId);
			ResultSet result = statement.executeQuery(sql);
			if(result.next() == false) {
				throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "Company with id:"+companyId+" not present");
			}
		}catch(SQLException e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "query execution failed");
		}
		
		try (Statement statement = connection.createStatement()){
			String sql = String.format("INSERT INTO employee(company_id,name,gender,address,phoneNumber,start,salary)VALUES(%d,'%s','%s','%s',%s,'%s','%s')",companyId,name,
					gender,address,phoneNumber, startDate.toString(),salary);
			int result = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
			if(result == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if(resultSet.next()) employeeId = resultSet.getInt(1);
			}
		}catch(SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "query execution failed");
		}
		
		try(Statement statement = connection.createStatement();){
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format("INSERT INTO payroll(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay)VALUES(%d,%2f,%2f,%2f,%2f,%2f)",
					employeeId,salary,deductions,taxablePay,tax,netPay);
			int result = statement.executeUpdate(sql);
			if(result == 1) {
				employeePayrollData = new EmployeePayrollData(employeeId, name, phoneNumber, address, gender, salary, startDate,companyMap.get(companyId));
			}
		}catch(SQLException e) {
			try {
				connection.rollback();
			} 
			catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "query execution failed");
		}try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
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
		return this.updateEmployeeDataUsingStatement(name,salary);
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
			String sql="UPDATE payroll SET basic_pay = ? WHERE employee_id IN (select id from employee where name = ?);";

			employeePayrollUpdateDataStatement=connection.prepareStatement(sql);
		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}
		private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update payroll set basic_pay = %2f where employee_id IN (select id from employee where name = '%s') ;",salary,name);
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
		HashMap<Integer,ArrayList<Department>> departmentList = getDepartmentList();
		HashMap<Integer, Company> companyMap = getCompany();
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
				int companyId = result.getInt("company_id");
				employeePayrollList.add(new EmployeePayrollData(id, name, phoneNumber, address, gender, salary, start,companyMap.get(companyId)));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql=	"SELECT	* FROM employee WHERE name = ?";
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
			String sql=" SELECT * FROM employee e,payroll p WHERE p.employee_id = e.id AND e.start BETWEEN ? AND ?;";
			employeePayrollDateStatement=connection.prepareStatement(sql);
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

		public Map<String, Double> getAverageSalaryByGender() {
		String sql = "SELECT gender,avg(salary) as avg_salary FROM employee e,payroll p where e.id=p.employee_id GROUP BY gender;";
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
		HashMap<String,Double> salaryMap = new HashMap<>();
		String sql = "SELECT gender,SUM(basic_pay) as sum_salary  FROM employee e, payroll p WHERE e.id = p.employee_id GROUP BY gender;";
		try(Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(sql);
			while(result.next()) {
				String key = result.getString("gender");
				double value = result.getDouble("sum_salary");
				salaryMap.put(key, value);
			}
		}
		catch (SQLException e) {
			throw new EmployeePayrollException(EmployeePayrollException.ExceptionType.CANNOT_EXECUTE_QUERY, "Cannot execute the query");
		}	
		return salaryMap;
	}

	public Map<String, Double> getMaxSalaryByGender() {
		String sql = "SELECT gender,max(salary) as max_salary FROM employee e,payroll p where e.id=p.employee_id GROUP BY gender;";
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
		String sql = "SELECT gender,min(salary) as min_salary FROM employee e,payroll p where e.id=p.employee_id GROUP BY gender;";
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
