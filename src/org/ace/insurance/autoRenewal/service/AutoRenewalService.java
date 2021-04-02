package org.ace.insurance.autoRenewal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.autoRenewal.AutoRenewal;
import org.ace.insurance.autoRenewal.AutoRenewalCriteria;
import org.ace.insurance.autoRenewal.AutoRenewalDTO;
import org.ace.insurance.autoRenewal.AutoRenewalStatus;
import org.ace.insurance.autoRenewal.persistence.interfaces.IAutoRenewalDAO;
import org.ace.insurance.autoRenewal.service.interfaces.IAutoRenewalService;
import org.ace.insurance.coinsurance.service.interfaces.ICoinsuranceService;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.workflow.persistence.interfaces.IWorkFlowDAO;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "AutoRenewalService")
public class AutoRenewalService extends BaseService implements IAutoRenewalService {
	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "AcceptedInfoService")
	private IAcceptedInfoService acceptedInfoService;

	@Resource(name = "CoinsuranceService")
	private ICoinsuranceService coinsuranceService;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "AutoRenewalDAO")
	private IAutoRenewalDAO autoRenewalDAO;

	@Resource(name = "WorkFlowDAO")
	private IWorkFlowDAO workFlowDAO;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void scheduler() {
		List<AutoRenewal> newAutoRenewalList = new ArrayList<AutoRenewal>();
		List<AutoRenewal> commonAutoRenewalList = new ArrayList<AutoRenewal>();
		List<AutoRenewalDTO> motorAutoRenewalList = new ArrayList<AutoRenewalDTO>();
		List<AutoRenewalDTO> fireAutoRenewalList = new ArrayList<AutoRenewalDTO>();
		try {

			autoRenewalDAO.updateStatusToDeactivate();
			// get autoRenewal List
			newAutoRenewalList = changeDTOToInstance(motorAutoRenewalList, fireAutoRenewalList);
			for (AutoRenewal newObject : newAutoRenewalList) {
				innerLoop: for (AutoRenewal oldObject : autoRenewalDAO.findAllActiveInstance()) {
					if (newObject.getPolicyId().equals(oldObject.getPolicyId())) {
						commonAutoRenewalList.add(newObject);
						break innerLoop;
					}
				}
			}
			// remove All of overlap object
			newAutoRenewalList.removeAll(commonAutoRenewalList);
			// insert new Object and generate Notice Letter
			for (AutoRenewal newObject : newAutoRenewalList) {
				addNewAutoRenewal(newObject);

			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to insert AutoRenewal", e);
		}
	}

	public List<AutoRenewal> changeDTOToInstance(List<AutoRenewalDTO> dtoList, List<AutoRenewalDTO> dtoFireList) {
		List<AutoRenewal> autoRenewalList = new ArrayList<AutoRenewal>();
		AutoRenewal autoRenewal = null;
		for (AutoRenewalDTO dto : dtoList) {
			autoRenewal = new AutoRenewal();
			autoRenewal.setPolicyId(dto.getPolicyId());
			autoRenewal.setPolicyNo(dto.getPolicyNo());
			autoRenewal.setBranchId(dto.getBranchId());
			autoRenewal.setCustomerName(dto.getCustomerName());
			autoRenewal.setCustomerNrc(dto.getCustomerNrc());
			autoRenewal.setInsuranceType(dto.getInsuranceType());
			autoRenewal.setStatus(AutoRenewalStatus.ACTIVE);
			autoRenewal.setActivedPolicyStartDate(Utils.resetStartDate(dto.getActivedPolicyStartDate()));
			autoRenewal.setActivedPolicyEndDate(Utils.resetStartDate(dto.getActivedPolicyEndDate()));
			autoRenewal.setScheduleStartDate(Utils.resetStartDate(Utils.addDay(new Date(), 1)));
			autoRenewal.setScheduleEndDate(Utils.resetStartDate(Utils.addMonth(autoRenewal.getScheduleStartDate(), 1)));
			autoRenewalList.add(autoRenewal);
		}
		for (AutoRenewalDTO dto : dtoFireList) {
			autoRenewal = new AutoRenewal();
			autoRenewal.setPolicyId(dto.getPolicyId());
			autoRenewal.setPolicyNo(dto.getPolicyNo());
			autoRenewal.setBranchId(dto.getBranchId());
			autoRenewal.setCustomerName(dto.getCustomerName());
			autoRenewal.setCustomerNrc(dto.getCustomerNrc());
			autoRenewal.setInsuranceType(dto.getInsuranceType());
			autoRenewal.setStatus(AutoRenewalStatus.ACTIVE);
			autoRenewal.setActivedPolicyStartDate(Utils.resetStartDate(dto.getActivedPolicyStartDate()));
			autoRenewal.setActivedPolicyEndDate(Utils.resetStartDate(dto.getActivedPolicyEndDate()));
			autoRenewal.setScheduleStartDate(Utils.resetStartDate(Utils.addDay(new Date(), 1)));
			autoRenewal.setScheduleEndDate(Utils.resetStartDate(Utils.addMonth(autoRenewal.getScheduleStartDate(), 1)));
			autoRenewalList.add(autoRenewal);
		}
		return autoRenewalList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AutoRenewal addNewAutoRenewal(AutoRenewal autoRenewal) {
		try {
			// TODO FIX
			String autoRenewalNo = ""; // customIDGenerator.getNextIdForAutoRenewal(SystemConstants.MOTOR_AUTO_RENEWAL_NO);
			String prefix = customIDGenerator.getPrefixForAutoRenewal(AutoRenewal.class);
			autoRenewal.setPrefix(prefix);
			autoRenewal.setAutoRenewalNo(autoRenewalNo);
			autoRenewalDAO.insert(autoRenewal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to insert AutoRenewal", e);
		}
		return autoRenewal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAutoRenewal(AutoRenewal autoRenewal) {
		try {
			autoRenewalDAO.update(autoRenewal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update AutoRenewal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(AutoRenewalStatus status, String id) {
		try {
			autoRenewalDAO.updateStatus(status, id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update status of AutoRenewal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteAutoRenewal(AutoRenewal autoRenewal) {
		try {
			autoRenewalDAO.delete(autoRenewal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete AutoRenewal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public AutoRenewal findAutoRenewalById(String id) {
		AutoRenewal result = null;
		try {
			result = autoRenewalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find AutoRenewal", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AutoRenewal> findAllAutoRenewal() {
		List<AutoRenewal> result = null;
		try {
			result = autoRenewalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all of AutoRenewal", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AutoRenewal> findAllActiveAutoRenewal() {
		List<AutoRenewal> result = null;
		try {
			result = autoRenewalDAO.findAllActiveInstance();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find all Active AutoRenewal", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<AutoRenewal> findByAutoRenewalCriteria(AutoRenewalCriteria criteria) {
		List<AutoRenewal> result = null;
		try {
			result = autoRenewalDAO.findByCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find AutoRenewal by AutoRenewalCriteria", e);
		}
		return result;
	}

	private void calculateHomeAmountAcceptedInfo(double rate, AcceptedInfo acceptedInfo) {
		acceptedInfo.setRate(rate);
		acceptedInfo.setHomeAddOnPremium(rate * acceptedInfo.getAddOnPremium());
		acceptedInfo.setHomeAdministrationFees(rate * acceptedInfo.getAdministrationFees());
		acceptedInfo.setHomeBasicPremium(rate * acceptedInfo.getBasicPremium());
		acceptedInfo.setHomeEndorsementAddOnPremium(rate * acceptedInfo.getEndorsementAddOnPremium());
		acceptedInfo.setHomeEndorsementNetPremium(rate * acceptedInfo.getEndorsementNetPremium());
		acceptedInfo.setHomeNcbPremium(rate * acceptedInfo.getNcbPremium());
		acceptedInfo.setHomePenaltyPremium(rate * acceptedInfo.getPenaltyPremium());
		acceptedInfo.setHomeServicesCharges(rate * acceptedInfo.getServicesCharges());
		acceptedInfo.setHomeStampFees(rate * acceptedInfo.getStampFees());
	}

	private double getPremiumByPeriod(int periodMonth, double premium) {
		if (periodMonth <= 3) {
			premium = Utils.getPercentOf(35, premium);
		} else if (periodMonth <= 6 && periodMonth > 3) {
			premium = Utils.getPercentOf(60, premium);
		} else if (periodMonth <= 9 && periodMonth > 6) {
			premium = Utils.getPercentOf(85, premium);
		}
		return premium;
	}

	private boolean isWithinOneMonth(Date date) {
		Date plusOneMonth = Utils.addMonth(date, 1);
		Date present = Utils.resetStartDate(new Date());
		if (present.compareTo(plusOneMonth) <= 0) {
			return true;
		}
		return false;

	}

}
