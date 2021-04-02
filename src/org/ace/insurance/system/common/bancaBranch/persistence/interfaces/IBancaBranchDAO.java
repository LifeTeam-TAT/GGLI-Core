package org.ace.insurance.system.common.bancaBranch.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.java.component.persistence.exception.DAOException;

public interface IBancaBranchDAO {

	public void insert(BancaBranch bancaBranch) throws DAOException;

	public void delete(BancaBranch bancaBranch) throws DAOException;

	public void update(BancaBranch bancaBranch) throws DAOException;

	public BancaBranch findById(String BancaBranchId) throws DAOException;

	public List<BancaBranch> findAllBancaBranch();

	List<BancaBranch> findAllBancaBranchByChannel(String channelId) throws DAOException;

	public boolean checkExistingBancaBranch(BancaBranch bancaBranch) throws DAOException;

}
