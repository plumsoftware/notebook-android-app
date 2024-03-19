package ru.plumsoftware.notebook.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.nativeads.MediaView;
import com.yandex.mobile.ads.nativeads.NativeAd;
import com.yandex.mobile.ads.nativeads.NativeAdEventListener;
import com.yandex.mobile.ads.nativeads.NativeAdException;
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration;
import com.yandex.mobile.ads.nativeads.NativeAdView;
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder;
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoadListener;
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.notebook.activities.AddNoteActivity;
import ru.plumsoftware.notebook.activities.MainActivity;
import ru.plumsoftware.notebook.dialogs.ProgressDialog;
import ru.plumsoftware.notebook.R;
import ru.plumsoftware.notebook.data.items.Note;
import ru.plumsoftware.notebook.databases.DatabaseConstants;
import ru.plumsoftware.notebook.databases.SQLiteDatabaseManager;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {
    private Context context;
    private Activity activity;
    private List<Note> noteList;

    private SQLiteDatabase sqLiteDatabaseNotes;
    private ProgressDialog progressDialog;

    private int
            color,
            opacityRes = R.drawable.ic_coffee,
            mode;
    private Calendar cal = Calendar.getInstance();
    private Calendar cal2 = Calendar.getInstance();

    public NoteAdapter(Context context, Activity activity, List<Note> noteList) {
        this.context = context;
        this.activity = activity;
        this.noteList = noteList;
    }

    public NoteAdapter(Context context, Activity activity, List<Note> noteList, int mode) {
        this.context = context;
        this.activity = activity;
        this.noteList = noteList;
        this.mode = mode;

        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(context).inflate(R.layout.note_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        // ADS
        MobileAds.initialize(context, new InitializationListener() {
            @Override
            public void onInitializationCompleted() {

            }
        });

        //Ads
        if (position == 2 || position == 8 || position == 15 || position == 20) {
//        if (position == -1) {

            holder.adsCard.setVisibility(View.GONE);

            //Load ad
            final NativeBulkAdLoader nativeBulkAdLoader = new NativeBulkAdLoader(context);
            final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder("R-M-1957919-1").build();
            //final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder("R-M-1769412-1").build();
            //final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder("R-M-1742395-1").build();
            nativeBulkAdLoader.loadAds(nativeAdRequestConfiguration, 1);
            nativeBulkAdLoader.setNativeBulkAdLoadListener(new NativeBulkAdLoadListener() {
                @Override
                public void onAdsLoaded(@NonNull final List<NativeAd> nativeAds) {
                    try {
                        for (final NativeAd nativeAd : nativeAds) {
                            final NativeAdViewBinder nativeAdViewBinder = new NativeAdViewBinder.Builder(holder.mNativeAdView)
                                    .setAgeView(holder.age)
                                    .setBodyView(holder.bodyView)
                                    .setCallToActionView(holder.call_to_action)
                                    .setDomainView(holder.domain)
                                    //.setFaviconView(notesViewHolder.favicon)
                                    .setFeedbackView(holder.imageViewFeedback)
                                    .setIconView(holder.favicon)
                                    .setMediaView(holder.mediaView)
                                    .setPriceView(holder.priceView)
                                    //.setRatingView((MyRatingView) findViewById(R.id.rating))
                                    //.setReviewCountView((TextView) findViewById(R.id.review_count))
                                    .setSponsoredView(holder.storeView)
                                    .setTitleView(holder.tvHeadline)
                                    .setWarningView(holder.warning)
                                    .build();

                            try {
                                nativeAd.bindNativeAd(nativeAdViewBinder);
                                nativeAd.setNativeAdEventListener(new NativeAdEventListener() {
                                    @Override
                                    public void onAdClicked() {

                                    }

                                    @Override
                                    public void onLeftApplication() {

                                    }

                                    @Override
                                    public void onReturnedToApplication() {

                                    }

                                    @Override
                                    public void onImpression(@Nullable ImpressionData impressionData) {

                                    }
                                });
                                holder.mNativeAdView.setVisibility(View.VISIBLE);
                                holder.adsCard.setVisibility(View.VISIBLE);
                            } catch (final NativeAdException exception) {
                                Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdsFailedToLoad(@NonNull final AdRequestError error) {
                    holder.adsCard.setVisibility(View.GONE);
                }
            });
        } else {
            holder.adsCard.setVisibility(View.GONE);
        }

        //Set data
        holder.tvTitle.setText(note.getNoteName());
        holder.tvNote.setText(note.getNoteText());

        holder.imageButtonPin.setVisibility(note.getIsPinned() == 1 ? View.VISIBLE : View.GONE);

        final int[] opacityRes = {note.getNotePromoResId()};
        final int[] color = {note.getColor()};

        cal.setTimeInMillis(note.getAddNoteTime());

        if (
                cal.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
                        cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                        cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
        ) {
            holder.tvAddDate.setText("Сегодня");
        } else {
            String addDate = new SimpleDateFormat("dd.MM.yyyy HH.mm", Locale.getDefault()).format(new Date(cal.getTimeInMillis()));
            holder.tvAddDate.setText(addDate);
        }

        holder.noteLogo.setImageResource(opacityRes[0]);

//        Card color
        holder.mainCard.setCardBackgroundColor(color[0]);

//        if (mode == 1)
//            holder.remove.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    activity.startActivity(new Intent(context, MainActivity.class));
//                    activity.overridePendingTransition(0, 0);
//                    activity.finish();
//                }
//            });
//        else
//            holder.remove.setVisibility(View.GONE);

//        Clickers
        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, AddNoteActivity.class);
                intent.putExtra("update", true);
                intent.putExtra("note", note);
                intent.putExtra("LoadInterstitialAd", true);
                intent.putExtra("isLoadAppOpenAd", false);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();

//                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
//                bottomSheetDialog.setContentView(R.layout.add_note_layout);
//                bottomSheetDialog.setCancelable(true);
//                bottomSheetDialog.setDismissWithAnimation(true);
//
//                ImageButton addOpacity = (ImageButton) bottomSheetDialog.findViewById(R.id.noteOpacity);
//                ImageButton addColor = (ImageButton) bottomSheetDialog.findViewById(R.id.noteColor);
//                ImageButton btnDone = (ImageButton) bottomSheetDialog.findViewById(R.id.btnDone1);
//                EditText tvTitle = (EditText) bottomSheetDialog.findViewById(R.id.Title);
//                EditText tvText = (EditText) bottomSheetDialog.findViewById(R.id.Text);
//                CardView cardViewBtnDone = (CardView) bottomSheetDialog.findViewById(R.id.cardBtnDone1);
//                //RecyclerView recyclerGroups = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerGroups);
//
//                //List<Group> groups = MainActivity.loadGroups(context);
//                //GroupAdapter groupAdapter = new GroupAdapter(context, activity, groups);
////                assert recyclerGroups != null;
////                recyclerGroups.setHasFixedSize(true);
////                recyclerGroups.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                //recyclerGroups.setAdapter(groupAdapter);
//
//                assert tvTitle != null;
//                tvTitle.setText(note.getNoteName());
//                assert tvText != null;
//                tvText.setText(note.getNoteText());
//
//                Objects.requireNonNull(cardViewBtnDone).setCardBackgroundColor(color[0]);
//
//                bottomSheetDialog.show();
//
//                //Clickers
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
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_blue)));
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_green)));
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_orange)));
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_pink)));
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_purple)));
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_red)));
//                        colors.add(new Colors(activity.getResources().getColor(R.color.note_yellow)));
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
//                //Opacity
//                Objects.requireNonNull(addOpacity).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(context, R.style.BottomSheetTheme);
//                        bottomSheetDialog1.setContentView(R.layout.color_picker);
//                        bottomSheetDialog1.setCancelable(true);
//                        bottomSheetDialog1.setDismissWithAnimation(true);
//
//                        GridView colorGridView = (GridView) bottomSheetDialog1.findViewById(R.id.colorGridView);
//
//                        ArrayList<Shape> shapes = new ArrayList<>();
//                        shapes.add(new Shape(R.drawable.ic_coffee));
//                        shapes.add(new Shape(R.drawable.ic_child_care));
//                        shapes.add(new Shape(R.drawable.ic_fitness_center));
//                        shapes.add(new Shape(R.drawable.ic_headphones));
//                        shapes.add(new Shape(R.drawable.ic_hotel));
//                        shapes.add(new Shape(R.drawable.ic_local_shipping));
//                        shapes.add(new Shape(R.drawable.ic_perm_phone_msg));
//                        shapes.add(new Shape(R.drawable.ic_phishing));
//                        shapes.add(new Shape(R.drawable.ic_work));
//                        shapes.add(new Shape(R.drawable.ic_work_outline));
//                        shapes.add(new Shape(R.drawable.ic_receipt_long));
//                        shapes.add(new Shape(R.drawable.ic_rocket_launch));
//                        shapes.add(new Shape(R.drawable.ic_school));
//                        shapes.add(new Shape(R.drawable.ic_shopping_basket));
//                        shapes.add(new Shape(R.drawable.ic_spa));
//                        shapes.add(new Shape(R.drawable.ic_square_foot));
//                        shapes.add(new Shape(R.drawable.ic_grade));
//
//                        OpacityAdapter shapeAdapter = new OpacityAdapter(context, 0, shapes);
//                        Objects.requireNonNull(colorGridView).setAdapter(shapeAdapter);
//
//                        bottomSheetDialog1.show();
//
//                        //Clicker
//                        colorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                opacityRes[0] = shapes.get(position).getShapeRes();
//                                bottomSheetDialog1.dismiss();
//                            }
//                        });
//                    }
//                });
//
//                //Done
//                Objects.requireNonNull(btnDone).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        progressDialog.showDialog();
//                        String name = Objects.requireNonNull(tvTitle).getText().toString();
//                        String text = Objects.requireNonNull(tvText).getText().toString();
//
////                        SAVE
//                        long time = System.currentTimeMillis();
//
//                        if (name.isEmpty())
//                            name = "";
//                        if (text.isEmpty())
//                            text = "";
//
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(DatabaseConstants._NOTE_NAME, name);
//                        contentValues.put(DatabaseConstants._NOTE_TEXT, text);
//                        contentValues.put(DatabaseConstants._NOTE_PROMO, opacityRes[0]);
//                        contentValues.put(DatabaseConstants._NOTE_COLOR, color[0]);
//                        contentValues.put(DatabaseConstants._IS_LIKED, note.getIsLiked());
//                        contentValues.put(DatabaseConstants._IS_PINNED, note.getIsPinned());
//                        contentValues.put(DatabaseConstants._ADD_NOTE_TIME, note.getAddNoteTime());
//                        sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(note.getAddNoteTime())});
//                        //NotepadActivity.reloadRecyclerView(context, activity);
//                        activity.startActivity(new Intent(context, NotepadActivity.class));
//                        activity.overridePendingTransition(0, 0);
//                        activity.finish();
//
//                        List<Group> groupList = GroupAdapter.addedGroups;
//
//                        for (int i = 0; i < groupList.size(); i++) {
//                            String nameG = groupList.get(i).getName();
//
//                            ContentValues contentValuesG = new ContentValues();
//                            contentValuesG.put(DatabaseConstants._GROUP_NAME, nameG);
//                            contentValuesG.put(DatabaseConstants._NOTE_COLOR, color[0]);
//                            contentValuesG.put(DatabaseConstants._ADD_NOTE_TIME, time);
//                            contentValuesG.put(DatabaseConstants._ADD_NOTE_TIME, System.currentTimeMillis());
//                            NotepadActivity.sqLiteDatabaseNotes.update(DatabaseConstants._GROUPS_TABLE_NAME, contentValuesG, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(time)});
//                            try {
////                                NotepadActivity.reloadRecyclerView(context, activity);
//                                activity.startActivity(new Intent(context, NotepadActivity.class));
//                                activity.overridePendingTransition(0, 0);
//                                activity.finish();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        progressDialog.dismiss();
//                        bottomSheetDialog.dismiss();
//                    }
//                });
//
//                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
////                        UPDATE
////                        NotepadActivity.reloadRecyclerView(context, activity);
//                    }
//                });
            }
        });
        holder.mainCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                }
                if (note.getIsPinned() == 0)
                    showPopupMenu(view, note.getAddNoteTime(), note);
                else if (note.getIsPinned() == 1)
                    showPopupMenu2(view, note.getAddNoteTime(), note);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private void showPopupMenu(View v, long addTime, Note note) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.popup_menu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                progressDialog.showDialog();
                                sqLiteDatabaseNotes.delete(DatabaseConstants._NOTES_TABLE_NAME, DatabaseConstants._ADD_NOTE_TIME + " = ? ", new String[]{Long.toString(addTime)});
