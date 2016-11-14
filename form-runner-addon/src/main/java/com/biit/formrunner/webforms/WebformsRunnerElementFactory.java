package com.biit.formrunner.webforms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.biit.form.entity.TreeObject;
import com.biit.form.result.QuestionWithValueResult;
import com.biit.formrunner.common.IRunnerElement;
import com.biit.formrunner.common.ImagePreview;
import com.biit.formrunner.common.NumberLocaleModifier;
import com.biit.formrunner.common.Runner;
import com.biit.formrunner.common.RunnerDateField;
import com.biit.formrunner.common.RunnerElementWithImage;
import com.biit.formrunner.common.RunnerField;
import com.biit.formrunner.common.RunnerImage;
import com.biit.formrunner.common.RunnerSelection;
import com.biit.formrunner.common.RunnerStaticField;
import com.biit.formrunner.common.RunnerTextArea;
import com.biit.formrunner.common.validators.DoubleValidator;
import com.biit.formrunner.common.validators.LongValidator;
import com.biit.webforms.persistence.entity.Answer;
import com.biit.webforms.persistence.entity.ElementWithImage;
import com.biit.webforms.persistence.entity.Question;
import com.biit.webforms.persistence.entity.SystemField;
import com.biit.webforms.persistence.entity.Text;
import com.biit.webforms.utils.math.domain.range.RealRangeDouble;
import com.biit.webforms.utils.math.domain.range.RealRangeLong;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

public class WebformsRunnerElementFactory {

	private static final String HORIZONTAL_STYLE = "horizontal";

	/**
	 * If the element has an image, create a composed layout of the element plus
	 * the image. If not, call generate(element, runner);
	 * 
	 * @param element
	 * @param runner
	 * @return
	 */
	public static IRunnerElement generateElementWithImage(TreeObject element, Runner runner) {
		if (element == null) {
			return null;
		}
		if (element != null && element instanceof ElementWithImage && ((ElementWithImage) element).getImage() != null) {
			IRunnerElement elementComponent = generate(element, runner);
			IRunnerElement imageComponent = generateImage(element, runner);
			if (imageComponent != null) {
				return new RunnerElementWithImage(elementComponent, (RunnerImage) imageComponent, runner);
			}
		}
		// No image defined.
		return generate(element, runner);
	}

	private static IRunnerElement generateImage(TreeObject element, Runner runner) {
		if (element != null && element instanceof ElementWithImage && ((ElementWithImage) element).getImage() != null) {
			ImagePreview imagePreview = new ImagePreview();
			imagePreview.setSizeFull();
			imagePreview.setVisible(true);
			imagePreview.setClickEnabled(false);

			final ByteArrayOutputStream imageMemoryOutputStream = ((ElementWithImage) element).getImage().getStream();
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
			imagePreview.setWidth(((ElementWithImage) element).getImage().getWidth(), Unit.PIXELS);
			imagePreview.setHeight(((ElementWithImage) element).getImage().getHeight(), Unit.PIXELS);

			RunnerImage image = new RunnerImage(((ElementWithImage) element).getImage().getFileName(), imagePreview, runner, element.getPath());
			image.setWidth(((ElementWithImage) element).getImage().getWidth(), Unit.PIXELS);
			image.setHeight(((ElementWithImage) element).getImage().getHeight(), Unit.PIXELS);

			return image;
		}
		return null;
	}

	public static IRunnerElement generate(TreeObject element, Runner runner) {
		if (element instanceof Question) {
			return generateQuestion((Question) element, runner);
		} else if (element instanceof Text) {
			return generateText((Text) element, runner);
		} else if (element instanceof SystemField) {
			return generateSystemField((SystemField) element, runner);
		}

		throw new UnsupportedOperationException("TreeObject '" + element.getName() + "' is of an unsupported type '" + element.getClass().getName() + "'");
	}

