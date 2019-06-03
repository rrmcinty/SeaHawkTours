package edu.uncw.seahawktours;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_BUILDINGID = "buildingId";
    private int buildingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        BuildingDetailFragment frag = (BuildingDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_frag);
        buildingId = (int) getIntent().getExtras().get(EXTRA_BUILDINGID);
        frag.setBuildingId(buildingId);
    }
}
