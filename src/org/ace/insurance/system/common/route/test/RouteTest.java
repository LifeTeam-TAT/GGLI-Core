package org.ace.insurance.system.common.route.test;

import org.ace.insurance.system.common.route.Route;
import org.ace.insurance.system.common.route.service.interfaces.IRouteService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class RouteTest {
	
	public static IRouteService routeService;

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
		IRouteService routeService = (IRouteService) context
				.getBean("RouteService");
		
		Route route = new Route();
		route.setId("12345");
		route.setName("first");
		route.setDescription("description");
		
		//routeService.addNewRoute(route);
		
		Route result = routeService.findRouteById("12345");
		
		System.err.println("Name ============"+result.getName());
		
		result.setName("second");
		
		routeService.updateRoute(result);
		
		Route result2 = routeService.findRouteById("12345");
		System.err.println("Name ============"+result2.getName());
		
		System.out.println("Finished--------");

	}
	

}
