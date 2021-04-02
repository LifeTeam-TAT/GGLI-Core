package org.ace.insurance.system.common.entitys.persistence.interfaces;

import java.util.List;

import org.ace.insurance.system.common.entitys.Entitys;

public interface IEntityDAO {

	public void insert(Entitys entitys);

	public void delete(Entitys entitys);

	public void update(Entitys entitys);

	public List<Entitys> findAllEntitys();

}
