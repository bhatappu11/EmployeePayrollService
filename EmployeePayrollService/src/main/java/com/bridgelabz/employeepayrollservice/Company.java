package com.bridgelabz.employeepayrollservice;

public class Company {
	int companyId;
	String companyName;
	public Company(int companyId, String companyName) {
		this.companyId = companyId;
		this.companyName = companyName;
	}
	public int getCompanyId() {
		return companyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
}
