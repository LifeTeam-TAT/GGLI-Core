package org.ace.insurance.web.dialog;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.Route001;
import org.ace.insurance.system.common.route.service.interfaces.IRouteService;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "RouteDialogActionBean")
@ViewScoped
public class RouteDialogActionBean {

	@ManagedProperty(value = "#{RouteService}")
	private IRouteService routeService;

	public void setRouteService(IRouteService routeService) {
		this.routeService = routeService;
	}

	private List<Route001> routeList;
	private String criteria;

	@PostConstruct
	public void init() {
		routeList = routeService.findAll();
	}

	public void selectRoute(Route001 route001) {
		Route route = routeService.findRouteById(route001.getId());
		PrimeFaces.current().dialog().closeDynamic(route);
	}

	public void search() {
		routeList = routeService.findByCriteria001(criteria);
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public List<Route001> getRouteList() {
		return routeList;
	}
}
