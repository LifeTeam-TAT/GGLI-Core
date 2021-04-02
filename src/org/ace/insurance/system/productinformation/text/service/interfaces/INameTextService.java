package org.ace.insurance.system.productinformation.text.service.interfaces;
import java.util.List;

import org.ace.insurance.system.productinformation.text.NameText;

public interface INameTextService {
	
	public void addNewNameText(NameText nameText) ;

	public void updateNameText(NameText nameText) ;

	public void deleteNameText(NameText nameText) ;

	public NameText findNameTextById(String id);

	public List<NameText> findAllNameText();
}
