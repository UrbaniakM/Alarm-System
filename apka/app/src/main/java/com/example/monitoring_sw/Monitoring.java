package com.example.monitoring_sw;

/**
 * Represents an item in a ToDo list
 */
public class Monitoring {


    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("urzadzenie")
    private String mUrzadzenie;

    @com.google.gson.annotations.SerializedName("stan")
    private Integer mStan;
    /**
     * ToDoItem constructor
     */
    public Monitoring() {

    }

   /* @Override
    public String toString() {
        return getText();
    }
*/
    public Monitoring(String urzadzenie, Integer stan, String id) {
        this.setUrzadzenie(urzadzenie);
        this.setStan(stan);
        this.setId(id);
    }

    /**
     * Returns the item text
     */
    public String getUrzadzenie() {
        return mUrzadzenie;
    }
    public Integer getStan() {
        return mStan;
    }



    public final void setUrzadzenie(String urzadzenie) {
        mUrzadzenie = urzadzenie;
    }
    public final void setStan(Integer stan) {
        mStan = stan;
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
        return o instanceof Monitoring && ((Monitoring) o).mId == mId;
    }
}