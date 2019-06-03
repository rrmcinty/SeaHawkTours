package edu.uncw.seahawktours;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * creates boxstore and adds building opbject to it
 */
public class App extends Application {

    public static BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the main data access object
        boxStore = MyObjectBox.builder().androidContext(App.this).build();

        // Get the wrapper (Box) for the Building table that lets us store Building objects
        Box<Building> buildingBox = boxStore.boxFor(Building.class);

        // Initialize with some data
        if (buildingBox.count() == 0) {
            List<Building> initialBooks = new ArrayList<>();
            initialBooks.add(new Building("Bear Hall", R.string.bear_string, R.string.bear_caption, R.string.bear_paragraph, R.drawable.bear, R.string.bear_link, 34.228628, -77.872832));
            initialBooks.add(new Building("CIS Building", R.string.cis_string, R.string.cis_caption, R.string.cis_paragraph, R.drawable.cisbuilding, R.string.cis_link, 34.226250, -77.871832));
            initialBooks.add(new Building("Dobo Hall", R.string.dobo_string, R.string.dobo_caption, R.string.dobo_paragraph, R.drawable.dobo, R.string.dobo_link, 34.226774, -77.868277));
            initialBooks.add(new Building("DePaolo Hall", R.string.depaolo_string, R.string.depaolo_caption, R.string.depaolo_paragraph, R.drawable.depaolo, R.string.depaolo_link, 34.22608, -77.875564));
            initialBooks.add(new Building("Morton Hall", R.string.morton_string, R.string.morton_caption, R.string.morton_paragraph, R.drawable.morton, R.string.morton_link, 34.227681, -77.872639));


            // ObjectBox is smart enough to handle CRUD on Collections of entities
            buildingBox.put(initialBooks);
        }
    }
    //made boxStore static instead.
    //was having issues otherwise
    public BoxStore getBoxStore() {
        return boxStore;
    }
}
