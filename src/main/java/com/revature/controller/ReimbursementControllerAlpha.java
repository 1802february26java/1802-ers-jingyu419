package com.revature.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

import com.revature.ajax.ClientMessage;
import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.service.ReimbursementServiceAlpha;
import com.revature.thread.EmailThread;

public class ReimbursementControllerAlpha implements ReimbursementController {

	private static Logger logger = Logger.getLogger(ReimbursementControllerAlpha.class);

	private static ReimbursementController reimbursementController = new ReimbursementControllerAlpha();
	
	private ReimbursementControllerAlpha() {}
	
	public static ReimbursementController getInstance() {
		
		return reimbursementController;
		
	}
	
	@Override
	public Object submitRequest(HttpServletRequest request) throws IOException, ServletException {
		
        logger.trace("This is a create reimbursement method");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		//If he/she is a manager not a regular employee
		if(loggedEmployee.getEmployeeRole().getId()==2){
			
			logger.trace("Creating a reimbursement can only be done by regular Employee.");
			return "403.html";
			
		}
		
		if (request.getMethod().equals("GET")) {
			
			logger.trace("The employee only wants a view back.");
			return "reimbursement.html";
			
		}

		
		/* Logic for POST */
		Part receiptPart = request.getPart("reimbursementImage");
        logger.trace(receiptPart.getSize());
        String imageName = Paths.get(receiptPart.getSubmittedFileName()).getFileName().toString(); 
        logger.trace("fileName"+imageName);
        InputStream receiptFile =  receiptPart.getInputStream();
   

		ReimbursementStatus status = new ReimbursementStatus(1,"PENDING");
		ReimbursementType type = new ReimbursementType(Integer.parseInt(
				request.getParameter("reimbursementTypeId")),request.getParameter("reimbursementTypeName"));
		//Only employee ID matters here.
		Employee manager = new Employee(21,"TEST","MANAGER","testmanager","test","test@gmail.com",new EmployeeRole());
		
		Reimbursement reimbursement = new Reimbursement(99,LocalDateTime.now(),null,
				Double.parseDouble(request.getParameter("amount")),request.getParameter("description"),
				loggedEmployee,manager,status,type,receiptFile);
        logger.trace("current reimbursement data: "+reimbursement);
 
                
		if (ReimbursementServiceAlpha.getInstance().submitRequest(reimbursement)) {			
			return new ClientMessage("A REIMBURSEMENT HAS BEEN CREATED SUCCESSFULLY");
		} else {
			return new ClientMessage("SOMETHING WENT WRONG");
		}
		
			
	}

	@Override
	public Object singleRequest(HttpServletRequest request) throws IOException {

        logger.trace("This is a single request method");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		
		Reimbursement reimbursement = new Reimbursement(Integer.parseInt(request.getParameter("reimbursementId")));
		logger.trace("We are getting a single reimbursement back");
		return ReimbursementServiceAlpha.getInstance().getSingleRequest(reimbursement);
		
		
	}

