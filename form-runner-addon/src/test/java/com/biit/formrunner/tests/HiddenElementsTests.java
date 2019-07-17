package com.biit.formrunner.tests;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.form.exceptions.CharacterNotAllowedException;
import com.biit.form.exceptions.ElementIsReadOnly;
import com.biit.form.exceptions.NotValidChildException;
import com.biit.form.exceptions.TooManyResultsFoundException;
import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.biit.formrunner.mock.TestFormRunner;
import com.biit.persistence.entity.exceptions.FieldTooLongException;
import com.biit.utils.file.FileReader;
import com.biit.webforms.persistence.entity.Form;
import com.biit.webforms.persistence.entity.Question;

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
}
