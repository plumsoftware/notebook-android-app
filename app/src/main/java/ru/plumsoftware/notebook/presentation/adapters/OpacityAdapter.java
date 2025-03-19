package ru.plumsoftware.notebook.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import ru.plumsoftware.data.model.ui.Shape;
import ru.plumsoftware.notebook.R;

public class OpacityAdapter extends ArrayAdapter<Shape> {
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Shape shape = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.color_layout, parent, false);
        }

        assert shape != null;
        int shapeRes = shape.getShapeRes();

        ImageView imageView = (ImageView) convertView.findViewById(R.id.shapeView);
        imageView.setBackgroundResource(shapeRes);

        return convertView;
    }

    public OpacityAdapter(@NonNull Context context, int resource, @NonNull List<Shape> objects) {
        super(context, resource, objects);
    }
}
