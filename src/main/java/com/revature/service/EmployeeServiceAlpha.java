package com.revature.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeToken;
import com.revature.repository.EmployeeRepositoryJdbc;
import com.revature.thread.EmailThread;


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
		
		if (employee.getId()>0){
		return EmployeeRepositoryJdbc.getInstance().select(employee.getId());
		}
		else{
	    return EmployeeRepositoryJdbc.getInstance().select(employee.getUsername());
		}
		
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
		
		employee.setPassword(EmployeeRepositoryJdbc.getInstance().getPasswordHash(employee));
		
		return EmployeeRepositoryJdbc.getInstance().update(employee);
		
	}

	
	@Override
	public boolean isUsernameTaken(Employee employee) {
		
	Employee existedEmployee = EmployeeRepositoryJdbc.getInstance().select(employee.getUsername());
		
	    if(existedEmployee.getUsername() == null){
	    	logger.trace("we did not find this username in database");
	    	return false;
	    	
	    }
		//if(existedEmployee.getUsername().equals(employee.getUsername()))
	    else{
			logger.trace("We are in service layer. THis username has been taken.");
			return true;
		}
		//else{
	//		return false;
	//	}
		
	}

	
	@Override
	public boolean createPasswordToken(Employee employee) {
		
		EmployeeToken token = new EmployeeToken(LocalDateTime.now(),employee);
		logger.trace(token);
		 if(EmployeeRepositoryJdbc.getInstance().insertEmployeeToken(token)){
			 
			EmployeeToken createdToken = EmployeeRepositoryJdbc.getInstance().selectEmployeeToken(token);
			
			logger.trace("We are in service. CreatedToken:"+createdToken);
			
			sendEmailToEmployee(employee,createdToken);
			
			 return true; 
		 }
		
		 else{
			 return false;
		 }
		 
	}

	
	@Override
	public boolean deletePasswordToken(EmployeeToken employeeToken) {
		
	    return EmployeeRepositoryJdbc.getInstance().deleteEmployeeToken(employeeToken);
	    
	}

	
	@Override
	public boolean isTokenExpired(EmployeeToken employeeToken) {
		
		EmployeeToken token =  EmployeeRepositoryJdbc.getInstance().selectEmployeeToken(employeeToken);
		
		logger.trace(token);		
		
		if(token.getId() == 0){
			return true;
		}
		else{
			logger.trace("Check if the token was created 3 mins earlier. "+token.getCreationDate());
			logger.trace("The duration between creation time and now time: "+(180L-Duration.between(token.getCreationDate(), LocalDateTime.now()).getSeconds()));
			boolean boolIsMoreThanThreeMins = (180L-Duration.between(token.getCreationDate(), LocalDateTime.now()).getSeconds())>0?false:true;
			
			//If the token was created 3 mins ago, delete it and return true;
			if (boolIsMoreThanThreeMins){
				EmployeeRepositoryJdbc.getInstance().deleteEmployeeToken(token);
			return true;	
			}
			//The token is valid and not expired
			return false;
		}
		
	}
	
	
	private void sendEmailToEmployee(Employee employee,EmployeeToken createdToken){
		
		  String subject = "Reset your password";
		   String body = "Please use below link to reset your password.\n"
		                 +"http://localhost:8085/ERS/resetPasswordRequest.do?id="+employee.getId()+"&token="+createdToken.getToken()+"\n"
		                 +"Note: Above link will become invalid after 3 minutes.";
		   
		   String email = employee.getEmail();
		   
		      EmailThread runnableThread = new EmailThread(subject,body,email);
		        
		        Thread t = new Thread(runnableThread);
		        
		        t.start(); 
		
	}

	
}
