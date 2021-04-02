package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.currency.Currency;
import org.ace.ws.cargo.model.currency.CurrencyDTO;

public class CurrencyFactory {
	public static CurrencyDTO convertCurrencyDTO(Currency currency) {
		CurrencyDTO currencyDTO = new CurrencyDTO();
		currencyDTO.setId(currency.getId());
		currencyDTO.setCurrencyCode(currency.getCurrencyCode());
		currencyDTO.setDescription(currency.getDescription());
		currencyDTO.setSymbol(currency.getSymbol());
		currencyDTO.setInwordDesp1(currency.getInwordDesp1());
		currencyDTO.setInwordDesp2(currency.getInwordDesp2());
		currencyDTO.setMyanInwordDesp1(currency.getMyanInwordDesp1());
		currencyDTO.setMyanInwordDesp2(currency.getMyanInwordDesp2());
		currencyDTO.setIsHomeCur(currency.getIsHomeCur());
		currencyDTO.setM1(currency.getM1());
		currencyDTO.setM1(currency.getM2());
		currencyDTO.setM1(currency.getM3());
		currencyDTO.setM1(currency.getM4());
		currencyDTO.setM1(currency.getM5());
		currencyDTO.setM1(currency.getM6());
		currencyDTO.setM1(currency.getM7());
		currencyDTO.setM1(currency.getM8());
		currencyDTO.setM1(currency.getM9());
		currencyDTO.setM1(currency.getM10());
		currencyDTO.setM1(currency.getM11());
		currencyDTO.setM1(currency.getM12());
		currencyDTO.setM1(currency.getM13());
		currencyDTO.setVersion(currency.getVersion());
		return currencyDTO;

	}

	public static List<CurrencyDTO> convertCurrencyDTOList(List<Currency> currencyList) {
		List<CurrencyDTO> currDTOList = new ArrayList<CurrencyDTO>();
		for (Currency curr : currencyList) {
			CurrencyDTO currDTO = convertCurrencyDTO(curr);
			currDTOList.add(currDTO);
		}
		return currDTOList;
	}
}
