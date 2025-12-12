//package ru.plumsoftware.notebook.data.items;
//
//public class Note {
//    private int id, count, notePromoResId, isPinned, isLiked, color;
//    private String noteName, noteText;
//    private long addNoteTime;
//    private int opacity = 0x35674824;
//
//    public Note(int id, int count, int notePromoResId, int isPinned, int isLiked, int color, String noteName, String noteText, long addNoteTime, int opacity) {
//        this.id = id;
//        this.count = count;
//        this.notePromoResId = notePromoResId;
//        this.isPinned = isPinned;
//        this.isLiked = isLiked;
//        this.color = color;
//        this.noteName = noteName;
//        this.noteText = noteText;
//        this.addNoteTime = addNoteTime;
//        this.opacity = opacity;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }
//
//    public int getNotePromoResId() {
//        return notePromoResId;
//    }
//
//    public void setNotePromoResId(int notePromoResId) {
//        this.notePromoResId = notePromoResId;
//    }
//
//    public int getIsPinned() {
//        return isPinned;
//    }
//
//    public void setIsPinned(int isPinned) {
//        this.isPinned = isPinned;
//    }
//
//    public int getIsLiked() {
//        return isLiked;
//    }
//
//    public void setIsLiked(int isLiked) {
//        this.isLiked = isLiked;
//    }
//
//    public String getNoteName() {
//        return noteName;
//    }
//
//    public void setNoteName(String noteName) {
//        this.noteName = noteName;
//    }
//
//    public String getNoteText() {
//        return noteText;
//    }
//
//    public void setNoteText(String noteText) {
//        this.noteText = noteText;
//    }
//
//    public long getAddNoteTime() {
//        return addNoteTime;
//    }
//
//    public void setAddNoteTime(long addNoteTime) {
//        this.addNoteTime = addNoteTime;
//    }
//
//    public int getOpacity() {
//        return opacity;
//    }
//
//    public void setOpacity(int opacity) {
//        this.opacity = opacity;
//    }
//
//    public int getColor() {
//        return color;
//    }
//
//    public void setColor(int color) {
//        this.color = color;
//    }
//}
package ru.plumsoftware.data.model.ui;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Note implements Parcelable {
    private int id, count, notePromoResId, isPinned, isLiked, color, isNotify;
    private String noteName, noteText, notificationChannelId;
    private long addNoteTime;
    private int opacity = 0x35674824;

    public Note(
            int id,
            int count,
            int notePromoResId,
            int isPinned,
            int isLiked,
            int color,
            String noteName,
            String noteText,
            long addNoteTime,
            int opacity
    ) {
        this.id = id;
        this.count = count;
        this.notePromoResId = notePromoResId;
        this.isPinned = isPinned;
        this.isLiked = isLiked;
        this.color = color;
        this.noteName = noteName;
        this.noteText = noteText;
        this.addNoteTime = addNoteTime;
        this.opacity = opacity;
    }

    public Note(@NonNull Parcel in) {
        id = in.readInt();
        count = in.readInt();
        notePromoResId = in.readInt();
        isPinned = in.readInt();
        isLiked = in.readInt();
        color = in.readInt();
        noteName = in.readString();
        noteText = in.readString();
        addNoteTime = in.readLong();
        opacity = in.readInt();
        notificationChannelId = in.readString();
        isNotify = in.readInt();
    }

    public Note(
            int id,
            int count,
            int notePromoResId,
            int isPinned,
            int isLiked,
            int color,
            int isNotify,
            String noteName,
            String noteText,
            String notificationChannelId,
            long addNoteTime,
            int opacity
    ) {
        this.id = id;
        this.count = count;
        this.notePromoResId = notePromoResId;
        this.isPinned = isPinned;
        this.isLiked = isLiked;
        this.color = color;
        this.isNotify = isNotify;
        this.noteName = noteName;
        this.noteText = noteText;
        this.notificationChannelId = notificationChannelId;
        this.addNoteTime = addNoteTime;
        this.opacity = opacity;
    }

    public Note(
            int id,
            int count,
            int notePromoResId,
            int isPinned,
            int isLiked,
            int color,
            String noteName,
            String noteText,
            long addNoteTime,
            int opacity,
            String notificationChannelId
    ) {
        this.id = id;
        this.count = count;
        this.notePromoResId = notePromoResId;
        this.isPinned = isPinned;
        this.isLiked = isLiked;
        this.color = color;
        this.noteName = noteName;
        this.noteText = noteText;
        this.addNoteTime = addNoteTime;
        this.opacity = opacity;
        this.notificationChannelId = notificationChannelId;
    }

    public Note(
            int id,
            int count,
            int notePromoResId,
            int isPinned,
            int isLiked,
            int color,
            String noteName,
            String noteText,
            long addNoteTime,
            int opacity,
            String notificationChannelId,
            int isNotify
    ) {
        this.id = id;
        this.count = count;
        this.notePromoResId = notePromoResId;
        this.isPinned = isPinned;
        this.isLiked = isLiked;
        this.color = color;
        this.noteName = noteName;
        this.noteText = noteText;
        this.addNoteTime = addNoteTime;
        this.opacity = opacity;
        this.notificationChannelId = notificationChannelId;
        this.isNotify = isNotify;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNotePromoResId() {
        return notePromoResId;
    }

    public void setNotePromoResId(int notePromoResId) {
        this.notePromoResId = notePromoResId;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(int isPinned) {
        this.isPinned = isPinned;
    }

    public int getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(int isLiked) {
        this.isLiked = isLiked;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public long getAddNoteTime() {
        return addNoteTime;
    }

    public void setAddNoteTime(long addNoteTime) {
        this.addNoteTime = addNoteTime;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getNotificationChannelId() {
        return notificationChannelId;
    }

    public void setNotificationChannelId(String notificationChannelId) {
        this.notificationChannelId = notificationChannelId;
    }

    public int getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(int isNotify) {
        this.isNotify = isNotify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(count);
        dest.writeInt(notePromoResId);
        dest.writeInt(isPinned);
        dest.writeInt(isLiked);
        dest.writeInt(color);
        dest.writeString(noteName);
        dest.writeString(noteText);
        dest.writeLong(addNoteTime);
        dest.writeInt(opacity);
        dest.writeString(notificationChannelId);
        dest.writeInt(isNotify);
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return
                Integer.toString(id) + "\n" +
                        Integer.toString(count) + "\n" +
                        Integer.toString(notePromoResId) + "\n" +
                        Integer.toString(isPinned) + "\n" +
                        Integer.toString(isLiked) + "\n" +
                        Integer.toString(color) + "\n" +
                        noteName + "\n" +
                        noteText + "\n" +
                        new SimpleDateFormat("yyyy.MM.dd, hh:mm:ss", Locale.getDefault()).format(new Date(addNoteTime)) + "\n" +
                        Integer.toString(opacity) + "\n" +
                        notificationChannelId + "\n" +
                        Integer.toString(isNotify);


    }
}
