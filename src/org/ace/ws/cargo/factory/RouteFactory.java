package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.system.common.route.Route;
import org.ace.ws.cargo.model.route.RouteDTO;

public class RouteFactory {

	public static Route convertRoute(RouteDTO routeDTO) {
		Route route = new Route();
		route.setId(routeDTO.getId());
		route.setName(routeDTO.getName());
		route.setDescription(routeDTO.getDescription());
		route.setVersion(routeDTO.getVersion());
		return route;
	}

	public static RouteDTO convertRouteDTO(Route route) {
		RouteDTO routeDTO = new RouteDTO();
		routeDTO.setId(route.getId());
		routeDTO.setName(route.getName());
		routeDTO.setDescription(route.getDescription());
		routeDTO.setVersion(route.getVersion());
		return routeDTO;

	}

	public static List<RouteDTO> convertRouteDTOList(List<Route> routeList) {
		List<RouteDTO> routeDTOList = new ArrayList<RouteDTO>();
		for (Route route : routeList) {
			RouteDTO routeDTO = convertRouteDTO(route);
			routeDTOList.add(routeDTO);
		}
		return routeDTOList;
	}

}
