/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.country.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.country.COUNTRY001;
import org.ace.insurance.system.common.country.Country;

public interface ICountryService {
	public void addNewCountry(Country Country);

	public void updateCountry(Country Country);

	public void deleteCountry(Country Country);

	public Country findCountryById(String id);

	public List<COUNTRY001> findCountry();

}
