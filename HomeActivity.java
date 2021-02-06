package com.example.friendtracker.Activity;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.friendtracker.Fragment.AddFriendFragment;
import com.example.friendtracker.Fragment.ChangePasswordFragment;
import com.example.friendtracker.Fragment.FriendListFragment;
import com.example.friendtracker.Fragment.HomeFragment;
import com.example.friendtracker.Fragment.MyProfileEditFragment;
import com.example.friendtracker.Libs.GlobalDataService;
import com.example.friendtracker.Libs.PopupWindow;
import com.example.friendtracker.Model.UserDetails;
import com.example.friendtracker.Model.UserLatLng;
import com.example.friendtracker.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.friendtracker.Fragment.HomeFragment.autoUpdateCurrentLocation;
import static com.example.friendtracker.Libs.Params.PLACE;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int FINE_LOCATION_PERMISSION = 101;
    public TextView currentScreenName;
    public RelativeLayout addFriendBtn;
    FragmentManager manager;
    FragmentTransaction ft;
    View.OnClickListener actionBarBackBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private DatabaseReference databaseReference;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    View.OnClickListener editUserBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                pushFragments(new MyProfileEditFragment(), true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener addFriendBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                }
                pushFragments(new AddFriendFragment(), true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener showKeyBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                PopupWindow.showKeyPopup(HomeActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener changePasswordBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                pushFragments(new ChangePasswordFragment(), true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener homeBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                pushFragments(new HomeFragment(), true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener helpBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                PopupWindow.showHelpPopup(HomeActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener friendsBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                pushFragments(new FriendListFragment(), true, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener logOutBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                drawerLayout.closeDrawer(Gravity.START);
                PopupWindow.showLogoutPopup(HomeActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private RelativeLayout editUserBtn;
    private UserDetails userDetails;
    private Toolbar toolbar;
    private TextView nameTv, homeBtn, showKeyBtn, friendsBtn, logOutBtn, changePasswordBtn, helpBtn;
    private ImageView actionBarBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home);
            findAllIds();
            userDetails = GlobalDataService.getInstance().getUserDetails(HomeActivity.this);
            actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this, drawerLayout, toolbar, R.string.open, R.string.close);
            actionBarDrawerToggle.syncState();
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            showUserName();
            databaseReference = FirebaseDatabase.getInstance().getReference(PLACE);
            createLocationRequest();
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        autoUpdateLocation(latitude, longitude);
                        autoUpdateCurrentLocation(HomeActivity.this, latitude, longitude);
                        GlobalDataService.getInstance().setMyLatitude(latitude);
                        GlobalDataService.getInstance().setMyLongitude(longitude);
                        Log.d("dfshfhsfd", "Latitude Updated: " + latitude + " Longitude updated: " + longitude);
                    }
                }
            };
            checkPermissionLocation();
            pushFragments(new HomeFragment(), false, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoUpdateLocation(double latitude, double longitude) {
        try {
            UserLatLng userLatLng = new UserLatLng(latitude, longitude);
            String currentUserId = userDetails.getUserId();
            databaseReference.child(currentUserId).setValue(userLatLng);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUserName() {
        try {
            String nameStr = userDetails.getFirstName() + " " + userDetails.getLastName();
            nameTv.setText(nameStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findAllIds() {
        try {
            drawerLayout = findViewById(R.id.drawerLayout);
            toolbar = findViewById(R.id.toolbar);
            nameTv = findViewById(R.id.nameTv);
            showKeyBtn = findViewById(R.id.showKeyBtn);
            friendsBtn = findViewById(R.id.friendsBtn);
            logOutBtn = findViewById(R.id.logOutBtn);
            editUserBtn = findViewById(R.id.editUserBtn);
            addFriendBtn = findViewById(R.id.addFriendBtn);
            changePasswordBtn = findViewById(R.id.changePasswordBtn);
            currentScreenName = findViewById(R.id.currentScreenName);
            actionBarBackBtn = findViewById(R.id.actionBarBackBtn);
            helpBtn = findViewById(R.id.helpBtn);
            homeBtn = findViewById(R.id.homeBtn);
            actionBarBackBtn.setVisibility(View.GONE);

            showKeyBtn.setOnClickListener(showKeyBtnListener);
            friendsBtn.setOnClickListener(friendsBtnListener);
            logOutBtn.setOnClickListener(logOutBtnListener);
            editUserBtn.setOnClickListener(editUserBtnListener);
            addFriendBtn.setOnClickListener(addFriendBtnListener);
            changePasswordBtn.setOnClickListener(changePasswordBtnListener);
            homeBtn.setOnClickListener(homeBtnListener);
            helpBtn.setOnClickListener(helpBtnListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pushFragments(Fragment fragment, boolean shouldAdd, String tag) {
        try {
            manager = getSupportFragmentManager();
            ft = manager.beginTransaction();
            ft.replace(R.id.contentMain, fragment, tag);
            if (manager.getBackStackEntryCount() >= 0) {
                //  actionBarBackBtn.setVisibility(View.VISIBLE);
            }
            if (shouldAdd) {
                ft.addToBackStack(null).commit();
            } else {
                manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ft.commit();
                //  actionBarBackBtn.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void checkPermissionLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            FINE_LOCATION_PERMISSION);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            FINE_LOCATION_PERMISSION);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    createLocationRequest();
                    Log.d("dfshfhsfd", "onRequestPermissionsResult: Fine location permission granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    protected void createLocationRequest() {
        try {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.d("dfshfhsfd", "UserLatLng setting task success");
                    // All location settings are satisfied. The client can initialize
                    // location requests here.
                    // ...
                }
            });

            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // UserLatLng settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Log.d("dfshfhsfd", "UserLatLng setting task failure");
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(HomeActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        checkPermissionLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            checkPermissionLocation();
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            fusedLocationClient.removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
