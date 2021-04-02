package org.ace.insurance.web.manage.life.groupfarmer.proposal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.ProductIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.UploadFileConfig;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaMethod.service.interfaces.IBancaMethodService;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.ggiorganization.GGIOrganization;
import org.ace.insurance.system.common.ggiorganization.service.interfaces.IGGIOrganizationService;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.percentage.Percentage;
import org.ace.insurance.system.common.percentage.service.interfaces.IPercentageService;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.insurance.system.common.relationshiptype.service.interfaces.IRelationShipTypeService;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.system.common.staff.Staff;
import org.ace.insurance.system.common.staff.service.interfaces.IStaffService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddNewGroupFarmerProposalActionBean")
public class AddNewGroupFarmerProposalActionBean extends BaseBean {

	private GroupFarmerProposal groupFarmerProposal;
	private Map<String, String> proposalUploadedFileMap;
	private String userType;
	private User responsiblePerson;
	private Product product;
	private User user;
	private final String PROPOSAL_DIR = "/upload/GroupFarmer-proposal/";
	private String temporyDir;
	private String groupFarmerProposalId;
	private Entitys entitys;
	private BancaassuranceProposal bancaassuranceProposal;
	private List<BancaMethod> bancaMethodList;

	private List<GGIOrganization> ggiOrganizationList;

	private List<Staff> staffList;

	private List<RelationShipType> relationShipTypeList;

	private Boolean selectItem;

	private GGIOrganization ggiOrganization;

	private Staff staff;

	private RelationShipType relationShipType;

	private Percentage percentage;

	private Eips eips;

	@ManagedProperty(value = "#{GGIOrganizationService}")
	private IGGIOrganizationService ggiOrganizationService;

	@ManagedProperty(value = "#{StaffService}")
	private IStaffService staffService;

	@ManagedProperty(value = "#{RelationShipTypeService}")
	private IRelationShipTypeService relationShipTypeService;

	@ManagedProperty(value = "#{PercentageService}")
	private IPercentageService percentageService;

	@ManagedProperty(value = "#{EipsService}")
	private IEipsService eipsService;

	@ManagedProperty(value = "#{BancaMethodService}")
	private IBancaMethodService bancaMethodService;

	public void setBancaMethodService(IBancaMethodService bancaMethodService) {
		this.bancaMethodService = bancaMethodService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{GroupfarmerProposalService}")
	private IGroupfarmerProposalService groupFarmerProposalService;

	public void setGroupFarmerProposalService(IGroupfarmerProposalService groupFarmerProposalService) {
		this.groupFarmerProposalService = groupFarmerProposalService;
	}

	@ManagedProperty(value = "#{SalePointService}")
	private ISalePointService salePointService;

	public void setSalePointService(ISalePointService salePointService) {
		this.salePointService = salePointService;
	}

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		bancaassuranceProposal = (BancaassuranceProposal) getParam("bancaassuranceProposal");
		bancaMethodList = bancaMethodService.findAllBanca();
		eips = new Eips();
		ggiOrganizationList = ggiOrganizationService.findAllGGIOrganization();
		relationShipTypeList = relationShipTypeService.findAllRelationShipType();
		proposalUploadedFileMap = new HashMap<String, String>();
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		loadData();
		createNewGroupFarmerProposal();
	}

	public void createNewGroupFarmerProposal() {
		groupFarmerProposal = new GroupFarmerProposal();
		groupFarmerProposal.setSubmittedDate(new Date());
		/*
		 * List<SalePoint> salePointList = salePointService.findAllSalePoint();
		 * if (salePointList.size() > 0) { groupFarmerProposal
		 * .setSalePoint(salePointList.get(0)); }
		 */
	}

	public String getUploadPath() {
		return UploadFileConfig.getUploadFilePathHome();
	}

	public void loadData() {
		product = productService.findProductById(ProductIDConfig.getFarmerId());
	}

	public String addNewGroupFarmerProposal() {
		String result = null;
		/*
		 * groupFarmerProposal.setStaffPlan(selectItem); if (selectItem == true)
		 * { saveEipsData(); groupFarmerProposal.setEips(eips); }
		 */
		try {
			if (validate()) {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupFarmerProposal.getId(), null, WorkflowTask.CONFIRMATION, WorkflowReferenceType.GROUPFARMER_PROPOSAL, user,
						responsiblePerson);
				groupFarmerProposal.setProposalType(ProposalType.UNDERWRITING);
				groupFarmerProposal.setEndDate(DateUtils.addYears(groupFarmerProposal.getSubmittedDate(), 1));
				groupFarmerProposalService.addNewGroupFarmerProposal(groupFarmerProposal, workFlowDTO, proposalUploadedFileMap, bancaassuranceProposal, PROPOSAL_DIR, temporyDir,
						getUploadPath());

				/*
				 * if (selectItem == true) {
				 * eips.setAmount((((percentage.getPercent()) / 100) *
				 * groupFarmerProposal.getPremium())); eipsService.save(eips); }
				 */
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupFarmerProposal.getProposalNo());
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				createNewGroupFarmerProposal();
				// moveUploadedFiles();
				result = "dashboard";
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public boolean validate() {
		String formID = "groupFarmerProposalEntryForm";
		if (groupFarmerProposal.getTotalSI() == 0) {
			addErrorMessage(formID + ":totalSI", MessageId.VALUE_IS_REQUIRED);
			return false;
		}
		if (groupFarmerProposal.getNoOfInsuredPerson() == 0.0) {
			addErrorMessage(formID + ":noOfIns", MessageId.VALUE_IS_REQUIRED);
			return false;
		}
		return true;

	}

	private void outjectGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		putParam("groupFarmerProposal", groupFarmerProposal);
	}

