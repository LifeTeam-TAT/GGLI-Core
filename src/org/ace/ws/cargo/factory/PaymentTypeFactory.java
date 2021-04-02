package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.ws.cargo.model.paymentType.PaymentTypeDTO;

public class PaymentTypeFactory {
	public static PaymentType convertPaymentType(PaymentTypeDTO paymentTypeDTO) {
		PaymentType paymentType = new PaymentType();
		paymentType.setId(paymentTypeDTO.getId());
		paymentType.setName(paymentTypeDTO.getName());
		paymentType.setDescription(paymentTypeDTO.getDescription());
		paymentType.setVersion(paymentTypeDTO.getVersion());
		paymentType.setMonth(paymentTypeDTO.getMonth());
		return paymentType;

	}

	public static PaymentTypeDTO convertPaymentTypeDTO(PaymentType paymentType) {
		PaymentTypeDTO paymentTypeDTO = new PaymentTypeDTO();
		paymentTypeDTO.setId(paymentType.getId());
		paymentTypeDTO.setName(paymentType.getName());
		paymentTypeDTO.setDescription(paymentType.getDescription());
		paymentTypeDTO.setVersion(paymentType.getVersion());
		paymentTypeDTO.setMonth(paymentType.getMonth());
		return paymentTypeDTO;

	}

	public static List<PaymentTypeDTO> convertPaymentTypeDTOList(List<PaymentType> PaymentTypeList) {
		List<PaymentTypeDTO> paymentTypeDTOList = new ArrayList<PaymentTypeDTO>();

		for (PaymentType paymentType : PaymentTypeList) {
			PaymentTypeDTO payDTO = convertPaymentTypeDTO(paymentType);
			paymentTypeDTOList.add(payDTO);
		}

		return paymentTypeDTOList;

	}

}
