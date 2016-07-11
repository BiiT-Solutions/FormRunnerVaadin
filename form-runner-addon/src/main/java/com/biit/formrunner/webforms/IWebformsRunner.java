package com.biit.formrunner.webforms;

import com.biit.webforms.persistence.entity.Form;
import com.vaadin.ui.Component;

public interface IWebformsRunner extends Component {

	void loadForm(Form form);

}
