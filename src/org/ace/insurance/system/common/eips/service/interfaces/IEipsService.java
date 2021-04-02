package org.ace.insurance.system.common.eips.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.eips.Eips;

public interface IEipsService {

	void save(Eips eips);

	void delete(Eips eips);

	Eips findById(String id);

	List<Eips> findAll();
}
