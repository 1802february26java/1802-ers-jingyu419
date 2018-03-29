package com.revature.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
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
	
    private static ReimbursementRepository repository = new ReimbursementRepositoryJdbc();
	
	private ReimbursementRepositoryJdbc(){}
	
	public static ReimbursementRepository getInstance(){
		return repository;
	}
	
	@Override
	public boolean insert(Reimbursement reimbursement) throws IOException {
		
		logger.trace("Inserting reimbursement.");
		
        
		try(Connection connection = ConnectionUtil.getConnection()) {
			
			int parameterIndex = 0;
			
			String sql = "INSERT INTO REIMBURSEMENT(R_ID,R_REQUESTED,R_RESOLVED,"
					+ "R_AMOUNT,R_DESCRIPTION,R_RECEIPT,EMPLOYEE_ID,MANAGER_ID,RS_ID,RT_ID)"
					+ " VALUES(NULL,?,NULL,?,?,?,?,?,?,?)";

			PreparedStatement statement = connection.prepareStatement(sql);								
								
			statement.setTimestamp(++parameterIndex, Timestamp.valueOf(reimbursement.getRequested()));
			statement.setDouble(++parameterIndex, reimbursement.getAmount());
			statement.setString(++parameterIndex, reimbursement.getDescription());
			
			//Receipt
            statement.setBinaryStream(++parameterIndex, (InputStream)reimbursement.getReceipt());
            
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
		
        logger.trace("Getting a reimbursement based on particular reimbursement Id.");
		
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
		    		+ "AND R_ID = ?";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setInt(++parameterIndex, reimbursementId);
			
			ResultSet result = statement.executeQuery();
			
			Reimbursement reimbursement = new Reimbursement();
			
			while(result.next()) {
				reimbursement = new Reimbursement(
						result.getInt("RE_R_ID"),
                        result.getTimestamp("RE_R_REQUESTED").toLocalDateTime(),   
                        (result.getString("RS_RS_STATUS").equals("PENDING"))?null:result.getTimestamp("RE_R_RESOLVED").toLocalDateTime(), 
                        result.getDouble("RE_R_AMOUNT"),
                        result.getString("RE_R_DESCRIPTION"),
                         
                        new Employee(result.getInt("E1_U_ID"),result.getString("E1_U_FIRSTNAME"),
                        		result.getString("E1_U_LASTNAME"), result.getString("E1_U_USERNAME"),
                        		result.getString("E1_U_PASSWORD"),result.getString("E1_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E1_UR_ID"),result.getString("R1_UR_TYPE"))),
                        new Employee(result.getInt("E2_U_ID"),result.getString("E2_U_FIRSTNAME"),
                        		result.getString("E2_U_LASTNAME"), result.getString("E2_U_USERNAME"),
                        		result.getString("E2_U_PASSWORD"),result.getString("E2_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E2_UR_ID"),result.getString("R2_UR_TYPE"))),
                        new ReimbursementStatus(result.getInt("RE_RS_ID"),result.getString("RS_RS_STATUS")),
                        new ReimbursementType(result.getInt("RE_RT_ID"),result.getString("RT_RT_TYPE")),
                      //ADDING RECEIPT HERE  
                        result.getBlob("RE_R_RECEIPT")
						);
			}
			 logger.trace("We are converting from blob to base64");
			 logger.trace("step 1");
		      Blob b=(Blob)reimbursement.getReceipt();  
		      logger.trace("step 2");
		      logger.trace(b.length());
		      logger.trace("step 3");
		      logger.trace(b.getBytes(1, (int)b.length()));
		      byte[] bytes = b.getBytes(1, (int)b.length());
		      String encoded = Base64.getEncoder().encodeToString(bytes);
		        logger.trace("step 4: Base64: "+encoded);
		        reimbursement.setReceipt(encoded);
		        logger.trace("Current reimbursement: "+reimbursement);
		        logger.trace("current receipt data in the reimbursement:"+(String)reimbursement.getReceipt());
			return reimbursement;
		     
		} catch (SQLException e) {
			logger.error("Error while selecting all reimbursements for this particular reimbursement Id.", e);
		}
		
		return new Reimbursement();		
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
		    		+ "AND RS.RS_STATUS = 'PENDING' "
		    		+ "AND RE.EMPLOYEE_ID = ?";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setInt(++parameterIndex, employeeId);
			
			ResultSet result = statement.executeQuery();

			Set<Reimbursement> set = new HashSet<>();
			
			while(result.next()) {
				set.add(new Reimbursement(
						result.getInt("RE_R_ID"),
                        result.getTimestamp("RE_R_REQUESTED").toLocalDateTime(),   
                        (result.getString("RS_RS_STATUS").equals("PENDING"))?null:result.getTimestamp("RE_R_RESOLVED").toLocalDateTime(), 
                        result.getDouble("RE_R_AMOUNT"),
                        result.getString("RE_R_DESCRIPTION"), 
                        new Employee(result.getInt("E1_U_ID"),result.getString("E1_U_FIRSTNAME"),
                        		result.getString("E1_U_LASTNAME"), result.getString("E1_U_USERNAME"),
                        		result.getString("E1_U_PASSWORD"),result.getString("E1_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E1_UR_ID"),result.getString("R1_UR_TYPE"))),
                        new Employee(result.getInt("E2_U_ID"),result.getString("E2_U_FIRSTNAME"),
                        		result.getString("E2_U_LASTNAME"), result.getString("E2_U_USERNAME"),
                        		result.getString("E2_U_PASSWORD"),result.getString("E2_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E2_UR_ID"),result.getString("R2_UR_TYPE"))),
                        new ReimbursementStatus(result.getInt("RE_RS_ID"),result.getString("RS_RS_STATUS")),
                        new ReimbursementType(result.getInt("RE_RT_ID"),result.getString("RT_RT_TYPE")),
                      //ADDING RECEIPT HERE  
                        result.getBinaryStream("RE_R_RECEIPT")
						));
			}
			logger.trace(set);
			return set;
		} catch (SQLException e) {
			logger.error("Error while selecting all pending reimbursement for this particular employeeId.", e);
		}
		
		return new HashSet<>();		
	}

	
	@Override
	public Set<Reimbursement> selectFinalized(int employeeId) {
		
       logger.trace("Getting approved or declined reimburements for particular employee ID.");
		
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
		    		+ " AND (RS.RS_STATUS = 'DECLINED' OR RS.RS_STATUS='APPROVED')"
		    		+ " AND RE.EMPLOYEE_ID = ?";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setInt(++parameterIndex, employeeId);
			
			ResultSet result = statement.executeQuery();

			Set<Reimbursement> set = new HashSet<>();
			
			while(result.next()) {
				set.add(new Reimbursement(
						result.getInt("RE_R_ID"),
                        result.getTimestamp("RE_R_REQUESTED").toLocalDateTime(),   
                        (result.getString("RS_RS_STATUS").equals("PENDING"))?null:result.getTimestamp("RE_R_RESOLVED").toLocalDateTime(),  
                        result.getDouble("RE_R_AMOUNT"),
                        result.getString("RE_R_DESCRIPTION"),
                        new Employee(result.getInt("E1_U_ID"),result.getString("E1_U_FIRSTNAME"),
                        		result.getString("E1_U_LASTNAME"), result.getString("E1_U_USERNAME"),
                        		result.getString("E1_U_PASSWORD"),result.getString("E1_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E1_UR_ID"),result.getString("R1_UR_TYPE"))),
                        new Employee(result.getInt("E2_U_ID"),result.getString("E2_U_FIRSTNAME"),
                        		result.getString("E2_U_LASTNAME"), result.getString("E2_U_USERNAME"),
                        		result.getString("E2_U_PASSWORD"),result.getString("E2_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E2_UR_ID"),result.getString("R2_UR_TYPE"))),
                        new ReimbursementStatus(result.getInt("RE_RS_ID"),result.getString("RS_RS_STATUS")),
                        new ReimbursementType(result.getInt("RE_RT_ID"),result.getString("RT_RT_TYPE")),
                      //ADDING RECEIPT HERE  
                        result.getBinaryStream("RE_R_RECEIPT")
						));
			}
			logger.trace(set);
			return set;
		} catch (SQLException e) {
			logger.error("Error while selecting all approved or declined reimbursement for this particular employeeId.", e);
		}
		
		return new HashSet<>();		
	}

	@Override
	public Set<Reimbursement> selectAllPending() {
		
        logger.trace("Getting ALL pending reimburements.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
	
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
		    		+ "AND RS.RS_STATUS = 'PENDING'";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
						
			ResultSet result = statement.executeQuery();
			
			Set<Reimbursement> set = new HashSet<>();
			
			while(result.next()) {
				set.add(new Reimbursement(
						result.getInt("RE_R_ID"),
                        result.getTimestamp("RE_R_REQUESTED").toLocalDateTime(),   
                        (result.getString("RS_RS_STATUS").equals("PENDING"))?null:result.getTimestamp("RE_R_RESOLVED").toLocalDateTime(), 
                        result.getDouble("RE_R_AMOUNT"),
                        result.getString("RE_R_DESCRIPTION"),
                        new Employee(result.getInt("E1_U_ID"),result.getString("E1_U_FIRSTNAME"),
                        		result.getString("E1_U_LASTNAME"), result.getString("E1_U_USERNAME"),
                        		result.getString("E1_U_PASSWORD"),result.getString("E1_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E1_UR_ID"),result.getString("R1_UR_TYPE"))),
                        new Employee(result.getInt("E2_U_ID"),result.getString("E2_U_FIRSTNAME"),
                        		result.getString("E2_U_LASTNAME"), result.getString("E2_U_USERNAME"),
                        		result.getString("E2_U_PASSWORD"),result.getString("E2_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E2_UR_ID"),result.getString("R2_UR_TYPE"))),
                        new ReimbursementStatus(result.getInt("RE_RS_ID"),result.getString("RS_RS_STATUS")),
                        new ReimbursementType(result.getInt("RE_RT_ID"),result.getString("RT_RT_TYPE")),
                      //ADDING RECEIPT HERE  
                        result.getBlob("RE_R_RECEIPT")
						));
			}
			
			logger.trace(set);
			
			Iterator<Reimbursement> it = set.iterator();
			Set<Reimbursement> newSet = new HashSet<>();
		     while(it.hasNext()){
		        Reimbursement reimbursement = it.next();
		        logger.trace("We are converting from blob to base64");
				logger.trace("step 1");
				Blob b=(Blob)reimbursement.getReceipt();  
			    logger.trace("step 2");
			    logger.trace(b.length());
			    logger.trace("step 3");
			    logger.trace(b.getBytes(1, (int)b.length()));
			    byte[] bytes = b.getBytes(1, (int)b.length());
			    String encoded = Base64.getEncoder().encodeToString(bytes);
			    logger.trace("step 4: Base64: "+encoded);
			    reimbursement.setReceipt(encoded);
			    logger.trace("Current reimbursement: "+reimbursement);
			    logger.trace("current receipt data in t.he reimbursement:"+(String)reimbursement.getReceipt());
			    newSet.add(reimbursement);
		     }
			   logger.trace("newSet: "+newSet);
			return newSet;
		} catch (SQLException e) {
			logger.error("Error while selecting all PENDING reimbursement.", e);
		}
		
		return new HashSet<>();		
	}

	@Override
	public Set<Reimbursement> selectAllFinalized() {
		
        logger.trace("Getting ALL APPROVED OR DECLINED reimburements.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
	
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
		    		+ "AND (RS.RS_STATUS = 'DECLINED' OR RS.RS_STATUS='APPROVED')";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
						
			ResultSet result = statement.executeQuery();
			
			Set<Reimbursement> set = new HashSet<>();
			
			while(result.next()) {
				set.add(new Reimbursement(
						result.getInt("RE_R_ID"),
                        result.getTimestamp("RE_R_REQUESTED").toLocalDateTime(),   
                        (result.getString("RS_RS_STATUS").equals("PENDING"))?null:result.getTimestamp("RE_R_RESOLVED").toLocalDateTime(), 
                        result.getDouble("RE_R_AMOUNT"),
                        result.getString("RE_R_DESCRIPTION"),  
                        new Employee(result.getInt("E1_U_ID"),result.getString("E1_U_FIRSTNAME"),
                        		result.getString("E1_U_LASTNAME"), result.getString("E1_U_USERNAME"),
                        		result.getString("E1_U_PASSWORD"),result.getString("E1_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E1_UR_ID"),result.getString("R1_UR_TYPE"))),
                        new Employee(result.getInt("E2_U_ID"),result.getString("E2_U_FIRSTNAME"),
                        		result.getString("E2_U_LASTNAME"), result.getString("E2_U_USERNAME"),
                        		result.getString("E2_U_PASSWORD"),result.getString("E2_U_EMAIL"),
                        		new EmployeeRole(result.getInt("E2_UR_ID"),result.getString("R2_UR_TYPE"))),
                        new ReimbursementStatus(result.getInt("RE_RS_ID"),result.getString("RS_RS_STATUS")),
                        new ReimbursementType(result.getInt("RE_RT_ID"),result.getString("RT_RT_TYPE")),
                      //ADDING RECEIPT HERE  
                        result.getBinaryStream("RE_R_RECEIPT")
						));
			}
			return set;
		} catch (SQLException e) {
			logger.error("Error while selecting all APPROVED OR DECLINED reimbursement.", e);
		}
		
		return new HashSet<>();		

	}

	@Override
	public Set<ReimbursementType> selectTypes() {
		
       logger.trace("Getting ALL  reimburements types.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
	
		    String sql = "SELECT * FROM REIMBURSEMENT_TYPE";
		   
			PreparedStatement statement = connection.prepareStatement(sql);
						
			ResultSet result = statement.executeQuery();
			
			Set<ReimbursementType> set = new HashSet<>();
			
			while(result.next()) {
				set.add(new ReimbursementType(
						result.getInt("RT_ID"),result.getString("RT_TYPE")	
						));
			}
			
			return set;
			
		} catch (SQLException e) {
			logger.error("Error while selecting all reimbursement types.", e);
		}
		
		return new HashSet<>();		
	}
	

	public static void main(String[] args) {
		
		ReimbursementRepositoryJdbc repository = new ReimbursementRepositoryJdbc();

		Employee employee = new Employee(1,"test","test","test","test","test@gmail.com",new EmployeeRole());
		Employee manager = new Employee(21,"test","test","test","test","test@gmail.com",new EmployeeRole());
		ReimbursementStatus status = new ReimbursementStatus(2,"DECLINED");
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

	//	Reimbursement reimbursement = new Reimbursement(21,LocalDateTime.now(),null,
	//			     1000D,"Saturday",employee,manager,status,type); 
		
	//	logger.trace("Inserting a new Reimbursement: "+repository.insert(reimbursement));
		//logger.trace(repository.update(reimbursement));
		
		
//		
//    try(Connection connection = ConnectionUtil.getConnection()) {
	    // logger.trace("reimbursement"+repository.select(222));
//	      Reimbursement reimbursement = repository.select(222);
//	     logger.trace("save it to local 1");
//	      Blob b=(Blob)reimbursement.getReceipt();  
//	      logger.trace("2");
//	      logger.trace(b.length());
//	      logger.trace("3");
//	      logger.trace(b.getBytes(1, (int)b.length()));
//		   }
//         catch (SQLException e) {
//	      logger.error("Retrieved faied.", e);
//            }

        
		
		
//		logger.trace("save it to local 2");
	//	logger.trace(b.getBinaryStream());
	//	logger.trace(b.getBytes(1, 10));
//		logger.trace("save it to local 3");
	//	InputStream imgInS=b.getBinaryStream();
        
        //OutputStream imgOuS=new FileOutputStream(new File("test.png"));
		//byte barr[]=b.getBytes(1,(int)b.length());//1 means first image  
	
		//logger.trace("save it to local 3");             
		//FileOutputStream fout=new FileOutputStream("C:/picture/test.png");  
		//logger.trace("save it to local 4");   
		//fout.write(barr);  
		              
		//fout.close();  
		//InputStream receiptFile = (InputStream)reimbursement.getReceipt();
		//logger.trace(receiptFile.available());
		//OutputStream outputStream = new FileOutputStream("C:/picture/test.png");
		 //int bytesRead = -1;
		 //        byte[] buffer = new byte[1024];
		 //        while ((bytesRead = receiptFile.read(buffer)) != -1) {
		 //            outputStream.write(buffer, 0, bytesRead);
		 //        }
		 //        receiptFile.close();
		 //        outputStream.close();


 
		//logger.trace(repository.selectPending(90));
		 logger.trace(repository.selectAllPending());
		 // logger.trace(repository.selectAllFinalized());
		//logger.trace(repository.selectFinalized(1));
		//logger.trace(repository.selectTypes());
		
	}
   
}