//                                NotepadActivity.reloadRecyclerView(context, activity);
                                activity.startActivity(new Intent(context, MainActivity.class));
                                activity.overridePendingTransition(0, 0);
                                activity.finish();
                                return true;
                            case R.id.pin:
                                progressDialog.showDialog();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DatabaseConstants._NOTE_NAME, note.getNoteName());
                                contentValues.put(DatabaseConstants._NOTE_TEXT, note.getNoteText());
                                contentValues.put(DatabaseConstants._NOTE_PROMO, note.getNotePromoResId());
                                contentValues.put(DatabaseConstants._NOTE_COLOR, note.getColor());
                                contentValues.put(DatabaseConstants._IS_LIKED, 0);
                                contentValues.put(DatabaseConstants._IS_PINNED, 1);
                                contentValues.put(DatabaseConstants._IS_NOTIFY, note.getIsNotify());
                                contentValues.put(DatabaseConstants._CHANNEL_ID, note.getNotificationChannelId());
                                contentValues.put(DatabaseConstants._ADD_NOTE_TIME, note.getAddNoteTime());
                                sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(addTime)});
//                                NotepadActivity.reloadRecyclerView(context, activity);
                                activity.startActivity(new Intent(context, MainActivity.class));
                                activity.overridePendingTransition(0, 0);
                                activity.finish();
                                return false;
                            default:
                                progressDialog.dismiss();
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

    private void showPopupMenu2(View v, long addTime, Note note) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.popup_menu_2);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
                        sqLiteDatabaseNotes = sqLiteDatabaseManager.getWritableDatabase();
                        switch (item.getItemId()) {
                            case R.id.delete:
                                progressDialog.showDialog();
                                sqLiteDatabaseNotes.delete(DatabaseConstants._NOTES_TABLE_NAME, DatabaseConstants._ADD_NOTE_TIME + " = ? ", new String[]{Long.toString(addTime)});
//                                NotepadActivity.reloadRecyclerView(context, activity);
                                activity.startActivity(new Intent(context, MainActivity.class));
                                activity.overridePendingTransition(0, 0);
                                activity.finish();
                                return true;
                            case R.id.unpin:
                                progressDialog.showDialog();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DatabaseConstants._NOTE_NAME, note.getNoteName());
                                contentValues.put(DatabaseConstants._NOTE_TEXT, note.getNoteText());
                                contentValues.put(DatabaseConstants._NOTE_PROMO, note.getNotePromoResId());
                                contentValues.put(DatabaseConstants._NOTE_COLOR, note.getColor());
                                contentValues.put(DatabaseConstants._IS_LIKED, 0);
                                contentValues.put(DatabaseConstants._IS_PINNED, 0);
                                contentValues.put(DatabaseConstants._IS_NOTIFY, note.getIsNotify());
                                contentValues.put(DatabaseConstants._CHANNEL_ID, note.getNotificationChannelId());
                                contentValues.put(DatabaseConstants._ADD_NOTE_TIME, note.getAddNoteTime());
                                sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(addTime)});
