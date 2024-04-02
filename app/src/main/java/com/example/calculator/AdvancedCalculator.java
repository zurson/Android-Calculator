package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class AdvancedCalculator extends SimpleCalculator {

    private static List<String> advancedOperators;
    private static HashMap<String, Runnable> functionsMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.advanced_calc);

        super.setup();

        prepareAdvancedOperators();
        prepareFunctionsMap();
        prepareButtons();
    }

    private String getClickedButtonText(View view) {
        return ((Button) view).getText().toString();
    }

    private void prepareFunctionsMap() {
        functionsMap = new HashMap<>();

        functionsMap.put(getStringConstant(R.string.SIN), this::handleSin);
        functionsMap.put(getStringConstant(R.string.COS), this::handleCos);
        functionsMap.put(getStringConstant(R.string.TAN), this::handleTan);
        functionsMap.put(getStringConstant(R.string.LN), this::handleLn);
        functionsMap.put(getStringConstant(R.string.LOG), this::handleLog);
        functionsMap.put(getStringConstant(R.string.PERCENTAGE), this::handlePercentage);
        functionsMap.put(getStringConstant(R.string.X2), this::handleX2);
        functionsMap.put(getStringConstant(R.string.XY), this::handleXY);
        functionsMap.put(getStringConstant(R.string.SQRT), this::handleSqrt);
    }

    private void prepareAdvancedOperators() {
        advancedOperators = new ArrayList<>();

        advancedOperators.add(getStringConstant(R.string.SIN));
        advancedOperators.add(getStringConstant(R.string.COS));
        advancedOperators.add(getStringConstant(R.string.TAN));
        advancedOperators.add(getStringConstant(R.string.LN));
        advancedOperators.add(getStringConstant(R.string.LOG));
        advancedOperators.add(getStringConstant(R.string.PERCENTAGE));
        advancedOperators.add(getStringConstant(R.string.X2));
        advancedOperators.add(getStringConstant(R.string.XY));
        advancedOperators.add(getStringConstant(R.string.SQRT));
        advancedOperators.add(getStringConstant(R.string.POWER_SYMBOL));
    }

    private void prepareButtons() {
        super.setButtonListener(R.id.button_sin, this);
        super.setButtonListener(R.id.button_cos, this);
        super.setButtonListener(R.id.button_tan, this);
        super.setButtonListener(R.id.button_percentage, this);
        super.setButtonListener(R.id.button_xy, this);
        super.setButtonListener(R.id.button_x2, this);
        super.setButtonListener(R.id.button_log, this);
        super.setButtonListener(R.id.button_ln, this);
        super.setButtonListener(R.id.button_sqrt, this);
    }


    private boolean isPowerCondition(String clickedButtonText) {
        if (!getSolution().isEmpty()) {
            String lastChar = getSolution().charAt(getSolution().length() - 1) + "";
            if (advancedOperators.contains(lastChar) && advancedOperators.contains(clickedButtonText))
                return true;
        }

        if (clickedButtonText.equals(getStringConstant(R.string.XY)) && !getSolution().isEmpty()) {
            setSolution(getSolution() + getStringConstant(R.string.POWER_SYMBOL));
            return true;
        }

        if (clickedButtonText.equals(getStringConstant(R.string.EQUALS)) && getSolution().contains(getStringConstant(R.string.POWER_SYMBOL))) {
            handleXY();
            return true;
        }

        return false;
    }

    private void runRequiredOperation(String clickedButtonText) {
        if (getSolution().isEmpty())
            return;

        Runnable functionToRun = functionsMap.get(clickedButtonText);

        if (functionToRun == null) {
            Toast.makeText(this, "Fatal error! (Lack of advanced operator)", Toast.LENGTH_SHORT).show();
            return;
        }

        functionToRun.run();
    }

    private boolean operatorAlreadyExistsCondition(boolean advancedOperatorClicked) {
        if (!advancedOperatorClicked)
            return false;

        for (String operator : advancedOperators) {
            if (getSolution().contains(operator)) {

                System.out.println("Solution contains: " + operator);

                if (operator.equals(getStringConstant(R.string.POWER_SYMBOL)))
                    isPowerCondition(getStringConstant(R.string.EQUALS));
                else
                    handleEquals(getSolution());

                return true;
            }

        }

        return false;
    }

    private void continueCalculations(boolean advancedOperatorClicked) {
        if (!advancedOperatorClicked)
            return;

        String solution = getSolution();

        if (isErrorDisplayed()) {
            setSolution("");
            setResult("0");
            return;
        }

        if (!solution.contains(getStringConstant(R.string.EQUALS)))
            return;

        setSolution(getResult());
        setResult("0");
    }

    @Override
    public void onClick(View view) {
        String clickedButtonText = getClickedButtonText(view);
        boolean advancedOperatorClicked = isAdvancedOperator(clickedButtonText);

        continueCalculations(advancedOperatorClicked);

        if (operatorAlreadyExistsCondition(advancedOperatorClicked))
            return;

        if (advancedOperatorClicked && getSolution().contains(getStringConstant(R.string.EQUALS)))
            return;

        if (isPowerCondition(clickedButtonText))
            return;

        if (!advancedOperatorClicked) {
            super.onClick(view);
            return;
        }

        runRequiredOperation(clickedButtonText);
    }

    private String getSecondNumberAsString(String data) {
        if (data == null || data.isEmpty() || data.contains(getStringConstant(R.string.EQUALS)))
            return null;

        StringBuilder sb = new StringBuilder();
        char[] chars = data.toCharArray();
        boolean prevDigit = false;

        for (int i = chars.length - 1; i >= 0; i--) {
            char c = chars[i];

            if (Character.isDigit(c) || String.valueOf(c).equals(getStringConstant(R.string.DOT))) {
                sb.append(c);
                prevDigit = true;
            } else if (prevDigit && String.valueOf(c).equals(getStringConstant(R.string.SUBTRACT))) {
                sb.append(c);
                break;
            } else
                break;

        }

        return sb.reverse().toString();
    }

    private String getFirstNumberAsString(String data) {
        if (data == null || data.isEmpty() || data.contains(getStringConstant(R.string.EQUALS)))
            return null;

        StringBuilder sb = new StringBuilder();
        char[] chars = data.toCharArray();

        int i = 0;

        if (String.valueOf(chars[0]).equals(getStringConstant(R.string.SUBTRACT))) {
            sb.append(chars[0]);
            i = 1;
        }

        for (; i < chars.length; i++) {
            char c = chars[i];

            if (Character.isDigit(c) || String.valueOf(c).equals(getStringConstant(R.string.DOT))) {
                sb.append(c);
                continue;
            }

            break;
        }

        return sb.toString();
    }

    private String removeLastNumber(String data, String textNumber) {
        if (data == null || data.isEmpty() || textNumber == null || textNumber.isEmpty())
            return data;

        int nrLen = textNumber.length();
        data = data.substring(0, data.length() - nrLen);

        return data;
    }


    private void doOperation(BiFunction<Double, Double, Double> operation, boolean replaceLast) {
        String solution = getSolution();

        String textFirstNumber = getFirstNumberAsString(solution);
        String textSecondNumber = getSecondNumberAsString(solution);

        if (textFirstNumber == null || textSecondNumber == null) {
            Toast.makeText(this, "Err", Toast.LENGTH_SHORT).show();
            return;
        }

        double result, firstVal, secondVal;

        try {
            firstVal = Double.parseDouble(textFirstNumber);
            secondVal = Double.parseDouble(textSecondNumber);

            if (replaceLast)
                solution = removeLastNumber(solution, textSecondNumber);
        } catch (NumberFormatException ignored) {
            setSolution("");
            setResult(ERROR_MESSAGE);
            return;
        }

        result = operation.apply(firstVal, secondVal);

        String formattedValue = formatNumber(result);

        if (formattedValue.length() > super.MAX_OUTPUT) {
            setSolution("");
            setResult(super.TOO_LONG_MSG);
            return;
        }

        if (replaceLast)
            solution += formattedValue;

        setSolution(solution);

        if (replaceLast)
            handleEquals(getSolution());
        else {
            setSolution("");
            setResult(formattedValue);
        }
    }


    private void handleSin() {
        doOperation((firstVal, secondVal) -> Math.sin(secondVal), true);
    }

    private void handleCos() {
        doOperation((firstVal, secondVal) -> Math.cos(secondVal), true);
    }

    private void handleTan() {
        doOperation((firstVal, secondVal) -> Math.tan(secondVal), true);
    }

    private void handlePercentage() {
        doOperation((firstVal, secondVal) -> firstVal * secondVal / 100, true);
    }

    private void handleLog() {
        doOperation((firstVal, secondVal) -> Math.log10(secondVal), true);
    }

    private void handleLn() {
        doOperation((firstVal, secondVal) -> Math.log(secondVal), true);
    }

    private void handleXY() {
        doOperation(Math::pow, false);
    }

    private void handleX2() {
        doOperation((firstVal, secondVal) -> secondVal * secondVal, true);
    }

    private void handleSqrt() {
        doOperation((firstVal, secondVal) -> Math.sqrt(secondVal), false);
    }

    private boolean isAdvancedOperator(String operator) {
        return advancedOperators.contains(operator);
    }

}
