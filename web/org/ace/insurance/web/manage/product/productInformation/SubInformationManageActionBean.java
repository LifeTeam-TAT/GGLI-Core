package org.ace.insurance.web.manage.product.productInformation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletContext;

import org.ace.insurance.filter.productinformation.dto.MainCategoryDTO;
import org.ace.insurance.filter.productinformation.dto.NameTextDTO;
import org.ace.insurance.filter.productinformation.dto.PhotoImageDTO;
import org.ace.insurance.filter.productinformation.dto.SubCategoryDTO;
import org.ace.insurance.filter.productinformation.factory.SubCategoryFactory;
import org.ace.insurance.medical.productprocess.PriorityControlType;
import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.io.FileUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "SubInformationManageActionBean")
public class SubInformationManageActionBean extends BaseBean {

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
	private MainCategoryDTO mainCategoryDTO;
	private SubCategoryDTO subCategoryDTO;
	private NameTextDTO nameTextDTO;
	private SubCategory subCategory;
	private PriorityControlType priorityControlType;
	private List<PriorityControlType> priorityControlTypes;
	private static final String tempDir = "/temp/MainCategory/";

	@PostConstruct
	public void init() {
		initializeInjection();
		if (mainCategoryDTO == null) {
			mainCategoryDTO = new MainCategoryDTO();
		}
		if (subCategoryDTO == null) {
			subCategoryDTO = new SubCategoryDTO();
		}
		List<PriorityControlType> list = new ArrayList<PriorityControlType>(Arrays.asList(PriorityControlType.values()));
		list.remove(PriorityControlType.CUSTOMIZE);
		priorityControlTypes = list;
	}

	public void initializeInjection() {
		subCategory = new SubCategory();
		mainCategoryDTO = (MainCategoryDTO) ((mainCategoryDTO == null) ? getParam("MAINCATEGORY") : mainCategoryDTO);
		subCategoryDTO = (SubCategoryDTO) ((subCategoryDTO == null) ? getParam("SUBCATEGORY") : subCategoryDTO);

		if (subCategoryDTO != null) {
			subCategory = SubCategoryFactory.createSubCategory(subCategoryDTO);
			updateFlag = subCategoryDTO.isUpdateFlag();
			updateSub = subCategoryDTO.isUpdateSub();
			if (subCategoryDTO.getNameTextDTOList() != null && subCategoryDTO.getNameTextDTOList().size() > 1) {
				createNewNameText = true;
			}
		}
	}

	public void prepareNameText() {
		nameTextDTO = new NameTextDTO();
		text = "";
		bold = false;
		italic = false;
		underline = false;
		editNameFlag = false;
	}

	public void prepareEditNameText(NameTextDTO name) {
		this.nameTextDTO = name;
		if (subCategoryDTO.getNameTextDTOList().contains(name)) {
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
		}
		if (subCategoryDTO.getNameTextDTOList().size() > 1) {
			languageDisable = true;
		}
	}

	public void removeNameText(NameTextDTO nameText) {
		if (subCategoryDTO.getNameTextDTOList().contains(nameText)) {
			subCategoryDTO.getNameTextDTOList().remove(nameText);
			languageDisable = false;
			editNameFlag = false;
			createNewNameText = false;
			PrimeFaces.current().ajax().update("subinformationEntryForm:addNewNameText");
		}
	}

	public void applySubCategoryNameText() {
		String formID = "subCategoryEntryForm";
		boolean flag = true;
		if (subCategoryDTO.getNameTextDTOList() != null && subCategoryDTO.getNameTextDTOList().size() != 0) {
			if (editNameFlag == false) {
				for (NameTextDTO name : subCategoryDTO.getNameTextDTOList()) {
					if (name.getLanguage() == nameTextDTO.getLanguage()) {
						flag = false;
						addErrorMessage(formID + ":language", MessageId.LANGUAGE_ALREADY_EXISTS, nameTextDTO.getLanguage());
					}
				}
			}
		}
		if (flag) {
			nameTextDTO.setName(text);
			subCategoryDTO.getNameTextDTOList().add(nameTextDTO);
			PrimeFaces.current().executeScript("PF('PF('subcategoryDialog').hide();");
			if (subCategoryDTO.getNameTextDTOList() != null && subCategoryDTO.getNameTextDTOList().size() == nameTextDTO.getLanguage().values().length) {
				createNewNameText = true;
				PrimeFaces.current().ajax().update("subinformationEntryForm:addNewNameText");
			}
		}
	}

	public void updateSubCategoryNameText() {
		boolean flag = true;
		if (subCategoryDTO.getNameTextDTOList() != null && subCategoryDTO.getNameTextDTOList().size() == nameTextDTO.getLanguage().values().length) {
			if (languageDisable == false) {
				nameTextDTO.setName(text);
				subCategoryDTO.addNameTextDTO(nameTextDTO);
			}
		}
		if (flag) {
			nameTextDTO.setName(text);
			subCategoryDTO.addNameTextDTO(nameTextDTO);
			PrimeFaces.current().executeScript("PF('PF('subcategoryDialog').hide();");
		}
	}

	public void cancleNameText() {
		subCategoryDTO.addNameTextDTO(nameTextDTO);
	}

	public String cancelMainCategory() {
		if (subCategory != null) {
			SubCategoryDTO sub = new SubCategoryDTO();
			sub = SubCategoryFactory.createSubCategoryDTO(subCategory);
			int index = 0;
			if (mainCategoryDTO.getSubCategoryDTOList().contains(sub)) {
				for (int i = 0; i < mainCategoryDTO.getSubCategoryDTOList().size(); i++) {
					if (mainCategoryDTO.getSubCategoryDTOList().get(i) == subCategoryDTO) {
						index = i;
						mainCategoryDTO.getSubCategoryDTOList().set(index, sub);
						break;
					}
				}
			}
		}
		return "manageInformation";
	}

