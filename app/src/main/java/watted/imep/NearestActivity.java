package watted.imep;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Amr on 01/08/2018.
 */

public class NearestActivity extends Activity implements PlacesDataSource.OnPlacesArrivedListener {
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_palace);

        Intent intent = getIntent();
        Location location = intent.getParcelableExtra("location");


        Toast.makeText(this, location.toString(), Toast.LENGTH_SHORT).show();
        PlacesDataSource.getPlaces(this, location);
    }

    public void onPlacesArrived(@Nullable final ArrayList<Place> places, @Nullable final Exception e) {
        //The places are received in a background thread.

        //runOnUIThread(Runnable runnable)
        //run on UI thread... a method that runs code on the ui (main) thread.

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //code that runs on the UI (Main) Thread...!

                if (places != null) {
                    updateUI(places);
                } else if (e != null) {

                    Toast.makeText(NearestActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    //TODO: present a dialog to the user... no internet
                }
            }
        });
    }

    private void updateUI(ArrayList<Place> palaces) {
        //1) find the recycler by id.

        RecyclerView rvPalaces = findViewById(R.id.rvPalaces);


        //the adapter takes Palaces and provides Views for the palaces.
        //2) MoviesAdapter adapter = new Movies adapter (palaces, context)
        PalacAdapter adapter = new PalacAdapter(palaces, NearestActivity.this);

        //3) recycler -> take the adapter.
        rvPalaces.setAdapter(adapter);

        //4)
        rvPalaces.setLayoutManager(new LinearLayoutManager(null));

    }


}
