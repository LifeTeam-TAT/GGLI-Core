package org.ace.insurance.filter.productinformation.dto;

import org.ace.insurance.filter.productinformation.factory.NameTextFactory;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.web.common.CommonDTO;

public class NameTextDTO extends CommonDTO {

	private String tempId;
	private String name;
	private String content;
	private Language language;

	public NameTextDTO() {
		super();
		// this.tempId = "NAME" + System.nanoTime();
	}

	public NameTextDTO(NameTextDTO nameTextDTO) {
		this.name = nameTextDTO.getName();
		this.content = nameTextDTO.getContent();
		this.language = nameTextDTO.getLanguage();
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void NameDTOtoMainCategory(NameTextDTO nameTextDTO) {
		if (nameTextDTO != null) {
			NameTextFactory.createNameText(nameTextDTO);
		}
	}

	public Language[] getLanguageType() {
		return Language.values();
	}

}
