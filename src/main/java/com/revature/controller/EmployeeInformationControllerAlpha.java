package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.service.EmployeeServiceAlpha;
import com.revature.thread.EmailThread;


public class EmployeeInformationControllerAlpha implements EmployeeInformationController {

	private static Logger logger = Logger.getLogger(LoginControllerAlpha.class);

	private static EmployeeInformationController employeeInformationController = new EmployeeInformationControllerAlpha();
	
	private EmployeeInformationControllerAlpha() {}
	
	public static EmployeeInformationController getInstance() {
		
		return employeeInformationController;
		
	}
	
	
	@Override
	public Object registerEmployee(HttpServletRequest request) {
		
		logger.trace("This is a register employee method");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		//If he/she is a employee not a manager
		if(loggedEmployee.getEmployeeRole().getId()==1){
			
			logger.trace("This loggedEmployeee is an employee type not a manager type.");
			return "403.html";
			
		}
		
		if (request.getMethod().equals("GET")) {
			
			logger.trace("The user only want a register page back");
			return "register.html";
			
		}

		/* Logic for POST */
		EmployeeRole employeeRole = new EmployeeRole(1,"EMPLOYEE");
		
		Employee employee = new Employee(999, request.getParameter("firstName"),
		     request.getParameter("lastName"),request.getParameter("username"),
		     request.getParameter("password"),request.getParameter("email"),employeeRole);
		
             logger.trace(employee);;

		if (EmployeeServiceAlpha.getInstance().createEmployee(employee)) {
		    
			sendEmailToEmployee(employee);
			
			return new ClientMessage("REGISTRATION SUCCESSFUL");
			
		} else {
			
			return new ClientMessage("SOMETHING WENT WRONG");
			
		}
		
	}

	@Override
	public Object updateEmployee(HttpServletRequest request) {
		
		logger.trace("This is a update employee information method.");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		
		loggedEmployee.setFirstName(request.getParameter("firstName"));
		loggedEmployee.setLastName(request.getParameter("lastName"));
		//loggedEmployee.setPassword(request.getParameter("password"));
	    loggedEmployee.setEmail(request.getParameter("email"));
        
		logger.trace("This is the updated employee we will send to database.");
		
		if (EmployeeServiceAlpha.getInstance().updateEmployeeInformation(loggedEmployee)) {
			return new ClientMessage("UPDATE EMPLOYEE INFORMATION SUCCESSFUL");
		} else {
			return new ClientMessage("SOMETHING WENT WRONG");
		}
		
		
	}

	@Override
	public Object viewEmployeeInformation(HttpServletRequest request) {

		logger.trace("This is a view employee information method.");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		
		logger.trace("Return back the most updated employee information.");
		if(loggedEmployee.getEmployeeRole().getId()==1){
		    if(request.getParameter("fetch") == null) {
			     return "employee-view-profile.html";
		    } else {
		        return EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		         }
		  }
		
		else{
			if(request.getParameter("fetch") == null) {
			     return "manager-view-profile.html";
		    } else {
		         return EmployeeServiceAlpha.getInstance().getEmployeeInformation(loggedEmployee);
		         }
		}
		
		
	}

	@Override
	public Object viewAllEmployees(HttpServletRequest request) {

        logger.trace("We are in viewAlEmployees method.");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		//If he/she is a employee not a manager
		if(loggedEmployee.getEmployeeRole().getId()==1){
			
			logger.trace("This loggedEmployeee is an employee type not a manager type.");
			return "403.html";
			
		}

		/* Client is requesting the view. */
		if(request.getParameter("fetch") == null) {
			return "list-employees.html";
		} else {
		return EmployeeServiceAlpha.getInstance().getAllEmployeesInformation();
		}
	}

	
	@Override
	public Object usernameExists(HttpServletRequest request) {
	       
		   logger.trace("We are in usernameExists method. return a message.");
		   
           Employee employee = new Employee(request.getParameter("username"));
           
		   if(EmployeeServiceAlpha.getInstance().isUsernameTaken(employee)){
			   return new ClientMessage("This username has been taken.");
		   }
		   else{
			   return new ClientMessage("This username has not been taken.");
		   }
			   
	}
	
	private void sendEmailToEmployee(Employee employee){
		
		   String subject = "New Employee Registration";
		   String body = "Here is your credential to ERS website.\n"+
		                 "Username: "+employee.getUsername()+"\n"
		                 +"Password: "+employee.getPassword()+"\n"
		                 +"Please visit below ERS link to login.\n"
		                 +"http://localhost:8085/ERS/";
		   String email = employee.getEmail();
		   
		      EmailThread runnableThread = new EmailThread(subject,body,email);
		        
		        Thread t = new Thread(runnableThread);
		        
		        t.start(); 
	}

}
