package com.example.monitoring_sw;

/**
 * Represents an item in a ToDo list
 */
public class Uzytkownicy {


    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("nazwa")
    private String mNazwa;

    @com.google.gson.annotations.SerializedName("haslo")
    private String mHaslo;

    @com.google.gson.annotations.SerializedName("typ")
    private String mTyp;
    /**
     * ToDoItem constructor
     */
    public Uzytkownicy() {

    }

   /* @Override
    public String toString() {
        return getText();
    }
*/
    public Uzytkownicy(String nazwa, String haslo,String typ, String id) {
        this.setNazwa(nazwa);
        this.setHaslo(haslo);
        this.setTyp(typ);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getNazwa() {
        return mNazwa;
    }
    public String getHaslo() {
        return mHaslo;
    }
    public String getTyp() {
        return mTyp;
    }


    public final void setNazwa(String nazwa) {
        mNazwa = nazwa;
    }
    public final void setHaslo(String haslo) {
        mHaslo = haslo;
    }
    public final void setTyp(String typ) {
        mTyp = typ;
    }

    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }

    /**
     * Indicates if the item is marked as completed
     */


    /**
     * Marks the item as completed or incompleted
     */

    @Override
    public boolean equals(Object o) {
        return o instanceof Uzytkownicy && ((Uzytkownicy) o).mId == mId;
    }
}