	@Override
	public Object multipleRequests(HttpServletRequest request) {
	  
        logger.trace("This is a multi requests method");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		
		if(loggedEmployee.getEmployeeRole().getId()==1){
			logger.trace("This is a regular Employee, so only return back his/her reimbursement list.");
			/* Client is requesting the view. */
		    if(request.getParameter("fetch") == null) {
			     return "employee-pending-reimbursements-list.html";
		    }
		    else if(request.getParameter("fetch").equals("resolved")){
		    	logger.trace("The fetch is resolved");
		         return "employee-resolved-reimbursement-list.html";
		    }
		    
		    else if(request.getParameter("fetch").equals("finalized")){
				return ReimbursementServiceAlpha.getInstance().getUserFinalizedRequests(loggedEmployee);
			}
			else if (request.getParameter("fetch").equals("pending")){
				logger.trace(loggedEmployee);
			    return ReimbursementServiceAlpha.getInstance().getUserPendingRequests(loggedEmployee);  
			}
		 
			else{
				Set<Reimbursement> set = new HashSet<Reimbursement>(ReimbursementServiceAlpha.getInstance().getUserPendingRequests(loggedEmployee));
				set.addAll(ReimbursementServiceAlpha.getInstance().getUserFinalizedRequests(loggedEmployee));				
				return set;
			}
		}
		
		else{
			
			logger.trace("This is a manager, so return all reimbursement list he/she requested.");
			/* Client is requesting the view. */
		    if(request.getParameter("fetch") == null) {
			     return "manager-pending-reimbursements-list.html";
		    }
			
		    else if(request.getParameter("fetch").equals("resolved")){
		    	logger.trace("The fetch is resolved");
		         return "manager-resolved-reimbursement-list.html";
		    }
		    
			else if(request.getParameter("fetch").equals("finalized")){
				return ReimbursementServiceAlpha.getInstance().getAllResolvedRequests();
			}
			else if (request.getParameter("fetch").equals("pending")){
			return ReimbursementServiceAlpha.getInstance().getAllPendingRequests();
			}
		    
		    
		 	else if (request.getParameter("fetch").equals("viewSelected")){
		 	logger.trace("we are are view selected reimbursements section.");
		 	logger.trace(loggedEmployee);
		 	return "selected-reimbursements-list.html";
		 	}
		    
		 	else if (request.getParameter("fetch").equals("viewSelectedList")){
                logger.trace("we are getting back reimburesment list for particular Id: "+request.getParameter("selectedEmployeeId"));       
			 	Employee selectedEmployee = new Employee(Integer.parseInt(request.getParameter("selectedEmployeeId")));	
			 	logger.trace("select employee info: "+selectedEmployee);
				Set<Reimbursement> set = new HashSet<Reimbursement>(ReimbursementServiceAlpha.getInstance().getUserPendingRequests(selectedEmployee));
				set.addAll(ReimbursementServiceAlpha.getInstance().getUserFinalizedRequests(selectedEmployee));				
		 		return set;
			 	}
		 			
			else{
				logger.trace("Maybe here");
				return "manager-pending-reimbursements-list.html";
		
			}
			
			
		}
		
		
	}

	@Override
	public Object finalizeRequest(HttpServletRequest request) throws IOException {
		
        logger.trace("This method is used to finalize the reimbursement requests.");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		//If he/she is a employee not a manager
		if(loggedEmployee.getEmployeeRole().getId()==1){
			
			logger.trace("Creating a reimbursement can only be done by regular Employee.");
			return "403.html";
			
		}
		
		Reimbursement reimbursement = new Reimbursement(Integer.parseInt(request.getParameter("reimbursementId")));
		ReimbursementStatus status = new ReimbursementStatus(Integer.parseInt(request.getParameter("statusId")),request.getParameter("status"));
		logger.trace("We are getting a single reimbursement back");
		Reimbursement reimbursementToUpdate = ReimbursementServiceAlpha.getInstance().getSingleRequest(reimbursement);
		reimbursementToUpdate.setStatus(status);
		reimbursementToUpdate.setResolved(LocalDateTime.now());
		
		
		if (ReimbursementServiceAlpha.getInstance().finalizeRequest(reimbursementToUpdate)) {	
			
			sendEmailToEmployee(reimbursementToUpdate);
			
			return new ClientMessage("A REIMBURSEMENT HAS BEEN UPDATED SUCCESSFULLY");
		} else {
			return new ClientMessage("SOMETHING WENT WRONG");
		}
		
	}

	@Override
	public Object getRequestTypes(HttpServletRequest request) {
		
        logger.trace("This method is used to get reimbursement types");
		
        Employee loggedEmployee = (Employee) request.getSession().getAttribute("loggedEmployee");
		
		/* If customer is not logged in */
		if(loggedEmployee == null ) {
		   
			logger.trace("We do not have logged Employee information, return back to login page.");
			return "login.html";
			
		}
		logger.trace("return back all reimbursement types now.");
		return ReimbursementServiceAlpha.getInstance().getReimbursementTypes();
		 
	}
	
	private void sendEmailToEmployee(Reimbursement reimbursement){
		
		   String subject = "One of your reimbursement is finalized";
		   String body = "Here is your new your reimbursement status.\n"
		                 +"Reimbursement Id: "+reimbursement.getId()+"\n"
		                 +"Request Date: "+reimbursement.getRequested()+"\n"
		                 +"Resolved Date: "+reimbursement.getResolved()+"\n"
		                 +"Amount($): "+reimbursement.getAmount()+"\n"
		                 +"Description: "+reimbursement.getDescription()+"\n"
		                 +"Request status: "+reimbursement.getStatus().getStatus()+"\n"
		                 +"Request type: "+reimbursement.getType().getType()+"\n";
		   
		   String email = reimbursement.getRequester().getEmail();
		   
		      EmailThread runnableThread = new EmailThread(subject,body,email);
		        
		        Thread t = new Thread(runnableThread);
		        
		        t.start(); 
	}
	

}
