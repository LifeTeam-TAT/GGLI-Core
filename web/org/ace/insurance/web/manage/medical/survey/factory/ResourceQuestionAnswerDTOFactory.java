package org.ace.insurance.web.manage.medical.survey.factory;

import org.ace.insurance.medical.surveyAnswer.ResourceQuestionAnswer;
import org.ace.insurance.web.manage.medical.survey.ResourceQuestionAnswerDTO;

public class ResourceQuestionAnswerDTOFactory {
	public static ResourceQuestionAnswerDTO getResourceQuestionAnswerDTO(ResourceQuestionAnswer resourceQuestionAnswer) {
		ResourceQuestionAnswerDTO resourceQuestionAnswerDTO = new ResourceQuestionAnswerDTO();
		if (resourceQuestionAnswer.getId() != null && (!resourceQuestionAnswer.getId().isEmpty())) {
			resourceQuestionAnswerDTO.setId(resourceQuestionAnswer.getId());
			resourceQuestionAnswerDTO.setVersion(resourceQuestionAnswer.getVersion());
			resourceQuestionAnswerDTO.setExistsEntity(true);
		}
		resourceQuestionAnswerDTO.setName(resourceQuestionAnswer.getName());
		resourceQuestionAnswerDTO.setResult(resourceQuestionAnswer.getResult());
		resourceQuestionAnswerDTO.setValue(resourceQuestionAnswer.getValue());
		if (resourceQuestionAnswer.getCommonCreateAndUpateMarks() != null) {
			resourceQuestionAnswerDTO.setCommonCreateAndUpateMarks(resourceQuestionAnswer.getCommonCreateAndUpateMarks());
		}
		return resourceQuestionAnswerDTO;
	}

	public static ResourceQuestionAnswer getResourceQuestionAnswer(ResourceQuestionAnswerDTO resourceQuestionAnswerDTO) {
		ResourceQuestionAnswer resourceQuestionAnswer = new ResourceQuestionAnswer();
		if (resourceQuestionAnswerDTO.isExistsEntity()) {
			resourceQuestionAnswer.setId(resourceQuestionAnswerDTO.getId());
			resourceQuestionAnswer.setVersion(resourceQuestionAnswerDTO.getVersion());
		}
		resourceQuestionAnswer.setName(resourceQuestionAnswerDTO.getName());
		resourceQuestionAnswer.setResult(resourceQuestionAnswerDTO.getResult());
		resourceQuestionAnswer.setValue(resourceQuestionAnswerDTO.getValue());
		if (resourceQuestionAnswerDTO.getCommonCreateAndUpateMarks() != null) {
			resourceQuestionAnswer.setCommonCreateAndUpateMarks(resourceQuestionAnswerDTO.getCommonCreateAndUpateMarks());
		}
		return resourceQuestionAnswer;
	}
}
