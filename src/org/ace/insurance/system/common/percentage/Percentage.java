package org.ace.insurance.system.common.percentage;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.relationshiptype.RelationShipType;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERCENTAGE)
@TableGenerator(name = "PERCENTAGE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERCENTAGE_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
@NamedQueries({
	@NamedQuery(name = "Percentage.findPercentWithRelationShip", query = "select p from Percentage p where p.relationshiptype.id =:typeId and p.product.id =:productId")
})
public class Percentage  implements Serializable {
	private static final long serialVersionUID = 7393371719430453210L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERCENTAGE_GEN")
	private String id;


	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID")
	private Product product;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RELATIONSHIPTYPE_ID", referencedColumnName = "ID")
	private RelationShipType relationshiptype;
	
	@Column(name="PERCENTAGE")
	private double percent;
	
	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	@Version
	private int version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public RelationShipType getRelationshiptype() {
		return relationshiptype;
	}

	public void setRelationshiptype(RelationShipType relationshiptype) {
		this.relationshiptype = relationshiptype;
	}


	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(percent);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((relationshiptype == null) ? 0 : relationshiptype.hashCode());
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
		Percentage other = (Percentage) obj;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(percent) != Double.doubleToLongBits(other.percent))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (relationshiptype == null) {
			if (other.relationshiptype != null)
				return false;
		} else if (!relationshiptype.equals(other.relationshiptype))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	
}