	public String addSubCategorytoMainCategory() {
		if (validate()) {
			if (subCategoryDTO != null) {
				if (updateFlag == false && updateSub == true) {
					int index = 0;
					if (mainCategoryDTO.getSubCategoryDTOList().contains(subCategoryDTO)) {
						for (int i = 0; i <= mainCategoryDTO.getSubCategoryDTOList().size(); i++) {
							if (mainCategoryDTO.getSubCategoryDTOList().get(i) == subCategoryDTO) {
								index = i;
								mainCategoryDTO.getSubCategoryDTOList().set(index, subCategoryDTO);
								break;
							}
						}
					}
				} else if (updateFlag == true && updateSub == true) {
					int index = 0;
					if (mainCategoryDTO.getSubCategoryDTOList().contains(subCategoryDTO)) {
						for (int i = 0; i <= mainCategoryDTO.getSubCategoryDTOList().size(); i++) {
							if (mainCategoryDTO.getSubCategoryDTOList().get(i) == subCategoryDTO) {
								index = i;
								mainCategoryDTO.getSubCategoryDTOList().set(index, subCategoryDTO);
								break;
							}
						}
					}
				} else {
					mainCategoryDTO.getSubCategoryDTOList().add(subCategoryDTO);
				}
				putParam("MAINCATEGORY", mainCategoryDTO);
			}
			return "manageInformation";
		} else {
			return "";
		}
	}

	public boolean validate() {
		boolean flag = true;
		String formId = "subinformationEntryForm";
		if (subCategoryDTO != null) {
			if (subCategoryDTO.getNameTextDTOList().size() == 0) {
				addErrorMessage(formId + ":subcategoryTable", MessageId.REQUIRED_VALUES, "");
				flag = false;
			}
			if (subCategoryDTO.getNameTextDTOList().size() < 2) {
				addErrorMessage(formId + ":subcategoryTable", MessageId.REQUIRED_ALL_LANGUAGE, "");
				flag = false;
			}
		}
		return flag;
	}

	public void handleFileUpload(FileUploadEvent event) throws IOException {
		UploadedFile uploadedFile = event.getFile();
		String mainCateId = mainCategoryDTO.getId() == null ? mainCategoryDTO.getTempId() : mainCategoryDTO.getId();
		String subCateId = subCategoryDTO.getId() == null ? subCategoryDTO.getTempId() : subCategoryDTO.getId();
		if (uploadedFile != null) {
			String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
			if (fileName != null && !fileName.isEmpty()) {
				String filePath = tempDir + mainCateId + "/" + subCateId + "/" + fileName;
				if (subCategoryDTO.getPhotoImageDTO() != null) {
					PhotoImageDTO photo = subCategoryDTO.getPhotoImageDTO();
					photo.setName(fileName);
					photo.setImagePath(filePath);
					photo.setImage(uploadedFile.getContents());
				} else
					subCategoryDTO.setPhotoImageDTO(new PhotoImageDTO(fileName, filePath, uploadedFile.getContents()));
				FileHandler.createFile(new File(getSystemPath() + filePath), uploadedFile.getContents());
			}
		}
	}

	public void removePhotoImage() {
		try {
			PhotoImageDTO image = subCategoryDTO.getPhotoImageDTO();
			FileUtils.forceDelete(new File(getRealSystemPath() + image.getImagePath()));
			subCategoryDTO.setPhotoImageDTO(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isShowImage() {
		if (subCategoryDTO.getPhotoImageDTO() != null && subCategoryDTO.getPhotoImageDTO().getImagePath() != null)
			return true;
		else
			return false;
	}

	public String getSystemPath() {
		Object context = getFacesContext().getExternalContext().getContext();
		String systemPath = ((ServletContext) context).getRealPath("/");
		return systemPath;
	}

	public boolean isEditNameFlag() {
		return editNameFlag;
	}

	public void setEditNameFlag(boolean editNameFlag) {
		this.editNameFlag = editNameFlag;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
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

	public SubCategoryDTO getSubCategoryDTO() {
		return subCategoryDTO;
	}

	public void setSubCategoryDTO(SubCategoryDTO subCategoryDTO) {
		this.subCategoryDTO = subCategoryDTO;
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

	public PriorityControlType getPriorityControlType() {
		return priorityControlType;
	}

	public void setPriorityControlType(PriorityControlType priorityControlType) {
		this.priorityControlType = priorityControlType;
	}

	public List<PriorityControlType> getPriorityControlTypes() {
		return priorityControlTypes;
	}

	public void setPriorityControlTypes(List<PriorityControlType> priorityControlTypes) {
		this.priorityControlTypes = priorityControlTypes;
	}

	public boolean isUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(boolean updateFlag) {
		this.updateFlag = updateFlag;
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

	public void checkBUI() {
		text = nameTextDTO.getName();
		if (nameTextDTO.getName() != null && !nameTextDTO.getName().isEmpty()) {
			if (bold)
				text = "<b>" + text + "</b>";
			if (italic)
				text = "<i>" + text + "</i>";
			if (underline)
				text = "<u>" + text + "</u>";
			PrimeFaces.current().ajax().update("subCategoryEntryForm:sampleFont");
		}
	}

	public void keyupName() {
		checkBUI();
		PrimeFaces.current().ajax().update("subCategoryEntryForm:sampleFont");
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

}
