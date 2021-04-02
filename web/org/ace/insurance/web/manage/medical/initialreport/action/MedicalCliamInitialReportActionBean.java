package org.ace.insurance.web.manage.medical.initialreport.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.medical.claim.ClaimInitialReport;
import org.ace.insurance.medical.claim.ClaimInitialReportICD;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.medical.claim.frontService.initialReport.interfaces.IMedicalClaimInitialReportFrontService;
import org.ace.insurance.medical.claim.service.interfaces.IClaimInitialReportService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.system.common.icd10.ICD10;
import org.ace.insurance.system.common.icd10.service.interfaces.IICD10Service;
import org.ace.insurance.system.common.saleman.SaleMan;
import org.ace.insurance.system.common.stateCode.StateCode;
import org.ace.insurance.system.common.stateCode.service.interfaces.IStateCodeService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.townshipCode.TownshipCode;
import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.web.common.UserType;
import org.ace.insurance.web.datamodel.ICD10DataModel;
import org.ace.insurance.web.manage.medical.claim.MedicalClaimInitialReportDTO;
import org.ace.insurance.web.manage.medical.initialreport.factory.MedicalClaimInitialReportFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;

/**
 * 
 * Medical MedicalCliamInitialReportActionBean
 * 
 * @author John Htet
 * @since 1.0.0
 * @date 2015/05/14
 */
