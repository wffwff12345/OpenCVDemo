package com.example.opencvdemo.utils;

import android.content.Context;
import android.text.InputType;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.example.opencvdemo.R;
import com.example.opencvdemo.http.JsonUtils;
import com.example.opencvdemo.model.Bean;
import com.example.opencvdemo.view.RecycleViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>输入对话框</p>
 *
 * @author lee
 * @date 2022/8/22 15:44
 */
public class InputTextDialog extends BaseDialog {
    /**
     * 文本键盘
     */
    public static final int KEYBOARD_TEXT = 1;
    /**
     * 数字键盘
     */
    public static final int KEYBOARD_NUMBER = 2;
    /**
     * 标题
     */
    private String title;
    /**
     * 键盘类型
     */
    private int keyboardType;

    private List<Bean> beans = new ArrayList<>();

    public InputTextDialog(Context context, int calledByViewId, String title) {
        super(context);
        this.title = title;
        this.calledByViewId = calledByViewId;
        initialize(context);
    }

    public                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          InputTextDialog(Context context, int calledByViewId, Bean bean, String title) {
        super(context);
        this.beans.clear();
        this.calledByViewId = calledByViewId;
        this.beans.add(bean);
        this.title = title;
        initialize(context);
    }

    public InputTextDialog(Fragment fragment, int calledByViewId, String title) {
        this(fragment.getActivity(), calledByViewId, title);
        setResult(fragment);
    }

    private void initialize(final Context context) {
        title(title);
        adapter(new RecycleViewAdapter(R.layout.item_edit, this.beans, true), new LinearLayoutManager(context));
        positiveText("确定");
        onPositive((dialog, which) -> {
            onResultClick(DialogAction.POSITIVE, JsonUtils.toJson(this.beans.get(0)));
            autoDismiss = true;
        });
        negativeText("取消");
        onNegative((dialog, which) -> autoDismiss = true
        );
    }

    public void setKeyboardType(int keyboardType) {
        this.keyboardType = keyboardType;
        if (keyboardType == KEYBOARD_TEXT) {
            inputType(InputType.TYPE_CLASS_TEXT);
        } else if (keyboardType == KEYBOARD_NUMBER) {
            inputType(InputType.TYPE_CLASS_NUMBER);
        }
    }
}