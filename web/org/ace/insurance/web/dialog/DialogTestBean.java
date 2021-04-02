package org.ace.insurance.web.dialog;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.buildingOccupation.BuildingOccupation;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.city.City;
import org.ace.insurance.system.common.company.Company;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.province.Province;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.workshop.WorkShop;
import org.ace.insurance.user.User;
import org.ace.java.web.common.BaseBean;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "DialogTestBean")
@ViewScoped
public class DialogTestBean extends BaseBean {
	private Express express;
	private WorkShop workShop;
	private Product product;
	private Agent agent;
	private Bank bank;
	private BankBranch bankBranch;
	private Branch branch;
	private BancaBRM bancaBrm;
	private BancaLIC bancaLIC;
	private Country country;
	private City city;
	private Organization organization;
	private Province province;
	private Qualification qualification;
	private RelationShip relationship;
	private SaleMan saleMan;
	private Township township;
	private User user;
	private Customer customer;
	private Company company;
	private BuildingOccupation buildingOccupation;
	private Channel channel;

	// express
	public void returnExpressDialog(SelectEvent event) {
		System.out.println("returnExpressDialog...........");
		express = (Express) event.getObject();
	}

	// express
	public void returnWorkshopDialog(SelectEvent event) {
		System.out.println("returnWorkshopDialog...........");
		workShop = (WorkShop) event.getObject();
	}

	// product
	public void returnProductDialog(SelectEvent event) {
		System.out.println("returnProductDialog...........");
		product = (Product) event.getObject();
	}

	// agent
	public void returnAgentDialog(SelectEvent event) {
		System.out.println("returnAgentDialog...........");
		agent = (Agent) event.getObject();
	}

	// bank
	public void returnBankDialog(SelectEvent event) {
		System.out.println("returnBankDialog...........");
		bank = (Bank) event.getObject();
	}

	// bankBranch
	public void returnBankBranchDialog(SelectEvent event) {
		System.out.println("returnBankBranchDialog...........");
		bankBranch = (BankBranch) event.getObject();
	}

	// branch
	public void returnBranchDialog(SelectEvent event) {
		System.out.println("returnBranchDialog...........");
		branch = (Branch) event.getObject();
	}

	// bancaBrm
	public void returnBancaBRMDialog(SelectEvent event) {
		System.out.println("returnBancaBRMDialog...........");
		bancaBrm = (BancaBRM) event.getObject();
	}

	// bancaLIC
	public void returnBancaLICDialog(SelectEvent event) {
		System.out.println("returnBancaLICDialog...........");
		bancaLIC = (BancaLIC) event.getObject();
	}

	// city
	public void returnCityDialog(SelectEvent event) {
		System.out.println("returnCityDialog...........");
		city = (City) event.getObject();
	}

	// country
	public void returnCountryDialog(SelectEvent event) {
		System.out.println("returnCountryDialog...........");
		country = (Country) event.getObject();
	}

	// organization
	public void returnOrganizationDialog(SelectEvent event) {
		System.out.println("returnOrganizationDialog...........");
		organization = (Organization) event.getObject();
	}

	// province
	public void returnProvinceDialog(SelectEvent event) {
		System.out.println("returnProvinceDialog...........");
		province = (Province) event.getObject();
	}

	// qualification
	public void returnQualificationDialog(SelectEvent event) {
		System.out.println("returnQualificationDialog...........");
		qualification = (Qualification) event.getObject();
	}

	// relationship
	public void returnRelationshipDialog(SelectEvent event) {
		System.out.println("returnRelationshipDialog...........");
		relationship = (RelationShip) event.getObject();
	}

	// saleman
	public void returnSalemanDialog(SelectEvent event) {
		System.out.println("returnSalemanDialog...........");
		saleMan = (SaleMan) event.getObject();
	}

	// township
	public void returnTownshipDialog(SelectEvent event) {
		System.out.println("returnTownshipDialog...........");
		township = (Township) event.getObject();
	}

	// User
	public void returnUserDialog(SelectEvent event) {
		System.out.println("returnUserDialog...........");
		user = (User) event.getObject();
	}

	// Manufacture
	public void returnManufactureDialog(SelectEvent event) {
		System.out.println("returnManufactureDialog...........");
	}

	// Type Of Body
	public void returnTypeOfBodyDialog(SelectEvent event) {
		System.out.println("returnTypeOfBodyDialog...........");
	}

	// customer
	public void returnCustomerDialog(SelectEvent event) {
		customer = (Customer) event.getObject();
	}

	// company
	public void returnCompanyDialog(SelectEvent event) {
		company = (Company) event.getObject();
	}

	// buildingOccupation
	public void returnBuildingOccupationDialog(SelectEvent event) {
		buildingOccupation = (BuildingOccupation) event.getObject();
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Township getTownship() {
		return township;
	}

	public void setTownship(Township township) {
		this.township = township;
	}

	public SaleMan getSaleMan() {
		return saleMan;
	}

	public void setSaleMan(SaleMan saleMan) {
		this.saleMan = saleMan;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public Qualification getQualification() {
		return qualification;
	}

	public void setQualification(Qualification qualification) {
		this.qualification = qualification;
	}

	public RelationShip getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationShip relationship) {
		this.relationship = relationship;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public BankBranch getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(BankBranch bankBranch) {
		this.bankBranch = bankBranch;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public BuildingOccupation getBuildingOccupation() {
		return buildingOccupation;
	}

	public void setBuildingOccupation(BuildingOccupation buildingOccupation) {
		this.buildingOccupation = buildingOccupation;
	}

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public WorkShop getWorkShop() {
		return workShop;
	}

	public void setWorkShop(WorkShop workShop) {
		this.workShop = workShop;
	}

	public BancaBRM getBancaBrm() {
		return bancaBrm;
	}

	public void setBancaBrm(BancaBRM bancaBrm) {
		this.bancaBrm = bancaBrm;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public BancaLIC getBancaLIC() {
		return bancaLIC;
	}

	public void setBancaLIC(BancaLIC bancaLIC) {
		this.bancaLIC = bancaLIC;
	}

}
