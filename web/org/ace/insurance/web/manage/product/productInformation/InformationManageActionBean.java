package org.ace.insurance.web.manage.product.productInformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.filter.productinformation.dto.MainCategoryDTO;
import org.ace.insurance.filter.productinformation.dto.NameTextDTO;
import org.ace.insurance.filter.productinformation.dto.PhotoImageDTO;
import org.ace.insurance.filter.productinformation.dto.SubCategoryDTO;
import org.ace.insurance.filter.productinformation.factory.MainCategoryFactory;
import org.ace.insurance.medical.productprocess.PriorityControlType;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.system.productinformation.maincategory.MainCategory;
import org.ace.insurance.system.productinformation.maincategory.service.interfaces.IMainCategoryService;
import org.ace.insurance.system.productinformation.photoimage.PhotoImage;
import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.insurance.system.productinformation.text.NameText;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.io.FileUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "InformationManageActionBean")
public class InformationManageActionBean extends BaseBean {

	@ManagedProperty(value = "#{MainCategoryService}")
	private IMainCategoryService mainCategoryService;

	public void setMainCategoryService(IMainCategoryService mainCategoryService) {
		this.mainCategoryService = mainCategoryService;
	}

	boolean bold;
	boolean italic;
	boolean underline;
	private boolean createNewNameText;
	private boolean languageDisable;
	private boolean updateFlag;
	private boolean editNameFlag;
	private boolean updateSub;
	String text = "";
	String originalText = "";
	private static final String tempDir = "/temp/MainCategory/";
	private MainCategoryDTO mainCategoryDTO;
	private NameTextDTO nameTextDTO;
	private PhotoImageDTO photoImageDTO;
	private SubCategoryDTO subCategoryDTO;
	private MainCategory mainCategory;
	private List<MainCategory> mainCategoryList;
	private Language language;
	private NameText nameText;
	private PriorityControlType priorityControlType;
	private List<PriorityControlType> priorityControlTypes;

	@PostConstruct
	public void init() {
		initializeInjection();
		if (mainCategoryDTO == null) {
			mainCategoryDTO = new MainCategoryDTO();
			subCategoryDTO = new SubCategoryDTO();
		}
		if (subCategoryDTO == null) {
			subCategoryDTO = new SubCategoryDTO();
		}
		if (subCategoryDTO != null && mainCategoryDTO != null) {
			if (subCategoryDTO.isUpdateFlag() == true || mainCategoryDTO.isUpdateFlag() == true) {
				updateFlag = true;
			} else {
				updateFlag = false;
			}
		}
		photoImageDTO = new PhotoImageDTO();
		updateSub = true;
		loadData();
		List<PriorityControlType> list = new ArrayList<PriorityControlType>(Arrays.asList(PriorityControlType.values()));
		// list.remove(PriorityControlType.CUSTOMIZE);
		priorityControlTypes = list;
	}

	public void initializeInjection() {
		mainCategoryDTO = (MainCategoryDTO) ((mainCategoryDTO == null) ? getParam("MAINCATEGORY") : mainCategoryDTO);
		subCategoryDTO = (SubCategoryDTO) ((subCategoryDTO == null) ? getParam("SUBCATEGORY") : subCategoryDTO);
		if (mainCategoryDTO != null) {
			if (mainCategoryDTO.getNameTextDTOList() != null && mainCategoryDTO.getNameTextDTOList().size() > 1) {
				createNewNameText = true;
			}
		}
		removeParam("MAINCATEGORY");
		removeParam("SUBCATEGORY");
	}

	public void loadData() {
		mainCategoryList = (List<MainCategory>) mainCategoryService.findAllMainCategory();
	}

	public void prepareUpdateInformation(MainCategoryDTO mainCategoryDTO) {
		this.mainCategoryDTO = mainCategoryDTO;

	}

	public void prepareEditInformation(MainCategory mainCategory) throws IOException {
		updateFlag = true;
		createNewNameText = true;
		this.mainCategoryDTO = MainCategoryFactory.createMainCategoryDTO(mainCategory);
		FileHandler.createFile(new File(getRealSystemPath() + mainCategory.getImage().getFilepath()), mainCategory.getImage().getImage());
		sortImageOrder();
	}

