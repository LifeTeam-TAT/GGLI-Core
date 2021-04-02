package org.ace.insurance.life.bancassurance.proposal;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.CommonCreateAndUpateMarks;
import org.ace.insurance.common.TableName;
import org.ace.insurance.groupfarmer.proposal.GroupFarmerProposal;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANCAASSURANCE_PROPOSAL)
@TableGenerator(name = "BANCAASSURANCE_PROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BANCAASSURANCE_PROPOSAL_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class BancaassuranceProposal {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BANCAASSURANCE_PROPOSAL_GEN")
	private String id;

	@OneToOne
	@JoinColumn(name = "CHANNELID", referencedColumnName = "ID")
	private Channel channel;

	@OneToOne
	@JoinColumn(name = "BANCALICID", referencedColumnName = "ID")
	private BancaLIC bancaLIC;

	@OneToOne
	@JoinColumn(name = "BANCABRMID", referencedColumnName = "ID")
	private BancaBRM bancaBRM;

	@OneToOne
	@JoinColumn(name = "BANCAMETHODID", referencedColumnName = "ID")
	private BancaMethod bancaMethod;

	@OneToOne
	@JoinColumn(name = "LIFEPROPOSALID", referencedColumnName = "ID")
	private LifeProposal lifeProposal;

	@OneToOne
	@JoinColumn(name = "GROUPFARMERPROPOSALID", referencedColumnName = "ID")
	private GroupFarmerProposal groupFarmerProposal;

	@OneToOne
	@JoinColumn(name = "TRAVELPROPOSALID", referencedColumnName = "ID")
	private TravelProposal travelProposal;

	@OneToOne
	@JoinColumn(name = "PERSONTRAVELPROPOSALID", referencedColumnName = "ID")
	private PersonTravelProposal personTravelProposal;

	@OneToOne
	@JoinColumn(name = "MEDICALPROPOSALID", referencedColumnName = "ID")
	private MedicalProposal medicalProposal;

	@OneToOne
	@JoinColumn(name = "BANCAREFFERALID", referencedColumnName = "ID")
	private BancaRefferal bancaRefferal;

	@OneToOne
	@JoinColumn(name = "BANCABRANCHID", referencedColumnName = "ID")
	private BancaBranch bancaBranch;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	public BancaassuranceProposal() {

	}

	public BancaassuranceProposal(String id, Channel channel, BancaLIC bancaLIC, BancaBRM bancaBRM, BancaMethod bancaMethod, LifeProposal lifeProposal,
			GroupFarmerProposal groupFarmerProposal, TravelProposal travelProposal, PersonTravelProposal personTravelProposal, MedicalProposal medicalProposal,
			BancaRefferal bancaRefferal, BancaBranch bancaBranch, int version, CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.id = id;
		this.channel = channel;
		this.bancaLIC = bancaLIC;
		this.bancaBRM = bancaBRM;
		this.bancaMethod = bancaMethod;
		this.lifeProposal = lifeProposal;
		this.groupFarmerProposal = groupFarmerProposal;
		this.travelProposal = travelProposal;
		this.personTravelProposal = personTravelProposal;
		this.medicalProposal = medicalProposal;
		this.bancaRefferal = bancaRefferal;
		this.bancaBranch = bancaBranch;
		this.version = version;
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	public String getId() {
		return id;
	}

	public BancaRefferal getBancaRefferal() {
		return bancaRefferal;
	}

	public void setBancaRefferal(BancaRefferal bancaRefferal) {
		this.bancaRefferal = bancaRefferal;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public BancaLIC getBancaLIC() {
		return bancaLIC;
	}

	public void setBancaLIC(BancaLIC bancaLIC) {
		this.bancaLIC = bancaLIC;
	}

	public BancaBRM getBancaBRM() {
		return bancaBRM;
	}

	public void setBancaBRM(BancaBRM bancaBRM) {
		this.bancaBRM = bancaBRM;
	}

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public void setBancaMethod(BancaMethod bancaMethod) {
		this.bancaMethod = bancaMethod;
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public void setLifeProposal(LifeProposal lifeProposal) {
		this.lifeProposal = lifeProposal;
	}

	public GroupFarmerProposal getGroupFarmerProposal() {
		return groupFarmerProposal;
	}

	public void setGroupFarmerProposal(GroupFarmerProposal groupFarmerProposal) {
		this.groupFarmerProposal = groupFarmerProposal;
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public void setMedicalProposal(MedicalProposal medicalProposal) {
		this.medicalProposal = medicalProposal;
	}

	public BancaBranch getBancaBranch() {
		return bancaBranch;
	}

	public void setBancaBranch(BancaBranch bancaBranch) {
		this.bancaBranch = bancaBranch;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	public void setPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		this.personTravelProposal = personTravelProposal;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CommonCreateAndUpateMarks getCommonCreateAndUpateMarks() {
		return commonCreateAndUpateMarks;
	}

	public void setCommonCreateAndUpateMarks(CommonCreateAndUpateMarks commonCreateAndUpateMarks) {
		this.commonCreateAndUpateMarks = commonCreateAndUpateMarks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bancaBRM == null) ? 0 : bancaBRM.hashCode());
		result = prime * result + ((bancaBranch == null) ? 0 : bancaBranch.hashCode());
		result = prime * result + ((bancaLIC == null) ? 0 : bancaLIC.hashCode());
		result = prime * result + ((bancaMethod == null) ? 0 : bancaMethod.hashCode());
		result = prime * result + ((bancaRefferal == null) ? 0 : bancaRefferal.hashCode());
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((commonCreateAndUpateMarks == null) ? 0 : commonCreateAndUpateMarks.hashCode());
		result = prime * result + ((groupFarmerProposal == null) ? 0 : groupFarmerProposal.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lifeProposal == null) ? 0 : lifeProposal.hashCode());
		result = prime * result + ((medicalProposal == null) ? 0 : medicalProposal.hashCode());
		result = prime * result + ((personTravelProposal == null) ? 0 : personTravelProposal.hashCode());
		result = prime * result + ((travelProposal == null) ? 0 : travelProposal.hashCode());
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
		BancaassuranceProposal other = (BancaassuranceProposal) obj;
		if (bancaBRM == null) {
			if (other.bancaBRM != null)
				return false;
		} else if (!bancaBRM.equals(other.bancaBRM))
			return false;
		if (bancaBranch == null) {
			if (other.bancaBranch != null)
				return false;
		} else if (!bancaBranch.equals(other.bancaBranch))
			return false;
		if (bancaLIC == null) {
			if (other.bancaLIC != null)
				return false;
		} else if (!bancaLIC.equals(other.bancaLIC))
			return false;
		if (bancaMethod == null) {
			if (other.bancaMethod != null)
				return false;
		} else if (!bancaMethod.equals(other.bancaMethod))
			return false;
		if (bancaRefferal == null) {
			if (other.bancaRefferal != null)
				return false;
		} else if (!bancaRefferal.equals(other.bancaRefferal))
			return false;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		if (commonCreateAndUpateMarks == null) {
			if (other.commonCreateAndUpateMarks != null)
				return false;
		} else if (!commonCreateAndUpateMarks.equals(other.commonCreateAndUpateMarks))
			return false;
		if (groupFarmerProposal == null) {
			if (other.groupFarmerProposal != null)
				return false;
		} else if (!groupFarmerProposal.equals(other.groupFarmerProposal))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lifeProposal == null) {
			if (other.lifeProposal != null)
				return false;
		} else if (!lifeProposal.equals(other.lifeProposal))
			return false;
		if (medicalProposal == null) {
			if (other.medicalProposal != null)
				return false;
		} else if (!medicalProposal.equals(other.medicalProposal))
			return false;
		if (personTravelProposal == null) {
			if (other.personTravelProposal != null)
				return false;
		} else if (!personTravelProposal.equals(other.personTravelProposal))
			return false;
		if (travelProposal == null) {
			if (other.travelProposal != null)
				return false;
		} else if (!travelProposal.equals(other.travelProposal))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
