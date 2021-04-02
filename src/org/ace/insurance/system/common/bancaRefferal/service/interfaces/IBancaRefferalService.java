package org.ace.insurance.system.common.bancaRefferal.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;

public interface IBancaRefferalService {

	public void insert(BancaRefferal bancarefferal);

	public void delete(BancaRefferal bancarefferal);

	public void update(BancaRefferal bancarefferal);

	public BancaRefferal findByBancaId(String bancarefferalId);

	public List<BancaRefferal> findAllBancaRefferal();
	
	public List<BancaRefferal> findAllBancaRefferalByAgentLicenseNo();

	public List<BancaRefferal> findAllBancaRefferalActive(String branchId);

	public List<BancaRefferal> findAllBancaRefferalByOTC(String branchId);

	public boolean checkExistingBancaRefferal(BancaRefferal bancarefferal);

}
