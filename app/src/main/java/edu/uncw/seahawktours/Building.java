package edu.uncw.seahawktours;

import android.app.Application;
import android.util.Log;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class Building extends Application {
    String TAG = "BUILDING";

    @Id
    private long id;

    @Index
    private int title;
    private int caption;
    private int paragraph;
    private int image;
    private int url;
    private double latitude;
    private double longitude;
    String string;

    /**
     * constructor required to fill App boxstore
     *
     * @param s string name
     * @param t title name
     * @param c caption
     * @param p building information paragraphs
     * @param i image id
     * @param u building information url
     */
    public Building(String s, int t, int c, int p, int i, int u, double lat, double lon) {
        this.title = t;
        this.caption = c;
        this.paragraph = p;
        this.image = i;
        this.url = u;
        this.string = s;
        this.latitude = lat;
        this.longitude = lon;
    }

    /**
     * empty constructor required by object box
     */
    public Building() {
        Log.i(TAG, "No-arg constructor used = " + string);
    }

    public String getString() {
        return string;
    }

    public void setString(String s) {
        this.string = s;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int t) {
        title = t;
    }

    public int getCaption() {
        return caption;
    }

    public int getParagraph() {
        return paragraph;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getUrl() {
        return url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
