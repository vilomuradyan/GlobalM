package com.globalm.platform.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.globalm.platform.R;
import com.globalm.platform.listeners.CallbackListener;
import com.globalm.platform.models.assingments.CreateResponseBody;
import com.globalm.platform.models.assingments.SendRespondResponse;
import com.globalm.platform.network.RequestManager;

public class RespondDialog extends Dialog {

    public static RespondDialog createDialog(Context context, int assignmentId) {
        return new RespondDialog(context, assignmentId);
    }

    private int assignmentId;

    public RespondDialog(@NonNull Context context, int assignmentId) {
        super(context);
        this.assignmentId = assignmentId;
    }

    public RespondDialog(@NonNull Context context, int themeResId, int assignmentId) {
        super(context, themeResId);
        this.assignmentId = assignmentId;
    }

    protected RespondDialog(@NonNull Context context,
                            boolean cancelable,
                            @Nullable DialogInterface.OnCancelListener cancelListener,
                            int assignmentId) {
        super(context, cancelable, cancelListener);
        this.assignmentId = assignmentId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_respond);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.CENTER);
        }
        findViewById(R.id.cancel).setOnClickListener((v) -> dismiss());
        findViewById(R.id.send_respond).setOnClickListener(getOnSendRespondClicked());
    }

    private View.OnClickListener getOnSendRespondClicked() {
        return (v) -> {
            CreateResponseBody body = new CreateResponseBody(getMessage());
            getRequestManager().createResponse(assignmentId, body, getCallbackListener());
        };
    }

    private CallbackListener<SendRespondResponse> getCallbackListener() {
        return new CallbackListener<SendRespondResponse>() {
            @Override
            public void onSuccess(SendRespondResponse o) {
                if (o.getCode() == 415
                        && o.getValidation() != null
                        && o.getValidation().getAssignment() != null) {
                    Toast.makeText(
                            getContext(),
                            o.getValidation().getAssignment().get(0),
                            Toast.LENGTH_LONG).show();
                } else if (o.getCode() == 405) {
                    Toast.makeText(
                            getContext(),
                            o.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else
                    {
                    dismiss();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
    }

    private String getMessage() {
        return ((EditText)findViewById(R.id.message)).getText().toString();
    }

    private RequestManager getRequestManager() {
        return RequestManager.getInstance();
    }
}
