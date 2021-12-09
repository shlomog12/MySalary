package com.Final.mysalary.DTO;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.Final.mysalary.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ShiftsAdapter extends ArrayAdapter<Shift> {

    // invoke the suitable constructor of the ArrayAdapter class
    public ShiftsAdapter(@NonNull Context context, ArrayList<Shift> arrayList) {
        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.shift_viewer, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        Shift currentShift = getItem(position);

        // then according to the position of the view assign the desired image for the same
        ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
        assert currentShift != null;
//        numbersImage.setImageResource(currentShift.getNumbersImageId());

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentShift.getJobId());

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        LocalDateTime localDate = currentShift.getStart();//For reference
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String formattedString = localDate.format(formatter);
        textView2.setText(formattedString);

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView3 = currentItemView.findViewById(R.id.textView3);
        LocalDateTime localDate3 = currentShift.getEnd();//For reference
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("dd LLLL yyyy");
        String formattedString3 = localDate.format(formatter);
        textView2.setText(formattedString3);

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView4 = currentItemView.findViewById(R.id.textView4);
        textView2.setText((String.valueOf(currentShift.getTotalHours())));

        // then according to the position of the view assign the desired TextView 2 for the same
        TextView textView5 = currentItemView.findViewById(R.id.textView5);
        textView2.setText(String.valueOf(currentShift.getTotalSalary()));

        // then return the recyclable view
        return currentItemView;
    }
}
