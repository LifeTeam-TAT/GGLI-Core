/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.deno.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.deno.Deno;
import org.ace.insurance.system.common.deno.persistence.interfaces.IDenoDAO;
import org.ace.insurance.system.common.deno.service.interfaces.IDenoService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;

@Service(value = "DenoService")
public class DenoService extends BaseService implements IDenoService {

	@Resource(name = "DenoDAO")
	private IDenoDAO denoDAO;

	public void addNewDeno(Deno deno) {
		try {
			deno.setPrefix(getPrefix(Deno.class));
			denoDAO.insert(deno);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Deno", e);
		}
	}

	public void updateDeno(Deno deno) {
		try {
			denoDAO.update(deno);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a Deno", e);
		}
	}

	public void deleteDeno(Deno deno) {
		try {
			denoDAO.delete(deno);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a Deno", e);
		}
	}

	public List<Deno> findAllDeno() {
		List<Deno> result = null;
		try {
			result = denoDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Deno)", e);
		}
		return result;
	}

	public Deno findDenoById(String id) {
		Deno result = null;
		try {
			result = denoDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a Deno (ID : " + id + ")", e);
		}
		return result;
	}
}