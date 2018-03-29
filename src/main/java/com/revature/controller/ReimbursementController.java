package com.revature.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * The ReimbursementController will consume the ReimbursementService
 * to provide CRUD operations for reimbursement requests.
 * 
 * Employees can submit requests, both regular and manager employees can see a 
 * specific reimbursement request, managers can see a whole list of reimbursements 
 * and finally, managers can approve or decline requests.
 * 
 * @author Revature LLC
 */
public interface ReimbursementController {

	/**
	 * Creates a reimbursement request.
	 * 
	 * This operation is only performed by regular employees.
	 * 
	 * It should return a message stating whether the reimbursement request
	 * was successfully created or not.
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public Object submitRequest(HttpServletRequest request) throws IOException, ServletException;
	
	/**
	 * Returns a single reimbursement request specified by the user.
	 * 
	 * This operation can be performed by regular and/or manager employees.
	 * @throws IOException 
	 */
	public Object singleRequest(HttpServletRequest request) throws IOException;
	
	/**
	 * Returns a collection of reimbursement requests.
	 * 
	 * This operation can be performed by regular and/or manager employees.
	 */
	public Object multipleRequests(HttpServletRequest request);
	
	/**
	 * Updates the status of a reimbursement request.
	 * 
	 * This operation can only be performed by managers.
	 * 
	 * It should return a message stating that the reimbursement request
	 * was successfully updated or not.
	 * @throws IOException 
	 */
	public Object finalizeRequest(HttpServletRequest request) throws IOException;
	
	/**
	 * Returns a collection of reimbursement types thats is displayed
	 * in a drop down on the view.
	 */
	public Object getRequestTypes(HttpServletRequest request);
}
