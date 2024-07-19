package com.example.opencvdemo.view;


import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.opencvdemo.R;
import com.example.opencvdemo.model.Bean;
import com.example.opencvdemo.utils.ToastUtil;

import java.math.BigDecimal;
import java.util.List;

public class RecycleViewAdapter extends BaseQuickAdapter<Bean, BaseViewHolder> {
    private boolean isEditView = false;

    private EditText nameEdit;
    private CounterView numberEdit;

    public RecycleViewAdapter(int layoutResId, List<Bean> data, boolean isEditView) {
        super(layoutResId, data);
        this.isEditView = isEditView;
    }

    public RecycleViewAdapter(int layoutResId, List<Bean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Bean item) {
        if (!this.isEditView) {
            helper.setText(R.id.nameView, item.getName());
            helper.setText(R.id.numberView, String.valueOf(item.getNumber()));
            helper.setText(R.id.TypeView, item.getType());
        } else {
            nameEdit = helper.getView(R.id.nameEdit);
            nameEdit.setText(item.getName());
            nameEdit.setSelection(nameEdit.getText().length());
            nameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if (!TextUtils.isEmpty(text)) {
                        item.setName(text);
                    } else {
                        ToastUtil.show(getContext(), "商品名称不能为空！");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            numberEdit = helper.getView(R.id.numberEdit);
            numberEdit.setMinValue(BigDecimal.ZERO);
            numberEdit.setMaxValue(new BigDecimal(100));
            numberEdit.setIncrement(BigDecimal.ONE);
            numberEdit.setDefaultValue(BigDecimal.valueOf(item.getNumber()));
            numberEdit.setOnValueChangedListener(new CounterView.OnValueChangedListener() {
                @Override
                public void onValueChanged(BigDecimal newValue) {
                    item.setNumber(newValue.longValue());
                }
            });
        }
    }
}
