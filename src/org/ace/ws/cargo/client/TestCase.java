package org.ace.ws.cargo.client;

import org.ace.ws.cargo.model.user.MobileUserDTO;

public class TestCase {

	public static String webUrl = "http://localhost:4040/ggip/cargows";

	public static void main(String[] args) throws Exception {

		// MobileUserDTO userDTO = new MobileUserDTO();

		MobileUserDTO userDTO = new MobileUserDTO();
		userDTO.setPassword("ggip");
		userDTO.setUserCode("mgmg");
		// userDTO.setId("INUSR005001000000003122092015");

		// userDTO.setName("sa");
		// userDTO.setUserCode("mgmg");
		// userDTO.setPassword("123");

		String url = webUrl + URIConstants.GET_PRODUCTPREMIUMRATEKEYFACTOR_LIST;

		String str = HttpUtility.doWithUrl(url, null);

		System.out.println(str);

	}
}
