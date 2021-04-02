package org.ace.insurance.report.coinsurance.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface ICoinsuranceEnquiryDAO {
	public List<CoinsuranceEnquiryDTO> findCoinsurances(CoinsuranceEnquiryCriteria coinsuranceEnquiryCriteria) throws DAOException;
	
}
