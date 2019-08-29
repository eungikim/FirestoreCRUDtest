package kr.eungi.firestorecrudtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import static kr.eungi.firestorecrudtest.util.Constant.DIALOG_FLAG_ADD;
import static kr.eungi.firestorecrudtest.util.Constant.DIALOG_FLAG_MODIFY;

/**
 * flag : 0은 추가, 1은 수정
 */
public class ControlDataDialog extends DialogFragment {
    static final String TAG = ControlDataDialog.class.getSimpleName();

    private int mFlag;
    private int mSelectedPosition;
    private MainActivity.DialogClickListener mOnClickListener;
    private EditText mNameInputEditText;

    ControlDataDialog(int flag) {
        this(flag, -1);
    }
    ControlDataDialog(int flag, int selectedPosition) {
        this.mFlag = flag;
        this.mSelectedPosition = selectedPosition;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.dialog_control_data, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        mNameInputEditText = dialogView.findViewById(R.id.dialog_control_data_edit_text);
        Button fistButton = dialogView.findViewById(R.id.dialog_control_data_first_button);
        Button secondButton = dialogView.findViewById(R.id.dialog_control_data_second_button);
        Button cancelButton = dialogView.findViewById(R.id.dialog_control_data_cancel_button);

        if (mFlag == DIALOG_FLAG_ADD) {
            builder.setTitle("새로운 이름 추가");
            fistButton.setText("추가");
            fistButton.setOnClickListener(v -> {
                mOnClickListener.onAddClickListener(getInsertedName());
                dismiss();
            });
            secondButton.setText("무작위");
            secondButton.setOnClickListener(v -> {
                mOnClickListener.onAddRandomClickListener();
                dismiss();
            });
        } else if (mFlag == DIALOG_FLAG_MODIFY) {
            builder.setTitle("이름 수정");
            fistButton.setText("수정");
            fistButton.setOnClickListener(v -> {
                mOnClickListener.onUpdateClickListener(getInsertedName(), mSelectedPosition);
                dismiss();
            });
            secondButton.setText("삭제");
            secondButton.setTextColor(ContextCompat.getColor(context, R.color.red_a400));
            secondButton.setOnClickListener(v -> {
                mOnClickListener.onDeleteClickListener(mSelectedPosition);
                dismiss();
            });
        }
        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(dialogView);
        return builder.create();
    }

    void setClickListener(MainActivity.DialogClickListener listener) {
        mOnClickListener = listener;
    }

    private String getInsertedName() {
        return mNameInputEditText.getText().toString();
    }
}
