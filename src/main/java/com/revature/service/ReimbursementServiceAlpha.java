package com.revature.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.revature.model.Employee;
import com.revature.model.Reimbursement;
import com.revature.model.ReimbursementType;
import com.revature.repository.ReimbursementRepositoryJdbc;

public class ReimbursementServiceAlpha implements ReimbursementService {
	
    private static Logger logger = Logger.getLogger(ReimbursementServiceAlpha.class);
	
    private static ReimbursementService reimbursementService = new ReimbursementServiceAlpha();
	
	private ReimbursementServiceAlpha() { }
	
	public static ReimbursementService getInstance() {
		return reimbursementService;
	}
	@Override
	public boolean submitRequest(Reimbursement reimbursement) throws IOException {
		
		// byte[] bytes = IOUtils.toByteArray(reimbursement.getReceipt());
	      //  String encoded = Base64.getEncoder().encodeToString(bytes);
	       // logger.trace("We are in service level.Base64: "+encoded);
		return ReimbursementRepositoryJdbc.getInstance().insert(reimbursement);
		
	}

	@Override
	public boolean finalizeRequest(Reimbursement reimbursement) {

        return ReimbursementRepositoryJdbc.getInstance().update(reimbursement);
		
	}

	@Override
	public Reimbursement getSingleRequest(Reimbursement reimbursement) throws IOException {
		
		return ReimbursementRepositoryJdbc.getInstance().select(reimbursement.getId());
		
	}

	@Override
	public Set<Reimbursement> getUserPendingRequests(Employee employee) {

        return ReimbursementRepositoryJdbc.getInstance().selectPending(employee.getId());
        
	}

	@Override
	public Set<Reimbursement> getUserFinalizedRequests(Employee employee) {
		
		return ReimbursementRepositoryJdbc.getInstance().selectFinalized(employee.getId());
		
	}

	@Override
	public Set<Reimbursement> getAllPendingRequests() {
		
		return ReimbursementRepositoryJdbc.getInstance().selectAllPending();
		
	}

	@Override
	public Set<Reimbursement> getAllResolvedRequests() {
		
		return ReimbursementRepositoryJdbc.getInstance().selectAllFinalized();
		
	}

	@Override
	public Set<ReimbursementType> getReimbursementTypes() {
		
		return ReimbursementRepositoryJdbc.getInstance().selectTypes();
		
	}

}
