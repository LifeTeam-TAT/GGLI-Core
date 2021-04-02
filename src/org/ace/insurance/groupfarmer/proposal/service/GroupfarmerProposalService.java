package org.ace.insurance.groupfarmer.proposal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.TypesOfCharges;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalAttachment;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalDTO;
import org.ace.insurance.groupfarmer.proposal.persistance.interfaces.IGroupfarmerProposalDAO;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.bancassurance.policy.service.interfaces.IBancaassurancePolicyService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.persistence.interfaces.IBancaassuranceProposalDAO;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.bancassurance.proposalhistory.service.interfaces.IBancaassuranceProposalHistoryService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonAddon;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.payment.AccountPayment;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.StaffAgentCommission;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.util.FileHandler;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "GroupfarmerProposalService")
public class GroupfarmerProposalService extends BaseService implements IGroupfarmerProposalService {

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "GroupfarmerProposalDAO")
	private IGroupfarmerProposalDAO groupFarmerProposalDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "LifeProposalDAO")
	private ILifeProposalDAO lifeProposalDAO;

	@Resource(name = "LifePolicyDAO")
	private ILifePolicyDAO lifePolicyDAO;

	@Resource(name = "LifePolicyService")
	private ILifePolicyService lifePolicyService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowService;

	@Resource(name = "BancaassuranceProposalDAO")
	private IBancaassuranceProposalDAO bancaassuranceProposalDAO;

	@Resource(name = "BancaassuranceProposalService")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	@Resource(name = "BancaassurancePolicyService")
	private IBancaassurancePolicyService bancaassuracePolicyService;

	@Resource(name = "BancaassuranceProposalHistoryService")
	private IBancaassuranceProposalHistoryService bancaassuranceProposalHistoryService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO, Map<String, String> proposalUploadedFileMap,
			BancaassuranceProposal bancaassuranceProposal, String PROPOSAL_DIR, String tempDir, String uploadPath) {
		try {
			double premium = 0.0;
			String soruceId = groupFarmerProposal.getId();
			premium = (groupFarmerProposal.getTotalSI() / 100) * 1;
			groupFarmerProposal.setPremium(premium);
			setProposalNo(groupFarmerProposal);
			setIDPrefixForInsert(groupFarmerProposal);
			groupFarmerProposal.setPrefix(getPrefix(GroupFarmerProposal.class));
			groupFarmerProposalDAO.insert(groupFarmerProposal);

			if (groupFarmerProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
				bancaassuranceProposal.setGroupFarmerProposal(groupFarmerProposal);
				bancaassuraceProposalService.insert(bancaassuranceProposal);
			}

			String destinationId = groupFarmerProposal.getId();
			if (proposalUploadedFileMap.size() > 0) {
				List<GroupFarmerProposalAttachment> attachmentList = new ArrayList<GroupFarmerProposalAttachment>();
				for (String fileName : proposalUploadedFileMap.keySet()) {
					String filePath = PROPOSAL_DIR + destinationId + "/" + fileName;
					attachmentList.add(new GroupFarmerProposalAttachment(fileName, filePath, groupFarmerProposal));
				}
				groupFarmerProposal.setAttachmentList(attachmentList);
				try {
					FileHandler.moveFiles(uploadPath, tempDir + soruceId, PROPOSAL_DIR + destinationId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			groupFarmerProposalDAO.update(groupFarmerProposal);
			workFlowDTO.setReferenceNo(groupFarmerProposal.getId());
			workFlowDTO.setReferenceType(WorkflowReferenceType.GROUPFARMER_PROPOSAL);
			workFlowDTOService.addNewWorkFlow(workFlowDTO);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TravelProposal", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, BancaassuranceProposal bancaassuranceProposal) {
		try {
			groupFarmerProposalDAO.update(groupFarmerProposal);
			bancaassuranceProposalHistoryService.addNewBancaassuranceProposalHistory(bancaassuranceProposalDAO.findById(bancaassuranceProposal.getId()));
			bancaassuranceProposalDAO.update(bancaassuranceProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to Update a Group Farmer", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		try {
			groupFarmerProposalDAO.delete(groupFarmerProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to Update a Group Farmer", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public GroupFarmerProposal findGroupFarmerById(String id) {
		GroupFarmerProposal result = null;
		try {
			result = groupFarmerProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Group Farmer (ID : " + id + ")", e);
		}
		return result;
	}

	private void setProposalNo(GroupFarmerProposal groupFarmerProposal) {
		String proposalNo = null;
		String groupFarmerProposalNo = SystemConstants.GROUPFARMER_LIFE_PROPOSAL_NO;
		proposalNo = customIDGenerator.getNextId(groupFarmerProposalNo, null, true);
		groupFarmerProposal.setProposalNo(proposalNo);
	}

	private void setIDPrefixForInsert(GroupFarmerProposal groupFarmerProposal) {
		String tProposalPrefix = customIDGenerator.getPrefix(GroupFarmerProposal.class, false);
		String ePrefix = customIDGenerator.getPrefix(GroupFarmerProposal.class, false);
		groupFarmerProposal.setPrefix(tProposalPrefix);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO, Branch branch, String status) {

		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			double rate = 1.0;
			Payment payment = new Payment();
			payment.setPaymentType(groupFarmerProposal.getPaymentType());
			payment.setBank(paymentDTO.getBank());
			payment.setBankAccountNo(paymentDTO.getBankAccountNo());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.GROUP_FARMER_PROPOSAL);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());

			if (paymentDTO.getBankWallet() != null) {
				payment.setBankWallet(paymentDTO.getBankWallet());
				if (payment.getBankWallet().getTypesOfCharges().equals(TypesOfCharges.FLATRATE)) {
					payment.setBankCharges(paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(rate * (paymentDTO.getBankWallet().getCharges() + paymentDTO.getBankWallet().getAdditionalCharges()));
				} else {
					payment.setBankCharges((paymentDTO.getBankWallet().getCharges() * groupFarmerProposal.getPremium() / 100) + paymentDTO.getBankWallet().getAdditionalCharges());
					payment.setHomeBankCharges(
							(rate * (paymentDTO.getBankWallet().getCharges() * groupFarmerProposal.getPremium() / 100)) + paymentDTO.getBankWallet().getAdditionalCharges());
				}
			}
			payment.setDiscountPercent(paymentDTO.getDiscountPercent());
			payment.setReceivedDeno(paymentDTO.getReceivedDeno());
			payment.setRefundDeno(paymentDTO.getRefundDeno());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBasicPremium(groupFarmerProposal.getPremium());
			payment.setAmount(payment.getTotalAmount());
			payment.setHomeAmount(payment.getTotalAmount());
			payment.setHomePremium(payment.getBasicPremium());

			payment.setFromTerm(1);
			payment.setToTerm(1);
			payment.setReferenceNo(groupFarmerProposal.getId());
			paymentList.add(payment);
			groupFarmerProposalDAO.update(groupFarmerProposal);
			paymentList = paymentService.prePayment(paymentList);

			prePaymentGroupFarmerProposal(groupFarmerProposal, paymentList, branch, status);

			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.CHEQUE) || paymentDTO.getPaymentChannel().equals(PaymentChannel.SUNDRY)) {
				prePaymentGroupFarmerProposalTransfer(groupFarmerProposal, paymentList, branch);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm a MedicalProposal", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void prePaymentGroupFarmerProposalTransfer(GroupFarmerProposal groupFarmerProposal, List<Payment> paymentList, Branch branch) {
		try {
			Product product = productService.findProductById(ProductIDConfig.getFarmerId());
			String currencyCode = CurrencyUtils.getCurrencyCode(product.getCurrency());
			String currencyId = product.getCurrency().getId();
			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			for (Payment payment : paymentList) {
				accountPaymentList.add(new AccountPayment(payment.getAccountBank().getAcode(), payment));
			}

			paymentService.activatePaymentTransfer(accountPaymentList, groupFarmerProposal.getOrganization().getId(), branch, false, currencyId,
					groupFarmerProposal.getSalePoint());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to pre-payment a MotorProposal ID : " + groupFarmerProposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	private void prePaymentGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, List<Payment> paymentList, Branch branch, String status) {
		try {
			List<AgentCommission> agentCommissionList = null;
			List<StaffAgentCommission> staffagentCommissionList = null;
			List<LifePolicy> policyList = lifePolicyDAO.findByProposalId(groupFarmerProposal.getId());
			PolicyReferenceType referenceType = paymentList.get(0).getReferenceType();
			Product product = productService.findProductById(ProductIDConfig.getFarmerId());
			String currencyID = product.getCurrency().getId();
			if (groupFarmerProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double totalCommission = 0.0;
				double commissionPercent = product.getFirstCommission();
				if (commissionPercent > 0) {
					double termPremium = groupFarmerProposal.getPremium();
					totalCommission = (termPremium / 100) * commissionPercent;
				}
				PolicyReferenceType policyReferenceType = PolicyReferenceType.GROUP_FARMER_PROPOSAL;
				agentCommissionList.add(new AgentCommission(groupFarmerProposal.getId(), policyReferenceType, groupFarmerProposal.getAgent(), totalCommission, new Date()));
			}

			List<AccountPayment> accountPaymentList = new ArrayList<AccountPayment>();
			String accountCode = product.getProductGroup().getAccountCode();
			for (Payment payment : paymentList) {
				if (groupFarmerProposal.getId().equals(payment.getReferenceNo())) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}
				if (payment.getIsReinstate()) {
					accountPaymentList.add(new AccountPayment(accountCode, payment));
					break;
				}
			}

			if (groupFarmerProposal.isStaffPlan()) {
				String eNo = accountPaymentList.get(0).getPayment().getId();
				Payment paymentByIndex = accountPaymentList.get(0).getPayment();
				staffagentCommissionList = new ArrayList<StaffAgentCommission>();
				for (LifePolicy lifePolicy : policyList) {
					// double firststaffagentCommissionList =
					// lifePolicy.getAgentCommission();
					staffagentCommissionList
							.add(new StaffAgentCommission(lifePolicy.getId(), referenceType, lifePolicy.getEips().getStaff(), lifePolicy.getEips().getAmount(), new Date()));
				}

				if (!groupFarmerProposal.getBranch().getId().equals(branch.getId())) {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, groupFarmerProposal.getBranch(), paymentByIndex, eNo, false, currencyID,
							groupFarmerProposal.getSalePoint());

				} else {
					paymentService.addStaffCommissionTLF(staffagentCommissionList, branch, paymentByIndex, eNo, false, currencyID, groupFarmerProposal.getSalePoint());
				}
			}

			if (!groupFarmerProposal.getBranch().getId().equals(branch.getId())) {
				paymentService.preActivatePaymentForInterBranch(accountPaymentList, groupFarmerProposal.getOrganization().getId(), branch, agentCommissionList, false,
						KeyFactorChecker.getKyatId(), groupFarmerProposal.getSalePoint(), groupFarmerProposal.getBranch());
			} else {
				paymentService.preActivatePayment(accountPaymentList, groupFarmerProposal.getOrganization().getId(), branch, agentCommissionList, false,
						KeyFactorChecker.getKyatId(), groupFarmerProposal.getSalePoint());
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MedicalProposal ID : " + groupFarmerProposal.getId(), e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, List<Payment> paymentList, Branch branch, String status) {
		try {

			List<AgentCommission> agentCommissionList = null;

			/* get receipt No */
			String receiptNo = paymentList.get(0).getReceiptNo();

			/* get agent commission of each policy */
			if (groupFarmerProposal.getAgent() != null) {
				agentCommissionList = new ArrayList<AgentCommission>();
				double firstAgentCommission = (groupFarmerProposal.getPremium() / 100) * 10;
				double commissionPercent = 10.0;
				Payment p = paymentService.findPaymentByReferenceNo(groupFarmerProposal.getId());
				AgentCommissionEntryType type = ProposalType.UNDERWRITING.equals(groupFarmerProposal.getProposalType()) ? AgentCommissionEntryType.UNDERWRITING
						: AgentCommissionEntryType.RENEWAL;
				agentCommissionList.add(new AgentCommission(groupFarmerProposal.getId(), paymentList.get(0).getReferenceType(), groupFarmerProposal.getAgent(),
						firstAgentCommission, new Date(), receiptNo, groupFarmerProposal.getPremium(), commissionPercent, type, p.getRate(), (p.getRate() * firstAgentCommission),
						p.getCur(), (p.getRate() * groupFarmerProposal.getPremium())));
			}

			/* add agent commission, activate TLF and Payment flag */
			// TODO FIXME PSH add salepoint
			paymentService.activatePaymentAndTLF(paymentList, agentCommissionList, branch, CurrencyUtils.getCurrencyCode(null), null);
			groupFarmerProposal.setPaymentComplete(true);
			groupFarmerProposalDAO.update(groupFarmerProposal);
			workFlowDTOService.deleteWorkFlowByRefNo(groupFarmerProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a MedicalProposal ID : " + groupFarmerProposal.getId(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewFarmerProposal(List<LifeProposal> proposalList, GroupFarmerProposal groupFarmerProposal) {
		try {
			for (LifeProposal lifeProposal : proposalList) {
				lifeProposal.setGroupFarmerProposal(groupFarmerProposal);
				lifeProposal.setBranch(groupFarmerProposal.getBranch());
				lifeProposal.setAgent(groupFarmerProposal.getAgent());
				lifeProposal.setReferral(groupFarmerProposal.getReferral());
				lifeProposal.setSaleMan(groupFarmerProposal.getSaleMan());
				lifeProposal.setOrganization(groupFarmerProposal.getOrganization());
				lifeProposal.setPaymentType(groupFarmerProposal.getPaymentType());
				lifeProposal.setSkipPaymentTLF(false);
				lifeProposal.setProposalType(groupFarmerProposal.getProposalType());
				lifeProposal.setSubmittedDate(groupFarmerProposal.getSubmittedDate());
				lifeProposal.setComplete(true);
				lifeProposal.setSalePoint(groupFarmerProposal.getSalePoint());
				setProposalNo(lifeProposal);
				setProposalInsPersonBenefiacialNo(lifeProposal);
				setIDPrefixForInsert(lifeProposal);
				lifeProposalDAO.insert(lifeProposal);

			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to insert a MedicalProposal ID : " + groupFarmerProposal.getId(), e);
		}
	}

	private void setIDPrefixForInsert(LifeProposal lifeProposal) {
		String mProposalPrefix = customIDGenerator.getPrefix(LifeProposal.class, true);
		String insuredPersonPrefix = customIDGenerator.getPrefix(ProposalInsuredPerson.class, false);
		String insuredPersonBeneficiaryPrefix = customIDGenerator.getPrefix(InsuredPersonBeneficiaries.class, false);
		String isPesAddOnPrefix = customIDGenerator.getPrefix(InsuredPersonKeyFactorValue.class, false);
		String insuredPersonAddOnPrefix = customIDGenerator.getPrefix(InsuredPersonAddon.class, false);

		lifeProposal.setPrefix(mProposalPrefix);
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			pv.setPrefix(insuredPersonPrefix);
			for (InsuredPersonKeyFactorValue vhKf : pv.getKeyFactorValueList()) {
				vhKf.setPrefix(isPesAddOnPrefix);
			}
			List<InsuredPersonAddon> insuredPersonAddOnList = pv.getInsuredPersonAddOnList();
			if (insuredPersonAddOnList != null && !insuredPersonAddOnList.isEmpty()) {
				for (InsuredPersonAddon insuredPersonAddOn : insuredPersonAddOnList) {
					insuredPersonAddOn.setPrefix(insuredPersonAddOnPrefix);
				}
			}
			List<InsuredPersonBeneficiaries> insuredPersonBeneficiaryList = pv.getInsuredPersonBeneficiariesList();
			if (insuredPersonBeneficiaryList != null && !insuredPersonBeneficiaryList.isEmpty()) {
				for (InsuredPersonBeneficiaries insuredpersonbeneficiary : insuredPersonBeneficiaryList) {
					insuredpersonbeneficiary.setPrefix(insuredPersonBeneficiaryPrefix);
				}
			}
		}
	}

	private void setProposalNo(LifeProposal lifeProposal) {
		String proposalNo = null;
		String productCode = lifeProposal.getProposalInsuredPersonList().get(0).getProduct().getProductGroup().getProposalNoPrefix();
		String farmerProposalNo = SystemConstants.FARMER_LIFE_PROPOSAL_NO;
		proposalNo = customIDGenerator.getNextId(farmerProposalNo, productCode, true);
		lifeProposal.setProposalNo(proposalNo);
	}

	private void setProposalInsPersonBenefiacialNo(LifeProposal proposal) {
		String beneficiaryNo = null;
		String insPersonCodeNo = null;
		insPersonCodeNo = customIDGenerator.getNextId(SystemConstants.LIFE_INSUREDPERSON_CODENO, null, false);
		for (ProposalInsuredPerson person : proposal.getProposalInsuredPersonList()) {
			if (person.getInsPersonCodeNo() == null) {
				person.setInsPersonCodeNo(insPersonCodeNo);
			}
			for (InsuredPersonBeneficiaries beneficiary : person.getInsuredPersonBeneficiariesList()) {
				if (beneficiary.getBeneficiaryNo() == null) {
					beneficiaryNo = customIDGenerator.getNextId(SystemConstants.LIFE_BENEFICIARY_NO, null, false);
					beneficiary.setBeneficiaryNo(beneficiaryNo);
				}
			}

		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupFarmerProposalDTO> findAllGroupFarmerProposalByisPaymentComplete(EnquiryCriteria criteria) {
		List<GroupFarmerProposalDTO> result = null;
		try {

			result = groupFarmerProposalDAO.findAllGroupFarmerProposalByisPaymentComplete(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find All GroupFarmerProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewFarmerPolicy(List<LifeProposal> lifeProposalList, User user) throws SystemException {
		try {
			GroupFarmerProposal groupFarmerProposal = groupFarmerProposalDAO.findById(lifeProposalList.get(0).getGroupFarmerProposal().getId());
			Date startDate = groupFarmerProposal.getSubmittedDate();
			Calendar cal = Calendar.getInstance();
			cal.setTime(startDate);
			cal.add(Calendar.YEAR, 1);
			Date endDate = cal.getTime();
			for (LifeProposal lifeProposal : lifeProposalList) {
				for (ProposalInsuredPerson insuredPerson : lifeProposal.getProposalInsuredPersonList()) {
					insuredPerson.setStartDate(startDate);
					insuredPerson.setEndDate(endDate);
				}
				LifePolicy lifePolicy = new LifePolicy(lifeProposal);
				List<PolicyInsuredPerson> personList = lifePolicy.getPolicyInsuredPersonList();
				Product product = personList.get(0).getProduct();
				String productCode = product.getProductGroup().getPolicyNoPrefix();
				lifePolicy.setPolicyNo(customIDGenerator.getNextId(SystemConstants.FARMER_LIFE_POLICY_NO, productCode, true));
				lifePolicy.setCommenmanceDate(startDate);
				lifePolicy.setDelFlag(false);
				lifePolicy.setLastPaymentTerm(1);
				lifePolicy.setActivedPolicyStartDate(startDate);
				lifePolicy.setActivedPolicyEndDate(endDate);
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeProposal.getId(), null, WorkflowTask.ISSUING, WorkflowReferenceType.GROUPFARMER_PROPOSAL, user, user);
				lifePolicyService.addNewLifePolicy(lifePolicy);
				workFlowService.createWorkFlowHistory(workFlowDTO);
			}
			groupFarmerProposal.setProcessComplete(true);
			groupFarmerProposalDAO.update(groupFarmerProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to create medicalPolicy", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject a TravelProposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO, Map<String, String> uploadedFileMap, String proposalDir,
			String temporyDir, String uploadPath) {
		try {
			double premium = 0.0;
			String sourceId = groupFarmerProposal.getId();
			String destId = groupFarmerProposal.getId();
			List<GroupFarmerProposalAttachment> attachmentList = new ArrayList<GroupFarmerProposalAttachment>();
			String filePath = null;
			for (String fileName : uploadedFileMap.keySet()) {
				filePath = proposalDir + destId + "/" + fileName;
				attachmentList.add(new GroupFarmerProposalAttachment(fileName, filePath, groupFarmerProposal));
			}
			groupFarmerProposal.getAttachmentList().clear();
			if (!attachmentList.isEmpty()) {
				groupFarmerProposal.setAttachmentList(attachmentList);
				try {
					FileHandler.moveFiles(uploadPath, temporyDir + sourceId, proposalDir + destId);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			premium = calculatePremium(groupFarmerProposal);
			groupFarmerProposal.setPremium(premium);
			groupFarmerProposalDAO.update(groupFarmerProposal);
			workFlowDTO.setReferenceNo(groupFarmerProposal.getId());
			workFlowDTO.setReferenceType(WorkflowReferenceType.GROUPFARMER_PROPOSAL);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a new Group Farmer Prposal", e);
		}
	}

	public double calculatePremium(GroupFarmerProposal proposal) {
		double premium = 0.0;
		premium = (proposal.getTotalSI() / 100) * 1;
		return premium;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupFarmerProposalDTO> findGroupFarmerProposalByEnquiryCriteria(EnquiryCriteria criteria) {
		List<GroupFarmerProposalDTO> result = null;
		try {
			result = groupFarmerProposalDAO.findGroupFarmerProposalByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a new Group Farmer Prposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayment(GroupFarmerProposal groupFarmerProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			groupFarmerProposalDAO.update(groupFarmerProposal);
			List<Payment> paymentList = paymentService.findByPolicy(groupFarmerProposal.getId());
			paymentService.deletePayments(paymentList);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete  Group Farmer Prposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<GroupFarmerProposal> finGroupFarmerProposalByPOByReceiptNo(String receiptNo, String bpmsReceiptNo) {
		List<GroupFarmerProposal> result = null;
		try {
			result = groupFarmerProposalDAO.finGroupFarmerProposalByPOByReceiptNo(receiptNo, bpmsReceiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Group Farmer Prposal", e);
		}
		return result;
	}
}
