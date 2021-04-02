package org.ace.insurance.ws.common.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode=FaultCode.SERVER)
public class WSServerFault extends RuntimeException{
	public WSServerFault(String message) {
		super(message);
	}
	
	public WSServerFault(String message, Throwable cause) {
		super(message, cause);
	}
}
