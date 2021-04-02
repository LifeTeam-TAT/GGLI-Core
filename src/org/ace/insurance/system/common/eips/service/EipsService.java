package org.ace.insurance.system.common.eips.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.system.common.eips.Eips;
import org.ace.insurance.system.common.eips.persistence.interfaces.IEipsDAO;
import org.ace.insurance.system.common.eips.service.interfaces.IEipsService;
import org.springframework.stereotype.Service;

@Service("EipsService")
public class EipsService implements IEipsService{

	@Resource(name = "EipsDAO")
	private IEipsDAO eipsDAO;

	@Override
	public void save(Eips eips) {
		eipsDAO.save(eips);
	}

	@Override
	public void delete(Eips eips) {
		eipsDAO.delete(eips);
	}

	@Override
	public Eips findById(String id) {
		return eipsDAO.findById(id);
	}

	@Override
	public List<Eips> findAll() {
		return eipsDAO.findAll();
	}
}
