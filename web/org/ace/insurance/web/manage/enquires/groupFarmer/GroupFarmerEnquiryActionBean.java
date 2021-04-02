package org.ace.insurance.web.manage.enquires.groupFarmer;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalDTO;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.WorkFlow;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "GroupFarmerEnquiryActionBean")
@ViewScoped
public class GroupFarmerEnquiryActionBean extends BaseBean {
	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{BancaassuraceProposalService}")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	public void setBancaassuraceProposalService(IBancaassuraceProposalService bancaassuraceProposalService) {
		this.bancaassuraceProposalService = bancaassuraceProposalService;
	}

	private User user;
	private WorkFlow workFlow;
	private EnquiryCriteria criteria;
	private String message;
	private boolean accessBranches;
	private List<GroupFarmerProposalDTO> groupFarmerProposalDTOList;
	private GroupFarmerProposal groupFarmerProposal;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		if (user.isAccessAllBranch()) {
			setAccessBranches(true);
		} else {
			criteria.setBranch(user.getBranch());
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		search();
		sortLists(groupFarmerProposalDTOList);
	}

	public void search() {
		List<GroupFarmerProposalDTO> proposalDTOList = groupFarmerProposalService.findGroupFarmerProposalByEnquiryCriteria(criteria);
		for (GroupFarmerProposalDTO dto : proposalDTOList) {
			List<LifeProposal> lifeProposallist = lifeProposalService.findAllLifeProposalByGroupFarmerId(dto.getId());
			dto.setLifeProposalList(lifeProposallist);
		}
		this.groupFarmerProposalDTOList = proposalDTOList;
	}

	public void sortLists(List<GroupFarmerProposalDTO> gfProposalList) {
		Collections.sort(gfProposalList, new Comparator<GroupFarmerProposalDTO>() {
			@Override
			public int compare(GroupFarmerProposalDTO obj1, GroupFarmerProposalDTO obj2) {
				if (obj1.getProposalNo().equals(obj2.getProposalNo()))
					return -1;
				else
					return obj1.getProposalNo().compareTo(obj2.getProposalNo());
			}
		});
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public void showDetailProposal(GroupFarmerProposalDTO dto) {
		this.groupFarmerProposal = new GroupFarmerProposal(dto);
	}

	public String editproposal(GroupFarmerProposalDTO proposal) {
		if (allowToEdit(proposal.getId())) {
			BancaassuranceProposal bancaassuranceProposal = bancaassuraceProposalService.findBancaassuranceProposalByLifeproposalId(proposal.getId());
			putParam("bancaassuranceProposal", bancaassuranceProposal);
			outjectGroupFarmerProposal(new GroupFarmerProposal(proposal));
			return "editGroupFarmerProposal";
		} else {
			return null;
		}
	}

	public void outjectGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		putParam("groupFarmerProposal", groupFarmerProposal);
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		if (wf == null) {
			flag = false;
			this.message = "This proposal is not in the editable workflow phase. It's been paid. ";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
		} else {
			if (wf.getWorkflowTask().equals(WorkflowTask.CONFIRMATION)) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");

			}
		}

		return flag;
	}

	private boolean allowToPrint(GroupFarmerProposalDTO proposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(proposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			this.message = "This proposal can't not print. It's currently at ";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			return true;
		}
	}

	private boolean allowToPrintForIssue(LifeProposal lifeProposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			this.message = "Does not allow to print, proposal does not finished  process yet";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			return true;
		}
	}

	String reportName = "";
	String pdfDirPath = "";
	String dirPath = "";
	String fileName = "";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateCashReceipt(GroupFarmerProposalDTO dto) {
		if (allowToPrint(dto, WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT)) {
			List<Payment> paymentList = paymentService.findByProposal(dto.getId(), PolicyReferenceType.GROUP_FARMER_PROPOSAL, null);
			PaymentDTO payment = new PaymentDTO(paymentList);
			reportName = "GroupFarmerCashReceipt";
			String customerName = dto.getOrganization().getName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			fileName = "GroupFarmer_" + customerName + "_Receipt" + ".pdf";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			DocumentBuilder.generateGroupFarmerCashReceipt(new GroupFarmerProposal(dto), payment, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}

	}

	public void generateFarmerPolicIssue(LifeProposal proposal) {
		LifePolicy lifePolicy = null;
		PaymentDTO payment = null;
		List<Payment> paymentList = null;
		if (allowToPrintForIssue(lifeProposalService.findLifeProposalById(proposal.getId()), WorkflowTask.ISSUING)) {
			lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(proposal.getId());
			paymentList = paymentService.findByProposal(proposal.getId(), PolicyReferenceType.LIFE_POLICY, true);
			payment = new PaymentDTO(paymentList);
			reportName = "FarmerPolicyIssue";
			String customerName = proposal.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			fileName = "Farmer_" + customerName + "_Issue" + ".pdf";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			DocumentBuilder.generateFarmerPolicy(lifePolicy, payment, dirPath, fileName);
			PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
		} else

		{
			this.message = "Does not allow to print, proposal does not finished  process yet";
		}
	}

	public void generateALLPolicyIssue(GroupFarmerProposalDTO dto) {
		List<LifeProposal> lifeProposallist = lifeProposalService.findAllLifeProposalByGroupFarmerId(dto.getId());
		if (lifeProposallist != null && !lifeProposallist.isEmpty()) {
			if (allowToPrintForIssue(lifeProposalService.findLifeProposalById(lifeProposallist.get(0).getId()), WorkflowTask.ISSUING)) {
				List<LifePolicy> lifePolicylist = lifePolicyService.findAllLifePolicyByGroupFarmerProposalID(dto.getId());
				reportName = "FarmerPolicyIssue";
				String customerName = dto.getOrganization().getName();
				if (customerName.contains("\\")) {
					customerName = customerName.replace("\\", "");
				}
				if (customerName.contains("/")) {
					customerName = customerName.replace("/", "");
				}
				fileName = "Farmer_" + customerName + "_Issue" + ".pdf";
				pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
				dirPath = getWebRootPath() + pdfDirPath;
				DocumentBuilder.generateGroupFarmerPolicy(lifePolicylist, dirPath, fileName);
				PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
			}
		} else {
			this.message = "Does not allow to print, proposal does not finished  process yet";
		}
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<GroupFarmerProposalDTO> getGroupFarmerProposalDTOList() {
		return groupFarmerProposalDTOList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

	public IPaymentService getPaymentService() {
		return paymentService;
	}

	public IProductService getProductService() {
		return productService;
	}

	public IWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public String getPdfDirPath() {
		return pdfDirPath;
	}

}
