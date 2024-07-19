package com.example.opencvdemo.view;

import android.content.Context;
import java.math.BigDecimal;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * 数量加减控件
 * 前面减号后面加号中间输入
 */
public class CounterView extends LinearLayout {
    private Button decreaseButton;  // 减少按钮
    private Button increaseButton;  // 增加按钮
    private EditText valueEditText; // 数值编辑框
    private View customView;        // 自定义视图

    private BigDecimal minValue = BigDecimal.ZERO;             // 最小值
    private BigDecimal maxValue = BigDecimal.valueOf(100); // 最大值
    private BigDecimal increment = BigDecimal.ONE;             // 步进值
    private BigDecimal defaultValue = BigDecimal.ZERO;         // 默认值
    private OnValueChangedListener valueChangedListener;       // 数值变化监听器
    private boolean decimalEnabled = false;                    // 是否允许小数
    private int decimalPlaces = 0;                             // 小数位数

    /**
     * 构造方法，用于创建 CustomCounterView 实例。
     *
     * @param context 上下文参数，不能为空
     */
    public CounterView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法，用于创建 CustomCounterView 实例。
     *
     * @param context 上下文参数，不能为空
     * @param attrs   属性参数
     */
    public CounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        applyAttributes(context, attrs);
    }

    /**
     * 构造方法，用于创建 CustomCounterView 实例。
     *
     * @param context  上下文参数，不能为空
     * @param attrs    属性参数
     * @param defStyle 默认样式参数
     */
    public CounterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        applyAttributes(context, attrs);
    }

    // 初始化方法
    private void init(Context context) {
        decreaseButton = new Button(context);
        decreaseButton.setWidth(50);
        decreaseButton.setText("-");
        addView(decreaseButton);

        valueEditText = new EditText(context);
        valueEditText.setWidth(50);
        valueEditText.setFocusable(true);
        valueEditText.setFocusableInTouchMode(true);
        valueEditText.setGravity(Gravity.CENTER);
        valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        addView(valueEditText);

        increaseButton = new Button(context);
        increaseButton.setWidth(50);
        increaseButton.setText("+");
        addView(increaseButton);

        customView = new View(context);
        addView(customView);

        decreaseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseValue();
            }
        });

        increaseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseValue();
            }
        });

        valueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    BigDecimal currentValue = new BigDecimal(s.toString());
                    if (currentValue.compareTo(minValue) < 0) {
                        setEditTextValue(minValue.toString());
                    } else if (currentValue.compareTo(maxValue) > 0) {
                        setEditTextValue(maxValue.toString());
                    }
                    notifyValueChanged();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    setEditTextValue("0");
                    notifyValueChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // 应用属性方法
    private void applyAttributes(Context context, AttributeSet attrs) {

    }

    // 设置默认值方法
    public void setDefaultValue(BigDecimal defaultValue) {
        this.defaultValue = defaultValue;
        setValue(defaultValue);
    }

    // 设置最小值方法
    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    // 设置最大值方法
    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    // 设置步进值方法
    public void setIncrement(BigDecimal increment) {
        this.increment = increment;
    }

    // 获取当前数值方法
    public BigDecimal getValue() {
        return BigDecimal.ZERO;
    }

    // 设置数值方法
    public void setValue(BigDecimal value) {
        valueEditText.setText(value.toString());
        // 将光标移动到文本末尾
        valueEditText.setSelection(valueEditText.getText().length());
    }

    // 更新文本数据方法
    private void setEditTextValue(String value) {
        valueEditText.setText(value);
        // 将光标移动到文本末尾
        valueEditText.setSelection(valueEditText.getText().length());
    }

    // 减少数值方法
    private void decreaseValue() {
        try {
            String input = valueEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(input)) {
                BigDecimal currentValue = new BigDecimal(input);
                if (currentValue.compareTo(BigDecimal.ZERO) == 0) {
                    return;
                }
                BigDecimal newValue = currentValue.subtract(increment);
                setEditTextValue(newValue.toString());
                notifyValueChanged();
            }
        } catch (NumberFormatException e) {
            setEditTextValue("0");
            notifyValueChanged();
        }
    }

    // 增加数值方法
    private void increaseValue() {
        try {
            String input = valueEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(input)) {
                BigDecimal currentValue = new BigDecimal(input);
                if (currentValue.compareTo(maxValue) >= 0) {
                    return;
                }
                BigDecimal newValue = currentValue.add(increment);
                setEditTextValue(newValue.toString());
                notifyValueChanged();
            }
        } catch (NumberFormatException e) {
            setEditTextValue("0");
            notifyValueChanged();
        }
    }

    /**
     * 开启小数输入
     *
     * @param decimalEnabled 是否开启  开：true 关：false
     * @param decimalPlaces  小数位数控制，如果是-1不控制位数，否则就是小数位数
     */
    public void setDecimalEnabled(boolean decimalEnabled, int decimalPlaces) {
        this.decimalEnabled = decimalEnabled;
        this.decimalPlaces = decimalPlaces;
        if (decimalEnabled) {
            if (decimalPlaces == -1) {
                valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else {
                valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }
        } else {
            valueEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        applyDecimalFilter();
    }

    private void applyDecimalFilter() {
        valueEditText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (decimalEnabled) {
                    String value = dest.toString().substring(0, dstart) + source.toString() + dest.toString().substring(dend);
                    if (!value.isEmpty() && !value.equals(".") && !value.equals("-")) {
                        try {
                            BigDecimal newValue = new BigDecimal(value);
                            if (decimalPlaces >= 0 && newValue.scale() > decimalPlaces) {
                                return "";
                            }
                        } catch (NumberFormatException | ArithmeticException e) {
                            return "";
                        }
                    }
                }
                return null;
            }
        }});
    }

    // 通知值已经改变方法
    private void notifyValueChanged() {
        if (valueChangedListener != null) {
            valueChangedListener.onValueChanged(new BigDecimal(valueEditText.getText().toString()));
        }
    }

    // 设置数值变化监听器方法
    public void setOnValueChangedListener(OnValueChangedListener listener) {
        this.valueChangedListener = listener;
    }

    // 数值变化监听器接口
    public interface OnValueChangedListener {
        void onValueChanged(BigDecimal newValue);
    }
}
