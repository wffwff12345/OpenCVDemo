package com.example.opencvdemo.utils;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.DialogAction;

/**
 * <p>确认框</p>
 *
 * @author lee
 * @date 2022/8/22 15:42
 */
public class ConfirmDialog extends BaseDialog {
    /**
     * 当前上下文
     */
    private Context context;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * true: 只有“确定”按钮
     * false: 确定和取消按钮
     */
    private boolean isSingleButton;

    private int index;

    public ConfirmDialog(Context context, int calledByViewId, String title, String content, boolean isSingleButton) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.calledByViewId = calledByViewId;
        this.isSingleButton = isSingleButton;
        initialize();
    }

    public ConfirmDialog(Fragment fragment, int calledByViewId, String title, String content, boolean isSingleButton) {
        this(fragment.getActivity(), calledByViewId, title, content, isSingleButton);
        setResult(fragment);
    }

    public ConfirmDialog(Context context, int calledByViewId, String title, String content, boolean isSingleButton, int index) {
        this(context, calledByViewId, title, content, isSingleButton);
        this.index = index;
    }

    private void initialize() {
        title(title);
        content(content);
        positiveText("确定");
        onPositive((dialog, which) -> onResultClick(DialogAction.POSITIVE, String.valueOf(this.index)));
        if (isSingleButton == false) {
            negativeText("取消");
            onNegative((dialog, which) -> onResultClick(DialogAction.NEGATIVE));
        }
    }
}
