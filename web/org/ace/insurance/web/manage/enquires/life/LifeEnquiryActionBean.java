package org.ace.insurance.web.manage.enquires.life;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RegNoSorter;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.life.snakebite.SnakeBitePolicy;
import org.ace.insurance.life.snakebite.service.interfaces.ISnakeBitePolicyService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.document.life.ShortEndowUnderwritingDOCFactory;
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
@ManagedBean(name = "LifeEnquiryActionBean")
public class LifeEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{SnakeBitePolicyService}")
	private ISnakeBitePolicyService snakeBitePolicyService;

	public void setSnakeBitePolicyService(ISnakeBitePolicyService snakeBitePolicyService) {
		this.snakeBitePolicyService = snakeBitePolicyService;
	}

	@ManagedProperty(value = "#{AcceptedInfoService}")
	private IAcceptedInfoService acceptedInfoService;

	public void setAcceptedInfoService(IAcceptedInfoService acceptedInfoService) {
		this.acceptedInfoService = acceptedInfoService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
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

	@ManagedProperty(value = "#{BancaassuranceProposalService}")
	private IBancaassuraceProposalService bancaassuranceProposalService;

	public void setBancaassuranceProposalService(IBancaassuraceProposalService bancaassuranceProposalService) {
		this.bancaassuranceProposalService = bancaassuranceProposalService;
	}

	private User user;
	private List<LPL001> lifeProposalList;
	private List<Product> productList;
	private WorkFlow workFlow;
	private EnquiryCriteria criteria;
	private boolean isAccessBranches;
	boolean approved = true;
	private AcceptedInfo acceptedInfo;
	private boolean isPublicLife;
	private boolean isGroupLife;
	private boolean isShortTermEndowmentLife;
	private boolean isShortTermSinglePremiumCreditLife;
	private boolean isSinglePremiumEndowmentLife;
	private boolean isSinglePremiumCreditLife;
	private boolean isSnakeBite;
	private boolean isSportMan;
	private boolean isSanction;
	private boolean isAcceptLetter;
	private boolean isCashReceipt;
	private boolean isPolicyIssue;
	private boolean isSchedule;
	private boolean isStudentLife;
	private boolean isSinglePrint;
	private boolean isPublicTermLife;
	private boolean isSimpleLife;
	private LifeProposal lifeProposal;
	private BancaassuranceProposal bancaassuranceProposal;
	private String keyFactor;
	
	@PostConstruct
	public void init() {
		resetCriteria();
		user = (User) getParam(Constants.LOGIN_USER);
		loadProductList();
		if (user.isAccessAllBranch()) {
			setIsAccessBranches(true);
		} else {
			criteria.setBranch(user.getBranch());
		}
	}

	public void loadProductList() {
		productList = productService.findProductByInsuranceType(InsuranceType.LIFE);
		String farmerId = ProductIDConfig.getFarmerId();
		for (Iterator<Product> iterator = productList.iterator(); iterator.hasNext();) {
			Product product = iterator.next();
			if (product.getId().equals(farmerId)) {
				iterator.remove();
			}
		}
	}

	public List<LPL001> getLifeProposalList() {
		RegNoSorter<LPL001> regNoSorter = new RegNoSorter<LPL001>(lifeProposalList);
		return regNoSorter.getSortedList();
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	private void showInformationDialog(String msg) {
		this.message = msg;
		PrimeFaces.current().executeScript("PF('informationDialog').show()");
	}

	public void findLifeProposalListByEnquiryCriteria() {
		criteria.setInsuranceType(InsuranceType.LIFE);
		lifeProposalList = lifeProposalService.findLifeProposalByEnquiryCriteria(criteria, null);
		if (criteria.getProduct() != null) {
			if (KeyFactorChecker.isPublicLife(criteria.getProduct())) {
				isSanction = true;
				isAcceptLetter = true;
				isCashReceipt = true;
				isPolicyIssue = true;
				isSchedule = true;
			} else if (KeyFactorChecker.isSnakeBite(criteria.getProduct())) {
				isSanction = false;
				isAcceptLetter = true;
				isCashReceipt = true;
				isPolicyIssue = false;
				isSchedule = false;
			} else if (KeyFactorChecker.isSportMan(criteria.getProduct())) {
				isSanction = false;
				isAcceptLetter = false;
				isCashReceipt = false;
				isPolicyIssue = true;
				isSchedule = false;
			} else {
				isSanction = true;
				isAcceptLetter = true;
				isCashReceipt = true;
				isPolicyIssue = true;
				isSchedule = false;
			}
		}
	}

	public boolean getIsAccessBranches() {
		return isAccessBranches;
	}

	public void setIsAccessBranches(boolean isAccessBranches) {
		this.isAccessBranches = isAccessBranches;
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		criteria.setProposalType(ProposalType.UNDERWRITING);
		lifeProposalList = new ArrayList<LPL001>();
	}

	public String editLifeProposal(LPL001 lifeProposalDTO) {
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		BancaassuranceProposal bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposalDTO.getId());
		if (allowToEdit(lifeProposal.getId())) {
			if (ProductIDConfig.isStudentLife(criteria.getProduct())) {
				putParam("bancaassuranceProposal", bancaassuranceProposal);
				putParam("lifeProposal", lifeProposal);
				putParam("enquiryEditLifeProposal", true);
				return "addNewStudentLifeProposal";
			} else {
				putParam("bancaassuranceProposal", bancaassuranceProposal);
				putParam("insuranceType", InsuranceType.LIFE);
				putParam("lifeProposal", lifeProposal);
				putParam("proposalType", ProposalType.UNDERWRITING);
				return "editLifeProposal";
			}
		} else {
			return null;
		}
	}

	private String message;

	public String getMessage() {
		return message;
	}

	public void findProduct() {
		isPublicLife = KeyFactorChecker.isPublicLife(criteria.getProduct());
		isShortTermEndowmentLife = KeyFactorChecker.isShortTermEndowment(criteria.getProduct().getId());
		isGroupLife = KeyFactorChecker.isGroupLife(criteria.getProduct());
		isSportMan = KeyFactorChecker.isSportMan(criteria.getProduct());
		isSnakeBite = KeyFactorChecker.isSnakeBite(criteria.getProduct());
		isStudentLife = KeyFactorChecker.isStudentLife(criteria.getProduct().getId());
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(criteria.getProduct());
		isSinglePremiumEndowmentLife = KeyFactorChecker.isSinglePremiumEndowmentLife(criteria.getProduct());
		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(criteria.getProduct());
		isSinglePremiumCreditLife = KeyFactorChecker.isSinglePremiumCreditLife(criteria.getProduct());
		isSimpleLife = KeyFactorChecker.isSimpleLife(criteria.getProduct());
	}

	public void returnProduct() {
		Product testProduct = criteria.getProduct();
	}

	public void printIssuePolicy(LPL001 lifeProposalDTO) {
		findProduct();
		LifePolicy lifePolicy = null;
		PaymentDTO payment = null;
		List<Payment> paymentList = null;
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.ISSUING)) {
			lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposalDTO.getId());
			paymentList = paymentService.findByProposal(lifeProposalDTO.getId(), PolicyReferenceType.LIFE_POLICY, true);
			payment = new PaymentDTO(paymentList);
			String customerName = lifePolicy.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			if (isGroupLife) {
				fileName = "GroupLife_" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateGroupLifePolicy(lifePolicy, payment, dirPath, fileName);
			} else if (isPublicLife) {
				fileName = "PublicLife_" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generatePublicLifePolicy(lifePolicy, payment, dirPath, fileName);
			} else if (isShortTermEndowmentLife) {
				fileName = "ShortEndowLife_" + customerName + "_Issue" + ".pdf";
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifePolicy(lifePolicy, dirPath, fileName);
			} else if (isSportMan) {
				fileName = "SportMan_" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateSportManPolicy(lifePolicy, payment, dirPath, fileName);
			} else if (isStudentLife) {
				fileName = "StudentLife_" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateStudentLifePolicyJasperPrint(lifePolicy, dirPath, fileName);
			} else if (isPublicTermLife) {
				fileName = "PublicTermLife" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generatePublicTermLifePolicy(lifePolicy, null, dirPath, fileName);
			} else if (isSinglePremiumEndowmentLife) {
				fileName = "SinglePremiumEndowmentLife" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateSinglePremiumEndowmentLifePolicy(lifePolicy, payment, dirPath, fileName);
			} else if (isShortTermSinglePremiumCreditLife) {
				fileName = "ShortTermSinglePremiumCreditLife" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateShortTermSinglePremiumCreditLifePolicy(lifePolicy, payment, dirPath, fileName);
			} else if (isSinglePremiumCreditLife) {
				fileName = "SinglePremiumCreditLife" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateSinglePremiumCreditLifePolicy(lifePolicy, payment, dirPath, fileName);
			}else if (isSimpleLife) {
				fileName = "SimpleLife" + customerName + "_Issue" + ".pdf";
				DocumentBuilder.generateSimpleLifePolicy(lifePolicy, payment, dirPath, fileName);
			} 
