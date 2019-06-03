package edu.uncw.seahawktours;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.objectbox.Box;


/**
 * this fragment shows the buildings details and adds share button to toolbar.
 */
public class BuildingDetailFragment extends Fragment {
    private int buildingId;
    private ShareActionProvider shareActionProvider;
    Box<Building> buildingBox = App.boxStore.boxFor(Building.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            buildingId = (int) savedInstanceState.getLong("buildingId");
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        Building building = buildingBox.get(buildingId);
        setShareActionIntent(getString(R.string.share_building_info) + getText(building.getTitle()) + ": " + getText(building.getUrl()));
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_building_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            //retrieve textview objects
            TextView title = (TextView) view.findViewById(R.id.building_name);
            TextView paragraph = (TextView) view.findViewById(R.id.building_info);
            TextView caption = (TextView) view.findViewById(R.id.building_caption);
            ImageView img = (ImageView) view.findViewById(R.id.building_img);
            TextView url = (TextView) view.findViewById(R.id.building_clickable_link);

            Building building = buildingBox.get(buildingId);

            title.setText(building.getTitle());
            paragraph.setText(building.getParagraph());
            caption.setText(building.getCaption());
            img.setImageResource(building.getImage());
            url.setText(building.getUrl());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("buildingId", buildingId);
    }

    public void setBuildingId(int id) {
        this.buildingId = id;
    }

}
