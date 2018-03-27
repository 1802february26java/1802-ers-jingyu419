package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.model.EmployeeToken;
import com.revature.service.EmployeeServiceAlpha;
import com.revature.util.FinalUtil;


public class PasswordRecoveryControllerAlpha implements PasswordRecoveryController {
    
    private static Logger logger = Logger.getLogger(PasswordRecoveryControllerAlpha.class);
	
    private static PasswordRecoveryController passwordRecoveryController = new PasswordRecoveryControllerAlpha();
	
	private PasswordRecoveryControllerAlpha() {}
	
	public static PasswordRecoveryController getInstance() {
		return passwordRecoveryController;
	}
	
	@Override
	public Object recoverPassword(HttpServletRequest request) {
		
		//If user only want a view returned back
		if(request.getMethod().equals("GET")) {
		      logger.trace("Thie action is GET, we return back a login view");
			  return "recoverPassword.html";
			}
		
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		logger.trace("We are in password reset controller. Here is the token: "+token);
		logger.trace(token+" "+password);
		
		EmployeeToken employeeToken = new EmployeeToken(token);
		employeeToken.setRequester(new Employee(0));
		if(EmployeeServiceAlpha.getInstance().isTokenExpired(employeeToken)){
			logger.trace("We are in password reset controller. The token is expired");
			return new ClientMessage("EXPIRED TOKEN");
			
		}
		else{
			logger.trace("We are in password reset controller. The token is valid");
			
			return new ClientMessage("VALID TOKEN");
		}
			
	}

	@Override
	public Object resetPassword(HttpServletRequest request) {
		
		//If user only want a view returned back
		if(request.getMethod().equals("GET")) {
		     logger.trace("Thie action is GET, we return back a login view");
			 return "resetPassword.html";
			}
		
		//Below is for POST
		   
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		
		Employee employee = new Employee();
		employee.setUsername(username);
		Employee employeeForToken = EmployeeServiceAlpha.getInstance().getEmployeeInformation(employee);
		logger.trace(employeeForToken.getEmail()+" "+email);
		//The email does not match
		if(employeeForToken.getEmail().equals(email)){
			logger.trace("Employee information we have: "+employeeForToken);
			
			if(EmployeeServiceAlpha.getInstance().createPasswordToken(employeeForToken)){
				
				logger.trace("We are password reset controller class.A reset email has been sent");
				
				return new ClientMessage(FinalUtil.CLIENT_MESSAGE_RESET_EMAIL_SENT);
				
			}
			else{
				
			return new ClientMessage(FinalUtil.CLIENT_MESSAGE_SOMETHING_WRONG);
			
			}	
			
		}
		else {
			return new ClientMessage(FinalUtil.CLIENT_MESSAGE_SOMETHING_WRONG);
		}
		
		
	}

	
	
}
