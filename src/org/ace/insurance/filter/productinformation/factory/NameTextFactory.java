package org.ace.insurance.filter.productinformation.factory;

import org.ace.insurance.filter.productinformation.dto.NameTextDTO;
import org.ace.insurance.system.productinformation.text.NameText;

public class NameTextFactory {
	
	public static NameText createNameText(NameTextDTO nameTextDTO) {
		NameText nameText = new NameText();
		
		if(nameTextDTO != null ) {
			nameText.setId(nameTextDTO.getId());
			nameText.setName(nameTextDTO.getName());
			nameText.setContent(nameTextDTO.getContent());
			nameText.setLanguage(nameTextDTO.getLanguage());
			nameText.setVersion(nameTextDTO.getVersion());
		}
		
		return nameText;
	}
	
	public static NameTextDTO createNameTextDTO(NameText nameText) {
		
		NameTextDTO nameTextDTO = new NameTextDTO();
		if(nameText != null) {
			nameTextDTO.setId(nameText.getId());
			nameTextDTO.setName(nameText.getName());
			nameTextDTO.setContent(nameText.getContent());
			nameTextDTO.setLanguage(nameText.getLanguage());
			nameTextDTO.setVersion(nameText.getVersion());
		}
			
		return nameTextDTO;
		
	}
}
