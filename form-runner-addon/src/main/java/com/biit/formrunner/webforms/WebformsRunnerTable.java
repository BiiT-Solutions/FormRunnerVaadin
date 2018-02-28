package com.biit.formrunner.webforms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.biit.form.entity.BaseGroup;
import com.biit.form.entity.BaseRepeatableGroup;
import com.biit.form.entity.TreeObject;
import com.biit.formrunner.common.IRunnerElement;
import com.biit.formrunner.common.ImagePreview;
import com.biit.formrunner.common.Runner;
import com.biit.formrunner.common.RunnerTable;
import com.biit.webforms.persistence.entity.ElementWithImage;
import com.biit.webforms.persistence.entity.TreeObjectImage;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.UI;

public abstract class WebformsRunnerTable extends RunnerTable {
	private static final long serialVersionUID = -1175869744919452626L;

	private final Runner runner;

	public WebformsRunnerTable(BaseGroup group, Runner runner) {
		super(group.getName(), group.getPath(), group);
		this.runner = runner;

		setCaption(group.getLabel());

		if (group instanceof BaseRepeatableGroup) {
			setRepeatable(((BaseRepeatableGroup) group).isRepeatable());
		} else {
			setRepeatable(false);
		}

		// Add image if needed.
		setImageLayoutUnvisible();
		if (runner.isImagesEnabled()
				&& UI.getCurrent().getPage().getBrowserWindowWidth() >= WebformsRunner.IMAGE_MINIMUM_WIDTH) {
			if (group instanceof ElementWithImage) {
				// Exists image and room enough to represent it.
				if (((ElementWithImage) group).getImage() != null) {
					setImage(((ElementWithImage) group).getImage());
					setImageLayoutVisible();
				}
			}
		}

		int row = 1;
		for (TreeObject child : group.getChildren()) {
			if (child instanceof BaseGroup) {
				int column = 1;
				for (TreeObject question : child.getChildren()) {
					addElementToTable(getElement((TreeObject) question, runner), column, row);
					column++;
				}
				row++;
			}
		}
	}

	public abstract IWebformsRunnerGroup getElement(BaseGroup group, Runner runner);

	public abstract IRunnerElement getElement(TreeObject element, Runner runner);

	protected ImagePreview getImageComponent(TreeObjectImage image) {
		if (image != null) {
			ImagePreview imagePreview = new ImagePreview();
			imagePreview.setSizeFull();
			imagePreview.setVisible(true);
			imagePreview.setClickEnabled(false);

			final ByteArrayOutputStream imageMemoryOutputStream = image.getStream();
			if (imageMemoryOutputStream != null) {
				StreamSource source = new StreamResource.StreamSource() {
					private static final long serialVersionUID = -7990668731450040256L;

					@Override
					public InputStream getStream() {
						return new ByteArrayInputStream(imageMemoryOutputStream.toByteArray());
					}
				};
				imagePreview.setStreamSource(source);
			} else {
				imagePreview.setStreamSource(null);
			}
			return imagePreview;
		}
		return null;
	}

	public Runner getRunner() {
		return runner;
	}

}
