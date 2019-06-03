package edu.uncw.seahawktours;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.objectbox.Box;

/**
 * This fragment displays the buildings in the main class with a reycler view using
 * CaptionedimagesAdapter
 */
public class BuildingListFragment extends Fragment {

    String TAG = "BUILDING LIST FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView started");
        Box<Building> buildingBox = App.boxStore.boxFor(Building.class);
        List<Building> buildingList = buildingBox.getAll();

        String[] buildingNames = new String[buildingList.size()];
        int[] buildingImages = new int[buildingList.size()];

        int index = 0;
        for (Building b : buildingList) {
            b.setId(index);
            index++;
        }

        for (int i = 0; i < buildingList.size(); i++) {
            int title = buildingList.get(i).getTitle();
            buildingNames[i] = getString(title);
            buildingImages[i] = buildingList.get(i).getImage();
            Log.i(TAG, "title= " + buildingNames[i] + " id = " + buildingList.get(i).getId());
        }
        final View root = inflater.inflate(R.layout.fragment_building, container, false);
        CaptionedImagesAdapter adapter = new CaptionedImagesAdapter(buildingNames, buildingImages);

        RecyclerView buildingRecycler = root.findViewById(R.id.building_recycler);
        buildingRecycler.setHasFixedSize(true);
        buildingRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        buildingRecycler.setAdapter(adapter);

        return root;
    }
}
