package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpleCalculator extends AppCompatActivity implements View.OnClickListener {

    protected static final String ERROR_MESSAGE = "Error!";
    protected static final String FATAL_ERROR_MSG = "Fatal Error!";
    private TextView resultTv, solutionTv;
    protected final String TOO_LONG_MSG = "Too long!";
    protected final String MAX_INPUT_REACHED_MSG = "Max input reached!";
    protected final String INVALID_INPUT_MSG = "Invalid input!";
    protected final int MAX_INPUT = 50;
    protected final int MAX_OUTPUT = 40;
    private static List<String> operators;
    private static final DecimalFormat df = new DecimalFormat("#.##########");


    protected void setup() {
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        prepareButtons();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.simple_calc);
        resultTv = findViewById(R.id.result_tv);
        resultTv = findViewById(R.id.result_tv);
        solutionTv = findViewById(R.id.solution_tv);

        prepareOperators();
        prepareButtons();
    }

    protected String formatNumber(double val) {
        return formatNumber(String.valueOf(val));
    }

    protected String formatNumber(String val) {
        String result = df.format(Double.parseDouble(val));
        result = result.replace(",", getStringConstant(R.string.DOT));
        return result;
    }

    private void prepareOperators() {
        operators = new ArrayList<>();

        operators.add(getStringConstant(R.string.ADD));
        operators.add(getStringConstant(R.string.SUBTRACT));
        operators.add(getStringConstant(R.string.MULTIPLY));
        operators.add(getStringConstant(R.string.DIVIDE));
    }

    private void prepareButtons() {
        setButtonListener(R.id.button_c, this);
        setButtonListener(R.id.button_ac, this);
        setButtonListener(R.id.button_back, this);
        setButtonListener(R.id.button_dot, this);
        setButtonListener(R.id.button_sign, this);

        setButtonListener(R.id.button_0, this);
        setButtonListener(R.id.button_1, this);
        setButtonListener(R.id.button_2, this);
        setButtonListener(R.id.button_3, this);
        setButtonListener(R.id.button_4, this);
        setButtonListener(R.id.button_5, this);
        setButtonListener(R.id.button_6, this);
        setButtonListener(R.id.button_7, this);
        setButtonListener(R.id.button_8, this);
        setButtonListener(R.id.button_9, this);

        setButtonListener(R.id.button_add, this);
        setButtonListener(R.id.button_subtract, this);
        setButtonListener(R.id.button_divide, this);
        setButtonListener(R.id.button_multiply, this);
        setButtonListener(R.id.button_equals, this);

    }

    protected void setButtonListener(int id, View.OnClickListener listener) {
        findViewById(id).setOnClickListener(listener);
    }

    protected String getSolution() {
        return solutionTv.getText().toString();
    }

    protected String getResult() {
        return resultTv.getText().toString();
    }

    protected void setSolution(String message) {
        solutionTv.setText(message);
    }

    protected void setResult(String message) {
        resultTv.setText(message);
    }

    protected String getStringConstant(int id) {
        return getResources().getString(id);
    }

    protected boolean isArithmeticOperator(String operator) {
        return operators.contains(operator);
    }

    protected boolean isLastArithmeticOperator(String dataToCalculate) {
        if (dataToCalculate.isEmpty())
            return false;

        return isArithmeticOperator(String.valueOf(dataToCalculate.charAt(dataToCalculate.length() - 1)));
    }

    protected String removeOperatorIfLast(String dataToCalculate) {
        if (isLastArithmeticOperator(dataToCalculate))
            return dataToCalculate.substring(0, dataToCalculate.length() - 1);

        return dataToCalculate;
    }

    protected boolean containsArithmeticsOperator(String dataToCalculate) {
        if (dataToCalculate == null || dataToCalculate.isEmpty())
            return false;

        char[] chars = dataToCalculate.toCharArray();
        chars[0] = ' ';

        for (char c : dataToCalculate.toCharArray()) {
            if (isArithmeticOperator(String.valueOf(c)))
                return true;
        }

        return false;
    }


    private void handleAC() {
        setSolution("");
        setResult("0");
    }

    private void handleBack(String dataToCalculate) {
        if (dataToCalculate.contains("="))
            dataToCalculate = "";
        else if (!dataToCalculate.isEmpty())
            dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);

        setSolution(dataToCalculate);
    }

    private void handleC() {
        setSolution("");
    }

    private boolean checkLastCharacter(String data, String characterToCompare) {
        if (data == null || data.isEmpty() || characterToCompare == null || characterToCompare.isEmpty())
            return false;

        String lastChar = String.valueOf(data.charAt(data.length() - 1));

        return lastChar.equals(characterToCompare);
    }

    protected void handleEquals(String dataToCalculate) {
        if (dataToCalculate.isEmpty() || checkLastCharacter(dataToCalculate, getStringConstant(R.string.EQUALS)))
            return;

        if (checkLastCharacter(dataToCalculate, getStringConstant(R.string.DOT)) || checkLastCharacter(dataToCalculate, "0")) {
            Toast.makeText(this, INVALID_INPUT_MSG, Toast.LENGTH_SHORT).show();
            return;
        }

        dataToCalculate = removeOperatorIfLast(dataToCalculate);

        String result = getResult(dataToCalculate);
        setSolution(dataToCalculate + "=");
        setResult(result);
    }

    private void signChangeAtBeginningAction(StringBuilder sb) {
        if (sb.charAt(0) == '-')
            sb.deleteCharAt(0);
        else
            sb.insert(0, "-");

        setSolution(sb.toString());
    }

    private void signChangeInsideAction(StringBuilder sb, int index) {
        if (sb.charAt(index) == '+')
            sb.setCharAt(index, '-');
        else if (sb.charAt(index) == '-') {
            if (!Character.isDigit(sb.charAt(index - 1)))
                sb.deleteCharAt(index);
            else
                sb.setCharAt(index, '+');
        } else
            sb.insert(index + 1, '-');

        setSolution(sb.toString());
    }

    private int getIndexOfSignChange(StringBuilder sb) {
        int i = sb.length() - 1;

        for (; i >= 0; i--) {
            char c = sb.charAt(i);
            if (!Character.isDigit(c) && !String.valueOf(c).equals(getStringConstant(R.string.DOT)))
                break;
        }

        return i;
    }

    private void handleSignChange(String solution) {
        if (solution.contains("="))
            return;

        if (isLastArithmeticOperator(solution)) {
            Toast.makeText(this, INVALID_INPUT_MSG, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder(solution);

        int index = getIndexOfSignChange(sb);

        if (index <= 0) {
            signChangeAtBeginningAction(sb);
            return;
        }

        signChangeInsideAction(sb, index);
    }

    private String[] splitAtNonDigit(String data) {
        StringBuilder regexBuilder = new StringBuilder("[^");
        for (int i = 0; i < 10; i++)
            regexBuilder.append(i);
        regexBuilder.append("\\").append(getStringConstant(R.string.DOT));
        regexBuilder.append("]");

        String[] pieces = data.split(regexBuilder.toString());
        return pieces;
    }

    private boolean onlyZeros(String dataToCalculate) {
        if (dataToCalculate.isEmpty())
            return false;

        String[] splitted = splitAtNonDigit(dataToCalculate);
        dataToCalculate = splitted[splitted.length - 1];

        dataToCalculate = dataToCalculate.replace("0", "");

        for (String operator : operators)
            dataToCalculate = dataToCalculate.replace(operator, "");

        return dataToCalculate.isEmpty();
    }

    private boolean isDotProperlyInserted(String dataToCalculate, boolean dotClicked) {
        if (!dotClicked)
            return true;

        if (dataToCalculate.isEmpty())
            return false;

        String dotHelper = getStringConstant(R.string.DOT);
        char dot = dotHelper.charAt(dotHelper.length() - 1);

        if (dataToCalculate.contains(String.valueOf(dot))) {
            if (Objects.equals(dataToCalculate.charAt(dataToCalculate.length() - 1), dot))
                return false;

            String[] splitted = splitAtNonDigit(dataToCalculate);
            String data = splitted[splitted.length - 1];

            if (data.contains(String.valueOf(dot)))
                return false;

        }

        if (isArithmeticOperator(String.valueOf(dataToCalculate.charAt(dataToCalculate.length() - 1))) && dotClicked)
            return false;

        return true;
    }

    private String validateDefaultInput(String dataToCalculate, String clickedButtonText) {
        boolean dotClicked = Objects.equals(clickedButtonText, getStringConstant(R.string.DOT));
        boolean zeroClicked = Objects.equals(clickedButtonText, "0");

        if (dotClicked && dataToCalculate.contains(getStringConstant(R.string.EQUALS))) {
            setSolution(getResult() + clickedButtonText);
            setResult("0");
            return null;
        }

        if (zeroClicked && onlyZeros(dataToCalculate))
            return null;

        if (!isDotProperlyInserted(dataToCalculate, dotClicked))
            return null;

        setSolution(dataToCalculate);

        if (dataToCalculate.contains("=")) {
            if (Character.isDigit(clickedButtonText.charAt(0))) {
                dataToCalculate = "";
                setSolution(dataToCalculate);
                setResult("0");
            }
        }

        return dataToCalculate;
    }

    private void handleDefaultInput(String dataToCalculate, String clickedButtonText) {
        dataToCalculate = validateDefaultInput(dataToCalculate, clickedButtonText);

        if (dataToCalculate == null)
            return;

        if (dataToCalculate.length() > MAX_INPUT) {
            Toast.makeText(this, MAX_INPUT_REACHED_MSG, Toast.LENGTH_SHORT).show();
            return;
        }

        dataToCalculate += clickedButtonText;
        solutionTv.setText(dataToCalculate);
    }

    protected boolean isErrorDisplayed() {

        try {
            double d = Double.parseDouble(getResult());
            return Double.isNaN(d);
        } catch (NumberFormatException ignored) {
            return true;
        }

    }

    private void handleNonOperatorAction(String clickedButtonText, String dataToCalculate) {

        if (clickedButtonText.equals(getStringConstant(R.string.AC)))
            handleAC();

        else if (clickedButtonText.equals(getStringConstant(R.string.C)))
            handleC();

        else if (clickedButtonText.equals(getStringConstant(R.string.BACK)))
            handleBack(dataToCalculate);

        else if (clickedButtonText.equals(getStringConstant(R.string.CHANGE_SIGN)))
            handleSignChange(dataToCalculate);

        else if (clickedButtonText.equals(getStringConstant(R.string.EQUALS)))
            handleEquals(dataToCalculate);

        else
            handleDefaultInput(dataToCalculate, clickedButtonText);
    }

    private String continuePreviousCalculations(String solution, String clickedButtonText) {
        if (solution == null)
            return null;

        boolean arithmeticOperator = isArithmeticOperator(clickedButtonText);

        if (solution.isEmpty() && arithmeticOperator) {
            if (!getResult().isEmpty()) {
                setSolution(getResult() + clickedButtonText);
                return null;
            }

            return null;
        }

        if (arithmeticOperator && (containsArithmeticsOperator(solution) ||
                solution.contains(getStringConstant(R.string.EQUALS)))) {

            handleEquals(solution);

            setSolution(getResult() + clickedButtonText);
            return null;
        }

        return solution;
    }

    protected void calculate(String clickedButtonText) {
        if (clickedButtonText == null || clickedButtonText.isEmpty())
            return;

        String solution = getSolution();

        if (isErrorDisplayed()) {
            setSolution("");
            setResult("0");
            return;
        }

        // Change operator if last
        if (isArithmeticOperator(clickedButtonText) && isLastArithmeticOperator(solution))
            solution = solution.substring(0, solution.length() - 1);

        solution = continuePreviousCalculations(solution, clickedButtonText);
        if (solution == null)
            return;

        handleNonOperatorAction(clickedButtonText, solution);
    }


    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String clickedButtonText = button.getText().toString();

        calculate(clickedButtonText);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("RESULT", getResult());
        outState.putString("SOLUTION", getSolution());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        setResult(savedInstanceState.getString("RESULT"));
        setSolution(savedInstanceState.getString("SOLUTION"));
    }

    private String getResult(String data) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String finalResult = context.evaluateString(scriptable, data, "Javascript", 1, null).toString();

            finalResult = formatNumber(finalResult);

            if (finalResult.length() > MAX_OUTPUT)
                return TOO_LONG_MSG;

            return finalResult;
        } catch (Exception e) {
            return ERROR_MESSAGE;
        }
    }


}
