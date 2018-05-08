package com.example.android.jaundice;

import android.animation.ValueAnimator;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class JaundiceInformation extends AppCompatActivity {
    private CardView cardView;
    private TextView info_part2;
    private Button moreButton;
    private TextView title;
    private static final String TAG=JaundiceInformation.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jaundice_information);
        // to get title and details and image to send to adapter
        final ArrayList<information>informations= new ArrayList<>();

        informations.add(new information(getResources().getString(R.string.app_name),
                getResources().getString(R.string.jaundice_information_part1),
                getResources().getString(R.string.jaundice_information_part2),
                R.mipmap.jaundice_picture));
        informations.add(new information(getResources().getString(R.string.types_title),
                getResources().getString(R.string.types_information_part1),
                getResources().getString(R.string.types_information_part2),
                R.mipmap.jaundice_types));
        informations.add(new information(getResources().getString(R.string.causes_title),
                getResources().getString(R.string.causes_information_part1),
                getResources().getString(R.string.causes_information_part2),
                R.mipmap.causes));
        informations.add(new information(getResources().getString(R.string.signs_and_symptoms_of_jaundice_title),
                getResources().getString(R.string.signs_information_part1),
                getResources().getString(R.string.signs_information_part2),
                R.mipmap.symptoms));
        informations.add(new information(getResources().getString(R.string.treatment_title),
                getResources().getString(R.string.treatment_information_part1),
                getResources().getString(R.string.treatment_information_part2),
                R.mipmap.treatment));

            informationAdapter adapter = new informationAdapter(this,R.layout.item_details,informations);
        ListView informationList = findViewById(R.id.info_list);
        informationList.setAdapter(adapter);
        Log.i(TAG,"list");
        informationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            cardView =view.findViewById(R.id.card_view);
            title = view.findViewById(R.id.info_title);
            info_part2=view.findViewById(R.id.information_part2);
            moreButton= view.findViewById(R.id.more_button);
                updateCardview();



            }
        });



        

    }

    private void updateCardview() {


        int height= getExpandHeight();
        toggleCardViewnHeight(height);
    }

    private int getExpandHeight() {

        int height= cardView.getHeight()+info_part2.getHeight();
        String titleString = title.getText().toString();
        // increase height because  treatment have more information
        if ( titleString.equals(getString(R.string.treatment_title))) {
            height += 100;
            Log.i(TAG, height + "");
        }
        return height;
    }


    private void toggleCardViewnHeight(int height)
    {
        int minHeight = (int)getResources().getDimension(R.dimen.CardView_height_shrink);

        if (cardView.getHeight() == minHeight) {
            // expand
            expandView(height); //'height' is the height of screen which we have measured already.

        } else {
            // collapse
            collapseView(minHeight);

        }



    }

    private void collapseView(int minHeight) {
        info_part2.setVisibility(View.INVISIBLE);
        moreButton.setText("More");

        ValueAnimator anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                minHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = val;
                cardView.setLayoutParams(layoutParams);

            }
        });
        anim.start();
    }

    private void expandView(int height) {
        info_part2.setVisibility(View.VISIBLE);
        moreButton.setText("Less");
        ValueAnimator anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = val;
                cardView.setLayoutParams(layoutParams);
            }
        });
        anim.start();

    }
}
