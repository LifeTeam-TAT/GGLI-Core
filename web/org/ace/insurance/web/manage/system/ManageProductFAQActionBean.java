package org.ace.insurance.web.manage.system;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.faq.FAQ;
import org.ace.insurance.system.faq.service.interfaces.IFAQService;
import org.ace.insurance.system.productfaq.ProductFAQ;
import org.ace.insurance.system.productfaq.service.interfaces.IProductFAQService;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.service.interfaces.IDataRepService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.commons.io.FileUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "ManageProductFAQActionBean")
public class ManageProductFAQActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{FAQService}")
	private IFAQService faqService;

	public void setFaqService(IFAQService faqService) {
		this.faqService = faqService;
	}

	@ManagedProperty(value = "#{ProductFAQService}")
	private IProductFAQService productFAQService;

	public void setProductFAQService(IProductFAQService productFAQService) {
		this.productFAQService = productFAQService;
	}

	@ManagedProperty(value = "#{DataRepService}")
	private IDataRepService<ProductFAQ> dataRepService;

	public void setDataRepService(IDataRepService<ProductFAQ> dataRepService) {
		this.dataRepService = dataRepService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private ProductFAQ productFAQ;
	private List<ProductFAQ> productFAQList;
	private List<FAQ> selectedFAQList;
	private FAQ selectedFAQ;
	private boolean isNewFAQ;
	private boolean createNew;
	private Map<String, FAQ> faqMap;
	private static final String tempDir = "/temp/ProductFAQ/";

	@PostConstruct
	public void init() {
		createNewProductFAQ();
		loadProduct();
	}

	public void createNewProductFAQ() {
		createNew = true;
		productFAQ = new ProductFAQ();
		selectedFAQList = new ArrayList<FAQ>();
		productFAQ.setLanguage(Language.ENGLISH);
		faqMap = new HashMap<String, FAQ>();
	}

	private void loadProduct() {
		productFAQList = productFAQService.findAllProductFAQ();
		if (productFAQList != null) {
			for (ProductFAQ proFAQ : productFAQList) {
				try {
					FileHandler.createFile(new File(getRealSystemPath() + proFAQ.getFilepath()), proFAQ.getImage());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void createNewFAQ() {
		selectedFAQ = new FAQ();
		selectedFAQ.setLanguage(productFAQ.getLanguage());
		isNewFAQ = true;
	}

	public void addNewFAQ() {
		faqMap.put(selectedFAQ.getTemId(), selectedFAQ);
		createNewFAQ();
		PrimeFaces.current().executeScript("PF('faqDialog').hide();");
	}

	public void prepareUpdateFAQ(FAQ faq) {
		isNewFAQ = false;
		this.selectedFAQ = faq;
	}

	public void updateFAQ() {
		faqMap.put(selectedFAQ.getTemId(), selectedFAQ);
		createNewFAQ();
		PrimeFaces.current().executeScript("PF('faqDialog').hide();");
	}

	public void deleteFAQ(FAQ faq) {
		faqMap.remove(faq.getTemId());
		selectedFAQ = null;
	}

	public void prepareProductFAQ(ProductFAQ productFAQ) {
		createNew = false;
		this.productFAQ = productFAQ;
		faqMap.clear();
		for (FAQ faq : this.productFAQ.getFaqList()) {
			faqMap.put(faq.getTemId(), faq);
		}
	}

	public void addNewProductFAQ() {
		try {
			if (validateFAQ()) {
				productFAQ.setFaqList(new ArrayList<FAQ>(faqMap.values()));
				productFAQService.insertProductFAQ(productFAQ);
				productFAQList.add(productFAQ);
				addInfoMessage(null, MessageId.INSERT_SUCCESS, productFAQ.getTitle());
				createNewProductFAQ();
			}
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void faqLanguageChangeEvent() {
		for (FAQ faq : faqMap.values()) {
			faq.setLanguage(productFAQ.getLanguage());
		}
	}

	private boolean validateFAQ() {
		if (productFAQList != null)
			for (ProductFAQ faq : productFAQList) {
				if (faq.getLanguage().equals(productFAQ.getLanguage()) && faq.getProduct().equals(productFAQ.getProduct())) {
					addErrorMessage(faq.getLanguage().getLabel() + " language is already added for Product " + faq.getProduct().getName() + ".");
					return false;
				}
			}
		return true;
	}

	public void updateProductFAQ() throws IOException {
		try {
			if (productFAQ.getFilepath() != null && !productFAQ.getFilepath().isEmpty())
				updateFilePath();
			productFAQ.setFaqList(new ArrayList<FAQ>(faqMap.values()));
			productFAQ = productFAQService.updateProductFAQ(productFAQ);
			addInfoMessage(null, MessageId.UPDATE_SUCCESS, productFAQ.getTitle());
			createNewProductFAQ();
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void deleteProductFAQ(ProductFAQ productFAQ) {
		try {
			productFAQList.remove(productFAQ);
			dataRepService.delete(productFAQ);
			createNewProductFAQ();
			addInfoMessage(null, MessageId.DELETE_SUCCESS, productFAQ.getTitle());
		} catch (SystemException e) {
			handelSysException(e);
		}
	}

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		productFAQ.setProduct(product);
	}

	public void handleCheckBox(FAQ faq) {
		if (faq.isFlag()) {
			selectedFAQList.add(faq);
		} else {
			selectedFAQList.remove(faq);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/* Image Upload */
	public void handleFileUpload(FileUploadEvent event) throws IOException {
		UploadedFile uploadedFile = event.getFile();
		String folerId = productFAQ.getId() == null ? productFAQ.getTempId() : productFAQ.getId();
		if (uploadedFile != null) {
			String fileName = uploadedFile.getFileName();
			if (fileName != null && !fileName.isEmpty()) {
				String filePath = tempDir + folerId + "/image/" + fileName;
				productFAQ.setFilepath(filePath);
				productFAQ.setImage(uploadedFile.getContents());
				FileHandler.createFile(new File(getRealSystemPath() + filePath), uploadedFile.getContents());
			}
		}
	}

	public void removePhotoImage() {
		try {
			FileUtils.forceDelete(new File(getRealSystemPath() + productFAQ.getFilepath()));
			productFAQ.setFilepath(null);
			productFAQ.setImage(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isShowImage() {
		if (productFAQ.getFilepath() != null && !productFAQ.getFilepath().isEmpty())
			return true;
		else
			return false;
	}

	// Replace tempId with id.
	private boolean updateFilePath() throws IOException {
		boolean isUpdate = false;
		String productFAQTempId = null;
		String[] parts = productFAQ.getFilepath().split("/");
		if (!parts[3].equals(productFAQ.getId())) {
			productFAQTempId = parts[3];
			isUpdate = true;
			productFAQ.setFilepath(productFAQ.getFilepath().replace(productFAQTempId, productFAQ.getId()));
			FileHandler.renameFile(getRealSystemPath() + tempDir + productFAQTempId + "/", getRealSystemPath() + tempDir + productFAQ.getId());
		}
		return isUpdate;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	public ProductFAQ getProductFAQ() {
		return productFAQ;
	}

	public void setProductFAQ(ProductFAQ productFAQ) {
		this.productFAQ = productFAQ;
	}

	public List<ProductFAQ> getProductFAQList() {
		return productFAQList;
	}

	public boolean isCreateNew() {
		return createNew;
	}

	public List<FAQ> getSelectedFAQList() {
		return selectedFAQList;
	}

	public void setSelectedFAQList(List<FAQ> selectedFAQList) {
		this.selectedFAQList = selectedFAQList;
	}

	public List<FAQ> getFAQList() {
		return new ArrayList<>(faqMap.values());
	}

	public FAQ getSelectedFAQ() {
		return selectedFAQ;
	}

	public void setSelectedFAQ(FAQ selectedFAQ) {
		this.selectedFAQ = selectedFAQ;
	}

	public boolean isNewFAQ() {
		return isNewFAQ;
	}

	public List<String> getProductList() {
		List<String> productNameList = new ArrayList<>();
		for (ProductFAQ faq : productFAQList) {
			if (!productNameList.contains(faq.getProduct().getName())) {
				productNameList.add(faq.getProduct().getName());
			}
		}
		return productNameList;
	}
}
