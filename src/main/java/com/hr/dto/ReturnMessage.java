package com.hr.dto;

import java.util.List;

public class ReturnMessage {
	private String message;
	private List<EmployeeDTO> empList;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<EmployeeDTO> getEmpList() {
		return empList;
	}
	public void setEmpList(List<EmployeeDTO> empList) {
		this.empList = empList;
	}
}
