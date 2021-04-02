package org.ace.insurance.ws.common.exceptions;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode=FaultCode.CLIENT)
public class WSClientFault extends RuntimeException {
	public WSClientFault(String message) {
		super(message);
	}
	
	public WSClientFault(String message, Throwable cause) {
		super(message, cause);
	}
}