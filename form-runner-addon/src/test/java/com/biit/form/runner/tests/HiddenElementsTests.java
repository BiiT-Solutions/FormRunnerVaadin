package com.biit.form.runner.tests;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.form.exceptions.CharacterNotAllowedException;
import com.biit.form.exceptions.ElementIsReadOnly;
import com.biit.form.exceptions.NotValidChildException;
import com.biit.form.exceptions.TooManyResultsFoundException;
import com.biit.form.result.FormResult;
import com.biit.form.result.QuestionWithValueResult;
import com.biit.form.runner.common.exceptions.PathDoesNotExist;
import com.biit.form.runner.mock.TestFormRunner;
import com.biit.persistence.entity.exceptions.FieldTooLongException;
import com.biit.utils.file.FileReader;
import com.biit.webforms.persistence.entity.Form;
import com.biit.webforms.persistence.entity.Group;
import com.biit.webforms.persistence.entity.Question;
import com.biit.webforms.persistence.entity.SystemField;

@Test(groups = "hiddenElements")
public class HiddenElementsTests {

	@Test
	public void readFormAndCheckFirstElement()
			throws FileNotFoundException, TooManyResultsFoundException, PathDoesNotExist {
		String jsonString = FileReader.getResource("EnergyBalance.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);
		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);
		Assert.assertTrue(
				formRunner.getElement(form.getChild(Question.class, "profile_first_name").getPath()).getRelevance());
		Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "SleepTime").getPath()).getRelevance());
	}

	@Test
	public void readFormAndCheckFirstElementNotHidden()
			throws FileNotFoundException, TooManyResultsFoundException, PathDoesNotExist {
		String jsonString = FileReader.getResource("EnergyBalance_personalHidden.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);
		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);
		// Previous element is a system field and is not hidden.
		Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "SleepTime").getPath()).getRelevance());
	}

	@Test
	public void readSliderWithInOperator() throws FileNotFoundException, PathDoesNotExist, TooManyResultsFoundException,
			NotValidChildException, ElementIsReadOnly, FieldTooLongException, CharacterNotAllowedException {
		String jsonString = FileReader.getResource("SliderInTest.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);

		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);

		// Slider value between 1 and 5, first question visible.
		Question question = form.getChild(Question.class, "question");
		formRunner.setAnswers(question.getPath(), Arrays.asList(new String[] { "3" }));
		formRunner.evaluate();

		Assert.assertFalse(
				formRunner.getElement(form.getChild(Question.class, "question610").getPath()).getRelevance());
		Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "question15").getPath()).getRelevance());
	}

	@Test
	public void checkFormulasExists() throws FileNotFoundException, PathDoesNotExist, TooManyResultsFoundException,
			NotValidChildException, ElementIsReadOnly, FieldTooLongException, CharacterNotAllowedException {
		String jsonString = FileReader.getResource("EnergyBalance.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);

		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);

		// No flow defined.
		Question question = form.getChild(Question.class, "EnergyBalance");
		formRunner.setAnswers(question.getPath(), Arrays.asList(new String[] { "1" }));
		formRunner.evaluate();

		Assert.assertFalse(
				formRunner.getElement(form.getChild(Question.class, "EnergyBalance").getPath()).getRelevance());
		Assert.assertFalse(formRunner.getElement(form.getChild(Question.class, "EnergyBalance").getPath()).isVisible());

		// Check Formulas category has values.
		FormResult formResult = formRunner.getFormResult();
		Assert.assertNotNull(formResult);
		Assert.assertNotNull(((QuestionWithValueResult) formRunner.getFormResult().getChild(question.getPath())));
	}

	@Test
	public void checkFormulasAreExported()
			throws FileNotFoundException, PathDoesNotExist, TooManyResultsFoundException {
		String jsonString = FileReader.getResource("LEC VO2MAX.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);

		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);

		// No flow defined.
		Question question = form.getChild(Question.class, "Type");
		formRunner.setAnswers(question.getPath(), Arrays.asList(new String[] { "Treadmill" }));
		formRunner.evaluate();

		formRunner.evaluate();

		String formResultAsJson = formRunner.getFormResult().toJson();
		// Check formulas category is there and also its questions with default values.
		Assert.assertTrue(formResultAsJson.contains("\"name\": \"Formulas\""));
		Assert.assertTrue(formResultAsJson.contains("\"name\": \"MetWaarde\""));
		Assert.assertTrue(formResultAsJson.contains("\"name\": \"MaximumVO2\""));
	}

	@Test
	public void checkSystemFieldsAndFlow() throws FileNotFoundException, PathDoesNotExist, TooManyResultsFoundException,
			NotValidChildException, ElementIsReadOnly, FieldTooLongException, CharacterNotAllowedException {
		String jsonString = FileReader.getResource("LEC VO2MAX.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);

		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);

		// No flow defined.
		Question question = form.getChild(Question.class, "Type");
		formRunner.setAnswers(question.getPath(), Arrays.asList(new String[] { "Treadmill" }));
		formRunner.evaluate();

		Assert.assertTrue(formRunner.getElement(form.getChild(Group.class, "Treadmill").getPath()).getRelevance());
		Assert.assertTrue(formRunner.getElement(form.getChild(Group.class, "Treadmill").getPath()).isVisible());
		Assert.assertFalse(formRunner.getElement(form.getChild(Group.class, "Bike").getPath()).getRelevance());
		Assert.assertFalse(formRunner.getElement(form.getChild(Group.class, "Bike").getPath()).isVisible());
		Assert.assertFalse(formRunner.getElement(form.getChild(SystemField.class, "Age").getPath()).isVisible());
	}

	@Test
	public void checkSliderHasNoDefaultValues()
			throws FileNotFoundException, PathDoesNotExist, TooManyResultsFoundException {
		String jsonString = FileReader.getResource("LEC PSK.json", Charset.defaultCharset());
		Form form = Form.fromJson(jsonString);

		TestFormRunner formRunner = new TestFormRunner();
		formRunner.loadForm(form);

		// No flow defined.
		Question question = form.getChild(Question.class, "walking");
		Assert.assertNotNull(question);
		formRunner.setAnswers(question.getPath(), Arrays.asList(new String[] { "climbing-stairs" }));
		formRunner.evaluate();

		Assert.assertTrue(
				formRunner.getElement(form.getChild(Question.class, "climbingStairs").getPath()).getRelevance());
		Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "climbingStairs").getPath()).isVisible());

		Assert.assertFalse(
				formRunner.getElement(form.getChild(Question.class, "walkOutdoorsNoLevel").getPath()).getRelevance());
		Assert.assertFalse(
				formRunner.getElement(form.getChild(Question.class, "walkOutdoorsNoLevel").getPath()).isVisible());

		// System.out.println(formRunner.getFormResult().toJson());
	}

}
