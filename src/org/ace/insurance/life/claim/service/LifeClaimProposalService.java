package org.ace.insurance.life.claim.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.claimaccept.service.interfaces.IClaimAcceptedInfoService;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimBeneficiaryPerson;
import org.ace.insurance.life.claim.LifeClaimNotification;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeClaimSurvey;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifeHospitalizedClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.persistence.interfaces.ILifeClaimProposalDAO;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimNotificationService;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.web.manage.life.claim.LifeDisabilityPaymentCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "LifeClaimProposalService")
public class LifeClaimProposalService implements ILifeClaimProposalService {

	@Resource(name = "LifeClaimProposalDAO")
	private ILifeClaimProposalDAO lifeClaimProposalDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "ClaimAcceptedInfoService")
	private IClaimAcceptedInfoService claimAcceptedInfoService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowService;

	@Resource
	private ILifeClaimNotificationService notificationService;

	private void calculateClaimAmount(LifeClaimProposal claimProposal) {
		double totalClaimAmt = 0.00;
		for (LifePolicyClaim claim : claimProposal.getClaimList()) {
			if (claim instanceof LifeHospitalizedClaim) {
				LifeHospitalizedClaim h = (LifeHospitalizedClaim) claim;
				totalClaimAmt = totalClaimAmt + h.getHospitalizedAmount();
				for (LifeClaimBeneficiaryPerson benefitPerson : claimProposal.getBeneficiaryList()) {
					if (benefitPerson.getBeneficiaryPerson() != null) {
						double hospitalizedAmount = h.getHospitalizedAmount() * benefitPerson.getBeneficiaryPerson().getPercentage() / 100;
						benefitPerson.setHospitalizedAmount(hospitalizedAmount);
					} else {
						benefitPerson.setHospitalizedAmount(h.getHospitalizedAmount());
					}
				}
			}
			if (claim instanceof LifeDeathClaim) {
				LifeDeathClaim d = (LifeDeathClaim) claim;
				totalClaimAmt = totalClaimAmt + d.getDeathClaimAmount();
				for (LifeClaimBeneficiaryPerson benefitPerson : claimProposal.getBeneficiaryList()) {
					if (benefitPerson.getBeneficiaryPerson() != null) {
						double benefitDeathClaimAmt = d.getDeathClaimAmount() * benefitPerson.getBeneficiaryPerson().getPercentage() / 100;
						benefitPerson.setDeathClaimAmount(benefitDeathClaimAmt);
					}
				}
			}
			if (claim instanceof DisabilityLifeClaim) {
				DisabilityLifeClaim disability = (DisabilityLifeClaim) claim;
				totalClaimAmt = totalClaimAmt + disability.getDisabilityAmount();
				for (LifeClaimBeneficiaryPerson benefitPerson : claimProposal.getBeneficiaryList()) {
					benefitPerson.setDisabilityAmount(disability.getDisabilityAmount());
				}
			}
		}
		claimProposal.setTotalClaimAmont(totalClaimAmt);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewLifeClaimProposal(LifeClaimProposal claimProposal, LifeClaimNotification lifeClaimNotification, WorkFlowDTO workFlow) {
		try {
			String claimProposalNo = customIDGenerator.getNextId(SystemConstants.LIFE_CLAIM_PROPOSAL_NO, null, true);
			claimProposal.setClaimProposalNo(claimProposalNo);
			calculateClaimAmount(claimProposal);
			lifeClaimProposalDAO.insert(claimProposal);
			lifeClaimNotification.setClaimStatus(ClaimStatus.CLAIM_APPLIED);
			notificationService.updateLifeClaimNotification(lifeClaimNotification);
			workFlow.setReferenceNo(claimProposal.getId());
			workFlowDTOService.addNewWorkFlow(workFlow);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add LifeClaimProposal", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewLifeClaimSurvey(LifeClaimSurvey lifeClaimSurvey, WorkFlowDTO workFlow) {
		try {
			lifeClaimProposalDAO.update(lifeClaimSurvey.getLifeClaimProposal());
			lifeClaimProposalDAO.insert(lifeClaimSurvey);
			workFlowDTOService.updateWorkFlow(workFlow);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to add LifeClaimProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void approveLifeClaim(LifeClaimProposal claimProposal, WorkFlowDTO workFlow) {
		try {
			lifeClaimProposalDAO.update(claimProposal);
			workFlowDTOService.updateWorkFlow(workFlow);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to approve LifeClaimProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void informLifeClaim(ClaimAcceptedInfo claimAcceptedInfo, LifeClaimProposal lifeClaimProposal, WorkFlowDTO workFlow) {
		try {

			lifeClaimProposalDAO.update(lifeClaimProposal);
			if (claimAcceptedInfo != null) {
				if (claimAcceptedInfo.getClaimAmount() == 0.00) {
					workFlowDTOService.deleteWorkFlowByRefNo(lifeClaimProposal.getId());
				} else {
					workFlowDTOService.updateWorkFlow(workFlow);
				}
				claimAcceptedInfoService.addNewClaimAcceptedInfo(claimAcceptedInfo);
			} else {
				workFlowDTOService.updateWorkFlow(workFlow);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to inform LifeClaimProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirmLifeClaimPropsal(LifeClaimProposal claimProposal, PaymentDTO paymentDTO, WorkFlowDTO workFlow) {
		Payment payment = null;
		try {

			payment = new Payment();
			payment.setConfirmDate(new Date());
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			payment.setClaimAmount(paymentDTO.getClaimAmount());
			payment.setReferenceNo(claimProposal.getId());
			payment.setReferenceType(PolicyReferenceType.LIFE_CLAIM);
			// medicalClaimProposalDTO.setTotalAllBeneAmt(paymentDTO.getTotalClaimAmount());
			lifeClaimProposalDAO.update(claimProposal);
			paymentService.prePaymentAndTlfLifeClaimProposal(payment, claimProposal);
			workFlowDTOService.updateWorkFlow(workFlow);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm Life Claim", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentLifeClaimProposal(LifeClaimProposal claimProposal, List<Payment> paymentList) {
		// To set complete true in lifeClaimProposal
		lifeClaimProposalDAO.update(claimProposal);
		String currencyCode = CurrencyUtils.getCurrencyCode(claimProposal.getClaimPerson().getProduct().getCurrency());
		// To get SaplePoint from lifeClaimProposal
		paymentService.activatePaymentAndTLF(paymentList, null, null, currencyCode, claimProposal.getSalePoint());
		workFlowDTOService.deleteWorkFlowByRefNo(claimProposal.getId());
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLifeClaimProposal(LifeClaimProposal claimProposal) {
		try {
			lifeClaimProposalDAO.update(claimProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to update LifeClaimProposal", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteLifeClaimProposal(LifeClaimProposal claimProposal) {
		try {
			lifeClaimProposalDAO.delete(claimProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to delete LifeClaimProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeClaimProposal findLifeClaimProposalById(String id) {
		LifeClaimProposal result = null;
		try {
			result = lifeClaimProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find LifeClaimProposal", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<DisabilityLifeClaim> findDisabilityLifeClaimByLifeClaimProposalNo(LifeDisabilityPaymentCriteria criteria) {
		List<DisabilityLifeClaim> result = null;
		try {
			result = lifeClaimProposalDAO.findDisabilityLifeClaimByLifeClaimProposalNo(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find DisabilityLifeClaim", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void confirmLifeDisabilityClaim(DisabilityLifeClaim disabilityClaim, PaymentDTO paymentDTO, WorkFlowDTO workFlow) {
		Payment payment = null;
		try {

			payment = new Payment();
			payment.setConfirmDate(new Date());
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			payment.setClaimAmount(paymentDTO.getClaimAmount());
			payment.setReferenceNo(disabilityClaim.getId());
			payment.setReferenceType(PolicyReferenceType.LIFE_CLAIM);
			// medicalClaimProposalDTO.setTotalAllBeneAmt(paymentDTO.getTotalClaimAmount());
			lifeClaimProposalDAO.update(disabilityClaim);

			// paymentService.prePaymentAndTlfLifeClaimProposal(payment,
			// claimProposal);
			// workFlowDTOService.addNewWorkFlow(workFlow);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm Life Claim", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectLifeClaimPropsal(LifeClaimProposal claimProposal, WorkFlowDTO workFlow) {
		try {
			workFlowService.createWorkFlowHistory(workFlow);
			workFlowService.deleteWorkFlowByRefNo(claimProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm Life Claim", e);
		}
	}
}
