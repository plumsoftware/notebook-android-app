package ru.plumsoftware.notebook.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.List;

import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.data.items.Colors;

public class ColorAdapter extends ArrayAdapter<Colors> {
    public ColorAdapter(@NonNull Context context, int resource, @NonNull List<Colors> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(R.layout.color_layout, parent, false);
        Colors colors = getItem(position);

        int colorRes = colors.getColorRes();

        CardView cardView = (CardView) convertView.findViewById(R.id.colorView);
        cardView.setCardBackgroundColor(colorRes);

        return convertView;
    }
}
