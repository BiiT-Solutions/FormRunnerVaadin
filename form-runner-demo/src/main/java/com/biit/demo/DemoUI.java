package com.biit.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;

import com.biit.form.runner.webforms.IWebformsRunner;
import com.biit.utils.file.FileReader;
import com.biit.webforms.persistence.entity.Form;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("MyComponent Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = true, ui = DemoUI.class, widgetset = "com.biit.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	private static final String JSON_PATH = "form";
	private static final String FILE_NAME = "test";

	@Override
	protected void init(VaadinRequest request) {
		// Show it in the middle of the screen
		final VerticalLayout layout = new VerticalLayout();
		layout.setStyleName("demoContentLayout");
		layout.setSizeFull();

		String file;
		try {
			file = FileReader.getResource(JSON_PATH + File.separatorChar + FILE_NAME + ".json", StandardCharsets.UTF_8);
			Form form = Form.fromJson(file);
			IWebformsRunner runner = new DemoWebformsRunner();
			runner.setCaption("Test form runner");
			runner.setSizeFull();
			runner.loadForm(form);

			layout.addComponent(runner);
			layout.setComponentAlignment(runner, Alignment.MIDDLE_CENTER);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		setContent(layout);

	}

}
