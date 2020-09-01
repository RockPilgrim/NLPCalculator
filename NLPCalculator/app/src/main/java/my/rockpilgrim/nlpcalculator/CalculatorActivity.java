package my.rockpilgrim.nlpcalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import my.rockpilgrim.nlpcalculator.databinding.CalculatorActivityBinding;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CalculatorActivity.class.getSimpleName();
    private static final String SAVED_INPUT = "my.rockpilgrim.nlpcalculator.input";
    private static final String SAVED_OUTPUT = "my.rockpilgrim.nlpcalculator.output";
    private static final int SEC = 2000;
    private static final int SEC_OPEN = 3000;

    private TextView inputText;
    private TextView outputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalculatorActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.calculator_activity);

        setupUI(binding,savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUI(CalculatorActivityBinding binding, Bundle savedInstanceState) {
        inputText = binding.inputTextView;
        outputText = binding.outputTextView;
        if (savedInstanceState != null) {
            Log.i(TAG, "getSaved");
            inputText.setText(savedInstanceState.getString(SAVED_INPUT, ""));
            outputText.setText(savedInstanceState.getString(SAVED_OUTPUT, ""));
        }


        binding.zeroButton.setOnClickListener(this::onClick);
        binding.oneButton.setOnClickListener(this::onClick);
        binding.twoButton.setOnClickListener(this::onClick);
        binding.threeButton.setOnClickListener(this::onClick);
        binding.fourButton.setOnClickListener(this::onClick);
        binding.fiveButton.setOnClickListener(this::onClick);
        binding.sixButton.setOnClickListener(this::onClick);
        binding.sevenButton.setOnClickListener(this::onClick);
        binding.eightButton.setOnClickListener(this::onClick);
        binding.nineButton.setOnClickListener(this::onClick);
        ///
        binding.bracketLeftButton.setOnClickListener(this::onClick);
        binding.bracketRightButton.setOnClickListener(this::onClick);
        binding.dotButton.setOnClickListener(this::onClick);
        ///
        binding.clearButton.setOnClickListener(this::onClick);
        binding.deleteButton.setOnClickListener(this::onClick);
        ///
        binding.plusButton.setOnClickListener(this::onClick);
        binding.minusButton.setOnClickListener(this::onClick);
        binding.multiplyButton.setOnClickListener(this::onClick);
        binding.divisionButton.setOnClickListener(this::onClick);
        binding.equalsButton.setOnClickListener(this::onClick);

        final long[] interval = {0};
        binding.equalsButton.setOnTouchListener((v, event) ->{

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                interval[0] = System.currentTimeMillis();
                Log.i(TAG, "actionDown");
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                long time = System.currentTimeMillis() - interval[0];
                calculate(inputText.getText().toString());
                if (time > SEC) {
                    interval[0] = System.currentTimeMillis();
                    makeToast(getString(R.string.up));
                    binding.secretButton.setVisibility(View.VISIBLE);
                }
                Log.i(TAG, "actionUp " + time);
            }
            return true;
        });

        binding.secretButton.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                Log.i(TAG, "secretButton single click");
            }
            @Override
            public void onDoubleClick(View view) {
                long time = System.currentTimeMillis() - interval[0];
                if (time < SEC_OPEN) {
                    nextActivity();
                    makeToast("Open secret menu");
                }
                binding.secretButton.setVisibility(View.GONE);
            }
        }));
    }

    private void nextActivity() {
        Intent intent = new Intent(this, SecretActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zeroButton:
                inputText.append("0");
                break;
            case R.id.oneButton:
                inputText.append("1");
                break;
            case R.id.twoButton:
                inputText.append("2");
                break;
            case R.id.threeButton:
                inputText.append("3");
                break;
            case R.id.fourButton:
                inputText.append("4");
                break;
            case R.id.fiveButton:
                inputText.append("5");
                break;
            case R.id.sixButton:
                inputText.append("6");
                break;
            case R.id.sevenButton:
                inputText.append("7");
                break;
            case R.id.eightButton:
                inputText.append("8");
                break;
            case R.id.nineButton:
                inputText.append("9");
                break;
                ////
            case R.id.bracketLeftButton:
                inputText.append("(");
                break;
            case R.id.bracketRightButton:
                inputText.append(")");
                break;
            case R.id.dotButton:
                inputText.append(".");
                break;
                ////
            case R.id.clearButton:
                inputText.setText("");
                break;
            case R.id.deleteButton:
                outputText.setText("");
                break;
                ////
            case R.id.plusButton:
                inputText.append("+");
                break;
            case R.id.minusButton:
                inputText.append("-");
                break;
            case R.id.multiplyButton:
                inputText.append("x");
                break;
            case R.id.divisionButton:
                inputText.append("/");
                break;
        }
    }

    private void calculate(String expression) {
        String _expression = expression.replace("x", "*");
        Log.d(TAG, _expression);
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("rhino");
        try {
            String expResult = scriptEngine.eval(_expression).toString();
            Log.i(TAG, "" + expResult);
            outputText.setText(expResult);
        } catch (ScriptException e) {
            e.printStackTrace();
            makeToast(getString(R.string.error));
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            makeToast(getString(R.string.enter_expression));
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
        outState.putString(SAVED_INPUT, inputText.getText().toString());
        outState.putString(SAVED_OUTPUT, outputText.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}