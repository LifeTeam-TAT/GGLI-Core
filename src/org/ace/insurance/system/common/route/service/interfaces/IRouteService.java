/***************************************************************************************
 * @author <<Chan Mrate Ko Ko>>
 * @Date 2015-08-05
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.system.common.route.service.interfaces;

import java.util.List;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.Route001;

public interface IRouteService {

	public Route addNewRoute(Route route);

	public Route updateRoute(Route route);

	public void deleteRoute(Route route);

	public List<Route> findAllRoute();

	public List<Route001> findAll();

	public Route findRouteById(String id);

	public List<Route> findByCriteria(String criteria);

	public List<Route001> findByCriteria001(String criteria);

}
