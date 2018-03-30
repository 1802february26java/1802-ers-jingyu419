package com.revature.test;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.service.EmployeeService;
import com.revature.service.EmployeeServiceAlpha;


public class EmployeeServiceTest {

	private static Logger logger = Logger.getLogger(EmployeeServiceTest.class);
	
	private EmployeeService employeeServiceTest;

	private Employee employeeTestSuccess;
	
	private Employee employeeTestFail;
    
	//Will execute before every @Test
	@Before
	public void setUp() {
	employeeServiceTest = EmployeeServiceAlpha.getInstance();		
		
	employeeTestSuccess = new Employee(21,"test1","employee","testemployee","123456","jy350200@gmail.com",new EmployeeRole(1,"EMPLOYEE"));
	
	employeeTestFail = new Employee(100,"bb","bb","bbb","bbb","bbb",new EmployeeRole(1,"EMPLOYEE"));
	
	}
			
			
	@Test
	public void userLoginSuccessTest() {
				
	 logger.trace("Testing employeeLoginSuccess.");	    
	  boolean loginSuccess = false;	    
	  logger.trace(employeeTestSuccess);
	  Employee employee = employeeServiceTest.authenticate(employeeTestSuccess);
	  logger.trace(employee.getId());
     loginSuccess = (employee.getId()>0)?true:false;
	  assertTrue(loginSuccess);
				
}
	
	@Test
	public void userLoginFailTest() {
				
	 logger.trace("Testing employeeLoginFail.");	    
	  boolean loginFail = false;	    
	  logger.trace(employeeTestFail);
	  Employee employee = employeeServiceTest.authenticate(employeeTestFail);
	  logger.trace(employee.getId());
     loginFail = (employee.getId()>0)?false:true;
	  assertTrue(loginFail);
	
}
	
	
}