	public void prepareNameText() {
		nameTextDTO = new NameTextDTO();
		editNameFlag = false;
		text = "";
		bold = false;
		italic = false;
		underline = false;
		languageDisable = false;
	}

	public void prepareEditNameText(NameTextDTO name) {
		this.nameTextDTO = name;
		editNameFlag = true;
		text = name.getName();
		originalText = name.getName();
		String str = "";
		if (!name.getName().isEmpty()) {
			str = name.getName();
			bold = false;
			underline = false;
			italic = false;
			if (str.contains("<b>")) {
				bold = true;
				str = str.replace("<b>", "");
				str = str.replace("</b>", "");
			}
			if (str.contains("<u>")) {
				underline = true;
				str = str.replace("<u>", "");
				str = str.replace("</u>", "");
			}
			if (str.contains("<i>")) {
				italic = true;
				str = str.replace("<i>", "");
				str = str.replace("</i>", "");
			}
		}
		name.setName(str);
		if (mainCategoryDTO.getNameTextDTOList().size() > 1) {
			languageDisable = true;
		}
	}

	public void preparePhoto() {
		photoImageDTO = new PhotoImageDTO();
	}

	public String prepareSubCategory(SubCategoryDTO subCategoryDTO) throws IOException {
		subCategoryDTO.setUpdateSub(updateSub);
		subCategoryDTO.setUpdateFlag(updateFlag);
		if (subCategoryDTO.getPhotoImageDTO() != null)
			FileHandler.createFile(new File(getRealSystemPath() + subCategoryDTO.getPhotoImageDTO().getImagePath()), subCategoryDTO.getPhotoImageDTO().getImage());
		putParam("SUBCATEGORY", subCategoryDTO);
		putParam("MAINCATEGORY", mainCategoryDTO);
		return "subInformation";
	}

	public String nextSubCategory() {
		if (updateFlag == true) {
			mainCategoryDTO.setUpdateFlag(updateFlag);
		}
		putParam("MAINCATEGORY", mainCategoryDTO);
		return "subInformation";
	}

	public void removeNameText(NameTextDTO nameTexxt) {
		if (mainCategoryDTO.getNameTextDTOList().contains(nameTexxt)) {
			mainCategoryDTO.getNameTextDTOList().remove(nameTexxt);
			languageDisable = false;
			editNameFlag = false;
			createNewNameText = false;
			PrimeFaces.current().ajax().update("informationEntryForm:addNewNameText");
		}
	}

	public void removeSubCategory(SubCategoryDTO subCategory) {
		if (mainCategoryDTO.getSubCategoryDTOList().contains(subCategory)) {
			mainCategoryDTO.getSubCategoryDTOList().remove(subCategory);
		}
	}

