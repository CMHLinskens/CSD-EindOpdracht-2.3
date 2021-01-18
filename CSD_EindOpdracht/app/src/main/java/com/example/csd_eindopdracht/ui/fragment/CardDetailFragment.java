package com.example.csd_eindopdracht.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.ui.popup.CardDescriptionPopUp;
import com.squareup.picasso.Picasso;

public class CardDetailFragment extends Fragment {
    Collectable collectable;

    public CardDetailFragment(Collectable collectable){
        this.collectable = collectable;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_detail, container, false);

        ImageView cardImage = view.findViewById(R.id.image_card_detail);
        cardImage.setOnClickListener(v -> {
            CardDescriptionPopUp popUp = new CardDescriptionPopUp(getActivity(), collectable.getDescription());
            popUp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popUp.getWindow().setGravity(Gravity.CENTER|Gravity.BOTTOM);
            popUp.show();
        });

        Picasso.get().load(collectable.getImgLink()).placeholder(R.drawable.card_placeholder).into(cardImage);

        return view;
    }
}
