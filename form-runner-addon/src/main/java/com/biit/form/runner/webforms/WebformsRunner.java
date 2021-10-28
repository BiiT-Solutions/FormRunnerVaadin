package com.biit.form.runner.webforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.biit.form.entity.BaseForm;
import com.biit.form.entity.BaseGroup;
import com.biit.form.entity.BaseQuestion;
import com.biit.form.entity.BaseQuestionWithValue;
import com.biit.form.entity.IQuestionWithAnswers;
import com.biit.form.entity.TreeObject;
import com.biit.form.exceptions.CharacterNotAllowedException;
import com.biit.form.exceptions.ElementIsReadOnly;
import com.biit.form.exceptions.NotValidChildException;
import com.biit.form.result.CategoryResult;
import com.biit.form.result.FormResult;
import com.biit.form.result.QuestionWithValueResult;
import com.biit.form.result.RepeatableGroupResult;
import com.biit.form.runner.common.FieldValueChanged;
import com.biit.form.runner.common.IRunnerElement;
import com.biit.form.runner.common.Result;
import com.biit.form.runner.common.ResultGroup;
import com.biit.form.runner.common.ResultQuestion;
import com.biit.form.runner.common.Runner;
import com.biit.form.runner.common.exceptions.PathDoesNotExist;
import com.biit.form.runner.copy.FormRunnerEquivalence;
import com.biit.form.runner.copy.FormRunnerMatcher;
import com.biit.form.runner.logger.FormRunnerLogger;
import com.biit.form.submitted.ISubmittedForm;
import com.biit.persistence.entity.exceptions.FieldTooLongException;
import com.biit.webforms.computed.ComputedFlowView;
import com.biit.webforms.condition.parser.WebformsParser;
import com.biit.webforms.condition.parser.expressions.WebformsExpression;
import com.biit.webforms.persistence.entity.Category;
import com.biit.webforms.persistence.entity.Flow;
import com.biit.webforms.persistence.entity.Form;
import com.biit.webforms.persistence.entity.condition.Token;
import com.biit.webforms.persistence.entity.condition.TokenComparationAnswer;
import com.biit.webforms.persistence.entity.condition.TokenComparationValue;
import com.biit.webforms.persistence.entity.condition.TokenWithQuestion;
import com.biit.webforms.utils.parser.exceptions.EmptyParenthesisException;
import com.biit.webforms.utils.parser.exceptions.ExpectedTokenNotFound;
import com.biit.webforms.utils.parser.exceptions.ExpressionNotWellFormedException;
import com.biit.webforms.utils.parser.exceptions.IncompleteBinaryOperatorException;
import com.biit.webforms.utils.parser.exceptions.MissingParenthesisException;
import com.biit.webforms.utils.parser.exceptions.NoMoreTokensException;
import com.biit.webforms.utils.parser.exceptions.ParseException;
import com.vaadin.ui.UI;

public abstract class WebformsRunner<FormGroup extends IWebformsRunnerGroup> extends Runner implements IWebformsRunner {
    private static final long serialVersionUID = -3424863413256347805L;
    public static final int IMAGE_MINIMUM_WIDTH = 1200;

    private Form form;
    private ComputedFlowView computedFlowView;
    private boolean valueNotSaved = false;

    // Suscribed to get with element has changed.
    private Set<FieldValueChanged> valueChangedListeners = new HashSet<>();

    private int tabIndexDelta;

