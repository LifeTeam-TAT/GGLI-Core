package org.ace.insurance.renewal.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;
import org.ace.insurance.renewal.persistence.interfaces.IRenewalNotificationDAO;
import org.ace.insurance.renewal.service.interfaces.IRenewalNotificationService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "RenewalNotificationService")
public class RenewalNotificationService implements IRenewalNotificationService {

	@Resource(name = "RenewalNotificationDAO")
	private IRenewalNotificationDAO renewalNotificationDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RenewalNotification> findMotorPolicies(RenewalNotificationCriteria criteria) {
		List<RenewalNotification> result = null;
		try {
			result = renewalNotificationDAO.findMotorPolicies(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeProposalReport by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RenewalNotification> findFirePolicies(RenewalNotificationCriteria criteria) {
		List<RenewalNotification> result = null;
		try {
			result = renewalNotificationDAO.findFirePolicies(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeProposalReport by criteria.", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<RenewalNotification> findGroupLifePolicies(String productId, RenewalNotificationCriteria criteria) {
		List<RenewalNotification> result = null;
		try {
			result = renewalNotificationDAO.findGroupLifePolicies(productId, criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find LifeProposalReport by criteria.", e);
		}
		return result;
	}

}
