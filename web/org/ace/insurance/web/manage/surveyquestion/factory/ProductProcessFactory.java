package org.ace.insurance.web.manage.surveyquestion.factory;

import org.ace.insurance.medical.productprocess.ProductProcess;
import org.ace.insurance.medical.surveyquestion.ProductProcessQuestionLink;
import org.ace.insurance.web.manage.product.ProductProcessDTO;
import org.ace.insurance.web.manage.surveyquestion.ProductProcessQuestionLinkDTO;

public class ProductProcessFactory {

	public static ProductProcess getProductProcess(ProductProcessDTO productProcessDTO) {
		ProductProcess productProcess = new ProductProcess();
		if (productProcessDTO.isExitingEntity()) {
			productProcess.setId(productProcessDTO.getId());
			productProcess.setVersion(productProcessDTO.getVersion());
		}
		productProcess.setStartDate(productProcessDTO.getStartDate());
		productProcess.setEndDate(productProcessDTO.getEndDate());
		productProcess.setProcess(productProcessDTO.getProcess());
		productProcess.setProduct(productProcessDTO.getProduct());
		productProcess.setActiveStatus(productProcessDTO.getActiveStatus());
		productProcess.setQuestionSetNo(productProcessDTO.getQuestionSetNo());
		productProcess.setStartDate(productProcessDTO.getStartDate());
		productProcess.setEndDate(productProcessDTO.getEndDate());
		productProcess.getProductProcessCriteria().setStudentAgeType(productProcessDTO.getStudentAgeType());
		productProcess.getProductProcessCriteria().setOptiontype(productProcessDTO.getOptionType());
		productProcess.getProductProcessCriteria().setMaxAge(productProcessDTO.getMaxAge());
		productProcess.getProductProcessCriteria().setMinAge(productProcessDTO.getMinAge());
		productProcess.getProductProcessCriteria().setSumInsured(productProcessDTO.getSumInsured());
		productProcess.getProductProcessCriteria().setOptiontype(productProcessDTO.getOptionType());
		if (productProcessDTO.getCommonCreateAndUpateMarks() != null) {
			productProcess.setCommonCreateAndUpateMarks(productProcessDTO.getCommonCreateAndUpateMarks());
		}

		for (ProductProcessQuestionLinkDTO ppqlDto : productProcessDTO.getProductProcessQuestionLinkList()) {
			ProductProcessQuestionLink ppql = ProductProcessQuestionLinkFactory.getProductProcessQuestionLink(ppqlDto);
			productProcess.addProcessQuestionLink(ppql);
		}
		return productProcess;
	}

	public static ProductProcessDTO getProductProcessDTO(ProductProcess productProcess) {
		ProductProcessDTO productProcessDto = new ProductProcessDTO();
		if (productProcess.getId() != null) {
			productProcessDto.setExitingEntity(true);
			productProcessDto.setId(productProcess.getId());
			productProcessDto.setVersion(productProcess.getVersion());
		}

		productProcessDto.setProcess(productProcess.getProcess());
		productProcessDto.setProduct(productProcess.getProduct());
		productProcessDto.setActiveStatus(productProcess.getActiveStatus());
		productProcessDto.setQuestionSetNo(productProcess.getQuestionSetNo());
		productProcessDto.setStartDate(productProcess.getStartDate());
		productProcessDto.setEndDate(productProcess.getEndDate());
		productProcessDto.setStudentAgeType(productProcess.getProductProcessCriteria().getStudentAgeType());
		productProcessDto.setOptionType(productProcess.getProductProcessCriteria().getOptiontype());
		productProcessDto.setMaxAge(productProcess.getProductProcessCriteria().getMaxAge());
		productProcessDto.setMinAge(productProcess.getProductProcessCriteria().getMinAge());
		productProcessDto.setSumInsured(productProcess.getProductProcessCriteria().getSumInsured());
		productProcessDto.setOptionType(productProcess.getProductProcessCriteria().getOptiontype());
		if (productProcess.getCommonCreateAndUpateMarks() != null) {
			productProcessDto.setCommonCreateAndUpateMarks(productProcess.getCommonCreateAndUpateMarks());
		}

		for (ProductProcessQuestionLink ppql : productProcess.getProductProcessQuestionLinkList()) {
			if (!ppql.getSurveyQuestion().isDeleteFlag()) {
				ProductProcessQuestionLinkDTO ppqlDto = ProductProcessQuestionLinkFactory.getProductProcessQuestionLinkDTO(ppql);
				productProcessDto.addProductProcessQuestionLink(ppqlDto);
			}
		}
		return productProcessDto;
	}

}
