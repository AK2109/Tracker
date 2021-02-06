package com.example.friendtracker.Fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendtracker.Activity.HomeActivity;
import com.example.friendtracker.Libs.LocationAddress;
import com.example.friendtracker.Libs.ProgressDialogShow;
import com.example.friendtracker.Model.FriendDetails;
import com.example.friendtracker.Model.FriendLatLng;
import com.example.friendtracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.friendtracker.Libs.Params.CALL_PERMISSION;
import static com.example.friendtracker.Libs.Params.PLACE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendDetailsPage extends Fragment implements OnMapReadyCallback {

        View view;
        FriendDetails friendDetails;
        ChatFragment chatFragment;
        View.OnClickListener emailBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{friendDetails.getEmail()});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                    emailIntent.setType("message/rfc822");
                    try {
                        startActivity(Intent.createChooser(emailIntent,
                                ""));
                    } catch (Exception e) {
                        Toast.makeText(getActivity(),
                                "No email clients installed.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        View.OnClickListener callBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
                    } else {
                        callPhone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    View.OnClickListener chatBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                openChatPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void openChatPage() {
        try {
            chatFragment = new ChatFragment();
            chatFragment.setreceiverId(friendDetails.getUserId());
            ((HomeActivity) getActivity()).pushFragments(chatFragment, true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    FriendLatLng friendLatLng;
    //  private NestedScrollView nestedScrollView;
    View.OnTouchListener outsideMapListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            setNestedScrollingEnable(false);
            return true;
        }
    };
    View.OnTouchListener mainBoxListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            setNestedScrollingEnable(true);
            return true;
        }
    };
    private GoogleMap mMap;
    private String friendNickNameStr;
    private ImageView emailBtn, callBtn, chatBtn;
    private TextView friendEmail, friendPhoneNo, friendLatitude, friendLongitude, friendAddress, fullName;
    private DatabaseReference databaseReference;
    private LinearLayout topBox, bottomBox, mainBox;

    private void callPhone() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + friendDetails.getMobileNo()));
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone();
                }
            }
        }
    }

    public void setFriendDetails(FriendDetails friendDetails) {
        try {
            this.friendDetails = friendDetails;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_friend_details_page, container, false);
            findAllIds();
            getFriendDetails();
            databaseReference = FirebaseDatabase.getInstance().getReference(PLACE);
            openFriendLocation();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void getFriendDetails() {
        try {
            friendNickNameStr = friendDetails.getNickname();
            String fullNameStr = friendDetails.getFirstName() + " " + friendDetails.getLastName();
            String email = friendDetails.getEmail();
            String mobileNo = friendDetails.getMobileNo();
            fullName.setText(fullNameStr);
            friendEmail.setText(email);
            friendPhoneNo.setText(mobileNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFriendLocation() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            // mMap.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAddressFromLocation() {
//        Location location = appLocationService
//                .getLocation(LocationManager.GPS_PROVIDER);

        //you can hard-code the lat & long if you have issues with getting it
        //remove the below if-condition and use the following couple of lines
        double latitude = friendLatLng.getLatitude();//37.422005;
        double longitude = friendLatLng.getLongitude();//-122.084095;

//        if (location != null) {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude,
                this.getContext(), new GeocoderHandler());
//        } else {
//            Toast.makeText(getActivity(), "Location not found", Toast.LENGTH_SHORT).show();
//        }
    }

    private void findAllIds() {
        try {
            emailBtn = view.findViewById(R.id.emailBtn);
            callBtn = view.findViewById(R.id.callBtn);
            friendEmail = view.findViewById(R.id.friendEmail);
            friendPhoneNo = view.findViewById(R.id.friendPhoneNo);
            friendLatitude = view.findViewById(R.id.friendLatitude);
            friendLongitude = view.findViewById(R.id.friendLongitude);
            friendAddress = view.findViewById(R.id.friendAddress);
            //     nestedScrollView = view.findViewById(R.id.nestedScrollView);
            topBox = view.findViewById(R.id.topBox);
            bottomBox = view.findViewById(R.id.bottomBox);
            mainBox = view.findViewById(R.id.mainBox);
            fullName = view.findViewById(R.id.fullName);
            chatBtn = view.findViewById(R.id.chatBtn);

            emailBtn.setOnClickListener(emailBtnListener);
            callBtn.setOnClickListener(callBtnListener);
            chatBtn.setOnClickListener(chatBtnListener);
            topBox.setOnTouchListener(outsideMapListener);
            bottomBox.setOnTouchListener(outsideMapListener);
            mainBox.setOnTouchListener(mainBoxListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ((HomeActivity) getActivity()).currentScreenName.setText(friendNickNameStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            getFriendLocation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getFriendLocation() {
        try {
            ProgressDialogShow.openProgress(getActivity());
            String friendUserId = friendDetails.getUserId();
            databaseReference.child(friendUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        ProgressDialogShow.closeProgress();
                        friendLatLng = dataSnapshot.getValue(FriendLatLng.class);
                        autoUpdateFriendLocation(friendLatLng.getLatitude(), friendLatLng.getLongitude());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    try {
                        ProgressDialogShow.closeProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void autoUpdateFriendLocation(final double latitude, final double longitude) {
        try {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            //   mMap.setMyLocationEnabled(true);
            String latitideStr = latitude + " ";
            String longitudeStr = longitude + " ";
            friendLatitude.setText(latitideStr);
            friendLongitude.setText(longitudeStr);
            getAddressFromLocation();
            final LatLng latLng = new LatLng(latitude, longitude);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("dfsfssfsd", "run: friend location called");
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                        // Showing the current location in Google Map
                        //  LatLng latLng = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions();

                        // Setting the position for the marker
                        markerOptions.position(latLng);

                        // Setting the title for the marker.
                        // This will be displayed on taping the marker
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        // Clears the previously touched position
                        mMap.clear();

                        // Animating to the touched position
//                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                        // Placing a marker on the touched position
                        mMap.addMarker(markerOptions);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNestedScrollingEnable(boolean isEnable) {
        try {
            if (isEnable) {
                //   nestedScrollView.startNestedScroll(0, 0);
            } else {
                //  nestedScrollView.stopNestedScroll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GeocoderHandler extends Handler {
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
            friendAddress.setText(locationAddress);
        }
    }
}
