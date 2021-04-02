/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.deno.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.deno.Deno;
import org.ace.java.component.persistence.exception.DAOException;

public interface IDenoDAO {
	public void insert(Deno deno) throws DAOException;

	public void update(Deno deno) throws DAOException;

	public void delete(Deno deno) throws DAOException;

	public Deno findById(String id) throws DAOException;

	public List<Deno> findAll() throws DAOException;
}
