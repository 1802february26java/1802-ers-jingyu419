package com.revature.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.EmployeeRole;
import com.revature.model.EmployeeToken;
import com.revature.util.ConnectionUtil;

public class EmployeeRepositoryJdbc implements EmployeeRepository {

	private static Logger logger = Logger.getLogger(EmployeeRepositoryJdbc.class);
	
	@Override
	public boolean insert(Employee employee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Employee employee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Employee select(int employeeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Employee select(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Employee> selectAll() {
		
		logger.trace("Getting all employees.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
			String sql = "SELECT * FROM USER_T";
			
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
						new EmployeeRole(result.getInt("UR_ID"),getEmployeeRole(result.getInt("UR_ID")))
						));
			}
			
			return set;
			
		} catch (SQLException e) {
			
			logger.error("Error while selecting all employees.", e);
			
		}
		
		return null;		
	}

	@Override
	public String getPasswordHash(Employee employee) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insertEmployeeToken(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteEmployeeToken(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EmployeeToken selectEmployeeToken(EmployeeToken employeeToken) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * This method receive user role Id and return back user role.
	 * When user role ID = 1, return back "EMPLOYEE"
	 * When user role ID = 2, return back "MANAGER"
	 * @param roleId
	 * @return user role
	 */
	public String getEmployeeRole(int roleId){
		 
        logger.trace("Getting the user role.");
		
		try(Connection connection = ConnectionUtil.getConnection()) {
			
            int parameterIndex = 0;

			String sql = "SELECT UR_TYPE FROM USER_ROLE WHERE UR_ID = ?";
			
			PreparedStatement statement = connection.prepareStatement(sql);
			
			statement.setInt(++parameterIndex, roleId);

			ResultSet result = statement.executeQuery();
			
			while(result.next()) {
				
				return result.getString("UR_TYPE");
						
			}
			
		} catch (SQLException e) {
			
			logger.error("Error while find employee role with this user role Id.", e);
			
		}
		
		return null;		
		   
	}
	
	public static void main(String[] args){
		
		EmployeeRepositoryJdbc repository = new EmployeeRepositoryJdbc();
		logger.trace(repository.selectAll());
		//logger.trace(repository.getEmployeeRole(1));
	}

}
