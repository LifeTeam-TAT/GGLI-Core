package org.ace.insurance.web.manage.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ace.insurance.medical.process.Process;
import org.ace.insurance.medical.productprocess.ActiveStatus;
import org.ace.insurance.medical.productprocess.OptionType;
import org.ace.insurance.medical.productprocess.StudentAgeType;
import org.ace.insurance.product.Product;
import org.ace.insurance.web.common.CommonDTO;
import org.ace.insurance.web.manage.surveyquestion.ProductProcessQuestionLinkDTO;

public class ProductProcessDTO extends CommonDTO {
	private int version;
	private String id;
	private Product product;
	private Process process;
	private List<ProductProcessQuestionLinkDTO> productProcessQuestionLinkList;
	private String questionSetNo;
	private ActiveStatus activeStatus;
	private StudentAgeType studentAgeType;
	private OptionType optionType;
	private int minAge;
	private int maxAge;
	private double sumInsured;
	private Date startDate;
	private Date endDate;
	private boolean exitingEntity;
	private boolean isEvent;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

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

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public List<ProductProcessQuestionLinkDTO> getProductProcessQuestionLinkList() {
		if (productProcessQuestionLinkList == null) {
			productProcessQuestionLinkList = new ArrayList<ProductProcessQuestionLinkDTO>();
		}
		return productProcessQuestionLinkList;
	}

	public void setProductProcessQuestionLinkList(List<ProductProcessQuestionLinkDTO> productProcessQuestionLinkList) {
		this.productProcessQuestionLinkList = productProcessQuestionLinkList;
	}

	public void addProductProcessQuestionLink(ProductProcessQuestionLinkDTO questionLink) {
		if (productProcessQuestionLinkList == null) {
			productProcessQuestionLinkList = new ArrayList<ProductProcessQuestionLinkDTO>();
		}
		productProcessQuestionLinkList.add(questionLink);
	}

	public void removeProductProcessQuestionLink(ProductProcessQuestionLinkDTO questionLink) {
		if (productProcessQuestionLinkList != null) {
			productProcessQuestionLinkList.remove(questionLink);
		}
	}

	public String getQuestionSetNo() {
		return questionSetNo;
	}

	public void setQuestionSetNo(String questionSetNo) {
		this.questionSetNo = questionSetNo;
	}

	public ActiveStatus getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(ActiveStatus activeStatus) {
		this.activeStatus = activeStatus;
	}

	public StudentAgeType getStudentAgeType() {
		return studentAgeType;
	}

	public void setStudentAgeType(StudentAgeType studentAgeType) {
		this.studentAgeType = studentAgeType;
	}

	public OptionType getOptionType() {
		return optionType;
	}

	public void setOptionType(OptionType optionType) {
		this.optionType = optionType;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isEvent() {
		return isEvent;
	}

	public void setEvent(boolean isEvent) {
		this.isEvent = isEvent;
	}

	public ProductProcessDTO() {
		super();
	}

	public boolean isExitingEntity() {
		return exitingEntity;
	}

	public void setExitingEntity(boolean exitingEntity) {
		this.exitingEntity = exitingEntity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((activeStatus == null) ? 0 : activeStatus.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + (exitingEntity ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isEvent ? 1231 : 1237);
		result = prime * result + maxAge;
		result = prime * result + minAge;
		result = prime * result + ((optionType == null) ? 0 : optionType.hashCode());
		result = prime * result + ((process == null) ? 0 : process.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((productProcessQuestionLinkList == null) ? 0 : productProcessQuestionLinkList.hashCode());
		result = prime * result + ((questionSetNo == null) ? 0 : questionSetNo.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((studentAgeType == null) ? 0 : studentAgeType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(sumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductProcessDTO other = (ProductProcessDTO) obj;
		if (activeStatus != other.activeStatus)
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (exitingEntity != other.exitingEntity)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isEvent != other.isEvent)
			return false;
		if (maxAge != other.maxAge)
			return false;
		if (minAge != other.minAge)
			return false;
		if (optionType != other.optionType)
			return false;
		if (process == null) {
			if (other.process != null)
				return false;
		} else if (!process.equals(other.process))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (productProcessQuestionLinkList == null) {
			if (other.productProcessQuestionLinkList != null)
				return false;
		} else if (!productProcessQuestionLinkList.equals(other.productProcessQuestionLinkList))
			return false;
		if (questionSetNo == null) {
			if (other.questionSetNo != null)
				return false;
		} else if (!questionSetNo.equals(other.questionSetNo))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (studentAgeType != other.studentAgeType)
			return false;
		if (Double.doubleToLongBits(sumInsured) != Double.doubleToLongBits(other.sumInsured))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}