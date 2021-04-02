package org.ace.insurance.medical.proposal.frontService.proposalService;

/***************************************************************************************
 * @author <<Kyaw Myat Htut>>
 * @Date 2014-6-31
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.CustomerStatus;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.proposal.CustomerInfoStatus;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonGuardian;
import org.ace.insurance.medical.proposal.frontService.proposalService.interfaces.IAddNewMedicalProposalFrontService;
import org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.product.NcbYear;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.service.interfaces.ICustomerService;
import org.ace.insurance.system.common.organization.service.interfaces.IOrganizationService;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.Utils;
import org.ace.insurance.web.manage.medical.proposal.MedProDTO;
import org.ace.insurance.web.manage.medical.proposal.factory.MedicalProposalFactory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.exception.CustomIDGeneratorException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "AddNewMedicalProposalFrontService")
public class AddNewMedicalProposalFrontService extends BaseService implements IAddNewMedicalProposalFrontService {
	@Resource(name = "MedicalProposalService")
	public IMedicalProposalService medicalProposalServices;

	@Resource(name = "WorkFlowService")
	public IWorkFlowService workFlowService;

	@Resource(name = "CustomerService")
	public ICustomerService customerService;

	@Resource(name = "MedicalProposalDAO")
	private IMedicalProposalDAO medicalProposalDAO;

	@Resource(name = "BancaassuranceProposalDAO")
	private IBancaassuranceProposalDAO bancaassuranceProposalDAO;

	@Resource(name = "ProductService")
	private IProductService productService;
	private String medicalProposalNo;

	@Resource(name = "OrganizationService")
	public IOrganizationService organizationService;

	@Resource(name = "BancaassuranceProposalService")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	@Resource(name = "BancaassurancePolicyService")
	private IBancaassurancePolicyService bancaassuracePolicyService;

	@Resource(name = "BancaassuranceProposalHistoryService")
	private IBancaassuranceProposalHistoryService bancaassuranceProposalHistoryService;

	private void calculateNCBPremium(MedicalProposalInsuredPerson insuredPerson, MedicalPolicy oldPolicy) {

	}

	/**
	 * @see org.ace.insurance.medical.proposal.frontService.proposalService.interfaces.IAddNewMedicalProposalFrontService
	 *      #addNewMedicalProposal(org.ace.insurance.medical.proposal.MedicalProposal,
	 *      org.ace.insurance.common.WorkFlowDTO)
	 */
	/*
	 * used in MedicalUnderwritingProposal, MedicalRenewalProposal, ClonePolicy
	 * etc
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal addNewMedicalProposal(MedProDTO medicalProposalDTO, WorkFlowDTO workFlowDTO, String status, BancaassuranceProposal bancaassuranceProposal) {
		MedicalProposal medicalProposal = MedicalProposalFactory.getMedicalProposal(medicalProposalDTO);
		HealthType healthType = null;
		if (medicalProposalDTO.getCustomer() != null) {
			healthType = medicalProposalDTO.getCustomer().getHealthType();
		}

		else if (medicalProposalDTO.getOrganization() != null) {
			healthType = medicalProposalDTO.getOrganization().getHealthType();
		}
		switch (healthType) {
			case CRITICALILLNESS:
				medicalProposalNo = SystemConstants.CRITICAL_ILLNESS_PROPOSAL_NO;
				break;
			case HEALTH:
				medicalProposalNo = SystemConstants.HEALTH_PROPOSAL_NO;
				break;
			case MICROHEALTH:
				medicalProposalNo = SystemConstants.MICRO_HEALTH_PROPOSAL_NO;
				break;
			default:
				break;

		}
		try {
			List<MedicalProposalInsuredPerson> insuredPersonList = medicalProposal.getMedicalProposalInsuredPersonList();
			medicalProposalServices.calculatePremium(medicalProposal);
			String productCode = insuredPersonList.get(0).getProduct().getProductGroup().getProposalNoPrefix();
			String proposalNo = null;
			String beneficiaryNo = null;
			String guardianNo = null;
			Date startDate;
			Date endDate;
			int periodYear = 0;
			boolean sameCustomer = false;
			Customer customer = medicalProposal.getCustomer();
			int oldPaymentTerm, newPaymentTerm;
			float ncbPercent;
			MedicalPolicy oldMedicalPolicy = medicalProposal.getOldMedicalPolicy();
			for (MedicalProposalInsuredPerson insuredPerson : insuredPersonList) {
				periodYear = insuredPerson.getPeriodYears();
				if (ProposalType.RENEWAL.equals(medicalProposal.getProposalType())) {
					/* submittedDate is policyStartDate in medical */
					startDate = medicalProposal.getSubmittedDate();
					endDate = Utils.addYears(startDate, periodYear);
					oldPaymentTerm = oldMedicalPolicy.getPaymentType().getMonth() == 0 ? 1 : (12 / oldMedicalPolicy.getPaymentType().getMonth());
					newPaymentTerm = medicalProposal.getPaymentType().getMonth() == 0 ? 1 : (12 / medicalProposal.getPaymentType().getMonth());
					ncbPercent = insuredPersonList.get(0).getProduct().getProductGroup().getNCBPercentage(NcbYear.FIXED);
					MedicalPolicyInsuredPerson policyInsuredPerson = oldMedicalPolicy.getMedicalPolicyInsuredPerson(insuredPerson.getCustomer());
					double wholeNcbPremium = (policyInsuredPerson.getTotalPremium() + policyInsuredPerson.getTotalDiscountPremium()) * oldPaymentTerm * ncbPercent / 100;
					insuredPerson.setTotalNcbPremium(wholeNcbPremium / newPaymentTerm);
				} else {/* clone policy */
					if (oldMedicalPolicy != null) {
						startDate = oldMedicalPolicy.getPolicyInsuredPersonList().get(0).getStartDate();
						endDate = oldMedicalPolicy.getPolicyInsuredPersonList().get(0).getEndDate();
					} else {
						startDate = medicalProposal.getSubmittedDate();
						endDate = Utils.addYears(startDate, periodYear);
					}
				}

				insuredPerson.setAge(org.ace.insurance.common.Utils.getAgeForNextYear(insuredPerson.getCustomer().getDateOfBirth()));

				insuredPerson.setStartDate(startDate);
				insuredPerson.setEndDate(endDate);
				if (insuredPerson.getGuardian() != null) {
					guardianNo = customIDGenerator.getNextId(SystemConstants.GUARDIAN_NO, null, false);
					insuredPerson.getGuardian().setGuardianNo(guardianNo);
				}
			}

			// ------------------ handle customer -----------------------------

			/** New Customer **/
			if (customer != null) {
				if (customer.getId() == null) {
					CustomerInfoStatus s = new CustomerInfoStatus();
					s.setStatusName(CustomerStatus.CONTRACTOR);
					customer.addCustomerInfoStatus(s);
					customerService.addNewCustomer(customer);
				} else {
					/** Exiting Customer **/
					if (!existCustomerInfoStatus(customer.getCustomerStatusList(), CustomerStatus.CONTRACTOR)) {
						CustomerInfoStatus s = new CustomerInfoStatus();
						s.setStatusName(CustomerStatus.CONTRACTOR);
						customer.addCustomerInfoStatus(s);
					}
					customer = customerService.updateCustomer(customer);
				}
			}
			for (MedicalProposalInsuredPerson insuredPerson : insuredPersonList) {
				if (insuredPerson.isSameCustomer()) {
					insuredPerson.setCustomer(customer);
					if (!existCustomerInfoStatus(insuredPerson.getCustomer().getCustomerStatusList(), CustomerStatus.INSUREDPERSON)) {
						CustomerInfoStatus insuStatus = new CustomerInfoStatus();
						insuStatus.setStatusName(CustomerStatus.INSUREDPERSON);
						insuredPerson.getCustomer().addCustomerInfoStatus(insuStatus);
						customer = customerService.updateCustomer(insuredPerson.getCustomer());
					}
				} else {
					Customer insuredusCustomer = insuredPerson.getCustomer();
					if (insuredusCustomer.getId() == null) {
						CustomerInfoStatus insuStatus = new CustomerInfoStatus();
						insuStatus.setStatusName(CustomerStatus.INSUREDPERSON);
						insuredusCustomer.addCustomerInfoStatus(insuStatus);
						customerService.addNewCustomer(insuredusCustomer);
					} else {
						/** Exiting Customer **/
						if (!existCustomerInfoStatus(insuredusCustomer.getCustomerStatusList(), CustomerStatus.INSUREDPERSON)) {
							CustomerInfoStatus s = new CustomerInfoStatus();
							s.setStatusName(CustomerStatus.INSUREDPERSON);
							insuredusCustomer.addCustomerInfoStatus(s);
						}
						customerService.updateCustomer(insuredusCustomer);
					}
				}

				if (insuredPerson.getGuardian() != null) {
					// same as policy holder
					MedicalProposalInsuredPersonGuardian guardian = insuredPerson.getGuardian();
					if (guardian.isSameCustomer()) {
						guardian.setCustomer(customer);
						if (!existCustomerInfoStatus(guardian.getCustomer().getCustomerStatusList(), CustomerStatus.GUARDIAN)) {
							CustomerInfoStatus insuStatus = new CustomerInfoStatus();
							insuStatus.setStatusName(CustomerStatus.GUARDIAN);
							guardian.getCustomer().addCustomerInfoStatus(insuStatus);
							customer = customerService.updateCustomer(guardian.getCustomer());
						}
					} else {
						Customer guardianCustomer = guardian.getCustomer();
						if (guardianCustomer.getId() == null) {
							CustomerInfoStatus insuStatus = new CustomerInfoStatus();
							insuStatus.setStatusName(CustomerStatus.GUARDIAN);
							guardianCustomer.addCustomerInfoStatus(insuStatus);
							customerService.addNewCustomer(guardianCustomer);
						} else {
							/** Exiting Customer **/
							if (!existCustomerInfoStatus(guardianCustomer.getCustomerStatusList(), CustomerStatus.GUARDIAN)) {
								CustomerInfoStatus s = new CustomerInfoStatus();
								s.setStatusName(CustomerStatus.GUARDIAN);
								guardianCustomer.addCustomerInfoStatus(s);
							}
							customerService.updateCustomer(guardianCustomer);
						}
					}

				}
			}

			// ------------------ handle customer end -------------------

			if (ProposalType.UNDERWRITING.equals(medicalProposal.getProposalType())) {
				proposalNo = customIDGenerator.getNextId(medicalProposalNo, productCode, true);
			}
			// TODO FIXME PSH HANDLE FOR NEW MEDICAL RENEWAL
			// if
			// (ProposalType.RENEWAL.equals(medicalProposal.getProposalType()))
			// {
			// proposalNo =
			// customIDGenerator.getNextId(SystemConstants.MEDICAL_RENEWAL_PROPOSAL_NO,
			// productCode, true);
			// }
			for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
				for (MedicalProposalInsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiaryNo = customIDGenerator.getNextId(SystemConstants.MEDICAL_BENEFICIARY_NO, null, false);
						beneficiary.setBeneficiaryNo(beneficiaryNo);
					}
				}
			}
			medicalProposal.setProposalNo(proposalNo);
			medicalProposal = medicalProposalDAO.insert(medicalProposal);

			if (medicalProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				bancaassuranceProposal.setMedicalProposal(medicalProposal);
				bancaassuraceProposalService.insert(bancaassuranceProposal);
			}

			if (null != workFlowDTO) {
				workFlowDTO.setReferenceNo(medicalProposal.getId());
				workFlowService.addNewWorkFlow(workFlowDTO);
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new MedicalProposal", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new MedicalProposal", e);
		}
		return medicalProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal updateMedicalProposal(MedProDTO medicalProposalDTO, WorkFlowDTO workFlowDTO, BancaassuranceProposal bancaassuranceProposal) {
		MedicalProposal medicalProposal = MedicalProposalFactory.getMedicalProposal(medicalProposalDTO);
		try {
			medicalProposalServices.calculatePremium(medicalProposal);
			boolean sameCustomer = false;
			String beneficiaryNo = null;
			String guardianNo = null;
			int paymentTerm = 0;
			List<MedicalProposalInsuredPerson> insuredPersonList = medicalProposal.getMedicalProposalInsuredPersonList();
			Customer customer = medicalProposal.getCustomer();
			// List<Customer> customerList = new ArrayList<Customer>();
			// Customer proposalCustomer = medicalProposal.getCustomer();
			// customerList.add(proposalCustomer);
			for (MedicalProposalInsuredPerson insuredPerson : insuredPersonList) {
				paymentTerm = insuredPerson.getPeriodYears();
				insuredPerson.setAge(org.ace.insurance.common.Utils.getAgeForNextYear(insuredPerson.getCustomer().getDateOfBirth()));
				Date startDate = medicalProposal.getSubmittedDate();
				Date endDate = Utils.addYears(startDate, paymentTerm);
				insuredPerson.setStartDate(startDate);
				insuredPerson.setEndDate(endDate);
				// if (!customerList.contains(insuredPerson.getCustomer())) {
				// customerList.add(insuredPerson.getCustomer());
				// }
				if (insuredPerson.getGuardian() != null) {
					guardianNo = customIDGenerator.getNextId(SystemConstants.GUARDIAN_NO, null, false);
					insuredPerson.getGuardian().setGuardianNo(guardianNo);
				}
			}

			/** New Customer **/
			// for (Customer customer : customerList) {
			if (customer != null) {
				if (customer.getId() == null) {
					CustomerInfoStatus status = new CustomerInfoStatus();
					status.setStatusName(CustomerStatus.CONTRACTOR);
					customer.addCustomerInfoStatus(status);
					customerService.addNewCustomer(customer);
				} else {
					/** Exiting Customer **/
					if (!existCustomerInfoStatus(customer.getCustomerStatusList(), CustomerStatus.CONTRACTOR)) {
						CustomerInfoStatus status = new CustomerInfoStatus();
						status.setStatusName(CustomerStatus.CONTRACTOR);
						customer.addCustomerInfoStatus(status);
					}
					customer = customerService.updateCustomer(customer);
				}
			}
			for (MedicalProposalInsuredPerson insuredPerson : insuredPersonList) {
				if (insuredPerson.isSameCustomer()) {
					insuredPerson.setCustomer(customer);
					if (!existCustomerInfoStatus(insuredPerson.getCustomer().getCustomerStatusList(), CustomerStatus.INSUREDPERSON)) {
						CustomerInfoStatus insuStatus = new CustomerInfoStatus();
						insuStatus.setStatusName(CustomerStatus.INSUREDPERSON);
						insuredPerson.getCustomer().addCustomerInfoStatus(insuStatus);
						customer = customerService.updateCustomer(insuredPerson.getCustomer());
					}
				} else {
					Customer insuredusCustomer = insuredPerson.getCustomer();
					if (insuredusCustomer.getId() == null) {
						CustomerInfoStatus insuStatus = new CustomerInfoStatus();
						insuStatus.setStatusName(CustomerStatus.INSUREDPERSON);
						insuredusCustomer.addCustomerInfoStatus(insuStatus);
						customerService.addNewCustomer(insuredusCustomer);
					} else {
						/** Exiting Customer **/
						if (!existCustomerInfoStatus(insuredusCustomer.getCustomerStatusList(), CustomerStatus.INSUREDPERSON)) {
							CustomerInfoStatus status = new CustomerInfoStatus();
							status.setStatusName(CustomerStatus.INSUREDPERSON);
							insuredusCustomer.addCustomerInfoStatus(status);
						}
						customerService.updateCustomer(insuredusCustomer);
					}
				}

				if (insuredPerson.getGuardian() != null) {
					// same as policy holder
					MedicalProposalInsuredPersonGuardian guardian = insuredPerson.getGuardian();
					if (guardian.isSameCustomer()) {
						guardian.setCustomer(customer);
						if (!existCustomerInfoStatus(guardian.getCustomer().getCustomerStatusList(), CustomerStatus.GUARDIAN)) {
							CustomerInfoStatus insuStatus = new CustomerInfoStatus();
							insuStatus.setStatusName(CustomerStatus.GUARDIAN);
							guardian.getCustomer().addCustomerInfoStatus(insuStatus);
							customer = customerService.updateCustomer(guardian.getCustomer());
						}
					} else {
						Customer guardianCustomer = guardian.getCustomer();
						if (guardianCustomer.getId() == null) {
							CustomerInfoStatus insuStatus = new CustomerInfoStatus();
							insuStatus.setStatusName(CustomerStatus.GUARDIAN);
							guardianCustomer.addCustomerInfoStatus(insuStatus);
							customerService.addNewCustomer(guardianCustomer);
						} else {
							/** Exiting Customer **/
							if (!existCustomerInfoStatus(guardianCustomer.getCustomerStatusList(), CustomerStatus.GUARDIAN)) {
								CustomerInfoStatus status = new CustomerInfoStatus();
								status.setStatusName(CustomerStatus.GUARDIAN);
								guardianCustomer.addCustomerInfoStatus(status);
							}
							customerService.updateCustomer(guardianCustomer);
						}
					}

				}
			}

			for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
				for (MedicalProposalInsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
					if (beneficiary.getBeneficiaryNo() == null) {
						beneficiaryNo = customIDGenerator.getNextId(SystemConstants.MEDICAL_BENEFICIARY_NO, null, false);
						beneficiary.setBeneficiaryNo(beneficiaryNo);
					}
				}
			}

			medicalProposalDAO.update(medicalProposal);
			// To Change Bancaassurance from Other SaleChannel
			if (medicalProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				BancaassuranceProposal oldProposal = bancaassuranceProposal.getId() == null ? null : bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
				if (null != oldProposal) {
					bancaassuraceProposalService.update(oldProposal);
				} else {
					bancaassuranceProposal.setMedicalProposal(medicalProposal);
					bancaassuraceProposalService.insert(bancaassuranceProposal);
				}
			} else {
				// CreateHistory And Remove BancaassuranceProposal
				if (null != bancaassuranceProposal && null != bancaassuranceProposal.getId()) {
					BancaassuranceProposal oldProposal = bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
					String medicalproposalId = bancaassuranceProposal.getMedicalProposal().getId();
					if (null != oldProposal) {
						bancaassuraceProposalService.CreateHistoryAndRemoveBancaassuranceProposalByMedicalproposalId(medicalproposalId);

					}
				}

			}

			// if(null != bancaassuranceProposal) {
			// BancaassuranceProposal oldBancaassuranceProposal =
			// bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId());
			// if(null != oldBancaassuranceProposal) {
			// bancaassuranceProposalHistoryService.addNewBancaassuranceProposalHistory(oldBancaassuranceProposal);
			// }
			// }

			if (workFlowDTO != null) {
				workFlowService.updateWorkFlow(workFlowDTO, workFlowDTO.getWorkflowTask());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new MedicalProposal", e);
		} catch (CustomIDGeneratorException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new MedicalProposal for customer  id generator ", e);
		}
		return medicalProposal;
	}

	private boolean existCustomerInfoStatus(List<CustomerInfoStatus> cisList, CustomerStatus customerStatus) {
		boolean exist = false;
		for (CustomerInfoStatus c : cisList) {
			if (c.getStatusName().equals(customerStatus)) {
				exist = true;
				break;
			}
		}
		return exist;
	}

}
