package com.revature.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeToken;
import com.revature.repository.EmployeeRepositoryJdbc;


public class EmployeeServiceAlpha implements EmployeeService {
   
	private static Logger logger = Logger.getLogger(EmployeeServiceAlpha.class);
	
    private static EmployeeService employeeService = new EmployeeServiceAlpha();
	
	private EmployeeServiceAlpha() { }
	
	public static EmployeeService getInstance() {
		return employeeService;
	}
	
	
	@Override
	public Employee authenticate(Employee employee) {
	
		Employee loggedEmployee = EmployeeRepositoryJdbc.getInstance().select(employee.getUsername());
						
	    if(loggedEmployee.getPassword().equals(EmployeeRepositoryJdbc.getInstance().getPasswordHash(employee))) {
	    	
		     return loggedEmployee;
				
		}
				
		     return new Employee();
	}

	
	@Override
	public Employee getEmployeeInformation(Employee employee) {
		
		return EmployeeRepositoryJdbc.getInstance().select(employee.getId());
		
	}

	
	@Override
	public Set<Employee> getAllEmployeesInformation() {
		
		return EmployeeRepositoryJdbc.getInstance().selectAll();
		
	}

	
	@Override
	public boolean createEmployee(Employee employee) {
		
		return EmployeeRepositoryJdbc.getInstance().insert(employee);
		
	}

	
	@Override
	public boolean updateEmployeeInformation(Employee employee) {
		
		return EmployeeRepositoryJdbc.getInstance().update(employee);
		
	}

	
	@Override
	public boolean updatePassword(Employee employee) {
		
		return EmployeeRepositoryJdbc.getInstance().update(employee);
		
	}

	
	@Override
	public boolean isUsernameTaken(Employee employee) {
		
	Employee existedEmployee = EmployeeRepositoryJdbc.getInstance().select(employee.getUsername());
		
		if(existedEmployee.getUsername().equals(employee.getUsername())){
			return false;
		}
		else{
			return true;
		}
		
	}

	
	@Override
	public boolean createPasswordToken(Employee employee) {
		
		return EmployeeRepositoryJdbc.getInstance().insertEmployeeToken(new EmployeeToken(LocalDateTime.now(),employee));
		
	}

	
	@Override
	public boolean deletePasswordToken(EmployeeToken employeeToken) {
		
	    return EmployeeRepositoryJdbc.getInstance().deleteEmployeeToken(employeeToken);
	    
	}

	
	@Override
	public boolean isTokenExpired(EmployeeToken employeeToken) {
		
		EmployeeToken token =  EmployeeRepositoryJdbc.getInstance().selectEmployeeToken(employeeToken);
		
		if(token.getId() == 0){
			return false;
		}
		else{
			return true;
		}
		
	}

	
}
