package org.ace.insurance.web.manage.enquires.life;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifePortalEnquiryActionBean")
public class LifePortalEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

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

	private LifeProposal lifeProposal;
	private Boolean isReversal;
	private List<LPL001> lifeProposalList;
	private List<Product> productList;
	private EnquiryCriteria criteria;

	@PostConstruct
	public void init() {
		resetCriteria();
		productList = productService.findProductByInsuranceType(InsuranceType.LIFE);
	}

	public List<LPL001> getLifeProposalList() {
		return lifeProposalList;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public void showDetailLifeProposal(LPL001 lifeProposalDTO) {
		this.lifeProposal = lifeProposalService.findLifePortalById(lifeProposalDTO.getId());
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

	public void findLifeProposalListByEnquiryCriteria() {
		this.lifeProposalList = lifeProposalService.findLifeProposalPortalByEnquiryCriteria(criteria);
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setNumber("");
		this.lifeProposalList = lifeProposalService.findLifeProposalPortalByEnquiryCriteria(criteria);
	}

	public String editLifeProposal(LPL001 lifeProposalDTO) {
		if (allowToEdit(lifeProposalDTO.getId())) {
			this.lifeProposal = lifeProposalService.findLifePortalById(lifeProposalDTO.getId());
			this.isReversal = true;
			return "addLifeProposal";
		} else {
			return null;
		}

	}

	private String message;

	public String getMessage() {
		return message;
	}

	private boolean allowToEdit(String id) {
		boolean flag = true;
		String status = lifeProposalService.findStatusById(id);
		if (status != null) {
			if (status.equals(RequestStatus.PENDING.toString()) || status.equals(RequestStatus.FINISHED.toString())) {
				flag = true;
			} else {
				flag = false;
				this.message = "This proposal is not in the editable state. It's currently at " + status + " state";
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

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		criteria.setProduct(product);
	}
}
