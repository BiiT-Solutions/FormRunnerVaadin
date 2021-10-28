package com.biit.form.runner.tests;

import com.biit.form.exceptions.TooManyResultsFoundException;
import com.biit.form.result.FormResult;
import com.biit.form.runner.common.exceptions.PathDoesNotExist;
import com.biit.form.runner.mock.TestFormRunner;
import com.biit.webforms.persistence.entity.Form;
import com.biit.webforms.persistence.entity.Group;
import com.biit.webforms.persistence.entity.Question;
import com.biit.webforms.persistence.entity.SystemField;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@Test(groups = "hiddenElements")
public class HiddenElementsTests {

    @Test
    public void readFormAndCheckFirstElement()
            throws IOException, TooManyResultsFoundException, PathDoesNotExist, URISyntaxException {
        String jsonString = readFileFromResources("EnergyBalance.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);
        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);
        Assert.assertTrue(
                formRunner.getElement(form.getChild(Question.class, "profile_first_name").getPath()).getRelevance());
        Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "SleepTime").getPath()).getRelevance());
    }

    @Test
    public void readFormAndCheckFirstElementNotHidden()
            throws IOException, TooManyResultsFoundException, PathDoesNotExist, URISyntaxException {
        String jsonString = readFileFromResources("EnergyBalance_personalHidden.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);
        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);
        // Previous element is a system field and is not hidden.
        Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "SleepTime").getPath()).getRelevance());
    }

    @Test
    public void readSliderWithInOperator() throws IOException, PathDoesNotExist, TooManyResultsFoundException, URISyntaxException {
        String jsonString = readFileFromResources("SliderInTest.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);
        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);

        // Slider value between 1 and 5, first question visible.
        Question question = form.getChild(Question.class, "question");
        formRunner.setAnswers(question.getPath(), Collections.singletonList("3"));
        formRunner.evaluate();

        Assert.assertFalse(
                formRunner.getElement(form.getChild(Question.class, "question610").getPath()).getRelevance());
        Assert.assertTrue(formRunner.getElement(form.getChild(Question.class, "question15").getPath()).getRelevance());
    }

    @Test
    public void checkFormulasExists() throws IOException, PathDoesNotExist, TooManyResultsFoundException, URISyntaxException {
        String jsonString = readFileFromResources("EnergyBalance.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);

        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);

        // No flow defined.
        Question question = form.getChild(Question.class, "EnergyBalance");
        formRunner.setAnswers(question.getPath(), Collections.singletonList("1"));
        formRunner.evaluate();

        Assert.assertFalse(
                formRunner.getElement(form.getChild(Question.class, "EnergyBalance").getPath()).getRelevance());
        Assert.assertFalse(formRunner.getElement(form.getChild(Question.class, "EnergyBalance").getPath()).isVisible());

        // Check Formulas category has values.
        FormResult formResult = formRunner.getFormResult();
        Assert.assertNotNull(formResult);
        Assert.assertNotNull((formRunner.getFormResult().getChild(question.getPath())));
    }

    @Test
    public void checkFormulasAreExported()
            throws IOException, PathDoesNotExist, TooManyResultsFoundException, URISyntaxException {
        String jsonString = readFileFromResources("LEC VO2MAX.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);

        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);

        // No flow defined.
        Question question = form.getChild(Question.class, "Type");
        formRunner.setAnswers(question.getPath(), Collections.singletonList("Treadmill"));
        formRunner.evaluate();

        formRunner.evaluate();

        String formResultAsJson = formRunner.getFormResult().toJson();
        // Check formulas category is there and also its questions with default values.
        Assert.assertTrue(formResultAsJson.contains("\"name\": \"Formulas\""));
        Assert.assertTrue(formResultAsJson.contains("\"name\": \"MetWaarde\""));
        Assert.assertTrue(formResultAsJson.contains("\"name\": \"MaximumVO2\""));
    }

    @Test
    public void checkSystemFieldsAndFlow() throws IOException, PathDoesNotExist, TooManyResultsFoundException, URISyntaxException {
        String jsonString = readFileFromResources("LEC VO2MAX.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);

        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);

        // No flow defined.
        Question question = form.getChild(Question.class, "Type");
        formRunner.setAnswers(question.getPath(), Collections.singletonList("Treadmill"));
        formRunner.evaluate();

        Assert.assertTrue(formRunner.getElement(form.getChild(Group.class, "Treadmill").getPath()).getRelevance());
        Assert.assertTrue(formRunner.getElement(form.getChild(Group.class, "Treadmill").getPath()).isVisible());
        Assert.assertFalse(formRunner.getElement(form.getChild(Group.class, "Bike").getPath()).getRelevance());
        Assert.assertFalse(formRunner.getElement(form.getChild(Group.class, "Bike").getPath()).isVisible());
        Assert.assertFalse(formRunner.getElement(form.getChild(SystemField.class, "Age").getPath()).isVisible());
    }

    @Test
    public void checkSliderHasNoDefaultValues()
            throws IOException, PathDoesNotExist, TooManyResultsFoundException, URISyntaxException {
        String jsonString = readFileFromResources("LEC PSK.json");
        Assert.assertTrue(jsonString.length() > 0);
        Form form = Form.fromJson(jsonString);

        TestFormRunner formRunner = new TestFormRunner();
        formRunner.loadForm(form);

        // No flow defined.
        Question question = form.getChild(Question.class, "walking");
        Assert.assertNotNull(question);
        formRunner.setAnswers(question.getPath(), Collections.singletonList("climbing-stairs"));
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

    public static String readFileFromResources(String filename) throws URISyntaxException, IOException {
        URL resource = HiddenElementsTests.class.getClassLoader().getResource(filename);
        byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
        return new String(bytes);
    }

}
