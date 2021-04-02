package org.ace.insurance.life.premium.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.life.premium.LifePolicyBilling;
import org.ace.insurance.life.premium.LifePolicyPremium;
import org.ace.insurance.life.premium.persistence.interfaces.ILifePolicyPremiumDAO;
import org.ace.insurance.life.premium.service.interfaces.ILifePolicyPremiumService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author T&D Infomation System Ltd
 * @since 1.0.0
 * @date 2013/07/01
 */

@Service(value = "LifePolicyPremiumService")
public class LifePolicyPremiumService extends BaseService implements ILifePolicyPremiumService {

	@Resource(name = "LifePolicyPremiumDAO")
	private ILifePolicyPremiumDAO lifePolicyPremiumDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewLifePolicyPremium(LifePolicyPremium lifePolicyPremium) {
		try {
			setIDPrefixForInsert(lifePolicyPremium);
			lifePolicyPremiumDAO.insert(lifePolicyPremium);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifePolicyPremium", e);
		}
	}

	private void setIDPrefixForInsert(LifePolicyPremium lifePolicyPremium) {
		String premiumPrefix = getPrefix(LifePolicyPremium.class);
		String billingPrefix = getPrefix(LifePolicyBilling.class);
		lifePolicyPremium.setPrefix(premiumPrefix);
		if (lifePolicyPremium.getLifePolicyBillingList() != null) {
			for (LifePolicyBilling lifePolicyBilling : lifePolicyPremium.getLifePolicyBillingList()) {
				lifePolicyBilling.setPrefix(billingPrefix);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLifePolicyPremium(LifePolicyPremium lifePolicyPremium) {
		try {
			setIDPrefixForUpdate(lifePolicyPremium);
			lifePolicyPremiumDAO.update(lifePolicyPremium);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifePolicyPremium", e);
		}
	}

	private void setIDPrefixForUpdate(LifePolicyPremium lifePolicyPremium) {
		String premiumPrefix = getPrefix(LifePolicyPremium.class);
		String billingPrefix = getPrefix(LifePolicyBilling.class);
		lifePolicyPremium.setPrefix(premiumPrefix);
		if (lifePolicyPremium.getLifePolicyBillingList() != null) {
			for (LifePolicyBilling lifePolicyBilling : lifePolicyPremium.getLifePolicyBillingList()) {
				lifePolicyBilling.setPrefix(billingPrefix);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteLifePolicyPremium(LifePolicyPremium lifePolicyPremium) {
		try {
			lifePolicyPremiumDAO.delete(lifePolicyPremium);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a LifePolicyPremium", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifePolicyPremium findLifePolicyPremiumById(String id) {
		LifePolicyPremium result = null;
		try {
			result = lifePolicyPremiumDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifePolicyPremium (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicyPremium> findAllLifePolicyPremium() {
		List<LifePolicyPremium> result = null;
		try {
			result = lifePolicyPremiumDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of LifePolicyPremium)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifePolicyPremium> findLifePolicyPremiumByLifePolicyPremium(String lifePolicyNo, String customerID, String agentID, Date startDate, Date endDate) {
		List<LifePolicyPremium> result = null;
		try {
			result = lifePolicyPremiumDAO.findByLifePolicyPremium(lifePolicyNo, customerID, agentID, startDate, endDate);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find the active LifePolicyPremium", e);
		}
		return result;
	}
}