	private static IRunnerElement generateQuestion(Question element, Runner runner) {
		switch (element.getAnswerType()) {
		case INPUT:
			return generateInputField(element, runner);
		case TEXT_AREA:
			return new RunnerTextArea(element.getName(), element.getLabel(), element.getDescription(), runner.getRequiredCaption(), element.isMandatory(),
					element.getDefaultValue(), runner, element.getPath());
		case SINGLE_SELECTION_LIST:
			return generateComboboxList(element, runner);
		case SINGLE_SELECTION_RADIO:
			return generateSelectionList(element, false, runner);
		case MULTIPLE_SELECTION:
			return generateSelectionList(element, true, runner);
		}
		throw new UnsupportedOperationException("Question '" + element.getName() + "' has an unsupported answer type '" + element.getAnswerType() + "'");
	}

	private static void addAnswersToSelection(Question element, AbstractSelect selectionList) {
		for (TreeObject child : element.getChildren()) {
			if (!(child instanceof Answer)) {
				continue;
			}
			Answer answer = (Answer) child;
			selectionList.addItem(answer.getName());
			// Embed images as base64.
			if (child instanceof ElementWithImage && ((ElementWithImage) child).getImage() != null) {
				selectionList.setItemCaption(answer.getName(),
						(answer.getLabel().length() > 0 ? "<div><span style=\"vertical-align: top\">" + answer.getLabel() + "</span></div>" : "")
								+ "<img style=\"vertical-align:middle;display:inline;\" height=\"" + ((ElementWithImage) child).getImage().getHeight()
								+ "\" width=\"" + ((ElementWithImage) child).getImage().getWidth() + "\" alt=\""
								+ ((ElementWithImage) child).getImage().getFileName() + "\" src=\"data:image/png;base64,"
								+ ((ElementWithImage) child).getImage().toBase64() + "\" />");
			} else {
				selectionList.setItemCaption(answer.getName(), answer.getLabel());
			}
		}
	}

	private static IRunnerElement generateSelectionList(Question element, boolean isMultiselect, Runner runner) {
		String requiredCaption = runner.getRequiredCaption();
		OptionGroup option = new OptionGroup(element.getLabel());
		option.setMultiSelect(isMultiselect);
		addAnswersToSelection(element, option);
		if (element.isHorizontal()) {
			option.addStyleName(HORIZONTAL_STYLE);
		}
		if (element.getDefaultValueAnswer() != null) {
			if (!isMultiselect) {
				option.setValue(element.getDefaultValueAnswer().getName());
			} else {
				Set<Object> values = new HashSet<>();
				values.add(element.getDefaultValueAnswer().getName());
				option.setValue(values);
			}
		}
		// Allow to embed images if any answer has an image.
		for (TreeObject child : element.getChildren()) {
			if (child instanceof ElementWithImage && ((ElementWithImage) child).getImage() != null) {
				option.setHtmlContentAllowed(true);
			}
		}
		return new RunnerSelection<OptionGroup>(element.getName(), option, element.getDescription(), element.isMandatory(), requiredCaption, runner,
				element.getPath());
	}

	private static IRunnerElement generateComboboxList(Question element, Runner runner) {
		String requiredCaption = runner.getRequiredCaption();

		ComboBox combobox = new ComboBox(element.getLabel());
		addAnswersToSelection(element, combobox);
		if (element.getDefaultValueAnswer() != null) {
			combobox.setValue(element.getDefaultValueAnswer().getName());
		}
		return new RunnerSelection<ComboBox>(element.getName(), combobox, element.getDescription(), element.isMandatory(), requiredCaption, runner,
				element.getPath());
	}

