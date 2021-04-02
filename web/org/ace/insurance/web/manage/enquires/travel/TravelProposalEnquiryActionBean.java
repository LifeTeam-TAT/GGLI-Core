package org.ace.insurance.web.manage.enquires.travel;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.PaymentOrderCashReceiptDTO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
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

@ViewScoped
@ManagedBean(name = "TravelProposalEnquiryActionBean")
public class TravelProposalEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

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

	private TravelProposal travelProposal;
	private User user;
	private List<TravelProposal> travelProposalList;
	private WorkFlow workFlow;
	private EnquiryCriteria criteria;
	private String message;
	private boolean accessBranches;

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
		travelProposalList = travelProposalService.findTravelProposalByEnquiryCriteria(criteria);
		sortLists(travelProposalList);
	}

	public void sortLists(List<TravelProposal> travelProposalList) {
		Collections.sort(travelProposalList, new Comparator<TravelProposal>() {
			@Override
			public int compare(TravelProposal obj1, TravelProposal obj2) {
				if (obj1.getProposalNo().equals(obj2.getProposalNo()))
					return -1;
				else
					return obj1.getProposalNo().compareTo(obj2.getProposalNo());
			}
		});
	}

	public void findTrabelProposalListByEnquiryCriteria() {
		travelProposalList = travelProposalService.findTravelProposalByEnquiryCriteria(criteria);
	}

	public void ShowDetailTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public String editTravelProposal(TravelProposal travelProposal) {
		if (allowToEdit(travelProposal.getId())) {
			BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByTravelproposalId(travelProposal.getId());
			putParam("bancaassuranceProposal", bancaassuranceProposal);
			outjectTravelProposal(travelProposal);
			return "editTravelProposal";
		} else {
			return null;
		}
	}

	public void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		if (wf == null) {
			flag = false;
			this.message = "This proposal is not in the editable workflow phase. It's been paid. ";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
		} else {
			if (wf.getWorkflowTask().equals(WorkflowTask.CONFIRMATION) || wf.getWorkflowTask().equals(WorkflowTask.PAYMENT)) {
				List<Payment> paymentList = paymentService.findByProposal(refNo, PolicyReferenceType.TRAVEL_PROPOSAL, false);
				PaymentDTO payment = new PaymentDTO(paymentList);
				flag = false;
				if (payment != null) {
					flag = true;
				}
				this.message = "This proposal is not in the editable workflow phase. It's been paid. ";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");
			}
		}
		return flag;
	}

	private boolean allowToPrint(TravelProposal travelProposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			this.travelProposal = travelProposal;
			return true;
		}
	}

	String pdfDirPath = "";
	String fileName = "";
	String dirPath = "";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateCashReceipt(TravelProposal travelProposal) {
		if (allowToPrint(travelProposal, WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT)) {
			List<Payment> paymentList = paymentService.findByProposal(travelProposal.getId(), PolicyReferenceType.TRAVEL_PROPOSAL, null);
			PaymentDTO payment = new PaymentDTO(paymentList);
			PaymentOrderCashReceiptDTO orderDto = null;
			String reportName = "TravelProposalCashReceipt";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			String customerName = travelProposal.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			fileName = "Travel_" + customerName + "_Receipt" + ".pdf";
			if (payment.getPaymentChannel() != null && payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				orderDto = paymentService.getPaymentOrderCashReceipt(travelProposal, InsuranceType.TRAVEL_INSURANCE, user.getBranch(), payment);
			}
			DocumentBuilder.generateTravelCashReceipt(travelProposal, payment, dirPath, fileName, orderDto);
			PrimeFaces.current().executeScript("PF('issuePolicyPDFDialogSingle').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}

	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public List<TravelProposal> getTravelProposalList() {
		return travelProposalList;
	}

	public void setTravelProposalList(List<TravelProposal> travelProposalList) {
		this.travelProposalList = travelProposalList;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId());
	}

	public String getMessage() {
		return message;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

}
