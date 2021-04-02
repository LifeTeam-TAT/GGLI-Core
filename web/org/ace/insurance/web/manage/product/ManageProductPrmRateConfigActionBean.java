/***************************************************************************************
 * @author <<Your Name>>
 * @Date 2013-02-11
 * @Version 1.0
 * @Purpose <<You have to write the comment the main purpose of this class>>
 * 
 *    
 ***************************************************************************************/
package org.ace.insurance.web.manage.product;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.ace.insurance.common.KeyFactorType;
import org.ace.insurance.common.utils.SumInsuredType;
import org.ace.insurance.product.KFRefValue;
import org.ace.insurance.product.PremiumRateType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductPremiumRate;
import org.ace.insurance.product.ProductPremiumRateKeyFactor;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.web.common.BaseBean;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.row.Row;

@ViewScoped
@ManagedBean(name = "ManageProductPrmRateConfigActionBean")
public class ManageProductPrmRateConfigActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private Product product;

	private Map<String, ProductPremiumRateDTO> productPrmRateMap;
	private Map<String, List<String>> dataMap;
	private List<ProductPremiumRateKeyFactorDTO> productPrmRateKeyFactorDTOList;
	private List<String> record;
	private ProductPremiumRateKeyFactorDTO productPrmRateKeyFactorDTO;
	private ProductPremiumRateDTO productPremiumRateDTO;
	private boolean isShortTermSinglePremiumCreditLife;
	private ProductPremiumRate updateProductPremiumRate;
	private DataTable dataTable;
	private String productPremiumRateId;
	private boolean createDataTable = true;
	private boolean createDataTableFromInit;
	private boolean createNew;
	private static final String DATA_TABLE_ID = "productPremiumRateTableForm:productPremiumRateTable";
	private static final String PAGINATOR_TEMPLATE = "{CurrentPageReport}  {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}";
	private static final String ROWS_PER_PAGE_TEMPLATE = "5,10,15";
	private List<KeyFactor> keyfactorList;
	private NumberFormat numberFormat = new DecimalFormat("##,###.0000");

	private void initializeInjection() {
		this.product = (this.product == null) ? (Product) getParam("productParam") : product;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		dataMap = new HashMap<String, List<String>>();

		isShortTermSinglePremiumCreditLife = KeyFactorChecker.isShortTermSinglePremiumCreditLife(product);
		
		createNewProductPremiumRate();
		List<ProductPremiumRate> productPremiumRateList = productService.findProductPremiumRateByProduct(product.getId());
		Collections.sort(productPremiumRateList);
		keyfactorList = new ArrayList<KeyFactor>();
		if (productPremiumRateList != null && productPremiumRateList.size() > 0) {
			for (ProductPremiumRateKeyFactor prkf : productPremiumRateList.get(0).getPremiumRateKeyFactor()) {
				keyfactorList.add(prkf.getKeyFactor());
			}
		}
		for (ProductPremiumRate productPremiumRate : productPremiumRateList) {
			setPremiumRateToDataMap(productPremiumRate.getId(), productPremiumRate);
		}
		createDataTableFromInit = true;
		populateDataTable();
	}

	public List<SelectItem> getKFRefValueList(ProductPremiumRateKeyFactorDTO prKFDTO, String entityName) {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		List<KFRefValue> kfRefValueList = productService.findReferenceValue(entityName, null);
		if(null != kfRefValueList) {
			for (KFRefValue kfRefValue : kfRefValueList) {
				prKFDTO.putRefValue(kfRefValue.getId(), kfRefValue.getName());
				itemList.add(new SelectItem(kfRefValue.getId(), kfRefValue.getName()));
			}
		}
		return itemList;
	}

	public PremiumRateType[] getPremiumRateTypes() {
		return PremiumRateType.values();
	}

	public void createNewProductPremiumRate() {
		createNew = true;
		productPrmRateKeyFactorDTOList = new ArrayList<ProductPremiumRateKeyFactorDTO>();
		for (KeyFactor kf : product.getKeyFactorList()) {
			if (product.getPremiumRateType().equals(PremiumRateType.PERCENT_OF_SUMINSURED)) {
				if (!KeyFactorChecker.isSumInsured(kf)) {
					productPrmRateKeyFactorDTO = new ProductPremiumRateKeyFactorDTO();
					productPrmRateKeyFactorDTO.setKeyFactor(kf);
					productPrmRateKeyFactorDTOList.add(productPrmRateKeyFactorDTO);
				}
			} else {
				productPrmRateKeyFactorDTO = new ProductPremiumRateKeyFactorDTO();
				productPrmRateKeyFactorDTO.setKeyFactor(kf);
				productPrmRateKeyFactorDTOList.add(productPrmRateKeyFactorDTO);
			}
		}
		this.productPremiumRateDTO = new ProductPremiumRateDTO();
	}

	public void addNewProductPremiumRate() {
		ProductPremiumRate productPremiumRate;
		ProductPremiumRateKeyFactor productPremiumRateKeyFactor;
		List<ProductPremiumRateKeyFactor> productPremiumRateKeyFactorList = null;

		for (ProductPremiumRateKeyFactorDTO kfDTO : productPrmRateKeyFactorDTOList) {
			kfDTO.setProductPremiumRateDTO(productPremiumRateDTO);
		}
		productPremiumRateDTO.setPremiumRateKeyFactorDTO(productPrmRateKeyFactorDTOList);
		productPremiumRateDTO.setProduct(product);
		// add to Database
		productPremiumRate = new ProductPremiumRate(productPremiumRateDTO.getPremiumRate(), productPremiumRateDTO.getProduct());
		productPremiumRateKeyFactorList = new ArrayList<ProductPremiumRateKeyFactor>();
		for (ProductPremiumRateKeyFactorDTO kfDTO : productPremiumRateDTO.getPremiumRateKeyFactorDTO()) {
			productPremiumRateKeyFactor = new ProductPremiumRateKeyFactor(kfDTO.getFrom(), kfDTO.getTo(), kfDTO.getValue(), kfDTO.getReferenceName(), kfDTO.getKeyFactor(),
					productPremiumRate);
			productPremiumRateKeyFactorList.add(productPremiumRateKeyFactor);
		}
		productPremiumRate.setPremiumRateKeyFactor(productPremiumRateKeyFactorList);
		productPremiumRate.setSumInsuredType(productPremiumRateDTO.getSumInsuredType());
		productPremiumRate = productService.addNewProductPremiumRate(productPremiumRate);

		// add to dataMap
		setPremiumRateToDataMap(productPremiumRate.getId(), productPremiumRate);
		populateDataTable();
		createNewProductPremiumRate();

	}

	public void prepareDeleteProductPremiumRate(String productPremiumRateId) {
		this.productPremiumRateId = productPremiumRateId;
	}

	public void deleteProductPremiumRate() {
		ProductPremiumRate productPremiumRate = productService.findProductPremiumRateById(productPremiumRateId);
		productService.deleteProductPremiumRate(productPremiumRate);
		dataMap.remove(productPremiumRateId);
		createDataTable = false;
	}

	public void prepareUpdateProductPremiumRate(String productPremiumRateId) {
		updateProductPremiumRate = productService.findProductPremiumRateById(productPremiumRateId);
		setToProductPremiumRateDTOList(updateProductPremiumRate);
		createNew = false;
	}

	private void setToProductPremiumRateDTOList(ProductPremiumRate productPremiumRate) {
		productPrmRateKeyFactorDTOList = new ArrayList<ProductPremiumRateKeyFactorDTO>();
		productPremiumRateDTO = new ProductPremiumRateDTO(productPremiumRate);
		for (ProductPremiumRateKeyFactor keyFactor : productPremiumRate.getPremiumRateKeyFactor()) {
			productPrmRateKeyFactorDTO = new ProductPremiumRateKeyFactorDTO(keyFactor);
			productPrmRateKeyFactorDTO.setProductPremiumRateDTO(productPremiumRateDTO);
			productPrmRateKeyFactorDTOList.add(productPrmRateKeyFactorDTO);
		}
		productPremiumRateDTO.setSumInsuredType(productPremiumRate.getSumInsuredType());
		productPremiumRateDTO.setPremiumRateKeyFactorDTO(productPrmRateKeyFactorDTOList);
		productPremiumRateDTO.setProduct(product);
	}

	public void updateProductPremiumRate() {
		updateProductPremiumRate.setPremiumRate(productPremiumRateDTO.getPremiumRate());
		for (ProductPremiumRateKeyFactorDTO kfDTO : productPremiumRateDTO.getPremiumRateKeyFactorDTO()) {
			for (ProductPremiumRateKeyFactor kf : updateProductPremiumRate.getPremiumRateKeyFactor()) {
				if (kf.getKeyFactor().getValue().equals(kfDTO.getKeyFactor().getValue())) {
					kf.setFrom(kfDTO.getFrom());
					kf.setTo(kfDTO.getTo());
					kf.setValue(kfDTO.getValue());
				}
			}
		}
		productService.updateProductPremiumRate(updateProductPremiumRate);
		setPremiumRateToDataMap(updateProductPremiumRate.getId(), updateProductPremiumRate);
		populateDataTable();
		createNewProductPremiumRate();
	}

	private void setPremiumRateToDataMap(String id, ProductPremiumRate productPremiumRate) {
		record = new ArrayList<String>();
		for (ProductPremiumRateKeyFactor keyFactor : productPremiumRate.getPremiumRateKeyFactor()) {
			if (keyFactor.getKeyFactor().getKeyFactorType() == KeyFactorType.FIXED) {
				record.add(keyFactor.getValue());
			}
			if (keyFactor.getKeyFactor().getKeyFactorType() == KeyFactorType.REFERENCE) {
				record.add(keyFactor.getReferenceName());
			}
			if (keyFactor.getKeyFactor().getKeyFactorType() == KeyFactorType.FROM_TO) {
				if (KeyFactorChecker.isSumInsured(keyFactor.getKeyFactor())) {
					record.add(numberFormat.format(keyFactor.getFrom()));
					record.add(numberFormat.format(keyFactor.getTo()));
				} else {
					record.add(String.valueOf(keyFactor.getFrom()));
					record.add(String.valueOf(keyFactor.getTo()));
				}
			}
		}
		record.add(String.valueOf(productPremiumRate.getPremiumRate()));
		dataMap.put(id, record);
	}

	private void populateDataTable() {
		if (keyfactorList == null) {
			return;
		}
		dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(DATA_TABLE_ID);
		if (dataTable == null) {
			dataTable = new DataTable();
		}
		// Create <h:dataTable
		// value="#{ProductPreRateConfigActionBean.dataList}" var="recordList"
		// rowIndexVar="loop">.
		dataTable.setValueExpression("value", createValueExpression("#{ManageProductPrmRateConfigActionBean.mapKeys}", List.class));
		dataTable.setVar("recordList");
		dataTable.setRowIndexVar("loop");
		dataTable.setPaginator(true);
		dataTable.setRows(10);
		dataTable.setStyle("width:100%;");
		dataTable.setPaginatorTemplate(PAGINATOR_TEMPLATE);
		dataTable.setRowsPerPageTemplate(ROWS_PER_PAGE_TEMPLATE);
		Column column;
		GraphicImage image;

		// Create <p:ColumnGroup type="header">
		ColumnGroup columnGroup = new ColumnGroup();
		dataTable.getChildren().add(columnGroup);
		columnGroup.setType("header");

		Row row1 = new Row();
		columnGroup.getChildren().add(row1);

		for (KeyFactor keyFactor : keyfactorList) {
			if (product.getPremiumRateType().equals(PremiumRateType.PERCENT_OF_SUMINSURED)) {
				if (!KeyFactorChecker.isSumInsured(keyFactor)) {
					if (keyFactor.getKeyFactorType() == KeyFactorType.FIXED) {
						column = new Column();
						row1.getChildren().add(column);
						column.setColspan(1);
						column.setHeaderText(keyFactor.getValue());
					}
					if (keyFactor.getKeyFactorType() == KeyFactorType.REFERENCE) {
						column = new Column();
						row1.getChildren().add(column);
						column.setColspan(1);
						column.setHeaderText(keyFactor.getValue());
					}
					if (keyFactor.getKeyFactorType() == KeyFactorType.FROM_TO) {
						column = new Column();
						row1.getChildren().add(column);
						column.setColspan(2);
						column.setHeaderText(keyFactor.getValue());
					}
				}
			} else {
				if (keyFactor.getKeyFactorType() == KeyFactorType.FIXED) {
					column = new Column();
					row1.getChildren().add(column);
					column.setColspan(1);
					column.setHeaderText(keyFactor.getValue());
				}
				if (keyFactor.getKeyFactorType() == KeyFactorType.REFERENCE) {
					column = new Column();
					row1.getChildren().add(column);
					column.setColspan(1);
					column.setHeaderText(keyFactor.getValue());
				}
				if (keyFactor.getKeyFactorType() == KeyFactorType.FROM_TO) {
					column = new Column();
					row1.getChildren().add(column);
					column.setColspan(2);
					column.setHeaderText(keyFactor.getValue());
				}
			}
		}
		// premiumRate Column
		column = new Column();
		row1.getChildren().add(column);
		column.setRowspan(2);
		column.setHeaderText("Premium Rate");

		// update Link Column
		column = new Column();
		row1.getChildren().add(column);
		column.setRowspan(2);
		column.setStyle("width:50px;");

		// delete Link Column
		column = new Column();
		row1.getChildren().add(column);
		column.setRowspan(2);
		column.setStyle("width:50px;");

		Row row2 = new Row();
		columnGroup.getChildren().add(row2);
		for (KeyFactor keyFactor : keyfactorList) {
			if (product.getPremiumRateType().equals(PremiumRateType.PERCENT_OF_SUMINSURED)) {
				if (!KeyFactorChecker.isSumInsured(keyFactor)) {
					if (keyFactor.getKeyFactorType() == KeyFactorType.FIXED) {
						column = new Column();
						row2.getChildren().add(column);
						column.setHeaderText("Value");
					}
					if (keyFactor.getKeyFactorType() == KeyFactorType.REFERENCE) {
						column = new Column();
						row2.getChildren().add(column);
						column.setHeaderText("Value");
					}
					if (keyFactor.getKeyFactorType() == KeyFactorType.FROM_TO) {
						column = new Column();
						row2.getChildren().add(column);
						column.setHeaderText("From");

						column = new Column();
						row2.getChildren().add(column);
						column.setHeaderText("To");
					}
				}
			} else {
				if (keyFactor.getKeyFactorType() == KeyFactorType.FIXED) {
					column = new Column();
					row2.getChildren().add(column);
					column.setHeaderText("Value");
				}
				if (keyFactor.getKeyFactorType() == KeyFactorType.REFERENCE) {
					column = new Column();
					row2.getChildren().add(column);
					column.setHeaderText("Value");
				}
				if (keyFactor.getKeyFactorType() == KeyFactorType.FROM_TO) {
					column = new Column();
					row2.getChildren().add(column);
					column.setHeaderText("From");

					column = new Column();
					row2.getChildren().add(column);
					column.setHeaderText("To");
				}
			}
		}

		// Iterate over columns.
		int size = 0;
		for (Map.Entry<String, List<String>> entry : dataMap.entrySet()) {
			List<String> list = entry.getValue();
			size = list.size();
			break;
		}
		for (int i = 0; i < size; i++) {
			// Create <h:column>.
			column = new Column();
			if ((dataMap.size() == 1 && createDataTable) || createDataTableFromInit) {
				// Create <h:outputText value="#{recordList[" + i + "]}"> for
				// the body of column.
				HtmlOutputText output = new HtmlOutputText();
				output.setValueExpression("value", createValueExpression("#{ManageProductPrmRateConfigActionBean.dataMap[recordList][" + i + "]}", String.class));
				column.getChildren().add(output);

				dataTable.getChildren().add(column);

			}
		}
		if ((dataMap.size() == 1 && createDataTable) || (dataMap.size() > 0 && createDataTableFromInit)) {
			column = new Column();
			dataTable.getChildren().add(column);
			// Create <h:commandLink
			// value="#{ProductPreRateConfigActionBean.dataList[loop.index]}"
			// action="#{ManageProductPrmRateConfigActionBean.prepareUpdateProductPremiumRate}"
			// />
			HtmlCommandLink editLink = new HtmlCommandLink();
			editLink.setId("edit");
			editLink.setActionExpression(createActionExpression("#{ManageProductPrmRateConfigActionBean.prepareUpdateProductPremiumRate(recordList)}", String.class));
			column.getChildren().add(editLink);

			// Create <p:graphicImage value="/images/delete.png"
			// styleClass="command-image"/>
			image = new GraphicImage();
			image.setValueExpression("value", createValueExpression("/images/edit.png", String.class));
			image.setStyleClass("editIcon");
			editLink.getChildren().add(image);

			column = new Column();
			dataTable.getChildren().add(column);
			// Create <h:commandLink
			// value="#{ProductPreRateConfigActionBean.dataList[loop.index]}"
			// action="#{ManageProductPrmRateConfigActionBean.removeProductPremiumRate}"
			// />
			CommandLink deleteLink = new CommandLink();
			deleteLink.setId("delete");
			deleteLink.setActionExpression(createActionExpression("#{ManageProductPrmRateConfigActionBean.prepareDeleteProductPremiumRate(recordList)}", String.class));
			deleteLink.setOncomplete("PF('confirmationDialog').show()");
			column.getChildren().add(deleteLink);

			// Create <p:graphicImage value="/images/delete.png"
			// styleClass="command-image"/>
			image = new GraphicImage();
			image.setValueExpression("value", createValueExpression("/images/delete.png", String.class));
			image.setStyleClass("deleteIcon");
			deleteLink.getChildren().add(image);
		}
		createDataTableFromInit = false;
	}

	private ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(), valueExpression, valueType);
	}

	private MethodExpression createActionExpression(String actionExpression, Class<?> returnType) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return facesContext.getApplication().getExpressionFactory().createMethodExpression(facesContext.getELContext(), actionExpression, returnType, new Class[0]);
	}

	public List<String> getMapKeys() {
		List<String> keys = new ArrayList<String>();
		for (String s : dataMap.keySet()) {
			keys.add(s);
		}
		return keys;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<ProductPremiumRateKeyFactorDTO> getProductPrmRateKeyFactorDTOList() {
		return productPrmRateKeyFactorDTOList;
	}

	public void setProductPrmRateKeyFactorDTOList(List<ProductPremiumRateKeyFactorDTO> productPrmRateKeyFactorDTOList) {
		this.productPrmRateKeyFactorDTOList = productPrmRateKeyFactorDTOList;
	}

	public ProductPremiumRateKeyFactorDTO getProductPrmRateKeyFactorDTO() {
		return productPrmRateKeyFactorDTO;
	}

	public void setProductPrmRateKeyFactorDTO(ProductPremiumRateKeyFactorDTO productPrmRateKeyFactorDTO) {
		this.productPrmRateKeyFactorDTO = productPrmRateKeyFactorDTO;
	}

	public ProductPremiumRateDTO getProductPremiumRateDTO() {
		return productPremiumRateDTO;
	}

	public void setProductPremiumRateDTO(ProductPremiumRateDTO productPremiumRateDTO) {
		this.productPremiumRateDTO = productPremiumRateDTO;
	}

	public Map<String, ProductPremiumRateDTO> getProductPrmRateMap() {
		return productPrmRateMap;
	}

	public void setProductPrmRateMap(Map<String, ProductPremiumRateDTO> productPrmRateMap) {
		this.productPrmRateMap = productPrmRateMap;
	}

	public Map<String, List<String>> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, List<String>> dataMap) {
		this.dataMap = dataMap;
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public boolean getCreateNew() {
		return createNew;
	}
	
	public boolean isShortTermSinglePremiumCreditLife() {
		return isShortTermSinglePremiumCreditLife;
	}
	

	public SumInsuredType[] getSumInsuredType() {
		return SumInsuredType.values();
	}



}
