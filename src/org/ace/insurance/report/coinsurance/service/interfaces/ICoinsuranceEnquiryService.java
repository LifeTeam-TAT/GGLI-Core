package org.ace.insurance.report.coinsurance.service.interfaces;

import java.util.List;

import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryCriteria;
import org.ace.insurance.report.coinsurance.CoinsuranceEnquiryDTO;

public interface ICoinsuranceEnquiryService {
	public List<CoinsuranceEnquiryDTO> findCoinsuranceReports(CoinsuranceEnquiryCriteria criteria);
	

}

