package ru.plumsoftware.notebook.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.plumsoftware.notebook.activities.MainActivity;
import ru.plumsoftware.notebook.activities.NotepadActivity;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.data.items.Group;
import ru.plumsoftware.notebook.databases.DatabaseConstants;

public class GroupAdapter extends RecyclerView.Adapter<GroupViewHolder> {
    private Context context;
    private Activity activity;
    private List<Group> groupList;
    private int mode = 0;
    public static List<Group> addedGroups = new ArrayList<>();

    public GroupAdapter(Context context, Activity activity, List<Group> groupList) {
        this.context = context;
        this.activity = activity;
        this.groupList = groupList;
    }

    public GroupAdapter(Context context, Activity activity, List<Group> groupList, int mode) {
        this.context = context;
        this.activity = activity;
        this.groupList = groupList;
        this.mode = mode;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(context).inflate(R.layout.group_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        holder.cardView.setCardBackgroundColor(group.getColor());
        holder.textView.setText(group.getName());

        final int[] color = {group.getColor()};

//        Clickers
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                showPopupMenu(view, group);
                return false;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
//                bottomSheetDialog.setContentView(R.layout.add_group_layout);
//                bottomSheetDialog.setCancelable(true);
//                bottomSheetDialog.setDismissWithAnimation(true);
//
//                ImageButton addColor = (ImageButton) bottomSheetDialog.findViewById(R.id.noteColor);
//                ImageButton btnDone = (ImageButton) bottomSheetDialog.findViewById(R.id.btnDone);
//                EditText tvTitle = (EditText) bottomSheetDialog.findViewById(R.id.Title);
//                CardView cardViewBtnDone = (CardView) bottomSheetDialog.findViewById(R.id.cardBtnDone);
//
//                color[0] = group.getColor();
//
//                Objects.requireNonNull(cardViewBtnDone).setCardBackgroundColor(color[0]);
//
//                bottomSheetDialog.show();
//
//                //Color
//                Objects.requireNonNull(addColor).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(context, R.style.BottomSheetTheme);
//                        bottomSheetDialog1.setContentView(R.layout.color_picker);
//                        bottomSheetDialog1.setCancelable(true);
//                        bottomSheetDialog1.setDismissWithAnimation(true);
//
//                        GridView colorGridView = (GridView) bottomSheetDialog1.findViewById(R.id.colorGridView);
//
//                        ArrayList<Colors> colors = new ArrayList<>();
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_blue)));
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_green)));
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_orange)));
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_pink)));
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_purple)));
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_red)));
//                        colors.add(new Colors(context.getResources().getColor(R.color.note_yellow)));
//
//                        ColorAdapter colorAdapter = new ColorAdapter(context, 0, colors);
//                        Objects.requireNonNull(colorGridView).setAdapter(colorAdapter);
//
//                        bottomSheetDialog1.show();
//
//                        //Clicker
//                        colorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                color[0] = colors.get(position).getColorRes();
//                                bottomSheetDialog1.dismiss();
//
//                                cardViewBtnDone.setCardBackgroundColor(color[0]);
//                            }
//                        });
//                    }
//                });
//
//                Objects.requireNonNull(tvTitle).setText(group.getName());
//
//                //Done
//                Objects.requireNonNull(btnDone).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String name = Objects.requireNonNull(tvTitle).getText().toString();
//
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(DatabaseConstants._GROUP_NAME, name);
//                        contentValues.put(DatabaseConstants._NOTE_COLOR, color[0]);
//                        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, 0);
//                        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, System.currentTimeMillis());
//                        //MainActivity.sqLiteDatabaseNotes.update(DatabaseConstants._GROUPS_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(note.getAddNoteTime())});
//                        try {
//                            NotepadActivity.reloadRecyclerView(context, activity);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//
//                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        activity.startActivity(new Intent(context, NotepadActivity.class));
//                        activity.overridePendingTransition(0, 0);
//                        activity.finish();
//                    }
//                });
                switch (mode) {
                    case 0:
                        if (!addedGroups.contains(group)) {
                            addedGroups.add(group);
                            Toast.makeText(context, "Записка будет добавлена в группу: " + group.getName() + ". Сохраните изменения.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        activity.startActivity(new Intent(context, NotepadActivity.class).putExtra("group", group.getName()));
                        activity.overridePendingTransition(0, 0);
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    private void showPopupMenu(View v, Group group) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.popup_menu_3);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                MainActivity.sqLiteDatabaseNotes.delete(DatabaseConstants._GROUPS_TABLE_NAME, DatabaseConstants._GROUP_NAME + " = ? ", new String[]{group.getName()});
                                try {
                                    //NotepadActivity.reloadRecyclerViewGroups(context, activity);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }
}

class GroupViewHolder extends RecyclerView.ViewHolder {
    protected CardView cardView;
    protected TextView textView;


    public GroupViewHolder(@NonNull View itemView) {
        super(itemView);

        cardView = (CardView) itemView.findViewById(R.id.cardView1);
        textView = (TextView) itemView.findViewById(R.id.textViewNoteName);
    }
}
