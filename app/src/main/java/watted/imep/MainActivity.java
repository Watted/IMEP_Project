package watted.imep;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity/* implements nearestDatasource.OnPlacesArrivedListener */ {
    //
    private static final int RC_SIGN_IN = 1;
    private Button btnhelp;
    private Button btnnearest;
    private String mUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocationClient = new FusedLocationProviderClient(this);

        requestLocation();
        addAuthStateListener();
        addListenerOnHelp();
        addListenerOnNearest();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }

    private void addListenerOnNearest() {

        final Context context = this;

        btnnearest = findViewById(R.id.btnnearest);

        btnnearest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (location == null) {
                    Toast.makeText(context, "Location Not Found", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(context, NearestActivity.class);
                    intent.putExtra("location", location);
                    startActivity(intent);
                }

            }

        });


    }

    private void addListenerOnHelp() {

        final Context context = this;

        btnhelp = findViewById(R.id.btnhelp);

        btnhelp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, helpersActivity.class);
                startActivity(intent);

            }

        });

    }

    private void addAuthStateListener() {
        //Add Listener for login State
        //Check if the user is logged in: else -> gotoLogin
        //Realtime listener: logout ==> gotoLogin()
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                if (firebaseAuth.getCurrentUser() == null) {
                    gotoLogin();
                }
                else{
                 //   onSignedInInitialiaze(user.getDisplayName());
                }
            }
        });
    }

   /* private void onSignedInInitialiaze(String user) {
        mUsername= user;
    }*/

    private void signOut() {
        AuthUI.getInstance().signOut(this);
    }

    private void gotoLogin() {
        List<AuthUI.IdpConfig> loginProviders = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );
        //Intent -> for login screen
        Intent loginIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.logo)
                .setAvailableProviders(loginProviders)
                .build();


        startActivityForResult(loginIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //Success
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button sign_in_cancelled

                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //no_internet_connection
                    Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //Error connecting t0 server
                    return;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {

            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static final int RC_REQUEST_LOCATION = 1;
    //Location:
    //Fused -> Both Cellular AND GPS
    private FusedLocationProviderClient mLocationClient;

    private void requestLocation() {
        int checkPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION); //BitMask
        //if no permission -> Reuqest the permission (And Return from the method)
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            //Request the permission:
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_REQUEST_LOCATION);
            return;//get out of the method
        }
        requestLocationUpdates();
    }

    private Location location;

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10 * 1000);
        request.setFastestInterval(1 * 1000);

        request.setNumUpdates(1);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        System.out.println("meeeeeekom" + Manifest.permission.ACCESS_FINE_LOCATION);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationClient.requestLocationUpdates(request, cb, null);
    }

    LocationCallback cb = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            MainActivity.this.location = locationResult.getLastLocation();
            mLocationClient.removeLocationUpdates(this);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            Toast.makeText(MainActivity.this, locationAvailability.toString(), Toast.LENGTH_SHORT).show();
        }
    };


}
