package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.addon.AddOn;
import org.ace.ws.cargo.model.addOn.AddOnDTO;

public class AddOnFactory {
	public static AddOnDTO convertAddOnDTO(AddOn addOn) {
		AddOnDTO addOnDTO = new AddOnDTO();
		addOnDTO.setId(addOn.getId());
		addOnDTO.setName(addOn.getName());
		addOnDTO.setDescription(addOn.getDescription());
		addOnDTO.setAddOnType(addOn.getAddOnType().getLabel().toString());
		addOnDTO.setMaxAmount(addOn.getMaxAmount());
		addOnDTO.setValue(addOn.getValue());
		addOnDTO.setVersion(addOn.getVersion());
		return addOnDTO;

	}

	public static List<AddOnDTO> convertAddOnDTOList(List<AddOn> addOnList) {
		List<AddOnDTO> addOnDTOList = new ArrayList<AddOnDTO>();
		for (AddOn addOn : addOnList) {
			AddOnDTO addOnDTO = convertAddOnDTO(addOn);
			addOnDTOList.add(addOnDTO);
		}
		return addOnDTOList;

	}
}