@ViewScoped
@ManagedBean(name = "MedicalCliamInitialReportActionBean")
public class MedicalCliamInitialReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ClaimInitialReportService}")
	private IClaimInitialReportService claimInitialReportService;

	public void setClaimInitialReportService(IClaimInitialReportService claimInitialReportService) {
		this.claimInitialReportService = claimInitialReportService;
	}

	@ManagedProperty(value = "#{MedicalClaimInitialReportFrontService}")
	private IMedicalClaimInitialReportFrontService medicalClaimInitialReportFrontService;

	public void setMedicalClaimInitialReportFrontService(IMedicalClaimInitialReportFrontService medicalClaimInitialReportFrontService) {
		this.medicalClaimInitialReportFrontService = medicalClaimInitialReportFrontService;
	}

	@ManagedProperty(value = "#{ICD10Service}")
	private IICD10Service iCD10Service;

	public void setiCD10Service(IICD10Service iCD10Service) {
		this.iCD10Service = iCD10Service;
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

	private List<ICD10> iCD10List;
	private ICD10DataModel icd10DataModel;
	private List<ICD10> iCD10InitialReportList;
	private List<ICD10> selectedICDList;
	private List<StateCode> stateCodeList = new ArrayList<StateCode>();
	private List<TownshipCode> townshipCodeList = new ArrayList<TownshipCode>();
	private boolean isNrcReporter;
	private boolean isStillApplyReporter;
	private boolean createNew;
	private String userType;
	private ClaimInitialReport claimInitialReport;
	private MedicalPolicyInsuredPerson policyInsuredPerson;
	private MedicalClaimInitialReportDTO medicalClaimInitialReportDTO;
	private List<MedicalPolicyInsuredPerson> policyInsuredPersonList;
	private String product;

	@PostConstruct
	public void init() {
		initialization();
		userType = UserType.AGENT.toString();
		iCD10List = iCD10Service.findAllICD10();
		icd10DataModel = new ICD10DataModel(iCD10List);
		loadClaimInitialReport();
		stateCodeList = stateCodeService.findAllStateCode();
		policyInsuredPersonList = new ArrayList<>();
	}

	@PreDestroy
	private void destroy() {
		removeParam("claimInitialReport");
	}

	private void initialization() {
		claimInitialReport = (ClaimInitialReport) getParam("claimInitialReport");
	}

	private void loadClaimInitialReport() {
		if (claimInitialReport != null) {
			createNew = false;
			this.medicalClaimInitialReportDTO = MedicalClaimInitialReportFactory.createMedicalClaimInitialReportDTO(claimInitialReport);
			this.medicalClaimInitialReportDTO.setClaimStatus(ClaimStatus.INITIAL_INFORM);
			for (ClaimInitialReportICD var : medicalClaimInitialReportDTO.getClaimInitialReportICDList()) {
				if (!iCD10InitialReportList.contains(var.getIcd10())) {
					iCD10InitialReportList.add(var.getIcd10());
				}
			}
		} else {
			createNewClaimInitialReport();
		}
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		if ("initialReportTap".equals(event.getOldStep())) {
			if (iCD10InitialReportList == null || iCD10InitialReportList.size() < 1) {
				addErrorMessage("medicalClaimInitialForm" + ":iCD10ListPanelGroup", MessageId.HOSPITALIZE_REASONS);
				valid = false;
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(UserType.AGENT.toString())) {
			medicalClaimInitialReportDTO.setSaleMan(null);
			medicalClaimInitialReportDTO.setReferral(null);
		} else if (userType.equals(UserType.SALEMAN.toString())) {
			medicalClaimInitialReportDTO.setAgent(null);
			medicalClaimInitialReportDTO.setReferral(null);
		} else if (userType.equals(UserType.REFERRAL.toString())) {
			medicalClaimInitialReportDTO.setSaleMan(null);
			medicalClaimInitialReportDTO.setAgent(null);
		}
	}

	public void changeStateCode(AjaxBehaviorEvent e) {
		StateCode stateCode = (StateCode) ((UIOutput) e.getSource()).getValue();
		townshipCodeList = townshipCodeService.findByStateCode(stateCode);
	}

	public void changeIdType(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		if (idType.equals(IdType.NRCNO)) {
			isNrcReporter = true;
			isStillApplyReporter = true;
		} else if (idType.equals(IdType.STILL_APPLYING)) {
			isNrcReporter = false;
			isStillApplyReporter = false;
		} else {
			isNrcReporter = false;
			isStillApplyReporter = true;
		}
	}

	public void selectMedicalPolicyNo() {
		selectMedicalPolicyNo(null);
	}

	public MedicalPolicyInsuredPerson getPolicyInsuredPerson() {
		return policyInsuredPerson;
	}

	public void setPolicyInsuredPerson(MedicalPolicyInsuredPerson policyInsuredPerson) {
		this.policyInsuredPerson = policyInsuredPerson;
	}

	public List<MedicalPolicyInsuredPerson> getPolicyInsuredPersonList() {
		return policyInsuredPersonList;
	}

	public void setPolicyInsuredPersonList(List<MedicalPolicyInsuredPerson> policyInsuredPersonList) {
		this.policyInsuredPersonList = policyInsuredPersonList;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public MedicalClaimInitialReportDTO getMedicalClaimInitialReportDTO() {
		return medicalClaimInitialReportDTO;
	}

	public void setMedicalClaimInitialReportDTO(MedicalClaimInitialReportDTO medicalClaimInitialReportDTO) {
		this.medicalClaimInitialReportDTO = medicalClaimInitialReportDTO;
	}

	public void returnSaleMan(SelectEvent event) {
		SaleMan saleMan = (SaleMan) event.getObject();
		medicalClaimInitialReportDTO.setSaleMan(saleMan);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		medicalClaimInitialReportDTO.setAgent(agent);
	}

	public void returnReferral(SelectEvent event) {
		medicalClaimInitialReportDTO.setReferral((Customer) event.getObject());
	}

	public void returnMedicalPolicyNo(SelectEvent event) {
		MedicalPolicy medicalPolicy = (MedicalPolicy) event.getObject();
		medicalClaimInitialReportDTO.setPolicyNo(medicalPolicy.getPolicyNo());
		medicalClaimInitialReportDTO.setAgent(medicalPolicy.getAgent());
		medicalClaimInitialReportDTO.setSaleMan(medicalPolicy.getSaleMan());
		policyInsuredPersonList = medicalPolicy.getPolicyInsuredPersonList();
		medicalClaimInitialReportDTO.setProduct(policyInsuredPersonList.get(0).getProduct());
	}

	public void returnMedicalPlaceDialog(SelectEvent event) {
		medicalClaimInitialReportDTO.setMedicalPlace((Hospital) event.getObject());
	}

	public void changePolicyInsuredPerson(AjaxBehaviorEvent event) {
		medicalClaimInitialReportDTO.setPolicyInsuredPerson(this.policyInsuredPerson);
	}

	public void changeProduct(AjaxBehaviorEvent event) {
		medicalClaimInitialReportDTO.setPolicyNo(null);
		policyInsuredPersonList = new ArrayList<>();
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setFatherName(null);
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setIdType(null);
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setIdNo("");
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setTownshipCode(null);
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setStateCode(null);
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setIdConditionType(null);
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setResidentAddress(null);
		medicalClaimInitialReportDTO.getPolicyInsuredPerson().getCustomer().setOccupation(null);
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public List<String> getProductList() {
		return Arrays.asList("Individual Critical Illness", "Group Critical Illness", "Individual Health", "Group Health", "Micro Health", "Medical");
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public ClaimInitialReport getClaimInitialReport() {
		return claimInitialReport;
	}

	public void setClaimInitialReport(ClaimInitialReport claimInitialReport) {
		this.claimInitialReport = claimInitialReport;
	}

	public List<ICD10> getiCD10InitialReportList() {
		return iCD10InitialReportList;
	}

	public void setiCD10InitialReportList(List<ICD10> iCD10InitialReportList) {
		this.iCD10InitialReportList = iCD10InitialReportList;
	}

	public List<ICD10> getiCD10List() {
		return iCD10List;
	}

	public List<ICD10> getSelectedICDList() {
		return selectedICDList;
	}

	public ICD10DataModel getIcd10DataModel() {
		return icd10DataModel;
	}

	public void setSelectedICDList(List<ICD10> selectedICDList) {
		this.selectedICDList = selectedICDList;
	}

	public List<StateCode> getStateCodeList() {
		return stateCodeList;
	}

	public List<TownshipCode> getTownshipCodeList() {
		return townshipCodeList;
	}

	public boolean isNrcReporter() {
		return isNrcReporter;
	}

	public boolean isStillApplyReporter() {
		return isStillApplyReporter;
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
	}

	public void returnResidentTownship(SelectEvent event) {
		Township residentTownship = (Township) event.getObject();
		medicalClaimInitialReportDTO.getClaimInitialReporter().setTownship((residentTownship));
	}

	public String submitInitialReport() {
		String result = null;
		try {
			if (!validReportInfo()) {
				return result;
			}

			medicalClaimInitialReportDTO.setClaimStatus(ClaimStatus.INITIAL_INFORM);
			ClaimInitialReport claimInitialReport = medicalClaimInitialReportFrontService.addNewMedicalClaimInitialRep(medicalClaimInitialReportDTO, iCD10InitialReportList);
			createNewClaimInitialReport();
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.MEDICAL_ClAIM_INITIAL_REPORT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, claimInitialReport.getClaimReportNo());
			result = "manageMedicalClaimInitialReport";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String updateInitialReport() {
		String result = null;
		try {
			if (!validReportInfo()) {
				return result;
			}
			ClaimInitialReport claimInitialReport = medicalClaimInitialReportFrontService.updateMedicalClaimInitialRep(medicalClaimInitialReportDTO, iCD10InitialReportList);
			addInfoMessage(null, MessageId.MEDICAL_ClAIM_INITIAL_REPORT_PROCESS_UPDATE, claimInitialReport.getClaimReportNo());
			createNewClaimInitialReport();
			result = "manageMedicalClaimInitialReport";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private boolean validReportInfo() {
		boolean valid = true;

		String formID = "medicalClaimInitialForm";
		if (isEmpty(medicalClaimInitialReportDTO.getClaimInitialReporter().getIdNo())
				&& !medicalClaimInitialReportDTO.getClaimInitialReporter().getIdType().equals(IdType.STILL_APPLYING)) {
			addErrorMessage(formID + ":reporterRegidNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (medicalClaimInitialReportDTO.getClaimInitialReporter().getIdType().equals(IdType.NRCNO)) {
			if ((medicalClaimInitialReportDTO.getClaimInitialReporter().getStateCode() == null && medicalClaimInitialReportDTO.getClaimInitialReporter().getTownshipCode() != null)
					|| (medicalClaimInitialReportDTO.getClaimInitialReporter().getStateCode() != null
							&& medicalClaimInitialReportDTO.getClaimInitialReporter().getTownshipCode() == null)) {
				addErrorMessage(formID + ":reporterRegidNo", MessageId.NRC_STATE_AND_TOWNSHIP_ERROR);
				valid = false;
			} else if (medicalClaimInitialReportDTO.getClaimInitialReporter().getIdNo().length() != 6
					&& medicalClaimInitialReportDTO.getClaimInitialReporter().getStateCode() != null
					&& medicalClaimInitialReportDTO.getClaimInitialReporter().getTownshipCode() != null) {
				addErrorMessage(formID + ":reporterRegidNo", MessageId.NRC_FORMAT_INCORRECT);
				valid = false;
			}
		}
		return valid;
	}

	public void createNewClaimInitialReport() {
		createNew = true;
		medicalClaimInitialReportDTO = new MedicalClaimInitialReportDTO();
		iCD10InitialReportList = new ArrayList<ICD10>();
		isNrcReporter = true;
		isStillApplyReporter = true;
	}

	public void prepareUpdateClaimInitialReport(ClaimInitialReport claimInitialReport) {
		createNew = false;
		this.medicalClaimInitialReportDTO = MedicalClaimInitialReportFactory.createMedicalClaimInitialReportDTO(claimInitialReport);
		// TODO FIX PSH
		this.medicalClaimInitialReportDTO.setClaimStatus(ClaimStatus.INITIAL_INFORM);
		for (ClaimInitialReportICD var : medicalClaimInitialReportDTO.getClaimInitialReportICDList()) {
			if (!iCD10InitialReportList.contains(var.getIcd10())) {
				iCD10InitialReportList.add(var.getIcd10());
			}
		}
	}

	public void removeICD10List(ICD10 icd) {
		iCD10InitialReportList.remove(icd);
	}

	public void addNewICD10(ICD10 icd) {

		if (iCD10InitialReportList == null) {
			iCD10InitialReportList = new ArrayList<ICD10>();
		}

		for (ICD10 var : selectedICDList) {
			if (!iCD10InitialReportList.contains(var)) {
				iCD10InitialReportList.add(var);
			}
		}
	}

	public List<TownshipCode> completeTownshipCode(String query) {
		List<TownshipCode> filteredTownshipCodes = new ArrayList<TownshipCode>();
		for (TownshipCode townshipCode : townshipCodeList) {
			if (townshipCode.getTownshipcodeno().startsWith(query)) {
				filteredTownshipCodes.add(townshipCode);
			}
		}
		return filteredTownshipCodes;
	}

}
