package com.example.csd_eindopdracht.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.csd_eindopdracht.R;

public class AlertPopUp extends Dialog {
    private final String title;
    private final String message;

    public AlertPopUp(Activity activity, String title, String message) {
        super(activity);
        this.title = title;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_pop_up);
        // TODO make corners transparent
        TextView titleView = findViewById(R.id.text_alert_title);
        TextView messageView = findViewById(R.id.text_alert_message);

        titleView.setText(title);
        messageView.setText(message);

        Button confirmButton = findViewById(R.id.btn_alert_confirm);
        confirmButton.setOnClickListener(view -> dismiss());
    }
}
