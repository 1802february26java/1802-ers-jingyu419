package com.revature.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * 
 * @author jingyu
 * Database Connection utility class. 
 *
 */
public class ConnectionUtil {

	 private static Logger logger = Logger.getLogger(ConnectionUtil.class);
	  
	  public static Connection getConnection() throws SQLException{
		  
		  String url = "jdbc:oracle:thin:@ersinstance.csj0uy543qxj.us-east-1.rds.amazonaws.com:1521:ORCL";
		  String username = "REIMBURSEMENT_DB";
		  String password = "p4ssw0rd";
		  
		  logger.trace("We are connecting database now.");
		  
		  return DriverManager.getConnection(url,username,password);
	  }
	  
	  /**
	  public static void main(String[] args){
		  
		  try(Connection connection = ConnectionUtil.getConnection()) {
			  
			  logger.trace("We are good.");
			  
		  } catch (SQLException e) {
			  
				logger.error("Connection is failed.", e);
				
			}
		  
	  }
	  */
}
