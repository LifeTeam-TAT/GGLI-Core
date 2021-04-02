package org.ace.ws.cargo.controller;

import javax.annotation.Resource;

import org.ace.java.component.common.ResponseManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BaseController {
	@Resource(name = "ResponseManager")
	public ResponseManager responseManager;

	public ApplicationContext context() {
		return new ClassPathXmlApplicationContext("spring-beans.xml");
	}
}
