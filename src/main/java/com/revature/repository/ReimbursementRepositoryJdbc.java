package com.revature.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementStatus;
import com.revature.model.ReimbursementType;
import com.revature.util.ConnectionUtil;

public class ReimbursementRepositoryJdbc implements ReimbursementRepository {

	private static Logger logger = Logger.getLogger(ReimbursementRepositoryJdbc.class);
	
	@Override
	public boolean insert(Reimbursement reimbursement) {
		
		logger.trace("Inserting reimbursement.");
		try(Connection connection = ConnectionUtil.getConnection()) {
			int parameterIndex = 0;
			
			String sql = "INSERT INTO REIMBURSEMENT(R_ID,R_REQUESTED,R_RESOLVED,"
					+ "R_AMOUNT,R_DESCRIPTION,R_RECEIPT,EMPLOYEE_ID,MANAGER_ID,RS_ID,RT_ID)"
					+ " VALUES(NULL,?,?,?,?,?,?,?,?,?)";

			PreparedStatement statement = connection.prepareStatement(sql);								
								
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getRequested()));
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getResolved()));
			statement.setDouble(++parameterIndex, reimbursement.getAmount());
			statement.setString(++parameterIndex, reimbursement.getDescription());
			
			//BLOB			
//			File blob = new File("src/main/resources/images/test1.jpg");
//			FileInputStream in;
//			try {
//				in = new FileInputStream(blob);
//				statement.setBinaryStream(++parameterIndex, in, (int)blob.length()); 
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			
			//statement.setBlob(++parameterIndex, (java.sql.Blob)reimbursement.getReceipt());
			statement.setInt(++parameterIndex, reimbursement.getRequester().getId());
			statement.setInt(++parameterIndex, reimbursement.getApprover().getId());
			statement.setInt(++parameterIndex, reimbursement.getStatus().getId());
			statement.setInt(++parameterIndex, reimbursement.getType().getId());

			if(statement.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("Exception thrown while inserting new Reimbursement", e);
		}
		return false;
	}

	@Override
	public boolean update(Reimbursement reimbursement) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Reimbursement select(int reimbursementId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Reimbursement> selectPending(int employeeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Reimbursement> selectFinalized(int employeeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Reimbursement> selectAllPending() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Reimbursement> selectAllFinalized() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ReimbursementType> selectTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args){
		
		ReimbursementRepositoryJdbc repository = new ReimbursementRepositoryJdbc();

		Employee employee = new Employee(1,"test","test","test","test","test@gmail.com",new EmployeeRole());
		Employee manager = new Employee(21,"test","test","test","test","test@gmail.com",new EmployeeRole());
		ReimbursementStatus status = new ReimbursementStatus(1,"PENDING");
        //		Reimbursement Status
        //		3	APPROVED
        //		2	DECLINED
        //		1	PENDING
		ReimbursementType type = new ReimbursementType(1,"OTHER");
        //		Reimbursement Type
        //		3	CERTIFICATION
        //		2	COURSE
        //		1	OTHER
        //		4	TRAVELING
		
		File blob = new File("src/main/resources/images/test1.jpg");
		InputStream in = null;
		try {
			in = new FileInputStream(blob);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		
		Reimbursement reimbursement = new Reimbursement(100,LocalDateTime.now(),LocalDateTime.now(),
				     300D,"A new test",employee,manager,status,type); 
		
		logger.trace("Inserting a new Reimbursement: "+repository.insert(reimbursement));
		
	}

}
