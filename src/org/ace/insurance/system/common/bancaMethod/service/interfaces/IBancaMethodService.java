package org.ace.insurance.system.common.bancaMethod.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaMethod.BancaMethod;

public interface IBancaMethodService {

	public void insert(BancaMethod banca);

	public void delete(BancaMethod banca);

	public void update(BancaMethod banca);

	public BancaMethod findByBancaId(String bancaId);

	public List<BancaMethod> findAllBanca();

	public boolean checkExistingBanca(BancaMethod banca);

}
