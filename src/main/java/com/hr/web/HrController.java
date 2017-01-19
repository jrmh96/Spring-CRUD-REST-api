package com.hr.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;

import com.hr.dto.DepartmentDTO;
import com.hr.dto.EmployeeDTO;
import com.hr.dto.ReturnMessage;
import com.hr.dto.JobDTO;
import com.hr.dto.UserDTO;
import com.hr.dto.UserSessionBean;
import com.hr.model.Employees;
import com.hr.model.Job;
import com.hr.service.DepartmentService;
import com.hr.service.EmployeeService;
import com.hr.service.MiscService;
import com.hr.service.UserService;


import com.google.common.collect.Lists;
import com.google.gson.Gson;

@RestController
public class HrController {
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private MiscService misc;
	@Autowired
	private DepartmentService deptServ;
	
	//Create
	@RequestMapping(value="/create", method=RequestMethod.POST, produces="application/json", consumes="application/json")
	public Object create(@RequestBody Map<String, Object> map){
		ReturnMessage r = new ReturnMessage();
		Job j = new Job();
		Employees e = new Employees();
		JobDTO job = misc.getJobByTitle((String) map.get("job"));
		
		j.setJobID(job.getJobID());
		j.setJobTitle(job.getJobTitle());
		j.setMaxSalary(job.getMaxSalary());
		j.setMinSalary(job.getMinSalary());
		
		e.setFirstName((String) map.get("firstName"));
		e.setLastName((String) map.get("lastName"));
		String email = (String) (map.get("firstName").toString().substring(0, 1).toUpperCase() + map.get("lastName").toString().toUpperCase());
		e.setEmail(email);
		e.setPhoneNumber((String) map.get("phone"));
		e.setHireDate(new Date());
		Long f = ((Integer) map.get("departmentID")).longValue();
		e.setDepartment(this.deptServ.getOne(f));
		e.setJob(j);
		
		try{
			this.employeeService.saveOrUpdate(e);
			ArrayList<EmployeeDTO> a = new ArrayList<EmployeeDTO>();
			a.add(toDTO(e));
			r.setEmpList(a);
			r.setMessage("Success");
		}catch (Exception ex){
			ex.printStackTrace();
			r.setMessage("Failed");
		}
		return r;
	}
	
	//Read
	@RequestMapping(value="/getAll", produces="application/json")
	public Object getAll(){
		List<EmployeeDTO> list = Lists.newArrayList();
		for(Employees e : employeeService.getAll()){
			EmployeeDTO edto = new EmployeeDTO();
			if(e.getDepartment()!=null){
				edto.setDepartmentID(e.getDepartment().getDepartmentId());
			}
			else{
				edto.setDepartmentID((long)000);
			}
				edto.setFirstName(e.getFirstName());
				edto.setLastName(e.getLastName());
				edto.setJobTitle(e.getJob().getJobTitle());
				edto.setID(e.getEmployeeId());
				list.add(edto);
		}
		String jsonString = new Gson().toJson(list);
		return jsonString;
	}
	
	//Update
	@RequestMapping(value="/update", method=RequestMethod.POST, produces="application/json", consumes="application/json")
	public Object update(@RequestBody Map<String, Object> map){
		ReturnMessage r = new ReturnMessage();
		Long id = ((Integer) map.get("id")).longValue();
		Employees e = this.employeeService.getbyID(id);
		
		e.setFirstName((String) map.get("firstName"));
		e.setLastName((String) map.get("lastName"));
		String email = (String) (map.get("firstName").toString().substring(0, 1).toUpperCase() + map.get("lastName").toString().toUpperCase());
		if(!email.equals(e.getEmail())){
			e.setEmail(email);
		}
		e.setPhoneNumber((String) map.get("phone"));
		e.setHireDate(new Date());
		id=((Integer) map.get("departmentID")).longValue();
		e.setDepartment(this.deptServ.getOne(id));
		
		try{
			this.employeeService.saveOrUpdate(e);
			r.setMessage("Success");
			ArrayList<EmployeeDTO> a = new ArrayList<EmployeeDTO>();
			a.add(toDTO(e));
			r.setEmpList(a);
		}catch (Exception ex){
			ex.printStackTrace();
			r.setMessage("Failed");
		}
		return r;
	}
	//Delete
	@RequestMapping(value="/delete", method=RequestMethod.POST, produces="application/json", consumes="application/json")
	public Object delete(@RequestBody Map<String, Long> map){
		ReturnMessage r = new ReturnMessage();
		try{
			this.employeeService.deleteEmployee(map.get("id"));
			r.setMessage("Success");
		}catch(Exception e){
			e.printStackTrace();
			r.setMessage("Failed");
		}
		return r;
	}
	
	private EmployeeDTO toDTO(Employees e) throws Exception{
		EmployeeDTO toReturn= new EmployeeDTO();
		
		toReturn.setFirstName(e.getFirstName());
		toReturn.setLastName(e.getLastName());
		toReturn.setEmail(e.getEmail());
		toReturn.setHireDate(e.getHireDate());
		if(e.getDepartment()!=null){
			toReturn.setDepartmentID(e.getDepartment().getDepartmentId());
		}
		else{
			toReturn.setDepartmentID((long)000);
		}
		toReturn.setPhoneNumber(e.getPhoneNumber());
		toReturn.setID(e.getEmployeeId());
		toReturn.setJobID(e.getJob().getJobID());
		toReturn.setJobTitle(e.getJob().getJobTitle());
		return toReturn;
	}
}
