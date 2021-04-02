package org.ace.insurance.system.common.bancaLIC.service.intefaces;

import java.util.List;

import org.ace.insurance.system.common.bancaLIC.BancaLIC;

public interface IBancaLICService {

	public void insert(BancaLIC bancaLIC);

	public void delete(BancaLIC bancaLIC);

	public void update(BancaLIC bancaLIC);

	public BancaLIC findByBancaLICId(String bancaLICId);

	public List<BancaLIC> findAllBancaLIC();

	public List<BancaLIC> findAllBancaLICActive();

	public boolean checkExistingBancaLIC(BancaLIC bancaLIC);

}
