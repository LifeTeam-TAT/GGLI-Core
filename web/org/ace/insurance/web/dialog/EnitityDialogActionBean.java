package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.entitys.Entitys;
import org.ace.insurance.system.common.entitys.service.interfaces.IEntityService;
import org.ace.insurance.user.User;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.PrimeFaces;

@ViewScoped
@ManagedBean(name = "EnitityDialogActionBean")
public class EnitityDialogActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{EnityService}")
	private IEntityService enityService;

	public void setEnityService(IEntityService enityService) {
		this.enityService = enityService;
	}

	User user;
	private List<Entitys> entitysList;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		if (isExistParam("ENTITYID")) {
			String entityId = (String) getParam("ENTITYID");
			entitysList = enityService.findAllEntitys();
		} else {
			entitysList = enityService.findAllEntitys();
		}
	}

	public List<Entitys> getEntitysList() {
		return entitysList;
	}

	public void setEntitysList(List<Entitys> entitysList) {
		this.entitysList = entitysList;
	}

	public void selectEntitys(Entitys entitys) {
		PrimeFaces.current().dialog().closeDynamic(entitys);
	}

}
