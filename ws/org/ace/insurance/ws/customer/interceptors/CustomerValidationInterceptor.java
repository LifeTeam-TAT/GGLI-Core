package org.ace.insurance.ws.customer.interceptors;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceTransformerException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.xml.sax.SAXParseException;

public class CustomerValidationInterceptor extends PayloadValidatingInterceptor {
	@Override
	protected Source getValidationRequestSource(WebServiceMessage request) {
		return transformSourceToStreamSourceWithStringReader(request.getPayloadSource());
	}

	@Override
	protected Source getValidationResponseSource(WebServiceMessage response) {
		return transformSourceToStreamSourceWithStringReader(response.getPayloadSource());
	}

	Source transformSourceToStreamSourceWithStringReader(Source notValidatableSource) {
		final Source source;
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter writer = new StringWriter();
			transformer.transform(notValidatableSource, new StreamResult(writer));

			String transformed = writer.toString();
			StringReader reader = new StringReader(transformed);
			source = new StreamSource(reader);

		} catch (TransformerException transformerException) {
			throw new WebServiceTransformerException("Could not convert the source to a StreamSource with a StringReader", transformerException);
		}
		return source;
	}

	@Override
	protected boolean handleRequestValidationErrors(MessageContext messageContext, SAXParseException[] errors) {
		if (messageContext.getResponse() instanceof SoapMessage) {
			SoapMessage response = (SoapMessage) messageContext.getResponse();
			SoapBody body = response.getSoapBody();
			// populate error message lines as one line
			if (getFaultStringOrReason().equals("Validation error")) {
				if (getAddValidationErrorDetail()) {
					String faultStr = null;
					if (errors.length > 1) {
						int lineNumber = 0;
						StringBuffer buf = new StringBuffer();
						for (SAXParseException error : errors) {
							if (lineNumber != error.getLineNumber()) {
								// first
								lineNumber = error.getLineNumber();
							} else {
								String[] ret = StringUtils.substringsBetween(error.getMessage(), "'", "'");
								String element = ret[1];
								if (element.contains(":"))
									element = element.substring(element.indexOf(":") + 1, element.length());
								buf.append(element + ", ");
								// lineNumber = 0;
							}
							logger.warn("XML validation error on request: " + error.getMessage());
						}
						// setAddValidationErrorDetail(false);
						String chunks = buf.toString();
						faultStr = "Invalid Values for [" + chunks.substring(0, chunks.lastIndexOf(",")) + "]";
						body.addClientOrSenderFault(faultStr, getFaultStringOrReasonLocale());
					} else {
						faultStr = "Validation error";
						body.addClientOrSenderFault(faultStr, getFaultStringOrReasonLocale());
					}
				} else {
					body.addClientOrSenderFault(getFaultStringOrReason(), getFaultStringOrReasonLocale());
				}
			} else {
				body.addClientOrSenderFault(getFaultStringOrReason(), getFaultStringOrReasonLocale());
			}
			/*
			 * Blocked detail error message lines SoapFault fault =
			 * body.addClientOrSenderFault(getFaultStringOrReason(),
			 * getFaultStringOrReasonLocale()); if
			 * (getAddValidationErrorDetail()) { SoapFaultDetail detail =
			 * fault.addFaultDetail(); for (SAXParseException error : errors) {
			 * SoapFaultDetailElement detailElement =
			 * detail.addFaultDetailElement(getDetailElementName( ));
			 * detailElement.addText(error.getMessage()); } } }
			 */
		}
		return false;
	}
	/*
	 * Not handle on response validation errros
	 * 
	 * @Override protected boolean handleResponseValidationErrors(MessageContext
	 * messageContext, SAXParseException[] errors) { for (SAXParseException
	 * error : errors) { logger.warn("XML validation error on request: " +
	 * error.getMessage()); } if (messageContext.getResponse() instanceof
	 * SoapMessage) { SoapMessage response = (SoapMessage)
	 * messageContext.getResponse(); SoapBody body = response.getSoapBody();
	 * SoapFault fault = body.addClientOrSenderFault(getFaultStringOrReason (),
	 * getFaultStringOrReasonLocale()); if (getAddValidationErrorDetail()) {
	 * SoapFaultDetail detail = fault.addFaultDetail(); for (SAXParseException
	 * error : errors) { SoapFaultDetailElement detailElement =
	 * detail.addFaultDetailElement(getDetailElementName( ));
	 * detailElement.addText("CUSTOM:" + error.getMessage()); } } } return
	 * false; }
	 */
}