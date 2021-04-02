package org.ace.ws.cargo.factory;

import java.util.ArrayList;
import java.util.List;

import org.ace.insurance.user.MobileUser;
import org.ace.ws.cargo.model.user.MobileUserDTO;

public class SystemFactory {

	public static MobileUser convertMobileUser(MobileUserDTO mobileUserDTO) {
		MobileUser muser = new MobileUser();
		muser.setId(mobileUserDTO.getId());
		muser.setName(mobileUserDTO.getName());
		muser.setUserCode(mobileUserDTO.getUserCode());
		muser.setPassword(mobileUserDTO.getPassword());
		muser.setPhone(mobileUserDTO.getPhone());
		muser.setEmail(mobileUserDTO.getEmail());
		muser.setAddress(mobileUserDTO.getAddress());
		muser.setChangePassword(mobileUserDTO.isChangePassword());
		muser.setVersion(mobileUserDTO.getVersion());
		muser.setUserType(mobileUserDTO.getUserType());
		muser.setAccessSync(mobileUserDTO.isAccessSync());
		muser.setAccountDisable(mobileUserDTO.isAccountDisable());
		return muser;
	}

	public static MobileUserDTO convertMobileUserDTO(MobileUser mobileUser) {
		MobileUserDTO muserDTO = new MobileUserDTO();
		muserDTO.setId(mobileUser.getId());
		muserDTO.setName(mobileUser.getName());
		muserDTO.setUserCode(mobileUser.getUserCode());
		muserDTO.setPassword(mobileUser.getPassword());
		muserDTO.setPhone(mobileUser.getPhone());
		muserDTO.setEmail(mobileUser.getEmail());
		muserDTO.setAddress(mobileUser.getAddress());
		muserDTO.setChangePassword(mobileUser.isChangePassword());
		muserDTO.setVersion(mobileUser.getVersion());
		muserDTO.setUserType(mobileUser.getUserType());
		muserDTO.setAccessSync(mobileUser.isAccessSync());
		muserDTO.setAccountDisable(mobileUser.isAccountDisable());
		return muserDTO;
	}

	public static MobileUser convertMobileUser(MobileUserDTO mobileUserDTO, MobileUser mobileUser) {
		mobileUser.setId(mobileUserDTO.getId());
		mobileUser.setName(mobileUserDTO.getName());
		mobileUser.setUserCode(mobileUserDTO.getUserCode());
		mobileUser.setPassword(mobileUserDTO.getPassword());
		mobileUser.setPhone(mobileUserDTO.getPhone());
		mobileUser.setEmail(mobileUserDTO.getEmail());
		mobileUser.setAddress(mobileUserDTO.getAddress());
		mobileUser.setChangePassword(mobileUserDTO.isChangePassword());
		mobileUser.setVersion(mobileUserDTO.getVersion());
		mobileUser.setUserType(mobileUserDTO.getUserType());
		mobileUser.setAccessSync(mobileUserDTO.isAccessSync());
		mobileUser.setAccountDisable(mobileUserDTO.isAccountDisable());
		return mobileUser;
	}

	public static List<MobileUserDTO> convertMobileUserDTOList(List<MobileUser> mobileUserlist) {
		List<MobileUserDTO> moibleUserDTOlist = new ArrayList<MobileUserDTO>();
		for (MobileUser mobileUser : mobileUserlist) {
			MobileUserDTO mobileUserDTO = convertMobileUserDTO(mobileUser);
			moibleUserDTOlist.add(mobileUserDTO);
		}
		return moibleUserDTOlist;

	}

}
