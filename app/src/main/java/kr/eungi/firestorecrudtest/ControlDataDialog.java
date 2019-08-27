package kr.eungi.firestorecrudtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * flag : 0은 추가, 1은 수정
 */
public class ControlDataDialog extends DialogFragment {
    static final String TAG = ControlDataDialog.class.getSimpleName();

    private int flag = 0;
    private MainActivity.DialogClickListener mOnClickListener;
    private EditText mNameInputEditText;
    private Button mFistButton;
    private Button mSecondButton;
    private Button mCancelButton;

    ControlDataDialog(int flag) {
        this.flag = flag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getActivity();

        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_control_data, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        mNameInputEditText = dialogView.findViewById(R.id.dialog_control_data_edit_text);
        mFistButton = dialogView.findViewById(R.id.dialog_control_data_first_button);
        mSecondButton = dialogView.findViewById(R.id.dialog_control_data_second_button);
        mCancelButton = dialogView.findViewById(R.id.dialog_control_data_cancel_button);

        if (flag == 0) {
            builder.setTitle("새로운 이름 추가");
            mFistButton.setText("추가");
            mFistButton.setOnClickListener(v -> {
                mOnClickListener.onAddClickListener(getName());
                dismiss();
            });
            mSecondButton.setText("무작위");
            mSecondButton.setOnClickListener(v -> {
                mOnClickListener.onAddRandomClickListener();
                dismiss();
            });
        } else if (flag == 1) {
            builder.setTitle("이름 수정");
            mFistButton.setText("수정");
            mFistButton.setOnClickListener(v -> {
                mOnClickListener.onUpdateClickListener(getName());
                dismiss();
            });
            mSecondButton.setText("삭제");
            mSecondButton.setTextColor(Color.red(170));
            mSecondButton.setOnClickListener(v -> {
                mOnClickListener.onDeleteClickListener();
                dismiss();
            });
        }
        mCancelButton.setOnClickListener(v -> dismiss());

        builder.setView(dialogView);
        return builder.create();
    }

    void setClickListener(MainActivity.DialogClickListener listener) {
        mOnClickListener = listener;
    }

    private String getName() {
        return mNameInputEditText.getText().toString();
    }
}
