package org.ace.insurance.travel.expressTravel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.occurrence.Occurrence;
import org.ace.java.component.FormatID;

@Entity
@Table(name = TableName.TOUR)
@TableGenerator(name = "TOUR_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "TOUR_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "Tour.findAll", query = "SELECT t FROM Tour t ORDER BY t.occurrence.description ASC"),
		@NamedQuery(name = "Tour.findById", query = "SELECT t FROM Tour t WHERE t.id = :id") })
@Access(value = AccessType.FIELD)
public class Tour implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private String id;
	private String prefix;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCCURRENCEID", referencedColumnName = "ID")
	private Occurrence occurrence;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tour", orphanRemoval = true)
	private List<ExpressDetail> expressDetailList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRAVELEXPRESSID", referencedColumnName = "ID")
	private TravelExpress travelExpress;

	@Version
	private int version;

	public Tour() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TOUR_GEN")
	@Access(AccessType.PROPERTY)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if (id != null) {
			this.id = FormatID.formatId(id, getPrefix(), 10);
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void addExpressDetail(ExpressDetail expressDetail) {
		if (!getExpressDetailList().contains(expressDetail)) {
			expressDetail.setTour(this);
			getExpressDetailList().add(expressDetail);
		}
	}

	public List<ExpressDetail> getExpressDetailList() {
		if (expressDetailList == null) {
			expressDetailList = new ArrayList<ExpressDetail>();
		}
		return expressDetailList;
	}

	public void setExpressDetailList(List<ExpressDetail> expressDetailList) {
		this.expressDetailList = expressDetailList;
	}

	public TravelExpress getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(TravelExpress travelExpress) {
		this.travelExpress = travelExpress;
	}

	public Occurrence getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Occurrence occurrence) {
		this.occurrence = occurrence;
	}

	public int getNoOfPassenger() {
		int result = 0;
		if (expressDetailList != null) {
			for (ExpressDetail expressDetail : expressDetailList) {
				result += expressDetail.getNoOfPassenger();
			}
		}
		return result;
	}

	public int getNoOfUnit() {
		int result = 0;
		if (expressDetailList != null) {
			for (ExpressDetail expressDetail : expressDetailList) {
				result += expressDetail.getNoOfUnit();
			}
		}
		return result;
	}

	public double getNetPremium() {
		double result = 0;
		if (expressDetailList != null) {
			for (ExpressDetail expressDetail : expressDetailList) {
				result += expressDetail.getNetPremium();
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((occurrence == null) ? 0 : occurrence.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((travelExpress == null) ? 0 : travelExpress.hashCode());
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
		Tour other = (Tour) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (occurrence == null) {
			if (other.occurrence != null)
				return false;
		} else if (!occurrence.equals(other.occurrence))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (travelExpress == null) {
			if (other.travelExpress != null)
				return false;
		} else if (!travelExpress.equals(other.travelExpress))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
