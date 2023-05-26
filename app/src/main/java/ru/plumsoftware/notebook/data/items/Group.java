package ru.plumsoftware.notebook.data.items;

public class Group {
    private String name;
    private int id, color;
    private long addGroup, addNote;

    public Group(String name, int id, int color, long addGroup, long addNote) {
        this.name = name;
        this.id = id;
        this.color = color;
        this.addGroup = addGroup;
        this.addNote = addNote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getAddGroup() {
        return addGroup;
    }

    public void setAddGroup(long addGroup) {
        this.addGroup = addGroup;
    }

    public long getAddNote() {
        return addNote;
    }

    public void setAddNote(long addNote) {
        this.addNote = addNote;
    }
}
