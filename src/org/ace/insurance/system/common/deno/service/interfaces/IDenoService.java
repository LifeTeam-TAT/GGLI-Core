/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.deno.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.deno.Deno;

public interface IDenoService {
	public void addNewDeno(Deno Deno) ;

	public void updateDeno(Deno Deno) ;

	public void deleteDeno(Deno Deno) ;

	public Deno findDenoById(String id);

	public List<Deno> findAllDeno() ;
}
