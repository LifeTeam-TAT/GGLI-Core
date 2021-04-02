package org.ace.insurance.web.manage.life.groupfarmer.policy;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.ComponentSystemEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalDTO;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;

@ManagedBean(name = "GroupFarmerPolicyIssueActionBean")
@ViewScoped
public class GroupFarmerPolicyIssueActionBean extends BaseBean {
	private static final String MESSAGE_PARAM_SUFFIX = "_PARAM";
	private static final String MESSAGE_REQUEST_PARAM_SUFFIX = "_REQUEST_PARAM";

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private GroupFarmerProposal groupFarmerProposal;
	private List<GroupFarmerProposalDTO> groupFarmerProposalDTOList;
	private PaymentDTO paymentDTO;
	private boolean printReview;
	private EnquiryCriteria criteria;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		resetCriteria();
	}

	public void search() {
		List<GroupFarmerProposalDTO> proposalDTOList = groupFarmerProposalService.findAllGroupFarmerProposalByisPaymentComplete(criteria);
		for (GroupFarmerProposalDTO dto : proposalDTOList) {
			List<LifeProposal> lifeProposallist = lifeProposalService.findAllLifeProposalByGroupFarmerId(dto.getId());
			dto.setLifeProposalList(lifeProposallist);
		}
		this.groupFarmerProposalDTOList = proposalDTOList;
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		search();
	}

	public double totalSumInsured(GroupFarmerProposal groupFarmerProposal) {
		double totalSumInsured = 0.0;
		List<LifeProposal> lifeProposallist = lifeProposalService.findAllLifeProposalByGroupFarmerId(groupFarmerProposal.getId());
		for (LifeProposal li : lifeProposallist) {
			totalSumInsured += li.getSumInsured();
		}
		return totalSumInsured;

	}

	public double totalPremium(GroupFarmerProposal groupFarmerProposal) {
		double totalPremium = 0.0;
		List<LifeProposal> lifeProposallist = lifeProposalService.findAllLifeProposalByGroupFarmerId(groupFarmerProposal.getId());
		for (LifeProposal li : lifeProposallist) {
			totalPremium += li.getPremium();
		}
		return totalPremium;

	}

	public void addNewFarmerPolicy(GroupFarmerProposalDTO groupFarmerProposal) {
		if (validate(groupFarmerProposal)) {
			groupFarmerProposalService.addNewFarmerPolicy(groupFarmerProposal.getLifeProposalList(), user);
			groupFarmerProposal.setShowIssueButton(true);
			addInfoMessage(null, MessageId.ISSUING_PROCESS_SUCCESS_PARAM, groupFarmerProposal.getProposalNo());
		}
	}

	public String prepareEditFarmerProposal(LifeProposal lifeProposal) {
		outjectLifeProposal(lifeProposal);
		return "editFarmerProposal";

	}

	private void outjectLifeProposal(LifeProposal lifeProposal) {
		putParam("lifeProposal", lifeProposal);
	}

	private boolean validate(GroupFarmerProposalDTO dto) {
		String formID = "proposalDetailsForm";
		boolean result = true;
		double totalSI = 0.0;
		int totalProposalInsuredPerson = 0;
		for (LifeProposal proposal : dto.getLifeProposalList()) {
			totalSI += proposal.getSumInsured();
			totalProposalInsuredPerson += proposal.getProposalInsuredPersonList().size();
		}
		if (totalSI == dto.getTotalSI() && totalProposalInsuredPerson == dto.getNoOfInsuredPerson()) {
			result = true;
		} else {
			addErrorMessage(formID + ":groupFarmerProposalTable", MessageId.TOTAL_SI_AND_INSUREDPERSON_MUST_BE_SAME, dto.getTotalSI(), dto.getNoOfInsuredPerson());
			result = false;
		}
		return result;
	}

	public String addNewInsuredPerson(GroupFarmerProposalDTO groupFarmerProposalDTO) {
		putParam("groupFarmerProposal", new GroupFarmerProposal(groupFarmerProposalDTO));
		return "addNewGroupFarmerInsuredPerson";
	}

	private final String reportName = "FarmerIssue";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generatePolicyIssue(LifeProposal lifeProposal) {
		LifePolicy lifePolicy = lifePolicyService.findByLifeProposalId(lifeProposal.getId());
		List<Payment> paymentList = paymentService.findByPolicy(lifeProposal.getGroupFarmerProposal().getId());
		paymentDTO = new PaymentDTO(paymentList);
		String customerName = lifePolicy.getCustomerName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "Farmer_" + customerName + "_Issue" + ".pdf";
		DocumentBuilder.generateFarmerPolicy(lifePolicy, paymentDTO, dirPath, fileName);

	}

	public void generateALLPolicyIssue(GroupFarmerProposalDTO groupfarmerDto) {
		String customerName = groupfarmerDto.getOrganization().getName();
		if (customerName.contains("\\")) {
			customerName = customerName.replace("\\", "");
		}
		if (customerName.contains("/")) {
			customerName = customerName.replace("/", "");
		}
		fileName = "Farmer_" + customerName + "_Issue" + ".pdf";
		List<LifePolicy> lifePolicylist = lifePolicyService.findAllLifePolicyByGroupFarmerProposalID(groupfarmerDto.getId());
		DocumentBuilder.generateGroupFarmerPolicy(lifePolicylist, dirPath, fileName);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkMessage(ComponentSystemEvent event) {
		ExternalContext extContext = getFacesContext().getExternalContext();
		String messageId = (String) extContext.getSessionMap().get(Constants.MESSAGE_ID);
		String proposalNo = (String) extContext.getSessionMap().get(Constants.PROPOSAL_NO);
		String requestNo = (String) extContext.getSessionMap().get(Constants.REQUEST_NO);
		if (messageId != null) {
			if (proposalNo != null) {
				addInfoMessage(null, messageId + MESSAGE_PARAM_SUFFIX, proposalNo);
				extContext.getSessionMap().remove(Constants.MESSAGE_ID);
				extContext.getSessionMap().remove(Constants.PROPOSAL_NO);
			} else {
				if (requestNo != null) {
					addInfoMessage(null, messageId + MESSAGE_REQUEST_PARAM_SUFFIX, requestNo);
					extContext.getSessionMap().remove(Constants.MESSAGE_ID);
					extContext.getSessionMap().remove(Constants.REQUEST_NO);
				} else {
					addInfoMessage(null, messageId);
					extContext.getSessionMap().remove(Constants.MESSAGE_ID);
				}
			}
		}
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public void setPaymentDTO(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public List<GroupFarmerProposalDTO> getGroupFarmerProposalDTOList() {
		return groupFarmerProposalDTOList;
	}

	public void setGroupFarmerProposalDTOList(List<GroupFarmerProposalDTO> groupFarmerProposalDTOList) {
		this.groupFarmerProposalDTOList = groupFarmerProposalDTOList;
	}

	public boolean isPrintReview() {
		return printReview;
	}

	public void setPrintReview(boolean printReview) {
		this.printReview = printReview;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

}
