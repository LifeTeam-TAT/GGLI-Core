package org.ace.insurance.web.manage.enquires.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "EnquiryEditTravelProposalActionBean")
public class EnquiryEditTravelProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
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

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	@ManagedProperty(value = "#{BancaassuraceProposalService}")
	private IBancaassuraceProposalService bancaassuraceProposalService;

	public void setBancaassuraceProposalService(IBancaassuraceProposalService bancaassuraceProposalService) {
		this.bancaassuraceProposalService = bancaassuraceProposalService;
	}

	private User user;
	private TravelProposal travelProposal;
	private TravelExpress travelExpress;
	private Map<String, TravelExpress> expressMap;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;
	private BancaMethod bancaMethod;

	private User responsiblePerson;
	private boolean isCreateNew;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (TravelProposal) getParam("travelProposal");
		bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
		bancaMethodList = bancaMethodService.findAllBanca();
	}

	@PostConstruct
	public void init() {
		System.out.println("**Init () **");
		initializeInjection();
		expressMap = new HashMap<String, TravelExpress>();
		if (travelProposal != null) {
			System.out.println("**tProposal not null **");
			for (TravelExpress e : travelProposal.getExpressList()) {
				expressMap.put(e.getTempId(), e);
			}
		} else {
			createNewProposal();
		}
		createNewExpress();
		isCreateNew = true;
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		travelProposal.setBranch(branch);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		travelProposal.setPaymentType(paymentType);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.TRAVEL);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public List<TravelExpress> getExpressList() {
		return new ArrayList<TravelExpress>(expressMap.values());
	}

	public void addExpress() {
		if (isValidateTravelExpress()) {
			double totalPremium = travelProposal.getProduct().getFixedValue() * travelExpress.getNoOfUnit();
			double commission = totalPremium / travelProposal.getProduct().getFirstCommission();
			double netPremium = totalPremium - commission;
			travelExpress.setNetPremium(netPremium);
			travelExpress.setCommission(commission);
			expressMap.put(travelExpress.getTempId(), travelExpress);
			createNewExpress();
		}
	}

	public String updateNewTravelPropoasl() {
		if (isValidTravelProposal()) {
			try {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), null, WorkflowTask.CONFIRMATION, WorkflowReferenceType.TRAVEL_PROPOSAL, user, responsiblePerson);
				travelProposal.setExpressList(getTravelExpressList());
				travelProposalService.updateTravelProposal(travelProposal, workFlowDTO);
				outjectTravelProposal(travelProposal);

				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
				createNewProposal();
			} catch (SystemException ex) {
				handelSysException(ex);
			}
			return "dashboard";
		}
		return null;
	}

	private boolean isValidateTravelExpress() {
		boolean valid = true;
		String formID = "travelProposalEntryForm";
		if (isEmpty(travelExpress.getExpress())) {
			addErrorMessage(formID + ":express", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelExpress.getNoOfPassenger() == 0) {
			addErrorMessage(formID + ":noOfPassenger", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelExpress.getNoOfUnit() == 0) {
			addErrorMessage(formID + ":noOfUnits", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;
	}

	private boolean isValidTravelProposal() {
		boolean valid = true;
		String formID = "travelProposalEntryForm";
		if (isEmpty(travelProposal.getSubmittedDate())) {
			addErrorMessage(formID + ":submittedDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getBranch())) {
			addErrorMessage(formID + ":branch", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getPaymentType())) {
			addErrorMessage(formID + ":paymentType", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(responsiblePerson)) {
			addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (expressMap.isEmpty()) {
			addErrorMessage(formID + ":expressTable", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;

	}

	public void createNewExpress() {
		travelExpress = new TravelExpress();
		isCreateNew = true;
	}

	public void createNewProposal() {
		travelProposal = new TravelProposal();
	}

	private List<TravelExpress> getTravelExpressList() {
		List<TravelExpress> expressList = new ArrayList<TravelExpress>();
		for (TravelExpress express : expressMap.values()) {
			express.setTravelProposal(travelProposal);
			expressList.add(express);
		}
		return expressList;
	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}

	public void prepareEditExpressInfo(TravelExpress express) {
		this.travelExpress = express;
		this.travelExpress.setNetPremium(300);
		isCreateNew = false;
	}

	public void removeExpress(TravelExpress express) {
		expressMap.remove(express.getTempId());
	}

	public boolean getIsCreateNew() {
		return isCreateNew;
	}

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

	public List<BancaMethod> getBancaMethodList() {
		return bancaMethodList;
	}

	public void setBancaMethodList(List<BancaMethod> bancaMethodList) {
		this.bancaMethodList = bancaMethodList;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public void setBancaMethod(BancaMethod bancaMethod) {
		this.bancaMethod = bancaMethod;
	}

	public TravelExpress getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(TravelExpress travelExpress) {
		this.travelExpress = travelExpress;
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		this.travelExpress.setExpress(express);
	}

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassuranceProposal.setChannel(channel);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassuranceProposal.setBancaLIC(bancaLIC);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBrm);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassuranceProposal.setBancaRefferal(bancaRefferal);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassuranceProposal.setBancaBranch(bancaBranch);
	}

}
