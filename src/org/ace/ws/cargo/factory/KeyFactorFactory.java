package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.ws.cargo.model.keyFactor.KeyFactorDTO;

public class KeyFactorFactory {
	public static KeyFactorDTO convertKeyFactorDTO(KeyFactor keyFactor) {
		KeyFactorDTO keyFactorDTO = new KeyFactorDTO();
		keyFactorDTO.setId(keyFactor.getId());
		keyFactorDTO.setValue(keyFactor.getValue());
		keyFactorDTO.setKeyfactor(keyFactor.getKeyFactorType().getLabel().toString());
		keyFactorDTO.setVersion(keyFactor.getVersion());
		return keyFactorDTO;

	}

	public static List<KeyFactorDTO> convertKeyFactorDTOList(List<KeyFactor> keyFactorList) {
		List<KeyFactorDTO> keyFactorDtoList = new ArrayList<KeyFactorDTO>();

		for (KeyFactor keyfactor : keyFactorList) {
			KeyFactorDTO keyFactorDto = convertKeyFactorDTO(keyfactor);
			keyFactorDtoList.add(keyFactorDto);
		}

		return keyFactorDtoList;

	}
}
