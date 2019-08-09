package com.biit.formrunner.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.biit.form.entity.BaseGroup;
import com.biit.form.entity.TreeObject;
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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class RunnerTable extends CustomComponent implements IRunnerElement {

	private static final long serialVersionUID = -2484037913536806393L;

	private static final String CLASSNAME = "v-form-runner-table";
	private static final String TABLESTYLENAME = "vFormRunnerElementTable";

	private static final String FULL = "100%";

	private final String name;

	private final VerticalLayout rootLayout;
	private final VerticalLayout cloneLayout;
	private final GridLayout tableElementsLayout;

	private final Button cloneButton;
	private final List<String> path;
	private final List<IRunnerElement> children;
	private final BaseGroup group;

	private ImagePreview imagePreview;

	private boolean relevance;
	private boolean hiddenElement = false;

	public RunnerTable(String name, List<String> path, BaseGroup group) {
		super();
		setWidth(FULL);
		setHeightUndefined();
		setStyleName(CLASSNAME);

		this.name = name;
		this.path = path;
		this.children = new ArrayList<>();
		this.group = group;

		rootLayout = new VerticalLayout();
		cloneLayout = new VerticalLayout();

		tableElementsLayout = new GridLayout(getColumns(group), getRows(group));
		tableElementsLayout.addStyleName(TABLESTYLENAME);
		configureTableLabels(group);
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
			// groupElementsLayout.addComponent(element);
		}
	}

	public void addElementToTable(IRunnerElement element, int column, int row) {
		if (element != null) {
			element.setWidth("150px");
			children.add(element);
			tableElementsLayout.addComponent(element, column, row);
		}
	}

	public void addLabelToTable(Label element, int column, int row) {
		if (element != null) {
			tableElementsLayout.addComponent(element, column, row);
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

	private int getRows(BaseGroup group) {
		int rows = 1;
		List<TreeObject> groupChildren = group.getChildren();
		for (int i = 0; i < groupChildren.size(); i++) {
			rows++;
		}
		return rows;
	}

	private int getColumns(BaseGroup group) {
		int columns = 1;
		for (TreeObject child : group.getChildren()) {
			List<TreeObject> groupChildren = child.getChildren();
			for (int i = 0; i < groupChildren.size(); i++) {
				columns++;
			}
		}
		return columns;
	}

	private void configureTableLabels(BaseGroup group) {
		int row = 1;
		for (TreeObject child : group.getChildren()) {
			Label groupName = new Label(child.getLabel());
			groupName.setWidth("80px");
			addLabelToTable(groupName, 0, row);
			row++;
		}
		if (!group.getChildren().isEmpty()) {
			TreeObject children = group.getChildren().get(0);
			int column = 1;
			for (TreeObject question : children.getChildren()) {
				Label questionLabel = new Label(question.getLabel());
				addLabelToTable(questionLabel, column, 0);
				column++;
			}
		}
	}

	private void configureLayouts() {
		rootLayout.setWidth(FULL);
		rootLayout.setHeightUndefined();
		rootLayout.setMargin(true);
		rootLayout.setSpacing(true);

		tableElementsLayout.setWidth(FULL);
		tableElementsLayout.setHeightUndefined();
		tableElementsLayout.setMargin(false);
		tableElementsLayout.setSpacing(true);

		cloneLayout.setWidth(FULL);
		cloneLayout.setHeightUndefined();
		cloneLayout.setMargin(false);

		imagePreview.setSizeFull();
		imagePreview.setVisible(true);
		imagePreview.setClickEnabled(false);
		rootLayout.addComponent(imagePreview);

		rootLayout.addComponent(tableElementsLayout);
		rootLayout.addComponent(cloneLayout);

		rootLayout.addComponent(cloneButton);
		rootLayout.setComponentAlignment(cloneButton, Alignment.BOTTOM_RIGHT);
	}

	@Override
	public List<Result> getAnswers() {
		List<Result> answers = new ArrayList<Result>();
		List<Result> rowGroupAnswers = new ArrayList<Result>();
		ResultGroup tableAnswers = new ResultGroup(this.group.getName());
		int row = 1;
		for (TreeObject child : this.group.getChildren()) {
			if (child instanceof BaseGroup) {
				int column = 1;
				ResultGroup rowAnswers = new ResultGroup(this.group.getChildren().get(row - 1).getName());
				for (int currentColumn = 0; currentColumn <= child.getChildren().size(); currentColumn++) {
					Component tableComponent = tableElementsLayout.getComponent(column, row);
					if (tableComponent instanceof IRunnerElement) {
						IRunnerElement component = (IRunnerElement) tableComponent;
						if (component.getRelevance()) {
							rowAnswers.addAnswers(component.getAnswers());
						}
					}
					column++;
				}
				rowGroupAnswers.add(rowAnswers);
				row++;
			}
		}
		tableAnswers.addAnswers(rowGroupAnswers);
		answers.add(tableAnswers);

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
		Iterator<Component> itr = tableElementsLayout.iterator();
		while (itr.hasNext()) {
			if (itr.next() instanceof IRunnerElement) {
				IRunnerElement component = (IRunnerElement) itr.next();
				component.clear();
			}
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
		Iterator<Component> itr = tableElementsLayout.iterator();
		while (itr.hasNext()) {
			if (itr.next() instanceof IRunnerElement) {
				IRunnerElement component = (IRunnerElement) itr.next();
				valid = valid && component.isValid();
			}
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
		if (path.size() == 2) {
			return (IRunnerElement) tableElementsLayout.getComponent(getColumn(path.get(1)), getRow(path.get(0)));
		} else if (path.size() == 1) {
			return (RunnerTable) this;
		}
		throw new PathDoesNotExist(path);
	}

	@Override
	public void setRelevance(boolean value) {
		relevance = value;
		setVisible(value && !hiddenElement);
	}

	@Override
	public List<String> getPath() {
		return path;
	}

	@Override
	public boolean getRelevance() {
		return relevance;
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
	 * irrelevant.
	 */
	public void checkRelevance() {
		Iterator<Component> itr = tableElementsLayout.iterator();
		while (itr.hasNext()) {
			Component element = itr.next();
			if (element instanceof IRunnerElement) {
				((IRunnerElement) element).setRelevance(true);
				setRelevance(true);
			}
		}
		// setRelevance(false);
	}

	public void checkIsHiddenElement() {
		Iterator<Component> itr = tableElementsLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement element = (IRunnerElement) itr.next();
			if (!element.isHiddenElement()) {
				setHiddenElement(false);
				return;
			}
		}
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
		Iterator<Component> itr = tableElementsLayout.iterator();
		while (itr.hasNext()) {
			Component element = itr.next();
			if (element instanceof IRunnerElement) {
				IRunnerElement component = (IRunnerElement) element;
				component.setLocale(locale);
			}
		}

		itr = cloneLayout.iterator();
		while (itr.hasNext()) {
			IRunnerElement component = (IRunnerElement) itr.next();
			component.setLocale(locale);
		}
	}

	private int getRow(String groupName) throws PathDoesNotExist {
		int row = 1;
		for (TreeObject child : this.group.getChildren()) {
			if (child.getName().equals(groupName)) {
				return row;
			}
			row++;
		}
		throw new PathDoesNotExist(path);
	}

	private int getColumn(String questionName) throws PathDoesNotExist {
		for (TreeObject child : group.getChildren()) {
			if (child instanceof BaseGroup) {
				int column = 1;
				for (TreeObject question : child.getChildren()) {
					if (question.getName().equals(questionName)) {
						return column;
					}
					column++;
				}
			}
		}
		throw new PathDoesNotExist(path);
	}

	public boolean isHiddenElement() {
		return hiddenElement;
	}

	public void setHiddenElement(boolean hiddenElement) {
		this.hiddenElement = hiddenElement;
		if (hiddenElement) {
			setVisible(false);
		}
	}

}
