package org.ace.insurance.web.dialog;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.ace.insurance.system.common.province.PROV001;
import org.ace.insurance.system.common.province.Province;
import org.ace.insurance.system.common.province.service.interfaces.IProvinceService;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;

@ManagedBean(name = "ProvinceDialogActionBean")
@ViewScoped
public class ProvinceDialogActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProvinceService}")
	private IProvinceService provinceService;

	public void setProvinceService(IProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	private List<PROV001> provinceList;

	@PostConstruct
	public void init() {
		provinceList = provinceService.findProvince();
	}

	public List<PROV001> getProvinceList() {
		return provinceList;
	}

	public void selectProvince(PROV001 province001) {
		Province province = provinceService.findProvinceById(province001.getId());
		PrimeFaces.current().dialog().closeDynamic(province);
	}

}
