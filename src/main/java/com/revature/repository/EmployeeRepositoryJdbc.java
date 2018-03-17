package com.revature.repository;

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
import com.revature.model.EmployeeToken;
import com.revature.util.ConnectionUtil;

public class EmployeeRepositoryJdbc implements EmployeeRepository {

	
	private static Logger logger = Logger.getLogger(EmployeeRepositoryJdbc.class);

	private static EmployeeRepository repository = new EmployeeRepositoryJdbc();
	
	private EmployeeRepositoryJdbc(){}
	
	public static EmployeeRepository getInstance(){
		return repository;
	}
	
	@Override
	public boolean insert(Employee employee) {
		
		   logger.trace("Inserting a new employee.");
		
		    try(Connection connection = ConnectionUtil.getConnection()) {
			
			int statementIndex = 0;
			
			String sql = "INSERT INTO USER_T VALUES (null,?,?,?,?,?,?)";

			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setString(++statementIndex, employee.getFirstName());
			statement.setString(++statementIndex, employee.getLastName());
			statement.setString(++statementIndex, employee.getUsername());
			statement.setString(++statementIndex, employee.getPassword());
			statement.setString(++statementIndex, employee.getEmail());
			statement.setInt(++statementIndex, employee.getEmployeeRole().getId());

			if(statement.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			logger.error("Exception creating a new employee", e);
		}
		return false;
	}

	@Override
	public boolean update(Employee employee) {
		
		logger.trace("Updating a new employee.");
		
	    try(Connection connection = ConnectionUtil.getConnection()) {
		
		int statementIndex = 0;
		
		String sql = "UPDATE USER_T SET U_FIRSTNAME = ?, U_LASTNAME = ?,"
				+ " U_PASSWORD = GET_HASH(?), U_EMAIL = ? WHERE U_ID = ?";

		PreparedStatement statement = connection.prepareStatement(sql);
		
		statement.setString(++statementIndex, employee.getFirstName());
		statement.setString(++statementIndex, employee.getLastName());
		statement.setString(++statementIndex, employee.getPassword());
		statement.setString(++statementIndex, employee.getEmail());
		statement.setInt(++statementIndex, employee.getId());

		if(statement.executeUpdate() > 0) {
			return true;
		}
	} catch (SQLException e) {
		logger.error("Exception updating a new employee", e);
	}
	return false;
	}

	@Override
	public Employee select(int employeeId) {
				
		logger.trace("Getting employee by their Id.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
			int parameterIndex = 0;
			
			String sql = "SELECT * FROM USER_T,USER_ROLE WHERE U_ID = ? AND USER_T.UR_ID = USER_ROLE.UR_ID";	
			
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setInt(++parameterIndex, employeeId);
			
			ResultSet result = statement.executeQuery();
		
			if(result.next()) {
				return new Employee(
						result.getInt("U_ID"),
						result.getString("U_FIRSTNAME"),
						result.getString("U_LASTNAME"),
						result.getString("U_USERNAME"),
						result.getString("U_PASSWORD"),
						result.getString("U_EMAIL"),
						new EmployeeRole(result.getInt("UR_ID"),result.getString("UR_TYPE"))
						);
			}
		} catch (SQLException e) {
			logger.error("Error while selecting employee by employee Id.", e);
		}
		return new Employee();
	}

	@Override
	public Employee select(String username) {
		
            logger.trace("Getting employee by his/her username.");
		
		    try(Connection connection = ConnectionUtil.getConnection()) {
			
			int parameterIndex = 0;
			
			String sql = "SELECT * FROM USER_T,USER_ROLE WHERE U_USERNAME = ? AND USER_T.UR_ID = USER_ROLE.UR_ID";	
			
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setString(++parameterIndex, username);
			
			ResultSet result = statement.executeQuery();
		
			if(result.next()) {
				return new Employee(
						result.getInt("U_ID"),
						result.getString("U_FIRSTNAME"),
						result.getString("U_LASTNAME"),
						result.getString("U_USERNAME"),
						result.getString("U_PASSWORD"),
						result.getString("U_EMAIL"),
						new EmployeeRole(result.getInt("UR_ID"),result.getString("UR_TYPE"))
						);
			}
		} catch (SQLException e) {
			logger.error("Error while selecting employee by employee username.", e);
		}
		return new Employee();
	}

	@Override
	public Set<Employee> selectAll() {
		
		logger.trace("Getting all employees.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
		    String sql = "SELECT * FROM USER_T,USER_ROLE WHERE USER_T.UR_ID = USER_ROLE.UR_ID";
			
			PreparedStatement statement = connection.prepareStatement(sql);

			ResultSet result = statement.executeQuery();

			Set<Employee> set = new HashSet<>();
			
			while(result.next()) {
				
				set.add(new Employee(	
						result.getInt("U_ID"),
						result.getString("U_FIRSTNAME"),
						result.getString("U_LASTNAME"),
						result.getString("U_USERNAME"),
						result.getString("U_PASSWORD"),
						result.getString("U_EMAIL"),
						new EmployeeRole(result.getInt("UR_ID"),result.getString("UR_TYPE"))
						));
			}
			
			return set;
			
		} catch (SQLException e) {
			
			logger.error("Error while selecting all employees.", e);
			
		}
		
		return new HashSet<>();		
	}

	@Override
	public String getPasswordHash(Employee employee) {
		
		logger.trace("Getting employee password hash.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
			int statementIndex = 0;
			
			String sql = "SELECT GET_HASH(?) AS HASH FROM DUAL";
			
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setString(++statementIndex, employee.getPassword());
			
			ResultSet result = statement.executeQuery();

			if(result.next()) {
				return result.getString("HASH");
			}
		} catch (SQLException e) {
			
			logger.warn("Exception getting employee hash", e);
			
		} 
		return new String();
	}

	@Override
	public boolean insertEmployeeToken(EmployeeToken employeeToken) {
		
		logger.trace("Inserting a new employee token.");
		
	    try(Connection connection = ConnectionUtil.getConnection()) {
		
		int statementIndex = 0;
		
		String sql = "INSERT INTO PASSWORD_RECOVERY VALUES(NULL,NULL,?,?)";

		PreparedStatement statement = connection.prepareStatement(sql);
		
		statement.setTimestamp(++statementIndex, Timestamp.valueOf(employeeToken.getCreationDate()));
		statement.setInt(++statementIndex, employeeToken.getRequester().getId());

		if(statement.executeUpdate() > 0) {
			return true;
		}
	} catch (SQLException e) {
		logger.error("Exception creating a new employee token", e);
	}
	return false;
	}

	@Override
	public boolean deleteEmployeeToken(EmployeeToken employeeToken) {
		
        logger.trace("Deleting an employee token.");
		
	    try(Connection connection = ConnectionUtil.getConnection()) {
		
		int statementIndex = 0;
		
		String sql = "DELETE PASSWORD_RECOVERY WHERE U_ID = ?";

		PreparedStatement statement = connection.prepareStatement(sql);
		
		statement.setInt(++statementIndex, employeeToken.getRequester().getId());

		if(statement.executeUpdate() > 0) {
			return true;
		}
	} catch (SQLException e) {
		logger.error("Exception deleting employee token", e);
	}
	    
	return false;
	
	}

	@Override
	public EmployeeToken selectEmployeeToken(EmployeeToken employeeToken) {
		
		logger.trace("Getting employee token.");
		
	    try(Connection connection = ConnectionUtil.getConnection()) {
		
		int parameterIndex = 0;
		
		String sql = "SELECT * FROM PASSWORD_RECOVERY WHERE U_ID = ?";	
		
		PreparedStatement statement = connection.prepareStatement(sql);
		
		statement.setInt(++parameterIndex, employeeToken.getRequester().getId());
		
		ResultSet result = statement.executeQuery();
	
		if(result.next()) {
			return new EmployeeToken(
					result.getInt("PR_ID"),
					result.getString("PR_TOKEN"),
					result.getTimestamp("PR_TIME").toLocalDateTime(),
					EmployeeRepositoryJdbc.getInstance().select(result.getInt("U_ID"))
					);
		}
	} catch (SQLException e) {
		logger.error("Error while selecting employee token.", e);
	}
	    
	return new EmployeeToken();
	
	}

	/*
	public static void main(String[] args){
		
		EmployeeRepositoryJdbc repository = new EmployeeRepositoryJdbc();
		Employee employee = new Employee(41,"test1","employee","testemployee","123456","jy350200@gmail.com",new EmployeeRole(1,"EMPLOYEE"));
		//logger.trace(repository.insert(employee));
		//logger.trace(repository.update(employee));
		//logger.trace(repository.select(21));
		//logger.trace(repository.select("testemployee"));
		//logger.trace(repository.getPasswordHash(employee));
       // logger.trace(repository.selectAll());
        EmployeeToken token = new EmployeeToken(LocalDateTime.now(),employee);
        //logger.trace(repository.insertEmployeeToken(token));
       // logger.trace(repository.selectEmployeeToken(token));
        logger.trace(repository.deleteEmployeeToken(token));
	}
	*/

}