//                                NotepadActivity.reloadRecyclerView(context, activity);
                                activity.startActivity(new Intent(context, MainActivity.class));
                                activity.overridePendingTransition(0, 0);
                                activity.finish();
                                return false;
                            default:
                                progressDialog.dismiss();
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

class NoteViewHolder extends RecyclerView.ViewHolder {
    protected TextView tvTitle, tvNote, tvAddDate;
    protected CardView mainCard, adsCard;
    //protected TextView remove;
    protected ImageButton imageButtonPin;
    protected ImageButton noteLogo;

    protected NativeAdView mNativeAdView;
    protected MediaView mediaView;
    protected TextView age;
    protected TextView bodyView;
    protected TextView call_to_action;
    protected TextView priceView;
    protected TextView storeView;
    protected TextView tvHeadline;
    protected TextView warning;
    protected TextView domain;
    protected ImageView favicon;
    protected ImageView imageViewFeedback;

    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);

        setIsRecyclable(true);

        tvTitle = (TextView) itemView.findViewById(R.id.textViewNoteName);
        tvNote = (TextView) itemView.findViewById(R.id.textView);
        tvAddDate = (TextView) itemView.findViewById(R.id.tvAddDate);
        mainCard = (CardView) itemView.findViewById(R.id.cardView1);
        adsCard = (CardView) itemView.findViewById(R.id.cardView2);
        //remove = (TextView) itemView.findViewById(R.id.tvRemove);
        noteLogo = (ImageButton) itemView.findViewById(R.id.buttonPromo);
        imageButtonPin = (ImageButton) itemView.findViewById(R.id.imageButton);

        //Native ads
        mNativeAdView = (NativeAdView) itemView.findViewById(R.id.nativeAdView);
        mediaView = (MediaView) itemView.findViewById(R.id.media);
        age = (TextView) itemView.findViewById(R.id.age);
        bodyView = (TextView) itemView.findViewById(R.id.tvAdvertiser);
        call_to_action = (TextView) itemView.findViewById(R.id.btnVisitSite);
        domain = (TextView) itemView.findViewById(R.id.textViewDomain);
        favicon = (ImageView) itemView.findViewById(R.id.adsPromo);
        imageViewFeedback = (ImageView) itemView.findViewById(R.id.imageViewFeedback);
        priceView = (TextView) itemView.findViewById(R.id.priceView);
        storeView = (TextView) itemView.findViewById(R.id.storeView);
        tvHeadline = (TextView) itemView.findViewById(R.id.tvHeadline);
        warning = (TextView) itemView.findViewById(R.id.textViewWarning);
    }
}
