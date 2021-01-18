package com.example.csd_eindopdracht.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.csd_eindopdracht.R;

public class CardDescriptionPopUp extends Dialog {
    private final String description;

    public CardDescriptionPopUp(Activity activity, String description){
        super(activity);
        this.description = description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.card_description_pop_up);

        TextView descriptionView = findViewById(R.id.text_card_description_pop_up);

        descriptionView.setText(description);
    }
}
