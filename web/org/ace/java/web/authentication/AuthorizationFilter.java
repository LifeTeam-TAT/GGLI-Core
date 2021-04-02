/***************************************************************************************
 * @author <<TNS>>
 * @Date 2018-01-23
 * @Version 1.0
 * @Purpose <<to check user permission and redirect to another page>>*   
 ***************************************************************************************/
package org.ace.java.web.authentication;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ace.insurance.role.ResourceItem;
import org.ace.insurance.role.Role;
import org.ace.insurance.user.User;
import org.ace.java.web.ApplicationSetting;
import org.ace.java.web.common.Constants;

@WebFilter(filterName = "AuthFilter", urlPatterns = { "*.xhtml" })
public class AuthorizationFilter implements Filter {

	public AuthorizationFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest reqt = (HttpServletRequest) request;
			HttpServletResponse resp = (HttpServletResponse) response;
			HttpSession ses = reqt.getSession(false);

			String reqURI = reqt.getRequestURI();
			if (reqURI.indexOf("/" + ApplicationSetting.getHomePageDir()) >= 0 || reqURI.indexOf("/public/") >= 0 || reqURI.contains("javax.faces.resource")) {
				chain.doFilter(request, response);
			} else if ((ses != null && ses.getAttribute(Constants.LOGIN_USER) != null)) {
				boolean authenticate = false;
				String[] values = reqURI.split("ggli");
				User user = (User) ses.getAttribute(Constants.LOGIN_USER);
				if (user == null) {
					resp.sendRedirect(reqt.getContextPath() + "/" + ApplicationSetting.getHomePageDir());
				}
				for (Role role : user.getRoleList()) {
					for (ResourceItem page : role.getResourceItemList()) {
						String url = page.getUrl().replace("/WebContent", "");
						url = url.replace(".seam", ".xhtml");
						// /WebContent/enquires/life/lifeProposalEnquiry.seam
						if (url.equalsIgnoreCase(values[1]) || values[1].startsWith("/dialog") || values[1].startsWith("/view/dashboard.xhtml")) {
							authenticate = true;
							break;
						}
					}
				}
				if (authenticate) {
					chain.doFilter(request, response);
				} else {
					resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				}
			} else {
				resp.sendRedirect(reqt.getContextPath() + "/" + ApplicationSetting.getHomePageDir());
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void destroy() {

	}
}
