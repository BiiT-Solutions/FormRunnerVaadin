package com.biit.formrunner.orbeon;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.biit.form.runner.logger.FormRunnerLogger;
import com.biit.utils.configuration.SystemVariableTextSourceFile;

public class OrbeonFormRunnerEquivalenceConfigReader {
	private final static String ORBEON_FILE = "orbeon.xml";
	private static final String SYSTEM_VARIABLE_CONFIG = "ORBEON_MATCHER_CONFIG";

	private final static String XML_QUESTION_NODE = "question";
	private final static String ORBEON_PATH_NODE = "orbeon";
	private final static String FORM_RUNNER_PATH_NODE = "formRunner";
	private final static String OPERATOR_NODE = "operator";
	private final static String PRIORITY_NODE = "priority";

	private final static String XML_TRANSLATIONS_NODE = "translation";
	private final static String TECHNICAL_NAME_NODE = "techincalName";
	private final static String TECHNICAL_NAME_TEXT = "text";

	public static OrbeonFormRunnerMatcher readConfig() {
		Set<OrbeonFormRunnerEquivalence> configuration = new HashSet<>();

		try {
			// String xmlText = FileReader.getResource(ORBEON_FILE,
			// StandardCharsets.UTF_8);
			SystemVariableTextSourceFile xmlFileReader = new SystemVariableTextSourceFile(SYSTEM_VARIABLE_CONFIG, ORBEON_FILE);
			String xmlText = xmlFileReader.loadFile();
			SAXReader xmlReader = new SAXReader();
			final Document document = xmlReader.read(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
			final Element formElement = document.getRootElement();

			// Add all question maps.
			for (Iterator<?> formChildren = formElement.elementIterator(XML_QUESTION_NODE); formChildren.hasNext();) {
				final Element questionElement = (Element) formChildren.next();
				String orbenPath = questionElement.attributeValue(ORBEON_PATH_NODE);
				String formRunnerPath = questionElement.attributeValue(FORM_RUNNER_PATH_NODE);
				String operator = questionElement.attributeValue(OPERATOR_NODE);
				OrbeonFormRunnerEquivalence orbeonFormRunnerEquivalence = new OrbeonFormRunnerEquivalence(orbenPath, formRunnerPath, operator);
				try {
					Integer priority = Integer.parseInt(questionElement.attributeValue(PRIORITY_NODE));
					orbeonFormRunnerEquivalence.setPriority(priority);
				} catch (Exception e) {

				}

				// Add all translations maps.
				Map<String, String> translations = new HashMap<>();
				for (Iterator<?> translationChildren = questionElement.elementIterator(XML_TRANSLATIONS_NODE); translationChildren.hasNext();) {
					final Element translationElement = (Element) translationChildren.next();
					String technicalName = translationElement.attributeValue(TECHNICAL_NAME_NODE);
					String text = translationElement.attributeValue(TECHNICAL_NAME_TEXT);
					translations.put(technicalName, text);
				}
				orbeonFormRunnerEquivalence.setTranslations(translations);

				configuration.add(orbeonFormRunnerEquivalence);
			}

		} catch (FileNotFoundException fnf) {
			FormRunnerLogger.warning(OrbeonFormRunnerEquivalenceConfigReader.class.getName(), "Orbeon equivalence with Form Runner file not found!");
		} catch (UnsupportedEncodingException | DocumentException e) {
			FormRunnerLogger.errorMessage(OrbeonFormRunnerEquivalenceConfigReader.class.getName(), e);
		}
		OrbeonFormRunnerMatcher orbeonFormRunnerMatcher = new OrbeonFormRunnerMatcher();
		orbeonFormRunnerMatcher.setEquivalences(configuration);
		return orbeonFormRunnerMatcher;
	}
}
