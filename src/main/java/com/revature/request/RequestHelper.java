package com.revature.request;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.revature.controller.EmployeeInformationControllerAlpha;
import com.revature.controller.ErrorControllerAlpha;
import com.revature.controller.HomeControllerAlpha;
import com.revature.controller.LoginControllerAlpha;
import com.revature.controller.PasswordRecoveryControllerAlpha;
import com.revature.controller.ReimbursementControllerAlpha;

/**
 * The RequestHelper class is consulted by the MasterServlet and provides
 * him with a view URL or actual data that needs to be transferred to the
 * client.
 * 
 * It will execute a controller method depending on the requested URI.
 * 
 * Recommended to change this logic to consume a ControllerFactory.
 * 
 * @author Revature LLC
 */
public class RequestHelper {
	private static RequestHelper requestHelper;

	private RequestHelper() {}

	public static RequestHelper getRequestHelper() {
		if(requestHelper == null) {
			return new RequestHelper();
		}
		else {
			return requestHelper;
		}
	}
	
	/**
	 * Checks the URI within the request object passed by the MasterServlet
	 * and executes the right controller with a switch statement.
	 * 
	 * @param request
	 * 		  The request object which contains the solicited URI.
	 * @return A String containing the URI where the user should be
	 * forwarded, or data (any object) for AJAX requests.
	 * @throws ServletException 
	 * @throws IOException 
	 */
	public Object process(HttpServletRequest request) throws IOException, ServletException {
		switch(request.getRequestURI())
		{
		case "/ERS/login.do":
			return LoginControllerAlpha.getInstance().login(request);
			
		case "/ERS/logout.do":
			return LoginControllerAlpha.getInstance().logout(request);
			
		case "/ERS/register.do":
			return EmployeeInformationControllerAlpha.getInstance().registerEmployee(request);
		
		case "/ERS/updateInformation.do":
			return EmployeeInformationControllerAlpha.getInstance().updateEmployee(request);
		
		case "/ERS/viewInformation.do":
			return EmployeeInformationControllerAlpha.getInstance().viewEmployeeInformation(request);
		
		case "/ERS/viewAllEmployee.do":
			return EmployeeInformationControllerAlpha.getInstance().viewAllEmployees(request);
		
		case "/ERS/isUserExisted.do":
			return EmployeeInformationControllerAlpha.getInstance().usernameExists(request);
		
		case "/ERS/home.do":
			return HomeControllerAlpha.getInstance().showEmployeeHome(request);
		
		case "/ERS/submitRequest.do":
			return ReimbursementControllerAlpha.getInstance().submitRequest(request);
		
		case "/ERS/singleRequest.do":
			return ReimbursementControllerAlpha.getInstance().singleRequest(request);
		
		case "/ERS/multipleRequests.do":
			return ReimbursementControllerAlpha.getInstance().multipleRequests(request);
			
		case "/ERS/finalizeRequest.do":
			return ReimbursementControllerAlpha.getInstance().finalizeRequest(request);
			
		case "/ERS/getRequestTypes.do":
			return ReimbursementControllerAlpha.getInstance().getRequestTypes(request);
		case "/ERS/sendResetEmail.do":
			return PasswordRecoveryControllerAlpha.getInstance().resetPassword(request);				
		case "/ERS/resetPasswordRequest.do":
			return PasswordRecoveryControllerAlpha.getInstance().recoverPassword(request);				
				
		default:
			return new ErrorControllerAlpha().showError(request);
		}
	}
}