	public void addNewInformation() throws IOException {
		try {
			if (validate()) {
				mainCategory = mainCategoryService.addNewMainCategory(MainCategoryFactory.createMainCategory(mainCategoryDTO));
				if (updateFilePath())
					mainCategoryService.updateMainCategory(mainCategory);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, "Successfully Commit");
				loadData();
				createNewNameText = false;
				mainCategoryDTO = new MainCategoryDTO();
				deleteTempDir();
			}

		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateInformation() throws IOException {
		try {
			if (validate()) {
				mainCategory = mainCategoryService.updateMainCategory(MainCategoryFactory.createMainCategory(mainCategoryDTO));
				if (updateFilePath())
					mainCategoryService.updateMainCategory(mainCategory);
				updateFlag = false;
				loadData();
				mainCategoryDTO = new MainCategoryDTO();
				createNewNameText = false;
				priorityControlType = null;
				deleteTempDir();
				addInfoMessage(null, MessageId.UPDATE_SUCCESS, "Successfully Update");
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void deleteInformation(MainCategory mainCategory) {
		try {
			mainCategoryService.deleteMainCategory(mainCategory);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, "Success Fully Delete");
			loadData();
			createNewNameText = false;
			updateFlag = false;
			priorityControlType = null;
			mainCategoryDTO = new MainCategoryDTO();
			deleteTempDir();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public boolean validate() {
		boolean flag = true;
		String formId = "informationEntryForm";
		if (mainCategoryDTO != null) {
			if (mainCategoryDTO.getNameTextDTOList().size() == 0) {
				addErrorMessage(formId + ":maincategoryTable", MessageId.REQUIRED_VALUES, "");
				flag = false;
			}
			if (mainCategoryDTO.getNameTextDTOList() != null && mainCategoryDTO.getNameTextDTOList().size() != 0) {
				if (mainCategoryDTO.getNameTextDTOList().size() < 2) {
					addErrorMessage(formId + ":maincategoryTable", MessageId.REQUIRED_ALL_LANGUAGE, "");
					flag = false;
				}
			}

			if (mainCategoryDTO.getSubCategoryDTOList() != null && mainCategoryDTO.getSubCategoryDTOList().size() > 0) {
				for (int i = 0; i < mainCategoryDTO.getSubCategoryDTOList().size(); i++) {
					if (mainCategoryDTO.getSubCategoryDTOList().get(i).getOrder() == 0) {
						addErrorMessage(formId + ":subCategoryTable", MessageId.REQUIRED, "Order");
						flag = false;
					} else {
						for (int j = i + 1; j <= mainCategoryDTO.getSubCategoryDTOList().size() - 1; j++) {
							if (mainCategoryDTO.getSubCategoryDTOList().get(i).getOrder() == mainCategoryDTO.getSubCategoryDTOList().get(j).getOrder()) {
								addErrorMessage(formId + ":subCategoryTable", MessageId.DUPLICATE_ORDER, "");
								flag = false;
							}
						}
					}
				}
			}
			if (mainCategoryDTO.getProduct() == null) {
				addErrorMessage(formId + ":product", MessageId.REQUIRED_VALUES, "");
			}
			// if (mainCategoryDTO.getPhotoDTO() == null) {
			// addErrorMessage(formId + ":imageUpload",
			// MessageId.REQUIRED_VALUES, "");
			// flag = false;
			// } else if (mainCategoryDTO.getPhotoDTO().getImagePath() == null)
			// {
			// addErrorMessage(formId + ":imageUpload",
			// MessageId.REQUIRED_VALUES, "");
			// flag = false;
			// }
		}

		return flag;
	}

	public void applyMainCategoryNameText() {
		String formID = "mainCategoryEntryForm";
		boolean flag = true;
		if (nameTextDTO.getName().equals("")) {
			flag = false;
			addErrorMessage(formID + ":maincategoryName", MessageId.REQUIRED_VALUES);
		}
		if (mainCategoryDTO.getNameTextDTOList() != null && mainCategoryDTO.getNameTextDTOList().size() != 0) {
			for (NameTextDTO name : mainCategoryDTO.getNameTextDTOList()) {
				if (name.getLanguage() == nameTextDTO.getLanguage()) {
					flag = false;
					addErrorMessage(formID + ":language", MessageId.LANGUAGE_ALREADY_EXISTS, nameTextDTO.getLanguage());
				}
			}
		}
		if (flag) {
			nameTextDTO.setName(text);
			mainCategoryDTO.getNameTextDTOList().add(nameTextDTO);
			PrimeFaces.current().executeScript("PF('PF('maincategoryDialog').hide();");
			if (mainCategoryDTO.getNameTextDTOList() != null && mainCategoryDTO.getNameTextDTOList().size() == nameTextDTO.getLanguage().values().length) {
				createNewNameText = true;
				PrimeFaces.current().ajax().update("informationEntryForm:addNewNameText");
			}
		}
	}

	public void updateMainCategoryNameText() {
		String formID = "mainCategoryEntryForm";
		boolean flag = true;
		if (nameTextDTO.getName().equals("")) {
			flag = false;
			addErrorMessage(formID + ":maincategoryName", MessageId.REQUIRED_VALUES);
		}
		if (flag) {
			nameTextDTO.setName(text);
			mainCategoryDTO.addNameTextDTO(nameTextDTO);
			PrimeFaces.current().executeScript("PF('PF('maincategoryDialog').hide();");
		}
	}

	public void changePriorityControl(AjaxBehaviorEvent e) {
		if (priorityControlType.equals(priorityControlType.CUSTOMIZE)) {
			for (SubCategoryDTO a : mainCategoryDTO.getSubCategoryDTOList()) {
				a.setOrder(0);
			}
		} else if (priorityControlType.equals(priorityControlType.ACENDING)) {
			for (int i = 0; i < mainCategoryDTO.getSubCategoryDTOList().size(); i++) {
				mainCategoryDTO.getSubCategoryDTOList().get(i).setOrder(i + 1);
			}
		} else {
			int i = mainCategoryDTO.getSubCategoryDTOList().size();
			for (SubCategoryDTO a : mainCategoryDTO.getSubCategoryDTOList()) {
				a.setOrder(i);
				i--;
			}
		}
	}

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		mainCategoryDTO.setProduct(product);
	}

	public MainCategoryDTO getMainCategoryDTO() {
		return mainCategoryDTO;
	}

	public void setMainCategoryDTO(MainCategoryDTO mainCategoryDTO) {
		this.mainCategoryDTO = mainCategoryDTO;
	}

	public NameTextDTO getNameTextDTO() {
		return nameTextDTO;
	}

	public void setNameTextDTO(NameTextDTO nameTextDTO) {
		this.nameTextDTO = nameTextDTO;
	}

	public MainCategory getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(MainCategory mainCategory) {
		this.mainCategory = mainCategory;
	}

	public List<MainCategory> getMainCategoryList() {
		return mainCategoryList;
	}

	public void setMainCategoryList(List<MainCategory> mainCategoryList) {
		this.mainCategoryList = mainCategoryList;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public PhotoImageDTO getPhotoImageDTO() {
		return photoImageDTO;
	}

	public void setPhotoImageDTO(PhotoImageDTO photoImageDTO) {
		this.photoImageDTO = photoImageDTO;
	}

	public PriorityControlType getPriorityControlType() {
		return priorityControlType;
	}

	public void setPriorityControlType(PriorityControlType priorityControlType) {
		this.priorityControlType = priorityControlType;
	}

	public NameText getNameText() {
		return nameText;
	}

	public List<PriorityControlType> getPriorityControlTypes() {
		return priorityControlTypes;
	}

	public void setPriorityControlTypes(List<PriorityControlType> priorityControlTypes) {
		this.priorityControlTypes = priorityControlTypes;
	}

	public void setNameText(NameText nameText) {
		this.nameText = nameText;
	}

	public SubCategoryDTO getSubCategoryDTO() {
		return subCategoryDTO;
	}

	public void setSubCategoryDTO(SubCategoryDTO subCategoryDTO) {
		this.subCategoryDTO = subCategoryDTO;
	}

	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
	}

	public boolean isEditNameFlag() {
		return editNameFlag;
	}

	public void setEditNameFlag(boolean editNameFlag) {
		this.editNameFlag = editNameFlag;
	}

	public boolean isLanguageDisable() {
		return languageDisable;
	}

	public void setLanguageDisable(boolean languageDisable) {
		this.languageDisable = languageDisable;
	}

	public boolean isCreateNewNameText() {
		return createNewNameText;
	}

	public void setCreateNewNameText(boolean createNewNameText) {
		this.createNewNameText = createNewNameText;
	}

	public void keyup() {
		mainCategoryDTO.getOrder();
	}

	public void keyupImgOrder() {
		for (SubCategoryDTO subCategory : mainCategoryDTO.getSubCategoryDTOList()) {
			subCategory.getOrder();
		}
	}

	public boolean isUpdateSub() {
		return updateSub;
	}

	public void setUpdateSub(boolean updateSub) {
		this.updateSub = updateSub;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void keyupName() {
		checkBUI();
		PrimeFaces.current().ajax().update("mainCategoryEntryForm:sampleFont");
	}

	public void checkBUI() {
		text = nameTextDTO.getName();
		if (nameTextDTO.getName() != null && !nameTextDTO.getName().isEmpty()) {
			if (bold)
				text = "<b>" + text + "</b>";
			if (italic)
				text = "<i>" + text + "</i>";
			if (underline)
				text = "<u>" + text + "</u>";
			PrimeFaces.current().ajax().update("mainCategoryEntryForm:sampleFont");
		}
	}

	public void sortImageOrder() {
		boolean isAcending = true;
		boolean isDecending = true;
		int count = 0;
		List<Integer> orderList = new ArrayList<>();
		if (mainCategoryDTO != null) {
			if (mainCategoryDTO.getSubCategoryDTOList() != null) {
				for (SubCategoryDTO subCategoryDTO : mainCategoryDTO.getSubCategoryDTOList()) {
					orderList.add(subCategoryDTO.getOrder());
				}
				int maxOrderNo = orderList.size();
				// for Acending
				for (int i = 1; i <= maxOrderNo; i++) {
					if (orderList.get(i - 1) == i) {
						isAcending = true;
					} else {
						isAcending = false;
						break;
					}
				}
				// for Decending
				if (!isAcending) {
					for (int i = maxOrderNo; i > 0; i--) {
						if (count < maxOrderNo) {
							if (orderList.get(count) == i) {
								isDecending = true;
							} else {
								isDecending = false;
								break;
							}
							count++;
						}

					}
				}
				priorityControlType = isAcending ? PriorityControlType.ACENDING : (isDecending ? PriorityControlType.DECENDING : PriorityControlType.CUSTOMIZE);
			}
		}
	}

	public void handleFileUpload(FileUploadEvent event) throws IOException {
		UploadedFile uploadedFile = event.getFile();
		String mainCateId = mainCategoryDTO.getId() == null ? mainCategoryDTO.getTempId() : mainCategoryDTO.getId();
		if (uploadedFile != null) {
			String fileName = uploadedFile.getFileName();
			if (fileName != null && !fileName.isEmpty()) {
				String filePath = tempDir + mainCateId + "/logo/" + fileName;
				if (mainCategoryDTO.getPhotoDTO() != null) {
					PhotoImageDTO photo = mainCategoryDTO.getPhotoDTO();
					photo.setName(fileName);
					photo.setImagePath(filePath);
					photo.setImage(uploadedFile.getContents());
				} else
					mainCategoryDTO.setPhotoDTO(new PhotoImageDTO(fileName, filePath, uploadedFile.getContents()));
				FileHandler.createFile(new File(getRealSystemPath() + filePath), uploadedFile.getContents());
			}
		}
	}

	public void removePhotoImage() {
		try {
			PhotoImageDTO image = mainCategoryDTO.getPhotoDTO();
			FileUtils.forceDelete(new File(getRealSystemPath() + image.getImagePath()));
			mainCategoryDTO.setPhotoDTO(new PhotoImageDTO());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isShowImage() {
		if (mainCategoryDTO.getPhotoDTO() != null && mainCategoryDTO.getPhotoDTO().getImagePath() != null)
			return true;
		else
			return false;
	}

	// Replace tempId with id.
	private boolean updateFilePath() throws IOException {
		boolean isUpdate = false;
		String mcDTOTempId = null;
		PhotoImage photoImage = mainCategory.getImage();
		if (photoImage != null && photoImage.getFilepath() != null) {
			String[] parts = photoImage.getFilepath().split("/");
			if (!parts[3].equals(mainCategory.getId())) {
				mcDTOTempId = parts[3];
				isUpdate = true;
				photoImage.setFilepath(photoImage.getFilepath().replace(mcDTOTempId, mainCategory.getId()));
				FileHandler.renameFile(getRealSystemPath() + tempDir + mcDTOTempId + "/", getRealSystemPath() + tempDir + mainCategory.getId());
			}
		}
		for (SubCategory subCategory : mainCategory.getSubCategoryList()) {
			PhotoImage image = subCategory.getImage();
			if (image != null) {
				String[] parts1 = image.getFilepath().split("/");
				String mailCategoryTempId = parts1[3];
				String tempId = parts1[4];
				if (!tempId.equals(subCategory.getId())) {
					isUpdate = true;
					image.setFilepath(image.getFilepath().replace(tempId, subCategory.getId()));
					image.setFilepath(image.getFilepath().replace(mailCategoryTempId, mainCategory.getId()));
					FileHandler.renameFile(getRealSystemPath() + tempDir + mainCategory.getId() + "/" + tempId + "/",
							getRealSystemPath() + tempDir + mainCategory.getId() + "/" + subCategory.getId());
				}
			}
		}
		return isUpdate;
	}
}