    @Override
    public void loadForm(Form form) {
        setLoading(true);
        if (form == null) {
            computedFlowView = new ComputedFlowView();
            return;
        }
        this.form = form;
        computedFlowView = form.getComputedFlowsView();
        for (TreeObject child : form.getChildren()) {
            // IWebformsRunnerGroup runnerGroup = new
            // WebformsRunnerGroup((Category) child, this);
            IWebformsRunnerGroup runnerGroup = createWebformsRunnerGroup((Category) child);
            addElement(runnerGroup);
            runnerGroup.addValueChangedListeners(new FieldValueChanged() {

                @Override
                public void valueChanged(IRunnerElement runnerElement) {
                    valueNotSaved = true;
                    // Send changes to any subscribed calls.
                    for (FieldValueChanged valueChangedListener : valueChangedListeners) {
                        valueChangedListener.valueChanged(runnerElement);
                    }
                }
            });
        }

        // Hide all the elements
        List<BaseQuestion> elements = form.getAll(BaseQuestion.class);
        int tabIndex = tabIndexDelta;
        for (TreeObject element : elements) {
            try {
                // Hide element.
                setRelevance(element.getPath(), false);
                setTabIndex(element.getPath(), tabIndex);
                tabIndex++;
            } catch (PathDoesNotExist e) {
                // Not possible.
                FormRunnerLogger.errorMessage(WebformsRunner.class.getName(), e);
                break;
            }
        }

        setLoading(false);

        evaluate();

        // Show image if exists and there is room enough
        if (isImagesEnabled() && UI.getCurrent() != null
                && UI.getCurrent().getPage().getBrowserWindowWidth() >= IMAGE_MINIMUM_WIDTH
                && form.getImage() != null) {
            setImageLayoutVisible();
            setImage(form.getImage());
        } else {
            setImageLayoutInvisible();
        }
    }

    public void evaluate() {
        try {
            evaluate(computedFlowView.getFirstElement().getPath());
        } catch (PathDoesNotExist e) {
            // Not possible.
            FormRunnerLogger.errorMessage(WebformsRunner.class.getName(), e);
        }
    }

    public abstract FormGroup createWebformsRunnerGroup(Category category);

    private void setTabIndex(List<String> path, int tabIndex) throws PathDoesNotExist {
        IRunnerElement element = getElement(path);
        element.setTabIndex(tabIndex);
    }

    @Override
    public void evaluate(List<String> path) throws PathDoesNotExist {
        if (form == null) {
            return;
        }
        TreeObject start = form.getChild(path);
        ArrayList<BaseQuestion> questions = new ArrayList<>(form.getAllChildrenInHierarchy(BaseQuestion.class));
        for (int i = questions.indexOf(start); i < questions.size(); i++) {
            boolean relevance = false;
            // If first question of form relevance is true
            if (computedFlowView.getFirstElement().equals(questions.get(i))) {
                relevance = true;
            } else {
                // Check parents
                Set<Flow> flowsByDestiny = computedFlowView.getFlowsByDestiny(questions.get(i));
                if (flowsByDestiny != null) {
                    for (Flow flow : flowsByDestiny) {
                        // Relevance is true if in previous iteration was true
                        // or if previous element is visible and the flow is valid.
                        relevance = relevance || (getRelevance(flow.getOrigin().getPath()) && checkIsValidFlow(flow));
                        if (relevance) {
                            break;
                        }
                    }
                } else {
                    // Hidden elements has not default flow. But we want that relevance is true and
                    // not visible by property hidden.
                    if (questions.get(i).isHiddenElement()) {
                        relevance = true;
                    }
                }
            }

            // Hidden elements must not been visible.
            relevance = relevance && !questions.get(i).isHiddenElement();

            // Set relevance
            setRelevance(questions.get(i).getPath(), relevance);
        }
    }

    private boolean checkIsValidFlow(Flow flow) throws PathDoesNotExist {
        List<Token> condition = flow.getConditionSimpleTokens();
        if (condition == null || condition.isEmpty()) {
            // Empty condition, valid flow.
            return true;
        }

        for (Token token : condition) {
            if (token instanceof TokenWithQuestion) {
                TokenWithQuestion tokenToEvaluate = (TokenWithQuestion) token;
                List<String> pathToQuestion = tokenToEvaluate.getQuestion().getPath();
                IRunnerElement element = getElement(pathToQuestion);
                if (token instanceof TokenComparationAnswer) {
                    evaluateToken((TokenComparationAnswer) token, element);
                    continue;
                }
                if (token instanceof TokenComparationValue) {
                    evaluateToken((TokenComparationValue) token, element);
                    continue;
                }
            }
        }
        try {
            WebformsExpression expression = (WebformsExpression) (new WebformsParser(condition.iterator()))
                    .parseCompleteExpression();
            Boolean value = expression.evaluate();
            return value != null && value;
        } catch (ParseException | ExpectedTokenNotFound | NoMoreTokensException | IncompleteBinaryOperatorException
                | MissingParenthesisException | ExpressionNotWellFormedException | EmptyParenthesisException e) {
            // If the form is valid this should never happen.
            FormRunnerLogger.errorMessage(this.getClass().getName(), e);
            return false;
        }
    }

