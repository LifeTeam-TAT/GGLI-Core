package org.ace.insurance.system.productinformation.subcategory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.productinformation.maincategory.MainCategory;
import org.ace.insurance.system.productinformation.photoimage.PhotoImage;
import org.ace.insurance.system.productinformation.text.NameText;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.SUBCATEGORY)
@TableGenerator(name = "SUBCATEGORY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", pkColumnValue = "SUBCATEGORY_GEN", valueColumnName = "GEN_VAL", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "SubCategory.findAll", query = "SELECT s FROM SubCategory s"),
		@NamedQuery(name = "SubCategory.findById", query = "SELECT s FROM SubCategory s WHERE s.id = :id") })
@EntityListeners(IDInterceptor.class)
public class SubCategory implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "SUBCATEGORY_GEN")
	private String id;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "subCategory", orphanRemoval = true)
	private List<NameText> textList;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "IMAGEID")
	private PhotoImage image;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MAINCATEGORYID", referencedColumnName = "ID")
	private MainCategory mainCategory;

	@Column(name = "ORDERNUMBER")
	private int order;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public SubCategory() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<NameText> getTextList() {
		if (textList == null) {
			textList = new ArrayList<NameText>();
		}
		return textList;
	}

	public void setTextList(List<NameText> textList) {
		this.textList = textList;
	}

	public MainCategory getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(MainCategory mainCategory) {
		this.mainCategory = mainCategory;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public PhotoImage getImage() {
		return image;
	}

	public void setImage(PhotoImage image) {
		this.image = image;
	}

	/**
	 * @return the commonCreateAndUpateMarks
	 */
	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	/**
	 * @param commonCreateAndUpateMarks
	 *            the commonCreateAndUpateMarks to set
	 */
	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void addNameAndText(NameText nameAndText) {
		if (this.textList == null) {
			textList = new ArrayList<NameText>();
		}
		this.textList.add(nameAndText);
		nameAndText.setSubCategory(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((mainCategory == null) ? 0 : mainCategory.hashCode());
		result = prime * result + order;
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubCategory other = (SubCategory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (mainCategory == null) {
			if (other.mainCategory != null)
				return false;
		} else if (!mainCategory.equals(other.mainCategory))
			return false;
		if (order != other.order)
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
