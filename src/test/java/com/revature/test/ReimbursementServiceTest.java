package com.revature.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.service.ReimbursementService;
import com.revature.service.ReimbursementServiceAlpha;

public class ReimbursementServiceTest {

	
private static Logger logger = Logger.getLogger(ReimbursementServiceTest.class);
	
	private ReimbursementService reimbursementServiceTest;
	private Employee employee;
	private Employee manager;
	private ReimbursementStatus status;
    private ReimbursementType type;
    private Reimbursement reimbursementTest;
	
	//Will execute before every @Test
	@Before
	public void setUp() {
	
    reimbursementServiceTest = ReimbursementServiceAlpha.getInstance();	
	employee = new Employee(41,"test1","employee","testemployee","123456","jy350200@gmail.com",new EmployeeRole(1,"EMPLOYEE"));
	manager = new Employee(21,"TEST","MANAGER","testmanager","123456","JY350200@GMAIL.COM",new EmployeeRole(2,"MANAGER"));
	
	status = new ReimbursementStatus(2,"DECLINED");
	type = new ReimbursementType(3,"CERTIFICATION");
	reimbursementTest = new Reimbursement(201,LocalDateTime.now(),null,
					     1000D,"new wed",employee,manager,status,type); 
	
	}
	
	@Test
	public void selectReimbursementTest() {
				
	 logger.trace("Testing select reimbursement.");	   
	 
	 try {
		Reimbursement reimbursement = reimbursementServiceTest.getSingleRequest(reimbursementTest);
		logger.trace(reimbursement);
		
		assertTrue(reimbursement.getId() == reimbursementTest.getId());
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
				
}
	
	//	Reimbursement reimbursement = new Reimbursement(21,LocalDateTime.now(),null,
	//			     1000D,"Saturday",employee,manager,status,type); 
		
	//	logger.trace("Inserting a new Reimbursement: "+repository.insert(reimbursement));
		//logger.trace(repository.update(reimbursement));
		
		
//		
//    try(Connection connection = ConnectionUtil.getConnection()) {
	    // logger.trace("reimbursement"+repository.select(222));
//	      Reimbursement reimbursement = repository.select(222);

	


}
