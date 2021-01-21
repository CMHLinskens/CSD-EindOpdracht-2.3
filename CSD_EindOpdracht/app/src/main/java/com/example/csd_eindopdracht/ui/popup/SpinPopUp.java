package com.example.csd_eindopdracht.ui.popup;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.example.csd_eindopdracht.R;

public class SpinPopUp extends Dialog {

    long duration;
    float degrees;

    public SpinPopUp(Activity activity, long duration, float degrees) {
        super(activity);
        this.duration = duration;
        this.degrees = degrees;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spin_pop_up);

        ImageView wheel = findViewById(R.id.image_spin_wheel);
        Button okButton = findViewById(R.id.btn_spinPopUp_ok);

        // Initialize okButton
        okButton.setOnClickListener(view -> dismiss());

        // Create the rotate animation with desired parameters
        RotateAnimation anim = new RotateAnimation(0, degrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(duration);
        anim.setRepeatCount(0);
        anim.setFillAfter(true);

        // Start animation
        wheel.startAnimation(anim);
    }
}
