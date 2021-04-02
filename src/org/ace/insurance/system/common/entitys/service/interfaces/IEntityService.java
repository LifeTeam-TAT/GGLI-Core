package org.ace.insurance.system.common.entitys.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.entitys.Entitys;

public interface IEntityService {

	public void insert(Entitys entitys);

	public void delete(Entitys entitys);

	public void update(Entitys entitys);

	public List<Entitys> findAllEntitys();

}
