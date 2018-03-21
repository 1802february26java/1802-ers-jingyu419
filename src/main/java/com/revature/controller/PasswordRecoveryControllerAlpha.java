package com.revature.controller;

import javax.servlet.http.HttpServletRequest;

import com.revature.model.EmployeeToken;
import com.revature.service.EmployeeServiceAlpha;

public class PasswordRecoveryControllerAlpha implements PasswordRecoveryController {

	@Override
	public Object recoverPassword(HttpServletRequest request) {
		
		//EmployeeToken employeeToken = request.getParameter("employeeToken");
		
		//EmployeeServiceAlpha.getInstance().isTokenExpired(employeeToken);
		return null;
	}

	@Override
	public Object resetPassword(HttpServletRequest request) {
		
		
		return null;
	}

}
