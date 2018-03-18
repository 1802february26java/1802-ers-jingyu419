package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.service.EmployeeServiceAlpha;


public class LoginControllerAlpha implements LoginController {
    
	private static Logger logger = Logger.getLogger(LoginControllerAlpha.class);
	
    private static LoginController loginController = new LoginControllerAlpha();
	
	private LoginControllerAlpha() {}
	
	public static LoginController getInstance() {
		return loginController;
	}
	
	@Override
	public Object login(HttpServletRequest request) {
		//If user only want a view returned back
		if(request.getMethod().equals("GET")) {
			return "login.html";
		}
		//Below is for POST
		Employee loggedEmployee = EmployeeServiceAlpha.getInstance().authenticate(
					new Employee(request.getParameter("username"),
								 request.getParameter("password"))
				);
		
		/* If authentication failed */
		if(loggedEmployee.getId() == 0) {
			return new ClientMessage("AUTHENTICATION FAILED");
		}
		
		/* Store the customer information on the session */
		request.getSession().setAttribute("loggedEmployee", loggedEmployee);
		
		return loggedEmployee;
		
	}

	
	@Override
	public String logout(HttpServletRequest request) {
		
		request.getSession().invalidate();
		return "login.html";
	}

}
