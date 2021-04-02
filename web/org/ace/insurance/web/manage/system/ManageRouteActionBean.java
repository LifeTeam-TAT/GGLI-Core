/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.system;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.service.interfaces.IRouteService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "ManageRouteActionBean")
public class ManageRouteActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{RouteService}")
	private IRouteService routeService;

	public void setRouteService(IRouteService routeService) {
		this.routeService = routeService;
	}

	private Route route;
	private boolean createNew;
	private String criteria;
	private List<Route> routeList;

	@PostConstruct
	public void init() {
		createNewRoute();
		loadRoute();
	}

	public void search() {
		routeList = routeService.findByCriteria(criteria);
	}

	public void loadRoute() {
		routeList = routeService.findAllRoute();
	}

	public void createNewRoute() {
		criteria = "";
		createNew = true;
		route = new Route();
	}

	public void prepareUpdateRoute(Route route) {
		createNew = false;
		this.route = route;
	}

	public void addNewRoute() {
		try {
			routeService.addNewRoute(route);
			addInfoMessage(null, MessageId.INSERT_SUCCESS, route.getName());
			createNewRoute();
			loadRoute();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void updateRoute() {
		try {
			routeService.updateRoute(route);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, route.getName());
			createNewRoute();
			loadRoute();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		createNewRoute();
		loadRoute();
	}

	public String deleteRoute() {
		try {
			routeService.deleteRoute(route);
			addInfoMessage(null, MessageId.DELETE_SUCCESS, route.getName());
			createNewRoute();
			loadRoute();
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return null;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<Route> getRouteList() {
		return routeList;
	}

	public void setCoutryList(List<Route> routeList) {
		this.routeList = routeList;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
}
