package com.bridgelabz.employeepayrollservice;

import java.time.LocalDate;

public class EmployeePayrollData {
	public int id;
	public String emp_id;
	public String name;
	public double salary;
	public String phoneNumber;
	public String gender;
	public LocalDate startDate;
	
	public EmployeePayrollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayrollData(String id2, String name2, String phoneNumber, String gender, LocalDate start) {
		this.emp_id = id2;
		this.name = name2;
		this.phoneNumber = phoneNumber;
		this.gender = gender;
		this.startDate = start;
	}

	@Override
	public String toString() {
		return "EmployeePayrollData [id=" + id + ", emp_id=" + emp_id + ", name=" + name + ", salary=" + salary
				+ ", phoneNumber=" + phoneNumber + ", gender=" + gender + ", startDate=" + startDate + "]";
	}

		@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayrollData other = (EmployeePayrollData) obj;
		if (emp_id == null) {
			if (other.emp_id != null)
				return false;
		} else if (!emp_id.equals(other.emp_id))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}
	
	
}