    private void evaluateToken(TokenComparationValue token, IRunnerElement element) {
        // If the question is not visible we evaluate with empty list.
        if (!element.getRelevance() || (element.isMandatory() && !element.isFilled())) {
            token.evaluate(null);
            return;
        } else {
            List<Result> answers = element.getAnswers();
            if (!answers.isEmpty()) {
                ResultQuestion answer = (ResultQuestion) answers.get(0);
                token.evaluate(answer.getAnswers().get(0));
            } else {
                // Evaluate as empty
                token.evaluate("");
            }
        }
    }

    private void evaluateToken(TokenComparationAnswer token, IRunnerElement element) {
        // If the question is not visible we evaluate with null or visible but
        // not filled.
        if (!element.getRelevance() || (element.isMandatory() && !element.isFilled())) {
            token.evaluate(null);
            return;
        } else {
            List<Result> answers = element.getAnswers();
            if (!answers.isEmpty()) {
                ResultQuestion answer = (ResultQuestion) answers.get(0);
                token.evaluate(answer.getAnswers());
            } else {
                // Evaluate as empty
                token.evaluate(new ArrayList<String>());
            }
        }
    }

    public void copyValues(ISubmittedForm submittedForm, FormRunnerMatcher formRunnerMatcher) throws PathDoesNotExist {
        if (submittedForm != null) {
            formRunnerMatcher.updateFormAnswers(submittedForm);
            // Stores equivalences according to the answer of the USMO Form
            // Runner
            Map<String, FormRunnerEquivalence> equivalences = new HashMap<>();
            List<IQuestionWithAnswers> questions = submittedForm.getChildren(IQuestionWithAnswers.class);
            for (IQuestionWithAnswers element : questions) {
                IQuestionWithAnswers submittedQuestion = (IQuestionWithAnswers) element;
                // Translate Orbeon path to form runner path.
                Set<FormRunnerEquivalence> equivalencesObtained = formRunnerMatcher
                        .getFormRunnerEquivalences(submittedQuestion);
                for (FormRunnerEquivalence equivalence : equivalencesObtained) {
                    if (equivalence != null) {
                        // Select the correct equivalence filtered by priority.
                        // High
                        // priority must be preferred.
                        if (equivalences.get(equivalence.getDestinationPath()) == null) {
                            equivalences.put(equivalence.getDestinationPath(), equivalence);
                        } else if (equivalences.get(equivalence.getDestinationPath()).getPriority() < equivalence
                                .getPriority()) {
                            equivalences.put(equivalence.getDestinationPath(), equivalence);
                        }
                    }
                }
            }

            FormRunnerLogger.debug(this.getClass().getName(), "Stored Equivalences: " + equivalences + "");
            for (FormRunnerEquivalence equivalence : equivalences.values()) {
                List<String> formRunnerElementPath = equivalence.getDestinationPathAsList();
                if (formRunnerElementPath != null && !formRunnerElementPath.isEmpty()) {
                    // Translate Orbeon answer to Form Runner value.
                    FormRunnerLogger.debug(this.getClass().getName(),
                            "Question '" + equivalence.getDestinationPath()
                                    + "' default value obtained from submitted question '" + equivalence.getSourcePath()
                                    + "'. Value is " + equivalence.getFormRunnerAnswers());
                    setAnswers(formRunnerElementPath, new ArrayList<>(equivalence.getFormRunnerAnswers()));
                } else {
                    FormRunnerLogger.debug(this.getClass().getName(),
                            "Submitted value not applied in examination: '" + equivalence + "'");
                }
            }
        }
    }

