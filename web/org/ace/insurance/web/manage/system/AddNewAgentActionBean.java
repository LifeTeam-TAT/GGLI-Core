/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 * 
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ProductGroupType;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.AgentAttachment;
import org.ace.insurance.system.common.agent.service.interfaces.IAgentService;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.country.Country;
import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.province.service.interfaces.IProvinceService;
import org.ace.insurance.system.common.qualification.Qualification;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.religion.Religion;
import org.ace.insurance.system.common.salepoint.SalePoint;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddNewAgentActionBean")
public class AddNewAgentActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{AgentService}")
	private IAgentService agentService;

	public void setAgentService(IAgentService agentService) {
		this.agentService = agentService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{ProvinceService}")
	private IProvinceService provinceService;

	public void setProvinceService(IProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{StateCodeService}")
	private IStateCodeService stateCodeService;

	public void setStateCodeService(IStateCodeService stateCodeService) {
		this.stateCodeService = stateCodeService;
	}

	@ManagedProperty(value = "#{TownshipCodeService}")
	private ITownshipCodeService townshipCodeService;

	public void setTownshipCodeService(ITownshipCodeService townshipCodeService) {
		this.townshipCodeService = townshipCodeService;
	}

	private final String PROFILE_DIR = "/upload/agent/";

	private Agent agent;
	private FamilyInfoDTO familyInfoDTO;
	private Entitys entitys;

	private String filePath;
	private String temporyDir;

	private List<RelationShip> relationShipList;
	private Map<String, FamilyInfoDTO> familyInfoDTOMap;
	private List<StateCode> stateCodeList;
	private List<TownshipCode> townshipCodeList;

	private boolean createNew;
	private boolean createNewFamilyInfo;
	private User user;

	@PreDestroy
	private void destroy() {
		removeParam("agent");
	}

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		initialization();
		temporyDir = "/temp/" + System.currentTimeMillis() + "/agent/";
		relationShipList = relationShipService.findAllRelationShip();
		townshipCodeList = new ArrayList<>();
		familyInfoDTOMap = new HashMap<>();
		loadProvinceNo();
		loadAgent();
	}

	private void loadAgent() {
		if (agent != null) {

			entitys = agent.getBranch().getEntity();
			createNew = false;

			if (agent.getIdType().equals(IdType.NRCNO)) {

				if (agent.getFullIdNo() == null) {
					agent.setFullIdNo(agent.getIdNo());
				}

				StringTokenizer tokenizer = new StringTokenizer(agent.getFullIdNo(), "/()");
				String stateCodeNo = tokenizer.nextToken();
				String townshipCodeNo = tokenizer.nextToken();
				StateCode stateCode = stateCodeService.findStateCodeByCodeNo(stateCodeNo, townshipCodeNo);
				TownshipCode townshipCode = townshipCodeService.findTownshipCodeByCodeNo(townshipCodeNo, stateCodeNo);
				IdConditionType type = IdConditionType.valueOf(tokenizer.nextToken());
				String idNo = tokenizer.nextToken();

				agent.setStateCode(stateCode);
				agent.setTownshipCode(townshipCode);
				agent.setIdConditionType(type);
				agent.setIdNo(idNo);

				changeAgentStateCode();

				String srcPath = getUploadPath() + PROFILE_DIR + agent.getId();
				String destPath = getUploadPath() + temporyDir + agent.getId();

				try {
					FileHandler.copyDirectory(srcPath, destPath);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			prepareAddNewFamilyInfo();
			loadFamilyInfoDTO();

		} else {
			createNewAgent();
		}
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("personalInfo".equals(event.getOldStep()) && "generalInfo".equals(event.getNewStep())) {
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	private void loadFamilyInfoDTO() {
		FamilyInfoDTO familyInfoDTO = null;
		StringTokenizer tokenizer = null;
		StateCode stateCode = null;
		TownshipCode townshipCode = null;
		String stateCodeNo = null;
		String townshipCodeNo = null;
		IdConditionType type = null;
		String idNo = null;

		for (FamilyInfo familyInfo : agent.getFamilyInfo()) {
			familyInfoDTO = new FamilyInfoDTO(familyInfo);

			if (familyInfoDTO.getIdType().equals(IdType.NRCNO)) {

				if (familyInfoDTO.getFullIdNo().contains("/()")) {

					tokenizer = new StringTokenizer(familyInfoDTO.getFullIdNo(), "/()");

					stateCodeNo = tokenizer.nextToken();
					townshipCodeNo = tokenizer.nextToken();
					stateCode = stateCodeService.findStateCodeByCodeNo(stateCodeNo, townshipCodeNo);
					townshipCode = townshipCodeService.findTownshipCodeByCodeNo(townshipCodeNo, stateCodeNo);
					type = IdConditionType.valueOf(tokenizer.nextToken());
					idNo = tokenizer.nextToken();

					familyInfoDTO.setStateCode(stateCode);
					familyInfoDTO.setTownshipCode(townshipCode);
					familyInfoDTO.setIdConditionType(type);
					familyInfoDTO.setIdNo(idNo);

				}
			} else {
				familyInfoDTO.setIdNo(familyInfoDTO.getFullIdNo());
			}

			familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
		}
	}

	public void createNewAgent() {
		createNew = true;
		agent = new Agent();
		agent.setName(new Name());
		agent.setPermanentAddress(new PermanentAddress());
		agent.setGender(Gender.FEMALE);
		agent.setIdType(IdType.NRCNO);
		agent.setMaritalStatus(MaritalStatus.SINGLE);
		agent.setGroupType(ProductGroupType.LIFE);
		familyInfoDTOMap = new HashMap<String, FamilyInfoDTO>();
		prepareAddNewFamilyInfo();
	}

	private void initialization() {
		agent = (Agent) getParam("agent");
	}

	private void createNewFamilyInfo() {
		createNewFamilyInfo = true;
		familyInfoDTO = new FamilyInfoDTO();
		familyInfoDTO.setIdType(IdType.NRCNO);
	}

	public void changeAgentIdType(AjaxBehaviorEvent e) {
		if (IdType.NRCNO.equals(agent.getIdType())) {
			agent.setFullIdNo(null);
			agent.setIdNo(null);
		} else {
			agent.setFullIdNo(null);
			agent.setIdNo(null);
			agent.setStateCode(null);
			agent.setTownshipCode(null);
			agent.setIdConditionType(null);
		}
	}

	public void changeFamilyIdType(AjaxBehaviorEvent e) {
		if (IdType.NRCNO.equals(familyInfoDTO.getIdType())) {
			familyInfoDTO.setIdNo(null);
		} else {
			familyInfoDTO.setIdNo(null);
			familyInfoDTO.setStateCode(null);
			familyInfoDTO.setTownshipCode(null);
			familyInfoDTO.setIdConditionType(null);
		}
	}

	public void prepareEditFamilyInfo(FamilyInfoDTO familyInfoDTO) {
		this.familyInfoDTO = familyInfoDTO;
		if (familyInfoDTO.getStateCode() != null) {
			changeFamilyStateCode();
		}
		this.createNewFamilyInfo = false;
	}

	public void saveFamilyInfo() {

		familyInfoDTO.setFullIdNo();
		familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
		createNewFamilyInfo();

	}

	public void saveAndMoreFamilyInfo() {
		familyInfoDTOMap.put(familyInfoDTO.getTempId(), familyInfoDTO);
		familyInfoDTO = new FamilyInfoDTO();
	}

	public void removeFamilyInfo(FamilyInfoDTO familyInfoDTO) {
		familyInfoDTOMap.remove(familyInfoDTO.getTempId());
	}

	public List<FamilyInfo> getFamilyInfoList() {
		List<FamilyInfo> result = new ArrayList<FamilyInfo>();
		if (familyInfoDTOMap.values() != null) {
			for (FamilyInfoDTO familyInfoDTO : familyInfoDTOMap.values()) {
				result.add(new FamilyInfo(familyInfoDTO.getInitialId(), familyInfoDTO.getFullIdNo(), familyInfoDTO.getIdType(), familyInfoDTO.getDateOfBirth(),
						familyInfoDTO.getName(), familyInfoDTO.getRelationShip(), familyInfoDTO.getIndustry(), familyInfoDTO.getOccupation()));
			}
		}
		return result;
	}

	public void handleFileUpload(FileUploadEvent event) throws IOException {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName();
		String filePath = temporyDir + fileName;
		createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		agent.setAttachment(new AgentAttachment(fileName, filePath));
	}

	public void removePhotoImage() {
		try {
			agent.getAttachment().setFilePath(null);
			agent.getAttachment().setName(null);
			FileUtils.forceDelete(new File(getUploadPath() + agent.getAttachment().getFilePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isShowImage() {
		if (agent.getAttachment() != null && agent.getAttachment().getFilePath() != null && !agent.getAttachment().getFilePath().isEmpty())
			return true;
		else
			return false;
	}

	private void moveUploadedFiles() {
		try {
			if (agent.getAttachment() != null) {
				FileHandler.moveFiles(getUploadPath(), temporyDir, PROFILE_DIR + agent.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean checkExistingAgent() {
		boolean result = false;
		agent.setFullIdNo(setAgentFullIdNo());
		result = agentService.isExitingAgent(agent);
		return result;
	}

	private String setAgentFullIdNo() {
		String result = "";
		if (agent.getIdType().equals(IdType.NRCNO)) {
			result = agent.getStateCode().getCodeNo() + "/" + agent.getTownshipCode().getTownshipcodeno() + "(" + agent.getIdConditionType().getLabel() + ")" + agent.getIdNo();
		} else if (agent.getIdType().equals(IdType.FRCNO) || agent.getIdType().equals(IdType.PASSPORTNO)) {
			result = agent.getIdNo();
		}
		return result;
	}

	public String addNewAgent() {
		String result = null;
		try {
			if (!checkExistingAgent()) {
				agent.setIdNo(setAgentFullIdNo());
				agent.setFamilyInfo(getFamilyInfoList());
				agentService.addNewAgent(agent);
				if (agent.getAttachment() != null) {
					AgentAttachment attachment = agent.getAttachment();
					String targetFilePath = getUploadPath() + PROFILE_DIR + agent.getId() + "/" + attachment.getName();
					attachment.setFilePath(targetFilePath);
				}

				moveUploadedFiles();
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.INSERT_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, agent.getFullName());
				result = "manageAgent";

			} else {
				addErrorMessage(null, MessageId.EXISTING_CUSTOMER, agent.getFullName());
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}

		return result;
	}

	public String updateAgent() {
		String result = null;
		try {
			if (!checkExistingAgent()) {
				if (agent.getAttachment() != null) {
					AgentAttachment attachment = agent.getAttachment();
					String targetFilePath = PROFILE_DIR + agent.getId() + "/" + attachment.getName();
					attachment.setFilePath(targetFilePath);
				}
				agent.setIdNo(setAgentFullIdNo());
				agent.setFamilyInfo(getFamilyInfoList());
				agentService.updateAgent(agent);
				moveUploadedFiles();
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UPDATE_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, agent.getFullName());
				result = "manageAgent";
			} else {
				addErrorMessage(null, MessageId.EXISTING_CUSTOMER, agent.getFullName());
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void selectSalePoint() {

		selectSalePointByBranch(agent.getBranch() == null ? "" : agent.getBranch().getId());
		// PrimeFaces.current().dialog().openDynamic("salePointDialog",
		// WebUtils.getDialogOption(), null);
	}

	public void removeBranch() {
		agent.setBranch(null);
		agent.setSalePoint(null);

	}

	public void returnSalePoint(SelectEvent event) {
		SalePoint salePoint = (SalePoint) event.getObject();
		agent.setSalePoint(salePoint);
	}

	public void selectBranchByEntity() {
		selectBranchByEntityIdAndBranchId(entitys == null ? "" : entitys.getId(), user.getBranch().getId());
	}

	public void returnEntity(SelectEvent event) {
		Entitys entity = (Entitys) event.getObject();
		if (entitys != null && !entity.getId().equals(entitys.getId())) {
			agent.setBranch(null);
			agent.setSalePoint(null);
		}
		this.entitys = entity;

	}

	public void removeEntity() {
		this.entitys = null;
		agent.setBranch(null);
		agent.setSalePoint(null);
	}

	public String getFilePath() {
		return filePath;
	}

	public String getTemporyDir() {
		return temporyDir;
	}

	public boolean isCreateNewFamilyInfo() {
		return createNewFamilyInfo;
	}

	public FamilyInfoDTO getFamilyInfoDTO() {
		return familyInfoDTO;
	}

	public void setFamilyInfoDTO(FamilyInfoDTO familyInfoDTO) {
		this.familyInfoDTO = familyInfoDTO;
	}

	public List<FamilyInfoDTO> getFamilyInfoDTOList() {
		if (familyInfoDTOMap.values() != null) {
			return new ArrayList<FamilyInfoDTO>(familyInfoDTOMap.values());
		}
		return new ArrayList<FamilyInfoDTO>();
	}

	public void prepareAddNewFamilyInfo() {
		createNewFamilyInfo();
	}

	public Gender[] getGenderSelectItemList() {
		return Gender.values();
	}

	public MaritalStatus[] getMaritalStatusSelectItemList() {
		return MaritalStatus.values();
	}

	public IdType[] getIdTypeSelectItemList() {
		return IdType.values();
	}

	public ProductGroupType[] getProductGroupTypeSelectItemList() {
		return ProductGroupType.values();
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public Agent getAgent() {
		return agent;
	}

	public List<RelationShip> getRelationShipList() {
		return relationShipList;
	}

	public void returnNationality(SelectEvent event) {
		Country nationality = (Country) event.getObject();
		agent.setCountry(nationality);
	}

	public void returnReligion(SelectEvent event) {
		Religion religion = (Religion) event.getObject();
		agent.setReligion(religion);
	}

	public void returnQualification(SelectEvent event) {
		Qualification qualification = (Qualification) event.getObject();
		agent.setQualification(qualification);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		agent.setOrganization(organization);
	}

	public void returnBankBranch(SelectEvent event) {
		BankBranch bankBranch = (BankBranch) event.getObject();
		agent.setBankBranch(bankBranch);

	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		agent.setBranch(branch);
		agent.setSalePoint(null);
	}

	public void returnResidentTownship(SelectEvent event) {
		Township residentTownship = (Township) event.getObject();
		agent.getResidentAddress().setTownship(residentTownship);
	}

	public void returnPermanentTownship(SelectEvent event) {
		Township permanentTownship = (Township) event.getObject();
		agent.getPermanentAddress().setTownship(permanentTownship);
	}

	public void returnFamilyOccupation(SelectEvent event) {
		Occupation familyOccupation = (Occupation) event.getObject();
		familyInfoDTO.setOccupation(familyOccupation);
	}

	public void returnFamilyIndustry(SelectEvent event) {
		Industry familyIndustry = (Industry) event.getObject();
		familyInfoDTO.setIndustry(familyIndustry);
	}

	public void loadProvinceNo() {
		stateCodeList = stateCodeService.findAllStateCode();
	}

	public void changeAgentStateCode() {
		townshipCodeList = townshipCodeService.findByStateCode(agent.getStateCode());
	}

	public void changeFamilyStateCode() {
		townshipCodeList = townshipCodeService.findByStateCode(familyInfoDTO.getStateCode());
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
	}

	public String setFullIdNo() {
		String result = "";
		if (familyInfoDTO.getIdType().equals(IdType.NRCNO)) {
			result = familyInfoDTO.getStateCode() + "/" + familyInfoDTO.getTownshipCode() + "(" + familyInfoDTO.getIdConditionType() + ")" + familyInfoDTO.getIdNo();
		} else if (familyInfoDTO.getIdType().equals(IdType.FRCNO) || familyInfoDTO.getIdType().equals(IdType.PASSPORTNO)) {
			result = familyInfoDTO.getIdNo();
		}
		return result;
	}

	public Entitys getEntitys() {
		return entitys;
	}

	public void setEntitys(Entitys entitys) {
		this.entitys = entitys;
	}

}
