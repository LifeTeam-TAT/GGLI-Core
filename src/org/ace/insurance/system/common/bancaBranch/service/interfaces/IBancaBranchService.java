package org.ace.insurance.system.common.bancaBranch.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.java.component.SystemException;

public interface IBancaBranchService {

	public void insert(BancaBranch bancaBranch);

	public void delete(BancaBranch bancaBranch);

	public void update(BancaBranch bancaBranch);

	public BancaBranch findByBancaBranchId(String bancaBranchId);

	public List<BancaBranch> findAllBancaBranch();

	public boolean checkExistingBancaBranch(BancaBranch bancaBranch);

	List<BancaBranch> findAll() throws SystemException;

	public List<BancaBranch> findAllBancaBranchByChannel(String channelId);

}
