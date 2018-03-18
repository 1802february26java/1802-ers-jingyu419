package com.revature.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
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
					+ " VALUES(NULL,?,?,?,?,NULL,?,?,?,?)";

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
		
		logger.trace("Updating reimbursement.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
			int parameterIndex = 0;
			
			String sql = "UPDATE REIMBURSEMENT SET R_RESOLVED =?,"
			+ "R_AMOUNT=?,R_DESCRIPTION=?,R_RECEIPT=NULL,RS_ID=?,RT_ID=? WHERE R_ID=?";

			PreparedStatement statement = connection.prepareStatement(sql);								
								
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getResolved()));
			statement.setDouble(++parameterIndex, reimbursement.getAmount());
			statement.setString(++parameterIndex, reimbursement.getDescription());
			statement.setInt(++parameterIndex, reimbursement.getStatus().getId());
			statement.setInt(++parameterIndex, reimbursement.getType().getId());
			statement.setInt(++parameterIndex, reimbursement.getId());

			if(statement.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("Exception thrown while updating a Reimbursement", e);
		}
		
		return false;
		
	}

	
	@Override
	public Reimbursement select(int reimbursementId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Reimbursement> selectPending(int employeeId) {
		
        logger.trace("Getting pending reimburements for particular employee ID.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
			int parameterIndex = 0;
	
		    String sql = "SELECT RE.R_ID AS RE_R_ID , RE.R_REQUESTED AS RE_R_REQUESTED,"
		    		+ " RE.R_RESOLVED AS RE_R_RESOLVED, RE.R_AMOUNT AS RE_R_AMOUNT,"
		    		+ " RE.R_DESCRIPTION AS RE_R_DESCRIPTION, RE.R_RECEIPT AS RE_R_RECEIPT,"
		    		+ " RE.EMPLOYEE_ID AS RE_EMPLOYEE_ID, RE.MANAGER_ID AS RE_MANAGER_ID,"
		    		+ " RE.RS_ID AS RE_RS_ID, RE.RT_ID AS RE_RT_ID,E1.U_ID AS E1_U_ID,"
		    		+ " E1.U_FIRSTNAME AS E1_U_FIRSTNAME, E1.U_LASTNAME AS E1_U_LASTNAME,"
		    		+ " E1.U_USERNAME AS E1_U_USERNAME, E1.U_PASSWORD AS E1_U_PASSWORD,"
		    		+ " E1.U_EMAIL AS E1_U_EMAIL, E1.UR_ID AS E1_UR_ID, R1.UR_TYPE AS R1_UR_TYPE,"
		    		+ "E2.U_ID AS E2_U_ID, E2.U_FIRSTNAME AS E2_U_FIRSTNAME,"
		    		+ " E2.U_LASTNAME AS E2_U_LASTNAME, E2.U_USERNAME AS E2_U_USERNAME,"
		    		+ " E2.U_PASSWORD AS E2_U_PASSWORD, E2.U_EMAIL AS E2_U_EMAIL,"
		    		+ " E2.UR_ID AS E2_UR_ID, R2.UR_TYPE AS R2_UR_TYPE,RS.RS_STATUS AS RS_RS_STATUS,"
		    		+ " RT.RT_TYPE AS RT_RT_TYPE FROM REIMBURSEMENT RE,USER_T E1, USER_ROLE R1,"
		    		+ " USER_T E2,USER_ROLE R2,REIMBURSEMENT_STATUS RS,REIMBURSEMENT_TYPE RT"
		    		+ " WHERE RE.EMPLOYEE_ID = E1.U_ID AND E1.UR_ID = R1.UR_ID AND RE.MANAGER_ID = E2.U_ID"
		    		+ " AND E2.UR_ID = R2.UR_ID AND RE.RS_ID = RS.RS_ID AND RE.RT_ID = RT.RT_ID "
		    		+ "AND RE.EMPLOYEE_ID = ?";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setInt(++parameterIndex, employeeId);
			
			ResultSet result = statement.executeQuery();
            logger.trace("1 "+employeeId);
			Set<Reimbursement> set = new HashSet<>();
			
			while(result.next()) {
				set.add(new Reimbursement(
						result.getInt("RE_R_ID"),
                        result.getTimestamp("RE_R_REQUESTED").toLocalDateTime(),   
                        result.getTimestamp("RE_R_RESOLVED").toLocalDateTime(), 
                        result.getDouble("RE_R_AMOUNT"),
                        result.getString("RE_R_DESCRIPTION"),
                        //ADDING RECEIPT HERE   
                        new Employee(result.getInt("E1_U_ID"),result.getString("E1_U_FIRSTNAME"),
                        		result.getString("E1_U_LASTNAME"), result.getString("E1_U_USERNAME"),
                        		result.getString("E1_U_PASSWORD"),result.getString("E1_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E1_UR_ID"),result.getString("R1_UR_TYPE"))),
                        new Employee(result.getInt("E2_U_ID"),result.getString("E2_U_FIRSTNAME"),
                        		result.getString("E2_U_LASTNAME"), result.getString("E2_U_USERNAME"),
                        		result.getString("E2_U_PASSWORD"),result.getString("E2_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E2_UR_ID"),result.getString("R2_UR_TYPE"))),
                        new ReimbursementStatus(result.getInt("RE_RS_ID"),result.getString("RS_RS_STATUS")),
                        new ReimbursementType(result.getInt("RE_RT_ID"),result.getString("RT_RT_TYPE"))
						));
			}
			return set;
		} catch (SQLException e) {
			logger.error("Error while selecting all reimbursement for this particular employeeId.", e);
		}
		
		return new HashSet<>();		
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
		ReimbursementStatus status = new ReimbursementStatus(3,"APPROVED");
        //		Reimbursement Status
        //		3	APPROVED
        //		2	DECLINED
        //		1	PENDING
		ReimbursementType type = new ReimbursementType(3,"CERTIFICATION");
        //		Reimbursement Type
        //		3	CERTIFICATION
        //		2	COURSE
        //		1	OTHER
        //		4	TRAVELING
		
//		File blob = new File("src/main/resources/images/test1.jpg");
//		InputStream in = null;
//		try {
//			in = new FileInputStream(blob);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}		
		
		Reimbursement reimbursement = new Reimbursement(21,LocalDateTime.now(),LocalDateTime.now(),
				     1000D,"Saturday",employee,manager,status,type); 
		
		//logger.trace("Inserting a new Reimbursement: "+repository.insert(reimbursement));
		//logger.trace(repository.update(reimbursement));
		 logger.trace(repository.selectPending(1));
		
	}

}