	public List<PaymentType> getPaymentTypes() {
		if (product == null) {
			return new ArrayList<PaymentType>();
		} else {
			return product.getPaymentTypeList();
		}
	}

	public SaleChannelType[] getSaleChannelType() {
		return SaleChannelType.values();
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (groupFarmerProposal.getSaleChannelType().equals(SaleChannelType.AGENT)) {
			groupFarmerProposal.setSaleMan(null);
			groupFarmerProposal.setReferral(null);
		} else if (groupFarmerProposal.getSaleChannelType().equals(SaleChannelType.SALEMAN)) {
			groupFarmerProposal.setAgent(null);
			groupFarmerProposal.setReferral(null);
		} else if (groupFarmerProposal.getSaleChannelType().equals(SaleChannelType.REFERRAL)) {
			groupFarmerProposal.setSaleMan(null);
			groupFarmerProposal.setAgent(null);
		} else if (groupFarmerProposal.getSaleChannelType().equals(SaleChannelType.BANCASSURANCE)) {
			groupFarmerProposal.setSaleMan(null);
			groupFarmerProposal.setAgent(null);
			groupFarmerProposal.setReferral(null);
			bancaassuranceProposal = new BancaassuranceProposal();
		}
	}

	public void handleProposalAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		if (!proposalUploadedFileMap.containsKey(fileName)) {
			// fixme
			String filePath = temporyDir + "/" + groupFarmerProposalId + "/" + fileName;
			proposalUploadedFileMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	public void createFile(File file, byte[] content) {
		try {
			/* At First : Create directory of target file */
			String filePath = file.getPath();
			int lastIndex = filePath.lastIndexOf("\\") + 1;
			FileUtils.forceMkdir(new File(filePath.substring(0, lastIndex)));

			/* Create target file */
			FileOutputStream outputStream = new FileOutputStream(file);
			IOUtils.write(content, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void removeProposalUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			proposalUploadedFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTemporyDir() {
		return temporyDir;
	}

	public void selectSalePoint() {
		selectSalePointByBranch(groupFarmerProposal.getBranch() == null ? "" : groupFarmerProposal.getBranch().getId());
	}

	public void removeBranch() {
		groupFarmerProposal.setBranch(null);
		groupFarmerProposal.setSalePoint(null);

	}

	public void selectUser() {
		selectUser(WorkflowTask.SURVEY, WorkFlowType.FARMER);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		groupFarmerProposal.setAgent(agent);

	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		groupFarmerProposal.setOrganization(organization);
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		groupFarmerProposal.setSaleMan(saleMan);
	}

	public void returnReferral(SelectEvent event) {
		Customer referral = (Customer) event.getObject();
		groupFarmerProposal.setReferral(referral);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		groupFarmerProposal.setBranch(branch);
		groupFarmerProposal.setSalePoint(null);
	}

	public void removeEntity() {
		entitys = null;
		groupFarmerProposal.setBranch(null);
		groupFarmerProposal.setSalePoint(null);
	}

	// public void returnPaymentType(SelectEvent event) {
	// PaymentType paymentType = (PaymentType) event.getObject();
	// groupFarmerProposal.setPaymentType(paymentType);
	// }

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		groupFarmerProposal.setSalePoint(salePoint);
	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			groupFarmerProposal.setBranch(null);
			groupFarmerProposal.setSalePoint(null);
		}
		this.entitys = entity;
	}

	public void returnChannel(SelectEvent event) {
		Channel channel = (Channel) event.getObject();
		bancaassuranceProposal.setChannel(channel);
	}

	public void returnBancaBranch(SelectEvent event) {
		BancaBranch bancaBranch = (BancaBranch) event.getObject();
		bancaassuranceProposal.setBancaBranch(bancaBranch);
	}

	public void returnBancaBrm(SelectEvent event) {
		BancaBRM bancaBrm = (BancaBRM) event.getObject();
		bancaassuranceProposal.setBancaBRM(bancaBrm);
	}

	public void returnBancaLIC(SelectEvent event) {
		BancaLIC bancaLIC = (BancaLIC) event.getObject();
		bancaassuranceProposal.setBancaLIC(bancaLIC);
	}

	public void returnBancaRefferal(SelectEvent event) {
		BancaRefferal bancaRefferal = (BancaRefferal) event.getObject();
		bancaassuranceProposal.setBancaRefferal(bancaRefferal);
	}

	public void changeBancaEvent(AjaxBehaviorEvent event) {

		bancaassuranceProposal.setBancaBRM(null);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);
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

//	public void selectBancaBrm() {
//		selectBancaBRM(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void selectBancaBranch() {
		selectBancaBranchByChannel(bancaassuranceProposal.getChannel() == null ? "" : bancaassuranceProposal.getChannel().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

//	public void selectBancaReferralByOTC() {
//		selectBancaReferralByOTC(bancaassuranceProposal.getBancaBranch().getId());
//	}
//
//	public void selectBancaReferral() {
//		selectBancaReferral(bancaassuranceProposal.getBancaBranch().getId());
//	}

	public void removeBancaBranch() {
		bancaassuranceProposal.setBancaBranch(null);
		bancaassuranceProposal.setBancaMethod(null);

	}

	public void removeBancaLIC() {
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);

	}

	public void removeBancaReferral() {
		bancaassuranceProposal.setBancaRefferal(null);

	}

	public void removeChannel() {
		bancaassuranceProposal.setChannel(null);
		bancaassuranceProposal.setBancaBranch(null);
		bancaassuranceProposal.setBancaMethod(null);
	}

	public void removeBancaBRM() {
		bancaassuranceProposal.setBancaBRM(null);
		bancaassuranceProposal.setBancaLIC(null);
		bancaassuranceProposal.setBancaRefferal(null);

	}

	public List<GGIOrganization> getGgiOrganizationList() {
		return ggiOrganizationList;
	}

	public void setGgiOrganizationList(List<GGIOrganization> ggiOrganizationList) {
		this.ggiOrganizationList = ggiOrganizationList;
	}

	public List<Staff> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<Staff> staffList) {
		this.staffList = staffList;
	}

	public List<RelationShipType> getRelationShipTypeList() {
		return relationShipTypeList;
	}

	public void setRelationShipTypeList(List<RelationShipType> relationShipTypeList) {
		this.relationShipTypeList = relationShipTypeList;
	}

	public Boolean getSelectItem() {
		return selectItem;
	}

	public void setSelectItem(Boolean selectItem) {
		if (selectItem == false) {
			clearData();
		}
		this.selectItem = selectItem;
	}

	public GGIOrganization getGgiOrganization() {
		return ggiOrganization;
	}

	public void setGgiOrganization(GGIOrganization ggiOrganization) {
		this.ggiOrganization = ggiOrganization;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public RelationShipType getRelationShipType() {
		return relationShipType;
	}

	public void setRelationShipType(RelationShipType relationShipType) {
		this.relationShipType = relationShipType;
	}

	public Percentage getPercentage() {
		return percentage;
	}

	public void setPercentage(Percentage percentage) {
		this.percentage = percentage;
	}

	public Eips getEips() {
		return eips;
	}

	public void setEips(Eips eips) {
		this.eips = eips;
	}

	public IGGIOrganizationService getGgiOrganizationService() {
		return ggiOrganizationService;
	}

	public void setGgiOrganizationService(IGGIOrganizationService ggiOrganizationService) {
		this.ggiOrganizationService = ggiOrganizationService;
	}

	public IStaffService getStaffService() {
		return staffService;
	}

	public void setStaffService(IStaffService staffService) {
		this.staffService = staffService;
	}

	public IRelationShipTypeService getRelationShipTypeService() {
		return relationShipTypeService;
	}

	public void setRelationShipTypeService(IRelationShipTypeService relationShipTypeService) {
		this.relationShipTypeService = relationShipTypeService;
	}

	public IPercentageService getPercentageService() {
		return percentageService;
	}

	public void setPercentageService(IPercentageService percentageService) {
		this.percentageService = percentageService;
	}

	public IEipsService getEipsService() {
		return eipsService;
	}

	public void setEipsService(IEipsService eipsService) {
		this.eipsService = eipsService;
	}

	private void clearData() {
		ggiOrganization = new GGIOrganization();
		staff = new Staff();
		relationShipType = new RelationShipType();
		percentage = new Percentage();
	}

	private void saveEipsData() {
		eips.setRelationShipType(relationShipType);
		eips.setStaff(staff);
		eips.setPercentage(percentage.getPercent());
		eipsService.save(eips);
	}
	// Choose Staff With Organization

	public void selectStaffWithOrganization() {
		staffList = staffService.findStaffWithGGIOrganization(ggiOrganization.getId());
	}

	public void showPercentageWithRelationShip() {
		percentage = percentageService.findPercentageWithRelationShip(relationShipType.getId(), product.getId());
	}

}
