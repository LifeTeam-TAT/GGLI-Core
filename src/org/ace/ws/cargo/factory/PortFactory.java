package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.port.Port;
import org.ace.ws.cargo.model.port.PortDTO;

public class PortFactory {
	public static PortDTO convertPortDTO(Port port) {
		PortDTO portDTO = new PortDTO();
		portDTO.setId(port.getId());
		portDTO.setName(port.getName());
		portDTO.setDescription(port.getDescription());
		portDTO.setPermanentAddress(port.getFullAddress());
		portDTO.setMobile(port.getContentInfo().getMobile());
		portDTO.setVersion(port.getVersion());
		return portDTO;
	}

	public static List<PortDTO> convertPortDTOList(List<Port> portList) {
		List<PortDTO> portDTOList = new ArrayList<PortDTO>();
		for (Port port : portList) {
			PortDTO portDTO = convertPortDTO(port);
			portDTOList.add(portDTO);
		}
		return portDTOList;
	}
}
