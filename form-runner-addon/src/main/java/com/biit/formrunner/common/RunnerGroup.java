package com.biit.formrunner.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.biit.webforms.persistence.entity.TreeObjectImage;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class RunnerGroup extends CustomComponent implements IRunnerElement {
	private static final long serialVersionUID = -660939531249503240L;
	private static final String CLASSNAME = "v-form-runner-group";
	private static final String FULL = "100%";

	private final String name;

	private final VerticalLayout rootLayout;
	private final VerticalLayout cloneLayout;
	private final VerticalLayout groupElementsLayout;

	private final Button cloneButton;
	private final List<String> path;
	private final List<IRunnerElement> children;

	private ImagePreview imagePreview;

	public RunnerGroup(String name, List<String> path) {
		super();
		setWidth(FULL);
		setHeightUndefined();
		setStyleName(CLASSNAME);

		this.name = name;
		this.path = path;
		this.children = new ArrayList<>();

		rootLayout = new VerticalLayout();
		cloneLayout = new VerticalLayout();
		groupElementsLayout = new VerticalLayout();
		cloneButton = new Button();

		imagePreview = new ImagePreview();

		configureLayouts();
		configureCloneButton();

		setCompositionRoot(rootLayout);
	}

	protected void setImageLayoutVisible() {
		imagePreview.setVisible(true);
	}

	protected void setImageLayoutUnvisible() {
		imagePreview.setVisible(false);
	}

	protected void setImage(TreeObjectImage image) {
		if (image == null) {
			imagePreview.setStreamSource(null);
		} else {
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
		}
	}

	@Override
	public void addElement(IRunnerElement element) {
		if (element != null) {
			children.add(element);
			groupElementsLayout.addComponent(element);
		}
	}

	private void configureCloneButton() {
		cloneButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -8238635390473554679L;

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO clone button now doesn't work.
			}
		});
	}

	private void configureLayouts() {
		rootLayout.setWidth(FULL);
		rootLayout.setHeightUndefined();
		rootLayout.setMargin(true);
		rootLayout.setSpacing(true);

		groupElementsLayout.setWidth(FULL);
		groupElementsLayout.setHeightUndefined();
		groupElementsLayout.setMargin(false);
		groupElementsLayout.setSpacing(true);

		cloneLayout.setWidth(FULL);
		cloneLayout.setHeightUndefined();
		cloneLayout.setMargin(false);

		imagePreview.setSizeFull();
		imagePreview.setVisible(true);
		imagePreview.setClickEnabled(false);
		rootLayout.addComponent(imagePreview);

		rootLayout.addComponent(groupElementsLayout);
		rootLayout.addComponent(cloneLayout);

		rootLayout.addComponent(cloneButton);
		rootLayout.setComponentAlignment(cloneButton, Alignment.BOTTOM_RIGHT);
	}

	@Override
	public List<Result> getAnswers() {
		List<Result> answers = new ArrayList<Result>();

		ResultGroup groupAnswer = new ResultGroup(getName());
		Iterator<Component> itr = groupElementsLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			groupAnswer.addAnswers(component.getAnswers());
		}
		answers.add(groupAnswer);

		itr = cloneLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			answers.addAll(component.getAnswers());
		}

		return answers;
	}

	public boolean isRepeatable() {
		return cloneLayout.isVisible();
	}

	public void setRepeatable(boolean repeatable) {
		// Hide clone layout and button.
		cloneLayout.setVisible(repeatable);
		cloneButton.setVisible(repeatable);
	}

	@Override
	public void clear() {
		Iterator<Component> itr = groupElementsLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			component.clear();
		}

		cloneLayout.removeAllComponents();
	}

	@Override
	public void setAnswers(List<String> list) {
		// Groups can't hold values
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isValid() {
		if (!getRelevance()) {
			return true;
		}

		boolean valid = true;
		Iterator<Component> itr = groupElementsLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			valid = valid && component.isValid();
		}

		itr = cloneLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			valid = valid && component.isValid();
		}

		return valid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
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
		Iterator<Component> itr = groupElementsLayout.iterator();

		while (itr.hasNext()) {
			IRunnerElement next = (IRunnerElement) itr.next();
			if (next.getName().equals(name)) {
				return next;
			}
		}
		throw new PathDoesNotExist(name);
	}

	@Override
	public void setRelevance(boolean value) {
		setVisible(value);
	}

	@Override
	public List<String> getPath() {
		return path;
	}

	@Override
	public boolean getRelevance() {
		return isVisible();
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

	@Override
	public boolean isFilled() {
		return false;
	}

	/**
	 * Checks if at least one child element is relevant. If not makes this group
	 * unrelevant.
	 */
	public void checkRelevance() {
		Iterator<Component> itr = groupElementsLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement element = (IRunnerElement) itr.next();
			if (element.getRelevance()) {
				setRelevance(true);
				return;
			}
		}
		setRelevance(false);
	}

	@Override
	public void addValueChangedListeners(FieldValueChanged listener) {
		for (IRunnerElement child : children) {
			child.addValueChangedListeners(listener);
		}
	}

	@Override
	public void setTabIndex(int tabIndex) {
		// Does not apply
	}

	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		Iterator<Component> itr = groupElementsLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			component.setLocale(locale);
		}

		itr = cloneLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			component.setLocale(locale);
		}
	}
}
