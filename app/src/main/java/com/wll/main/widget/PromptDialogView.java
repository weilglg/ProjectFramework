package com.wll.main.widget;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wll.main.R;

/**
 * Created by AUAS on 2016/3/11.
 */
public class PromptDialogView extends DialogFragment {

    private String confirmStr = "确认";
    private String cancelStr = "取消";
    private String titleStr = "提示";
    private String contentStr = "确认删除吗？";
    private OnDialogButtonClickListener affirmClickListener = new OnDialogButtonClickListener() {
        @Override
        public void onAffirmClick(View v, Dialog dialog) {

        }

        @Override
        public void onCancelClick(View v, Dialog dialog) {

        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.dialog_hint_layout, null, false);
        TextView confirm = (TextView) view
                .findViewById(R.id.dialog_hint_affirm);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_hint_cancel);
        confirm.setText(confirmStr);
        cancel.setText(cancelStr);
        final Dialog dialog = new Dialog(getActivity(), R.style.dialog_style);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        view.findViewById(R.id.dialog_hint_affirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        affirmClickListener.onAffirmClick(v, dialog);
                        dialog.dismiss();
                    }
                });
        view.findViewById(R.id.dialog_hint_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        affirmClickListener.onCancelClick(v, dialog);
                        dialog.dismiss();
                    }
                });
        TextView title = (TextView) view.findViewById(R.id.dialog_hint_title);
        title.setText(titleStr);
        TextView content = (TextView) view
                .findViewById(R.id.dialog_hint_content);
        content.setText(contentStr);
        return dialog;
    }

    /**
     * 确认、取消按钮的回调接口
     */
    public interface OnDialogButtonClickListener {
        public void onAffirmClick(View v, Dialog dialog);

        public void onCancelClick(View v, Dialog dialog);
    }

}
