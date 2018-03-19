package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.service.EmployeeServiceAlpha;


public class EmployeeInformationControllerAlpha implements EmployeeInformationController {

	private static Logger logger = Logger.getLogger(LoginControllerAlpha.class);

	private static EmployeeInformationController employeeInformationController = new EmployeeInformationControllerAlpha();
	
	private EmployeeInformationControllerAlpha() {}
	
	public static EmployeeInformationController getInstance() {
		
		return employeeInformationController;
		
	}
	
	
	@Override
	public Object registerEmployee(HttpServletRequest request) {
		
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


		if (EmployeeServiceAlpha.getInstance().createEmployee(employee)) {
			
			return new ClientMessage("REGISTRATION SUCCESSFUL");
			
		} else {
			
			return new ClientMessage("SOMETHING WENT WRONG");
			
		}
		
	}

	@Override
	public Object updateEmployee(HttpServletRequest request) {
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		
		Employee employee = new Employee(999, request.getParameter("firstName"),
		     request.getParameter("lastName"),loggedEmployee.getUsername(),
		     request.getParameter("password"),request.getParameter("email"),loggedEmployee.getEmployeeRole());

		if (EmployeeServiceAlpha.getInstance().updateEmployeeInformation(employee)) {
			return new ClientMessage("UPDATE EMPLOYEE INFORMATION SUCCESSFUL");
		} else {
			return new ClientMessage("SOMETHING WENT WRONG");
		}
		
		
	}

	@Override
	public Object viewEmployeeInformation(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object viewAllEmployees(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object usernameExists(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
