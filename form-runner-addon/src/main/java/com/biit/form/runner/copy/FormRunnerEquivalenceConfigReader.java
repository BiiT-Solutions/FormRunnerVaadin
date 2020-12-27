package com.biit.form.runner.copy;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import com.biit.utils.file.FileReader;

public class FormRunnerEquivalenceConfigReader {
    private static final String SYSTEM_VARIABLE_CONFIG = "INTAKE_MATCHER_CONFIG";

    private final static String XML_ROOT_NODE = "equivalence";
    private final static String SOURCE_PATH_NODE = "source";
    private final static String FORM_RUNNER_PATH_NODE = "destination";
    private final static String OPERATOR_NODE = "operator";
    private final static String PRIORITY_NODE = "priority";

    private final static String XML_TRANSLATIONS_NODE = "translation";
    private final static String TECHNICAL_NAME_NODE = "technicalName";
    private final static String TECHNICAL_NAME_TEXT = "text";

    public static Set<FormRunnerEquivalence> readConfig(String fileName) {
        Set<FormRunnerEquivalence> configuration = new HashSet<>();

        try {
            String xmlText = null;
            try {
                SystemVariableTextSourceFile xmlFileReader = new SystemVariableTextSourceFile(SYSTEM_VARIABLE_CONFIG, fileName);
                xmlText = xmlFileReader.loadFile();
            } catch (FileNotFoundException fnf) {
                FormRunnerLogger.warning(FormRunnerEquivalenceConfigReader.class.getName(), "Intake equivalence system variable not found!");
            }
            if (xmlText == null) {
                xmlText = FileReader.getResource(fileName, StandardCharsets.UTF_8);
            }
            SAXReader xmlReader = new SAXReader();
            final Document document = xmlReader.read(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
            final Element formElement = document.getRootElement();

            // Add all question maps.
            for (Iterator<?> formChildren = formElement.elementIterator(XML_ROOT_NODE); formChildren.hasNext(); ) {
                final Element questionElement = (Element) formChildren.next();
                String intakePath = questionElement.attributeValue(SOURCE_PATH_NODE);
                String formRunnerPath = questionElement.attributeValue(FORM_RUNNER_PATH_NODE);
                String operator = questionElement.attributeValue(OPERATOR_NODE);
                IntakeFormRunnerEquivalence intakeFormRunnerEquivalence = new IntakeFormRunnerEquivalence(intakePath, formRunnerPath, operator);
                try {
                    Integer priority = Integer.parseInt(questionElement.attributeValue(PRIORITY_NODE));
                    intakeFormRunnerEquivalence.setPriority(priority);
                } catch (Exception e) {
                    // Do nothing.
                }

                // Add all translations maps.
                Map<String, String> translations = new HashMap<>();
                for (Iterator<?> translationChildren = questionElement.elementIterator(XML_TRANSLATIONS_NODE); translationChildren.hasNext(); ) {
                    final Element translationElement = (Element) translationChildren.next();
                    String technicalName = translationElement.attributeValue(TECHNICAL_NAME_NODE);
                    String text = translationElement.attributeValue(TECHNICAL_NAME_TEXT);
                    translations.put(technicalName, text);
                }
                intakeFormRunnerEquivalence.setTranslations(translations);

                configuration.add(intakeFormRunnerEquivalence);
            }

        } catch (FileNotFoundException fnf) {
            FormRunnerLogger.warning(FormRunnerEquivalenceConfigReader.class.getName(), "Equivalence  file '" + fileName + "' not found!");
        } catch (UnsupportedEncodingException | DocumentException e) {
            FormRunnerLogger.errorMessage(FormRunnerEquivalenceConfigReader.class.getName(), e);
        }
        return configuration;
    }
}
