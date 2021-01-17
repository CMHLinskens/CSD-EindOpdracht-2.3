package com.example.csd_eindopdracht.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.csd_eindopdracht.R;
import com.example.csd_eindopdracht.dataModel.Data;
import com.example.csd_eindopdracht.dataModel.collectable.Collectable;
import com.example.csd_eindopdracht.ui.popup.ChooseCardPopUp;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RewardFragment extends Fragment {

    private Collectable yourCollectable;
    private Collectable cachedCollectable;
    private Button chooseCardButton;
    private TextView rarityBonusTextView;
    private Button acceptButton;
    private Button refuseButton;

    public RewardFragment(Collectable collectable) {
        this.cachedCollectable = collectable;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward, container, false);

        initializeButtons(view);
        initializeCachedCollectable(view);

        return view;
    }

    private void initializeCachedCollectable(View view) {
        View cachedCollectableView = view.findViewById(R.id.placeholder_collectable_cached);
        TextView nameTextView = cachedCollectableView.findViewById(R.id.text_view_collectable_name);
        ImageView cardImageView = cachedCollectableView.findViewById(R.id.image_view_collectable_card);

        nameTextView.setText(cachedCollectable.getName());
        Picasso.get().load(cachedCollectable.getImgLink()).placeholder(R.drawable.card_placeholder).into(cardImageView);
    }

    private void updateYourCollectable(View view) {
        View yourCollectableView = view.findViewById(R.id.placeholder_collectable_your);
        TextView nameTextView = yourCollectableView.findViewById(R.id.text_view_collectable_name);
        ImageView cardImageView = yourCollectableView.findViewById(R.id.image_view_collectable_card);

        nameTextView.setText(yourCollectable.getName());
        Picasso.get().load(yourCollectable.getImgLink()).placeholder(R.drawable.card_placeholder).into(cardImageView);

        rarityBonusTextView = view.findViewById(R.id.text_view_reward_rarity_bonus);
        rarityBonusTextView.setText(getString(R.string.rarity_bonus) + 0 + getString(R.string.points)); // TODO: decide rarity points
    }

    private void initializeButtons(View view) {
        initializeChooseCardButton(view);
        initializeAcceptButton(view);
        initializeRefuseButton(view);
    }

    private void initializeChooseCardButton(View view) {
        chooseCardButton = view.findViewById(R.id.btn_reward_choose);
        ChooseCardPopUp chooseCardPopUp = new ChooseCardPopUp(getContext(), yourCollectable);
        chooseCardButton.setOnClickListener(v -> {
            chooseCardPopUp.setOnDismissListener(dialog -> {
                if (yourCollectable != null)
                    updateYourCollectable(view);
            });
            chooseCardPopUp.show();
        });
    }

    private void initializeAcceptButton(View view) {
        acceptButton = view.findViewById(R.id.btn_reward_accept);
        acceptButton.setOnClickListener(v -> {
            // TODO: add alert pop-up: are you sure you accept this trade?
            List<Collectable> inventory = Data.INSTANCE.getInventory();
            inventory.remove(yourCollectable);
            inventory.add(cachedCollectable);
            // TODO: add rarity bonus
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit(); // TODO: use factory
        });
    }

    private void initializeRefuseButton(View view) {
        refuseButton = view.findViewById(R.id.btn_reward_refuse);
        refuseButton.setOnClickListener(v -> {
            // TODO: add alert pop-up: are you sure you refuse this trade?
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit(); // TODO: use factory
        });
    }
}