	private static IRunnerElement generateInputField(Question element, Runner runner) {
		String requiredCaption = runner.getRequiredCaption();
		switch (element.getAnswerFormat()) {
		case TEXT:
			TextField textField = new TextField(element.getLabel());
			textField.setValue(element.getDefaultValue());
			textField.setMaxLength(QuestionWithValueResult.MAX_LENGTH);
			switch (element.getAnswerSubformat()) {
			case EMAIL:
				textField.addValidator(new RegexpValidator(runner.getEmailRegex(), requiredCaption));
				break;
			case PHONE:
				textField.addValidator(new RegexpValidator(runner.getPhoneRegex(), requiredCaption));
				break;
			default:
				textField.addValidator(new RegexpValidator(runner.getTextRegex(), requiredCaption));
				break;
			}
			return new RunnerField<TextField>(element.getName(), textField, element.getDescription(), element.isMandatory(), requiredCaption, runner,
					element.getPath());
		case DATE:
			RunnerDateField field = new RunnerDateField(element.getName(), element.getLabel(), element.getDescription(), element.isMandatory(), requiredCaption, runner,
					element.getPath());
			//field.setCaption(element.getLabel());
			if (element.getDefaultValueTime() != null) {
				field.getComponent().setValue(element.getDefaultValueTime());
			}
			try {
				SimpleDateFormat sdf = new SimpleDateFormat();
				field.getComponent().setValue(sdf.parse(element.getDefaultValue()));
			} catch (ParseException e) {
				// ignore
			}
			return field;
		case NUMBER:
			TextField numberField = new TextField(element.getLabel());
			numberField.setValue(element.getDefaultValue());
			numberField.setMaxLength(QuestionWithValueResult.MAX_LENGTH);
			RunnerField<?> runnerNumberField = new RunnerField<TextField>(element.getName(), numberField, element.getDescription(), element.isMandatory(),
					requiredCaption, runner, element.getPath());
			switch (element.getAnswerSubformat()) {
			case NUMBER:
				numberField.addValidator(new LongValidator(runner.getInvalidCaption(), RealRangeLong.fullRange()));
				break;
			case POSITIVE_NUMBER:
				numberField.addValidator(new LongValidator(runner.getInvalidCaption(), RealRangeLong.positiveRange()));
				break;
			case NEGATIVE_NUMBER:
				numberField.addValidator(new LongValidator(runner.getInvalidCaption(), RealRangeLong.negativeRange()));
				break;
			case FLOAT:
				numberField.addValidator(new DoubleValidator(runner.getLocale(), runner.getInvalidCaption(), RealRangeDouble.fullRange()));
				runnerNumberField.setValueModifier(new NumberLocaleModifier(runner.getLocale(), Locale.ENGLISH));
				break;
			case POSITIVE_FLOAT:
				numberField.addValidator(new DoubleValidator(runner.getLocale(), runner.getInvalidCaption(), RealRangeDouble.positiveRange()));
				runnerNumberField.setValueModifier(new NumberLocaleModifier(runner.getLocale(), Locale.ENGLISH));
				break;
			case NEGATIVE_FLOAT:
				numberField.addValidator(new DoubleValidator(runner.getLocale(), runner.getInvalidCaption(), RealRangeDouble.negativeRange()));
				runnerNumberField.setValueModifier(new NumberLocaleModifier(runner.getLocale(), Locale.ENGLISH));
				break;
			default:
				// Do nothing
			}
			return runnerNumberField;
		case POSTAL_CODE:
			TextField postalCodeField = new TextField(element.getLabel());
			postalCodeField.setValue(element.getDefaultValue());
			postalCodeField.setMaxLength(QuestionWithValueResult.MAX_LENGTH);
			postalCodeField.addValidator(new RegexpValidator(runner.getPostalCodeRegex(), runner.getInvalidCaption()));
			return new RunnerField<TextField>(element.getName(), postalCodeField, element.getDescription(), element.isMandatory(), requiredCaption, runner,
					element.getPath());
		}
		throw new UnsupportedOperationException("Question '" + element.getName() + "' has an unsupported answer format '" + element.getAnswerFormat() + "'");
	}

	private static IRunnerElement generateSystemField(SystemField element, Runner runner) {
		Label label = new Label(element.getName());
		return new RunnerStaticField(element.getName(), label, runner, element.getPath());
	}

	private static IRunnerElement generateText(Text element, Runner runner) {
		Label label = new Label(element.getLabel());
		return new RunnerStaticField(element.getName(), label, runner, element.getPath());
	}

}