//			else if (isSimpleLife && lifePolicy.getLifeProposal().getAuditLog() != null){
//				if(lifePolicy.getLifeProposal().getAuditLog().getOrganization() != null) {
//					fileName ="SimpleLife_" + customerName + "_Issue" + ".pdf";
//					DocumentBuilder.generateSimpleLifePolicyforOrganization(lifePolicy, payment,dirPath, fileName); 
//				}	
//			}
				 
			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.PAYMENT.getLabel().toLowerCase());
		}
	}

	public void printAcceptedLetter(LPL001 lifeProposalDTO) {
		findProduct();
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		if (allowToPrint(lifeProposal, WorkflowTask.CONFIRMATION, WorkflowTask.PAYMENT, WorkflowTask.ISSUING)) {
			String customerName = lifeProposal.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			if (checkApproved(lifeProposal)) {
				if (isGroupLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.LIFE_PROPOSAL);
					fileName = "GroupLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isPublicLife || isSnakeBite) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.LIFE_PROPOSAL);
					fileName = (isPublicLife ? "PublicLife_" : "SnakeBite_") + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isShortTermEndowmentLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.SHORT_ENDOWMENT_LIFE_PROPOSAL);
					fileName = "ShortEndowLife_" + customerName + "_Inform" + ".pdf";
					ShortEndowUnderwritingDOCFactory.generateShortEndowLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isStudentLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.STUDENT_LIFE_PROPOSAL);
					fileName = "StudentLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateStudentLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isPublicTermLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.PUBLIC_TERM_LIFE_PROPOSAL);
					fileName = "PublicTermLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generatePublicTermLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isSinglePremiumEndowmentLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.SINGLE_PREMIUM_ENDOWMENT_LIFE_PROPOSAL);
					fileName = "SinglePremiumEndowmentLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateSinglePremiumEndowmentLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isShortTermSinglePremiumCreditLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.SHORT_TERM_SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL);
					fileName = "ShortTermSinglePremiumCreditLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateShortTermSinglePremiumCreditLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isSinglePremiumCreditLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.SINGLE_PREMIUM_CREDIT_LIFE_PROPOSAL);
					fileName = "SinglePremiumCreditLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateSinglePremiumCreditLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isSimpleLife) {
					acceptedInfo = acceptedInfoService.findAcceptedInfoByReferenceNo(lifeProposalDTO.getId(), ReferenceType.SIMPLE_LIFE_PROPOSAL);
					fileName = "isSimpleLife" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateSimpleLifeAcceptanceLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				}
			} else {
				acceptedInfo = new AcceptedInfo();
				if (isPublicLife || isGroupLife) {
					fileName = (isPublicLife ? "PublicLife_" : "GroupLife_") + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isShortTermEndowmentLife) {
					fileName = "ShortEndowLife_" + customerName + "_Inform" + ".pdf";
					ShortEndowUnderwritingDOCFactory.generateShortEndowLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isStudentLife) {
					fileName = "StudentLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateStudentLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isPublicTermLife) {
					fileName = "PublicTermLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generatePublicTermLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isSinglePremiumEndowmentLife) {
					fileName = "SinglePremiumEndowmentLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateSinglePremiumEndowmentLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isShortTermSinglePremiumCreditLife) {
					fileName = "ShortTermSinglePremiumCreditLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateShortTermSinglePremiumCreditLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				} else if (isSinglePremiumCreditLife) {
					fileName = "mSinglePremiumCreditLife_" + customerName + "_Inform" + ".pdf";
					DocumentBuilder.generateSinglePremiumCreditLifeRejectLetter(lifeProposal, acceptedInfo, dirPath, fileName);
				}
			}
			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.INFORM.getLabel().toLowerCase());
		}
	}

	public boolean checkApproved(LifeProposal lifeProposal) {
		approved = true;
		for (ProposalInsuredPerson pv : lifeProposal.getProposalInsuredPersonList()) {
			if (!pv.isApproved()) {
				approved = false;
				break;
			}
		}
		return approved;
	}

	private boolean allowToPrint(LifeProposal lifeProposal, WorkflowTask... workflowTasks) {
		List<WorkFlowHistory> wfhList = workFlowService.findWorkFlowHistoryByRefNo(lifeProposal.getId(), workflowTasks);
		if (wfhList == null || wfhList.isEmpty()) {
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
			return false;
		} else {
			return true;
		}
	}

	long currentTime = System.currentTimeMillis();

	/********************************************
	 * PDF Document Generation
	 ***************************************/
	String reportName = "LifePolicyLedger";
	String pdfDirPath = "/pdf-report/" + reportName + "/" + currentTime + "/";
	String dirPath = getWebRootPath() + pdfDirPath;
	String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	/********************************************
	 * PDF Document Generation
	 ***************************************/
	String reportNameLedger = "LifePolicyLedger";
	String pdfDirPathLedger = "/pdf-report/" + reportNameLedger + "/" + currentTime + "/";
	String dirPathLedger = getWebRootPath() + pdfDirPathLedger;
	String fileNameLedger = reportNameLedger + ".pdf";

	public String getReportStreamLedger() {
		return pdfDirPathLedger + fileNameLedger;
	}

	public void generateLifePolicyLedger(LPL001 lifeProposalDTO) {
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.ISSUING, WorkflowTask.PAYMENT)) {
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(lifeProposalDTO.getId());
			if (!checkGroupLife(lifePolicy)) {
				Date surveydate = lifeProposalService.findSurveyByProposalId(lifeProposalDTO.getId()).getDate();
				DocumentBuilder.generatePublicLifePolicyLedger(lifePolicy, surveydate, dirPathLedger, fileNameLedger);
				PrimeFaces.current().executeScript("PF('documentPrintDailogLedger').show()");
			} else {
				this.message = "Group Life Proposal have not Life Policy Schedule";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");
			}
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void handleCloseLifePolicyLedger(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/LifePolicyLedger/" + currentTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean checkGroupLife(LifePolicy lifePolicy) {
		boolean ans = false;
		for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
			if (person.getProduct().getId().equals(ProductIDConfig.getGroupLifeId())) {
				ans = true;
			}
		}
		return ans;
	}

	public void generateLifeSanction(LPL001 lifeProposalDTO) {
		findProduct();
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.ISSUING, WorkflowTask.PAYMENT, WorkflowTask.CONFIRMATION,
				WorkflowTask.INFORM, WorkflowTask.APPROVAL)) {
			String customerName = lifeProposal.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			if (isPublicLife || isGroupLife) {
				if (isSinglePrint(lifeProposal)) {
					isSinglePrint = true;
					fileName = (isPublicLife ? "PublicLife_" : "GroupLife_") + customerName + "_Sanction" + ".pdf";
					DocumentBuilder.generatePublicLifeSanction(lifeProposal, dirPath, fileName);
				} else if (isGroupPrint(lifeProposal)) {
					isSinglePrint = false;
					fileName = "GroupLife_" + customerName + "_Sanction" + ".pdf";
					DocumentBuilder.generateGroupLIfeSanction(lifeProposal, user.getName(), dirPath, fileName);
				}
			} else if (isShortTermEndowmentLife) {
				fileName = "ShortEndowLife_" + customerName + "_Sanction" + ".pdf";
				ShortEndowUnderwritingDOCFactory.generateShortEndowLifeSanction(lifeProposal, dirPath, fileName);
			} else if (isStudentLife) {
				fileName = "StudentLife_" + customerName + "_Sanction" + ".pdf";
				DocumentBuilder.generateStudentLifeSanction(lifeProposal, dirPath, fileName);
			} else if (isPublicTermLife) {
				fileName = "PublicTermLife_" + customerName + "_Sanction" + ".pdf";
				DocumentBuilder.generatePublicTermLifeSanction(lifeProposal, dirPath, fileName);
			} else if (isSinglePremiumEndowmentLife) {
				fileName = "SinglePremiumEndowmentLife" + customerName + "_Sanction" + ".pdf";
				DocumentBuilder.generateSinglePremiumEndowmentLifeSanction(lifeProposal, dirPath, fileName);
			} else if (isShortTermSinglePremiumCreditLife) {
				fileName = "ShortTermSinglePremiumCreditLife_" + customerName + "_Sanction" + ".pdf";
				DocumentBuilder.generateShortTermSinglePremiumCreditLifeSanction(lifeProposal, dirPath, fileName);
			} else if (isSinglePremiumCreditLife) {
				fileName = "SinglePremiumCreditLife_" + customerName + "_Sanction" + ".pdf";
				DocumentBuilder.generateSinglePremiumCreditLifeSanction(lifeProposal, dirPath, fileName);
			} else if (isSimpleLife) {
				fileName = "SimpleLife_" + customerName + "_Sanction" + ".pdf";
				DocumentBuilder.generateSimpleLifeSanction(lifeProposal, dirPath, fileName);
			}

			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
		}
	}

	public void handleCloseLifeSanction(CloseEvent event) {
		try {
			if (isSinglePrint) {
				org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/PublicLifeSanction/" + currentTime);
			} else {
				org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/GroupLifeSanction/" + currentTime);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isSinglePrint(LifeProposal lifeProposal) {
		if (lifeProposal.getProposalInsuredPersonList().size() == 1) {
			return true;
		}
		return false;
	}

	private boolean isGroupPrint(LifeProposal lifeProposal) {
		if (lifeProposal.getProposalInsuredPersonList().size() > 1) {
			return true;
		}
		return false;
	}

	public void generateCashReceipt(LPL001 lifeProposalDTO) {
		findProduct();
		LifeProposal lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		LifePolicy lifePolicy = lifePolicyService.findByLifeProposalId(lifeProposal.getId());
		PaymentDTO paymentDTO;
		if (allowToPrint(lifeProposalService.findLifeProposalById(lifeProposalDTO.getId()), WorkflowTask.ISSUING, WorkflowTask.PAYMENT)) {
			String customerName = lifePolicy.getCustomerName();
			if (customerName.contains("\\")) {
				customerName = customerName.replace("\\", "");
			}
			if (customerName.contains("/")) {
				customerName = customerName.replace("/", "");
			}
			if (isPublicLife || isGroupLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = (isPublicLife ? "PublicLife_" : "GroupLife_") + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			} else if (isShortTermEndowmentLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "ShortEndowLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			} else if (isSnakeBite) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				List<SnakeBitePolicy> snakeBitePolicyList = new ArrayList<SnakeBitePolicy>();
				snakeBitePolicyList = snakeBitePolicyService.findSnakeBitePolicyByReceiptNo(payment.getReceiptNo());
				fileName = "SnakeBite_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateSnakeBiteCashReceipt(snakeBitePolicyList, payment, dirPath, fileName);
			} else if (isStudentLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "StudentLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			} else if (isPublicTermLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "PublicTermLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			} else if (isSinglePremiumEndowmentLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "SinglePremiumEndowmentLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			} else if (isSinglePremiumCreditLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "SinglePremiumCreditLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			} else if (isShortTermSinglePremiumCreditLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "ShortTermSinglePremiumCreditLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			}else if (isSimpleLife) {
				Payment payment = paymentService.findPaymentForCashReceipt(1, lifePolicy.getId());
				paymentDTO = new PaymentDTO(payment);
				fileName = "SimpleLife_" + customerName + "_Receipt" + ".pdf";
				DocumentBuilder.generateLifeCashReceipt(lifeProposal, paymentDTO, dirPath, fileName);
			}

			PrimeFaces.current().executeScript("PF('documentPrintDailog').show()");
		} else {
			showInformationDialog(getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase()));
		}

	}

	public void handleCloseCashReceipt(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(getSystemPath() + "/pdf-report/LifeCashReceipt/" + currentTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		if (wf == null) {
			flag = false;
			this.message = "This proposal has been legalized as policy contract.";
			PrimeFaces.current().executeScript("PF('informationDialog').show()");
		} else {
			if (wf.getWorkflowTask().equals(WorkflowTask.UNDERWRITING) || wf.getWorkflowTask().equals(WorkflowTask.SURVEY) || wf.getWorkflowTask().equals(WorkflowTask.APPROVAL)
					|| wf.getWorkflowTask().equals(WorkflowTask.INFORM) || wf.getWorkflowTask().equals(WorkflowTask.CONFIRMATION)) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				PrimeFaces.current().executeScript("PF('informationDialog').show()");

			}
		}
		return flag;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		criteria.setOrganization(organization);
	}

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		criteria.setProduct(product);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		criteria.setSaleMan(saleMan);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public List<WorkFlowHistory> getWorkFlowList(String lifeProposalId) {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeProposalId);
	}

	public void openTemplateDialog(LPL001 lifeProposalDTO) {
		this.lifeProposal = lifeProposalService.findLifeProposalById(lifeProposalDTO.getId());
		this.bancaassuranceProposal = bancaassuranceProposalService.findBancaassuranceProposalByLifeproposalId(lifeProposalDTO.getId());
		isSimpleLife = KeyFactorChecker.isSimpleLife(criteria.getProduct());
		
		for (InsuredPersonKeyFactorValue vehKF : lifeProposal.getProposalInsuredPersonList().get(0).getKeyFactorValueList()) {
			if (KeyFactorChecker.isCoverOption(vehKF.getKeyFactor())) {
				keyFactor = vehKF.getValue();
				}
			}

		putParam("lifeProposal", lifeProposal);
		putParam("bancaassuranceProposal", bancaassuranceProposal);
		putParam("workFlowList", getWorkFlowList(lifeProposal.getId()));
		if (ProductIDConfig.isStudentLife(criteria.getProduct())) {
			openStudentLifeInfoTemplate();
		} else {
			PrimeFaces current = PrimeFaces.current();
			current.executeScript("PF('lifeProposalDetailsDialog').show();");
		}
	}

	public boolean isSanction() {
		return isSanction;
	}

	public boolean isAcceptLetter() {
		return isAcceptLetter;
	}

	public boolean isCashReceipt() {
		return isCashReceipt;
	}

	public boolean isPolicyIssue() {
		return isPolicyIssue;
	}

	public boolean isSchedule() {
		return isSchedule;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}
	
	public boolean getIsSimpleLife() {
		return isSimpleLife;
	}

	public String getKeyFactor() {
		return keyFactor;
	}

}
