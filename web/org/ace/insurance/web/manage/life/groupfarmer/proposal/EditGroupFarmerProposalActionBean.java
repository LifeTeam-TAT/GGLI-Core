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
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposalAttachment;
import org.ace.insurance.groupfarmer.proposal.service.interfaces.IGroupfarmerProposalService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.salepoint.service.interfaces.ISalePointService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.UserType;
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
@ManagedBean(name = "EditGroupFarmerProposalActionBean")
public class EditGroupFarmerProposalActionBean extends BaseBean {
	private GroupFarmerProposal groupFarmerProposal;
	private String userType;
	private User responsiblePerson;
	private Product product;
	private User user;
	private final String PROPOSAL_DIR = "/upload/GroupFarmer-proposal/";
	private String temporyDir;
	private Map<String, String> proposalUploadedFileMap;
	private String groupFarmerProposalId;
	private Entitys entitys;

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
		groupFarmerProposal = (groupFarmerProposal == null) ? (GroupFarmerProposal) getParam("groupFarmerProposal") : groupFarmerProposal;
		userType = groupFarmerProposal.getAgent() != null ? UserType.AGENT.toString()
				: groupFarmerProposal.getReferral() != null ? UserType.REFERRAL.toString() : UserType.SALEMAN.toString();
		entitys = groupFarmerProposal.getBranch() == null ? null : groupFarmerProposal.getBranch().getEntity();
		loadData();
		proposalUploadedFileMap = new HashMap<String, String>();
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";

		groupFarmerProposalId = groupFarmerProposal.getId();
		String filePath = null;
		for (GroupFarmerProposalAttachment attach : groupFarmerProposal.getAttachmentList()) {
			filePath = attach.getFilePath();
			filePath = filePath.replaceAll("/upload/GroupFarmer-proposal/", temporyDir);
			attach.setFilePath(filePath);
		}
		proposalUploadedFileMap = new HashMap<String, String>();
		if (!groupFarmerProposal.getAttachmentList().isEmpty()) {
			copyUploadedFiles();
			for (GroupFarmerProposalAttachment attachment : groupFarmerProposal.getAttachmentList()) {
				proposalUploadedFileMap.put(attachment.getName(), attachment.getFilePath());
			}
		}

	}

	private void copyUploadedFiles() {
		try {
			FileHandler.copyDirectory(getUploadPath() + PROPOSAL_DIR + groupFarmerProposalId, getUploadPath() + temporyDir + groupFarmerProposalId);
		} catch (IOException e) {
			e.printStackTrace();
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

	public void createNewGroupFarmerProposal() {
		groupFarmerProposal.setSubmittedDate(new Date());
		if (groupFarmerProposal.getSalePoint() == null && null != user.getSalePoint()) {
			groupFarmerProposal.setSalePoint(user.getSalePoint());
		}
	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public void loadData() {
		product = productService.findProductById(ProductIDConfig.getFarmerId());
	}

	public String updateGroupFarmerProposal() {
		String result = null;
		try {
			if (validate()) {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(groupFarmerProposal.getId(), null, WorkflowTask.CONFIRMATION, WorkflowReferenceType.GROUPFARMER_PROPOSAL, user,
						responsiblePerson);
				groupFarmerProposal.setProposalType(ProposalType.UNDERWRITING);
				groupFarmerProposalService.editGroupFarmerProposal(groupFarmerProposal, workFlowDTO, proposalUploadedFileMap, PROPOSAL_DIR, temporyDir, getUploadPath());
				outjectGroupFarmerProposal(groupFarmerProposal);
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, groupFarmerProposal.getProposalNo());
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				createNewGroupFarmerProposal();
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

	public void selectPaymentType() {
		selectPaymentType(product);
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			groupFarmerProposal.setSaleMan(null);
			groupFarmerProposal.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			groupFarmerProposal.setAgent(null);
			groupFarmerProposal.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			groupFarmerProposal.setSaleMan(null);
			groupFarmerProposal.setAgent(null);
		}
	}

	public void selectSalePoint() {
		selectSalePointByBranch(groupFarmerProposal.getBranch() == null ? "" : groupFarmerProposal.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void removeBranch() {
		groupFarmerProposal.setBranch(null);
		groupFarmerProposal.setSalePoint(null);

	}

	public void selectUser() {
		selectUser(WorkflowTask.SURVEY, WorkFlowType.FARMER);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		groupFarmerProposal.setOrganization(organization);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		groupFarmerProposal.setAgent(agent);

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

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		groupFarmerProposal.setPaymentType(paymentType);
	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		groupFarmerProposal.setSalePoint(salePoint);
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

	public Map<String, String> getProposalUploadedFileMap() {
		return proposalUploadedFileMap;
	}

	public void setProposalUploadedFileMap(Map<String, String> proposalUploadedFileMap) {
		this.proposalUploadedFileMap = proposalUploadedFileMap;
	}

	public String getPROPOSAL_DIR() {
		return PROPOSAL_DIR;
	}

	public void setTemporyDir(String temporyDir) {
		this.temporyDir = temporyDir;
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

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

}
