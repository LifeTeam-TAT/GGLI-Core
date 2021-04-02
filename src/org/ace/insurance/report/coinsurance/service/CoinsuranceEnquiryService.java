package org.ace.insurance.report.coinsurance.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryDTO;
import org.ace.insurance.report.coinsurance.persistence.interfaces.ICoinsuranceEnquiryDAO;
import org.ace.insurance.report.coinsurance.service.interfaces.ICoinsuranceEnquiryService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("CoinsuranceEnquiryService")
public class CoinsuranceEnquiryService implements ICoinsuranceEnquiryService {

	@Resource(name = "CoinsuranceDAO1")
	private ICoinsuranceEnquiryDAO coinsuranceEnquiryDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CoinsuranceEnquiryDTO> findCoinsuranceReports(

	CoinsuranceEnquiryCriteria criteria) {
		List<CoinsuranceEnquiryDTO> result = null;
		try {
			result = coinsuranceEnquiryDAO.findCoinsurances(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find CoinsuranceReports by criteria.", e);
		}
		return result;
	}

}