    @Override
    public void loadFormResult(FormResult formResult) {
        if (formResult != null) {
            List<BaseQuestionWithValue> questions = formResult.getAll(BaseQuestionWithValue.class);

            for (TreeObject element : questions) {
                BaseQuestionWithValue question = (BaseQuestionWithValue) element;
                try {
                    setAnswers(question.getPath(), question.getQuestionValues());
                } catch (PathDoesNotExist e) {
                    // Element does not exists is due to form restructuration.
                    FormRunnerLogger.debug(this.getClass().getName(), e.getMessage());
                }
            }

            // Evaluate again from the first element.
            try {
                evaluate(computedFlowView.getFirstElement().getPath());
            } catch (PathDoesNotExist e) {
                // Not possible.
                FormRunnerLogger.severe(this.getClass().getName(), "Error in form '" + form + "'.");
                FormRunnerLogger.errorMessage(this.getClass().getName(), e);
            }
        }
    }

    /**
     * Creates a new Form result object from the answers of the form.
     *
     * @return Form Result
     */
    @Override
    public FormResult getFormResult() {
        if (form == null) {
            return null;
        }
        try {
            FormResult result = new FormResult();
            result.setLabel(form.getLabel());
            result.setOrganizationId(form.getOrganizationId());
            result.setVersion(form.getVersion());

            for (Result answerGroup : getAnswers()) {
                addGroupInformation(result, (ResultGroup) answerGroup);
            }

            return result;
        } catch (FieldTooLongException | CharacterNotAllowedException | NotValidChildException | ElementIsReadOnly e) {
            // This should never happen form and form result have the same
            // restriction
            FormRunnerLogger.severe(this.getClass().getName(), "Error in form '" + form + "'.");
            FormRunnerLogger.errorMessage(this.getClass().getName(), e);
            return null;
        }
    }

    private void addGroupInformation(BaseForm result, ResultGroup element)
            throws FieldTooLongException, CharacterNotAllowedException, NotValidChildException, ElementIsReadOnly {
        CategoryResult category = new CategoryResult();
        category.setName(element.getPath());
        result.addChild(category);
        for (Result answerElement : element.getAnswerElements()) {
            addGroupInformation(category, answerElement);
        }
        category.setHiddenElement(form.getChild(category.getName()).isHiddenElement());
    }

    private void addGroupInformation(BaseGroup parentResultGroup, Result answerElement)
            throws FieldTooLongException, CharacterNotAllowedException, NotValidChildException, ElementIsReadOnly {
        // Recalculation of the path.
        List<String> path = new ArrayList<>(parentResultGroup.getPath());
        path.add(answerElement.getPath());

        if (answerElement instanceof ResultQuestion) {
            QuestionWithValueResult resultQuestion = new QuestionWithValueResult();
            resultQuestion.setName(answerElement.getPath());
            resultQuestion.addQuestionValues(((ResultQuestion) answerElement).getAnswerArray());
            parentResultGroup.addChild(resultQuestion);
            resultQuestion.setHiddenElement(form.getChild(path).isHiddenElement());
        } else {
            RepeatableGroupResult resultGroup = new RepeatableGroupResult();
            resultGroup.setName(answerElement.getPath());
            parentResultGroup.addChild(resultGroup);
            for (Result element : ((ResultGroup) answerElement).getAnswerElements()) {
                addGroupInformation(resultGroup, element);
            }
            if (form.getChild(answerElement.getPath()) != null) {
                resultGroup.setHiddenElement(form.getChild(answerElement.getPath()).isHiddenElement());
            }
        }
    }

    @Override
    public boolean isValueNotSaved() {
        return valueNotSaved;
    }

    @Override
    public void setValueNotSaved(boolean valueNotSaved) {
        this.valueNotSaved = valueNotSaved;
    }

    @Override
    public void setTabIndexDelta(int tabIndexDelta) {
        this.tabIndexDelta = tabIndexDelta;
    }

    @Override
    public void addValueChangedListeners(FieldValueChanged valueChanged) {
        valueChangedListeners.add(valueChanged);
    }

}
