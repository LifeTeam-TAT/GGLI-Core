package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.relationship.RELATION001;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "RelationshipDialogActionBean")
@ViewScoped
public class RelationshipDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	private List<RELATION001> relationshipList;

	@PostConstruct
	public void init() {
		relationshipList = relationShipService.findRelationShip();
	}

	public List<RELATION001> getRelationshipList() {
		return relationshipList;
	}

	public void selectRelationship(RELATION001 relationship001) {
		RelationShip relationship = relationShipService.findRelationShipById(relationship001.getId());
		PrimeFaces.current().dialog().closeDynamic(relationship);
	}
}
