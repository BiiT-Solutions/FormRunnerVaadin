package com.biit.form.runner.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.biit.form.runner.common.exceptions.PathDoesNotExist;
import com.biit.webforms.persistence.entity.TreeObjectImage;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class Runner extends CustomComponent {
	private static final long serialVersionUID = -7172803831669464289L;
	private static final String CLASSNAME = "v-form-runner";
	private static final String ROOT_LAYOUT_STYLE_NAME = "v-form-runner-root-layout";
	private static final String FULL = "100%";

	private final HorizontalLayout rootLayout;
	private final VerticalLayout formLayout;
	private final VerticalLayout mainImageLayout;
	private boolean loading;

	private ImagePreview imagePreview;

	private boolean imagesEnabled;
	private String emailRegex;
	private String phoneRegex;
	private String textRegex;
	private String postalCodeRegex;
	private String invalidCaption;
	private String requiredCaption;

	private boolean systemFieldsIgnored = true;

	public Runner() {
		super();
		setLocale(Locale.getDefault());
		this.imagesEnabled = true;
		this.emailRegex = "";
		this.phoneRegex = "";
		this.textRegex = "";
		this.postalCodeRegex = "";
		this.invalidCaption = "";
		this.requiredCaption = "";

		setWidth(FULL);
		setHeightUndefined();
		setStyleName(CLASSNAME);
		setLoading(false);

		rootLayout = new HorizontalLayout();
		rootLayout.setWidth(FULL);
		rootLayout.setHeightUndefined();
		rootLayout.setSpacing(true);
		rootLayout.setMargin(true);
		rootLayout.setStyleName(ROOT_LAYOUT_STYLE_NAME);

		// Panel at the left.
		formLayout = new VerticalLayout();
		formLayout.setWidth(FULL);
		formLayout.setHeightUndefined();
		formLayout.setSpacing(false);
		formLayout.setMargin(false);
		rootLayout.addComponent(formLayout);

		// Panel at the right for main image
		mainImageLayout = new VerticalLayout();
		mainImageLayout.setWidth(FULL);
		mainImageLayout.setHeightUndefined();
		mainImageLayout.setSpacing(false);
		mainImageLayout.setMargin(false);

		imagePreview = new ImagePreview();
		imagePreview.setSizeFull();
		imagePreview.setVisible(true);
		imagePreview.setClickEnabled(false);

		mainImageLayout.addComponent(imagePreview);
		rootLayout.addComponent(mainImageLayout);

		setCompositionRoot(rootLayout);
	}

	protected void setImageLayoutVisible() {
		mainImageLayout.setVisible(true);
	}

	protected void setImageLayoutInvisible() {
		mainImageLayout.setVisible(false);
	}

	public void addElement(IRunnerElement element) {
		formLayout.addComponent(element);
	}

	protected void setImage(TreeObjectImage image) {
		if (image == null) {
			imagePreview.setStreamSource(null);
		} else {
			final ByteArrayOutputStream imageMemoryOutputStream = image.getStream();
			if (imageMemoryOutputStream != null) {
				StreamSource source = new StreamResource.StreamSource() {
					private static final long serialVersionUID = -7990668731450040256L;

					public InputStream getStream() {
						return new ByteArrayInputStream(imageMemoryOutputStream.toByteArray());
					}
				};
				imagePreview.setStreamSource(source);
			} else {
				imagePreview.setStreamSource(null);
			}
		}
	}

	public void addElement(List<String> path, IRunnerElement element) throws PathDoesNotExist {
		if (path.isEmpty()) {
			formLayout.addComponent(element);
		} else {
			getElement(path).addElement(element);
		}
	}

	public IRunnerElement getElement(List<String> path) throws PathDoesNotExist {
		IRunnerElement element = getElement(path.get(0));
		if (path.size() == 1) {
			return element;
		} else {
			try {
				return element.getElement(path.subList(1, path.size()));
			} catch (PathDoesNotExist e) {
				throw new PathDoesNotExist(path);
			}
		}
	}

	private IRunnerElement getElement(String name) throws PathDoesNotExist {
		Iterator<Component> itr = formLayout.iterator();

		while (itr.hasNext()) {
			IRunnerElement next = (IRunnerElement) itr.next();
			if (next.getName().equals(name)) {
				return next;
			}
		}
		throw new PathDoesNotExist(name);
	}

	public boolean isValid() {
		Iterator<Component> itr = formLayout.iterator();
		boolean valid = true;
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			valid = valid && component.isValid();
		}
		return valid;
	}

	public void clear() {
		Iterator<Component> itr = formLayout.iterator();
		while (itr.hasNext()) {
			RunnerGroup group = (RunnerGroup) itr.next();
			group.clear();
		}
	}

	public void setRelevance(List<String> path, boolean value) throws PathDoesNotExist {
		IRunnerElement element = getElement(path);
		element.setRelevance(value);
		try {
			//Update relevance for all parent groups.
			for (int i = path.size() - 1; i > 0; i--) {
				((RunnerGroup) getElement(path.subList(0, i))).checkRelevance();
			}
		} catch (PathDoesNotExist | ClassCastException ignored) {
		}
	}

	public boolean getRelevance(List<String> path) throws PathDoesNotExist {
		return getElement(path).getRelevance();
	}

	public List<Result> getAnswers() {
		List<Result> answers = new ArrayList<Result>();

		Iterator<Component> itr = formLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			if (component.getRelevance() || component.isHidden()) {
				answers.addAll(component.getAnswers());
			}
		}

		return answers;
	}

	public void setAnswers(List<String> path, List<String> answers) throws PathDoesNotExist {
		if (path.isEmpty()) {
			throw new PathDoesNotExist(path);
		}
		getElement(path).setAnswers(answers);
	}

	public void evaluate(List<String> path) throws PathDoesNotExist {
		// Do nothing. Basic form runner doesn't evaluate elements.
	}

	public boolean isLoading() {
		return loading;
	}

	/**
	 * Set loading to true to avoid value change evaluations.
	 * 
	 * @param loading the value.
	 */
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		if (formLayout != null) {
			Iterator<Component> itr = formLayout.iterator();
			while (itr.hasNext()) {
				IRunnerElement component = (IRunnerElement) itr.next();
				component.setLocale(locale);
			}
		}
	}

	public boolean isImagesEnabled() {
		return imagesEnabled;
	}

	public String getEmailRegex() {
		return emailRegex;
	}

	public String getPhoneRegex() {
		return phoneRegex;
	}

	public String getTextRegex() {
		return textRegex;
	}

	public String getPostalCodeRegex() {
		return postalCodeRegex;
	}

	public String getInvalidCaption() {
		return invalidCaption;
	}

	public String getRequiredCaption() {
		return requiredCaption;
	}

	public void setImagesEnabled(boolean imagesEnabled) {
		this.imagesEnabled = imagesEnabled;
	}

	public void setEmailRegex(String emailRegex) {
		this.emailRegex = emailRegex;
	}

	public void setPhoneRegex(String phoneRegex) {
		this.phoneRegex = phoneRegex;
	}

	public void setTextRegex(String textRegex) {
		this.textRegex = textRegex;
	}

	public void setPostalCodeRegex(String postalCodeRegex) {
		this.postalCodeRegex = postalCodeRegex;
	}

	public void setInvalidCaption(String invalidCaption) {
		this.invalidCaption = invalidCaption;
	}

	public void setRequiredCaption(String requiredCaption) {
		this.requiredCaption = requiredCaption;
	}

	public boolean isSystemFieldsIgnored() {
		return systemFieldsIgnored;
	}

	public void setSystemFieldsIgnored(boolean systemFieldsIgnored) {
		this.systemFieldsIgnored = systemFieldsIgnored;
	}
}
