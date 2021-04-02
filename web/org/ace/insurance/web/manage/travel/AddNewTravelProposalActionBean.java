package org.ace.insurance.web.manage.travel;

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

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.bancassurance.proposal.service.interfaces.IBancaassuraceProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
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
@ManagedBean(name = "AddNewTravelProposalActionBean")
public class AddNewTravelProposalActionBean extends BaseBean implements Serializable {
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

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
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

	private final String formID = "travelProposalEntryForm";
	private User user;
	private TravelProposal travelProposal;
	private TravelExpress travelExpress;
	private Map<String, TravelExpress> expressMap;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;
	private BancaMethod bancaMethod;
	private Product product;
	private Entitys entity;
	private User responsiblePerson;
	private boolean isCreateNew;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		bancaMethod = new BancaMethod();
		bancaassuranceProposal = new BancaassuranceProposal();
		bancaMethodList = bancaMethodService.findAllBanca();
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		createNewProposal();
		createNewExpress();
		expressMap = new HashMap<String, TravelExpress>();
		isCreateNew = true;
		List<Product> productList = productService.findProductByInsuranceType(InsuranceType.TRAVEL_INSURANCE);
		if (!productList.isEmpty()) {
			product = productList.get(0);
		}

	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void removeEntity() {
		entity = null;
		travelProposal.setBranch(null);
		travelProposal.setSalePoint(null);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		travelProposal.setBranch(branch);
		travelProposal.setSalePoint(null);
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
			double totalPremium = product.getFixedValue() * travelExpress.getNoOfUnit();
			double commission = Utils.getPercentOf(product.getFirstCommission(), totalPremium);
			double netPremium = totalPremium - commission;
			travelExpress.setNetPremium(netPremium);
			travelExpress.setCommission(commission);
			expressMap.put(travelExpress.getTempId(), travelExpress);
			createNewExpress();
		}
	}

	public void createNewExpress() {
		travelExpress = new TravelExpress();
		isCreateNew = true;

	}

	public void selectPaymentTypeByProduct() {
		selectPaymentType(product);
	}

	public void createNewProposal() {
		travelProposal = new TravelProposal();
	}

	public String addNewTravelPropoasl() {
		String result = null;
		if (isValidTravelProposal()) {
			try {

				WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), null, WorkflowTask.CONFIRMATION, WorkflowReferenceType.TRAVEL_PROPOSAL, user, responsiblePerson);
				for (TravelExpress express : expressMap.values()) {
					travelProposal.addExpress(express);
				}
				travelProposal.setProduct(product);
				travelProposalService.addNewTravelProposal(travelProposal, workFlowDTO, RequestStatus.PROPOSED.name());
				outjectTravelProposal(travelProposal);

				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
				createNewProposal();
				result = "dashboard";
			} catch (SystemException ex) {
				handelSysException(ex);
			}
		}
		return result;
	}

	private boolean isValidateTravelExpress() {
		boolean valid = true;

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
		if (isEmpty(travelProposal.getFromDate())) {
			addErrorMessage(formID + ":fromDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getToDate())) {
			addErrorMessage(formID + ":toDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getSubmittedDate())) {
			addErrorMessage(formID + ":submittedDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getBranch())) {
			addErrorMessage(formID + ":branch", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getSalePoint())) {
			addErrorMessage(formID + ":salePoint", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(entity)) {
			addErrorMessage(formID + ":entity", UIInput.REQUIRED_MESSAGE_ID);
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

	public void selectSalePoint() {
		selectSalePointByBranch(travelProposal.getBranch() == null ? "" : travelProposal.getBranch().getId());
	}

	public void removeBranch() {
		travelProposal.setBranch(null);
		travelProposal.setSalePoint(null);

	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		travelProposal.setSalePoint(salePoint);
	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}

	public void prepareEditExpressInfo(TravelExpress express) {
		this.travelExpress = express;
		isCreateNew = false;
	}

	public void removeExpress(TravelExpress express) {
		expressMap.remove(express.getTempId());
	}

	public boolean getIsCreateNew() {
		return isCreateNew;
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

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entity == null ? "" : entity.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (this.entity != null && !entity.getId().equals(this.entity.getId())) {
			travelProposal.setBranch(null);
			travelProposal.setSalePoint(null);
		}
		this.entity = entity;
	}

	public Entitys getEntity() {
		return entity;
	}

	public void setEntity(Entitys entity) {
		this.entity = entity;
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
