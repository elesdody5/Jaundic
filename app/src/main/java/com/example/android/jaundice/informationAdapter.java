package com.example.android.jaundice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Elesdody on 25-Apr-18.
 */

public class informationAdapter extends ArrayAdapter<information> {






    public informationAdapter(@NonNull Context context, int resource, @NonNull List<information> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view==null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_details,parent,false);
        }

        information currentInfo=getItem(position);
         CardView cardView = view.findViewById(R.id.card_view);

         TextView title = view.findViewById(R.id.info_title);
         TextView infoPart1= view.findViewById(R.id.information_part1);
         TextView infoPart2 = view.findViewById(R.id.information_part2);
         ImageView image= view.findViewById(R.id.information_image);
        Button moreButton= view.findViewById(R.id.more_button);


        // set information
        title.setText(currentInfo.getTitle());
        infoPart1.setText(currentInfo.getPart1());
        infoPart2.setText(currentInfo.getPart2());
        image.setImageResource(currentInfo.getImage());


        return view;
    }


}
