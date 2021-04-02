package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.city.City;
import org.ace.ws.cargo.model.city.CityDTO;

public class CityFactory {
	public static CityDTO convertCityDTO(City city) {
		CityDTO cityDTO = new CityDTO();
		cityDTO.setId(city.getId());
		cityDTO.setName(city.getName());
		cityDTO.setDescription(city.getDescription());
		cityDTO.setProvince(city.getProvince().getFullProvience());
		cityDTO.setVersion(city.getVersion());
		return cityDTO;

	}

	public static List<CityDTO> convertCityDTOList(List<City> cityList) {
		List<CityDTO> cityDTOList = new ArrayList<CityDTO>();
		for (City city : cityList) {
			CityDTO cityDTO = convertCityDTO(city);
			cityDTOList.add(cityDTO);
		}
		return cityDTOList;

	}
}
