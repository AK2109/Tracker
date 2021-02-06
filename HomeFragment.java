package com.example.friendtracker.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Libs.GlobalDataService;
import com.example.friendtracker.Libs.LocationAddress;
import com.example.friendtracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap mMap;
    private static TextView myLatitude, myLongitude, myAddress;
    View view;

    public static void autoUpdateCurrentLocation(Context context, double latitude, double longitude) {
        try {
            String myLatitudeStr = latitude + "";
            String myLongitudeStr = longitude + "";
            myLatitude.setText(myLatitudeStr);
            myLongitude.setText(myLongitudeStr);
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    context, new HomeFragment.GeocoderHandler());
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            final LatLng latLng = new LatLng(latitude, longitude);
            ((HomeActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            findAllIds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void findAllIds() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myLatitude = view.findViewById(R.id.myLatitude);
        myLongitude = view.findViewById(R.id.myLongitude);
        myAddress = view.findViewById(R.id.myAddress);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((HomeActivity) getActivity()).currentScreenName.setText("Home");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            // getCurrentLocation();
            // getFriendLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFriendLocation() {
        try {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            try {
                                double myLatitude = GlobalDataService.getInstance().getMyLatitude();
                                double myLongitude = GlobalDataService.getInstance().getMyLongitude();
                                final LatLng latLng = new LatLng(myLatitude, myLongitude);
                                // mMap.addPolyline(new PolylineOptions().add(latLng));
                                // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(latLng).zoom(16).build()));
                                            mMap.addMarker(new MarkerOptions().position(latLng).title("You"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    5000
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            myAddress.setText(locationAddress);
        }
    }
}
