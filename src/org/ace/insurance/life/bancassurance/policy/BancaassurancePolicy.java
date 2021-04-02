package org.ace.insurance.life.bancassurance.policy;

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
import org.ace.insurance.life.bancassurance.proposal.BancaassuranceProposal;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.system.common.bancaBRM.BancaBRM;
import org.ace.insurance.system.common.bancaBranch.BancaBranch;
import org.ace.insurance.system.common.bancaLIC.BancaLIC;
import org.ace.insurance.system.common.bancaMethod.BancaMethod;
import org.ace.insurance.system.common.bancaRefferal.BancaRefferal;
import org.ace.insurance.system.common.channel.Channel;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.BANCAASSURANCE_POLICY)
@TableGenerator(name = "BANCAASSURANCE_POLICY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "BANCAASSURANCE_POLICY_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class BancaassurancePolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "BANCAASSURANCE_POLICY_GEN")
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
	@JoinColumn(name = "LIFEPOLICYID", referencedColumnName = "ID")
	private LifePolicy lifePolicy;

	@OneToOne
	@JoinColumn(name = "MEDICALPOLICYID", referencedColumnName = "ID")
	private MedicalPolicy medicalPolicy;

	@OneToOne
	@JoinColumn(name = "TRAVELPROPOSALID", referencedColumnName = "ID")
	private TravelProposal travelProposal;

	@OneToOne
	@JoinColumn(name = "PERSONTRAVELPOLICYID", referencedColumnName = "ID")
	private PersonTravelPolicy personTravelPolicy;

	@OneToOne
	@JoinColumn(name = "BANCAREFFERALID", referencedColumnName = "ID")
	private BancaRefferal bancaRefferal;

	@OneToOne
	@JoinColumn(name = "BANCABRANCHID", referencedColumnName = "ID")
	private BancaBranch bancaBranch;

	@OneToOne
	@JoinColumn(name = "BANCAASSURANCEPROPOSALID", referencedColumnName = "ID")
	private BancaassuranceProposal bancaassuranceProposal;

	@Version
	private int version;

	@Embedded
	private CommonCreateAndUpateMarks commonCreateAndUpateMarks;

	public BancaassurancePolicy() {
		super();
	}

	public BancaassurancePolicy(BancaassuranceProposal bancaassuranceProposal) {
		this.channel = bancaassuranceProposal.getChannel();
		this.bancaBRM = bancaassuranceProposal.getBancaBRM();
		this.bancaLIC = bancaassuranceProposal.getBancaLIC();
		this.bancaMethod = bancaassuranceProposal.getBancaMethod();
		this.bancaassuranceProposal = bancaassuranceProposal;
		this.bancaBranch = bancaassuranceProposal.getBancaBranch();
		this.bancaRefferal = bancaassuranceProposal.getBancaRefferal();
	}

	public String getId() {
		return id;
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

	public BancaMethod getBancaMethod() {
		return bancaMethod;
	}

	public void setBancaMethod(BancaMethod bancaMethod) {
		this.bancaMethod = bancaMethod;
	}

	public LifePolicy getLifePolicy() {
		return lifePolicy;
	}

	public void setLifePolicy(LifePolicy lifePolicy) {
		this.lifePolicy = lifePolicy;
	}

	public MedicalPolicy getMedicalPolicy() {
		return medicalPolicy;
	}

	public void setMedicalPolicy(MedicalPolicy medicalPolicy) {
		this.medicalPolicy = medicalPolicy;
	}

	public BancaRefferal getBancaRefferal() {
		return bancaRefferal;
	}

	public void setBancaRefferal(BancaRefferal bancaRefferal) {
		this.bancaRefferal = bancaRefferal;
	}

	public BancaBranch getBancaBranch() {
		return bancaBranch;
	}

	public void setBancaBranch(BancaBranch bancaBranch) {
		this.bancaBranch = bancaBranch;
	}

	public BancaBRM getBancaBRM() {
		return bancaBRM;
	}

	public void setBancaBRM(BancaBRM bancaBRM) {
		this.bancaBRM = bancaBRM;
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

	public BancaassuranceProposal getBancaassuranceProposal() {
		return bancaassuranceProposal;
	}

	public void setBancaassuranceProposal(BancaassuranceProposal bancaassuranceProposal) {
		this.bancaassuranceProposal = bancaassuranceProposal;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposl(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public PersonTravelPolicy getPersonTravelPolicy() {
		return personTravelPolicy;
	}

	public void setPersonTravelPolicy(PersonTravelPolicy personTravelPolicy) {
		this.personTravelPolicy = personTravelPolicy;
	}

}
