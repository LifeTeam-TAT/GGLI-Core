package org.ace.insurance.system.productinformation.text;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.productinformation.Language;
import org.ace.insurance.system.productinformation.maincategory.MainCategory;
import org.ace.insurance.system.productinformation.subcategory.SubCategory;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.NAMETEXT)
@TableGenerator(name = "NAMETEXT_GEN", pkColumnName = "GEN_NAME", pkColumnValue = "NAMETEXT_GEN", valueColumnName = "GEN_VAL", table = "ID_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "NameText.findAll", query = "SELECT n FROM NameText n"),
		@NamedQuery(name = "NameText.findById", query = "SELECT n FROM NameText n WHERE n.id = :id") })
@EntityListeners(IDInterceptor.class)
public class NameText implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "NAMETEXT_GEN")
	private String id;

	@Column(name = "NAME_TEXT")
	private String name;

	@Column(name = "CONTENT_TEXT")
	private String content;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MAINCATEGORYID", referencedColumnName = "ID")
	private MainCategory mainCategory;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SUBCATEGORYID", referencedColumnName = "ID")
	private SubCategory subCategory;

	@Enumerated(EnumType.STRING)
	private Language language;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public NameText() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public MainCategory getMainCategory() {
		return mainCategory;
	}

	public void setMainCategory(MainCategory mainCategory) {
		this.mainCategory = mainCategory;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((mainCategory == null) ? 0 : mainCategory.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((subCategory == null) ? 0 : subCategory.hashCode());
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
		NameText other = (NameText) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language != other.language)
			return false;
		if (mainCategory == null) {
			if (other.mainCategory != null)
				return false;
		} else if (!mainCategory.equals(other.mainCategory))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (subCategory == null) {
			if (other.subCategory != null)
				return false;
		} else if (!subCategory.equals(other.subCategory))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}