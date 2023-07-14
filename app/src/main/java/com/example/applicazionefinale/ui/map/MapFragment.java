package com.example.applicazionefinale.ui.map;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.applicazionefinale.MainActivity;
import com.example.applicazionefinale.R;
import com.example.applicazionefinale.helper.GeofenceHelper;
import com.example.applicazionefinale.model.ContactModel;
import com.example.applicazionefinale.persistence.ContactsManager;
import com.example.applicazionefinale.ui.home.CustomBottomSheetDialog;
import com.google.android.gms.internal.location.zzbv;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private ContactsManager contactsManager = null;

    private List<ContactModel> contatti = null;
    private Context mContext = null;
    private GoogleMap googleMap;

    private boolean modify = false;
    private Map<String, Circle> circleMap = new HashMap<>();
    private Map<String, Marker> markerMap = new HashMap<>();
    private GeofenceHelper geofenceHelper;

    private GeofencingClient geofencingClient;

    private boolean is_first_entry = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);


        this.mContext = getContext();
        geofencingClient = LocationServices.getGeofencingClient(mContext);
        geofenceHelper = new GeofenceHelper(mContext);

        this.contactsManager = ContactsManager.getInstance(mContext);

        contatti = contactsManager.getContactsList();
        observeLogData();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

        return view;
    }



        private void addMarkerOnAddMap(LatLng latLng, String nome, int id) {

            if (modify == true) {
                markerMap.get(Integer.toString(id)).remove();
                circleMap.get(Integer.toString(id)).remove();
            }

            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            Marker marker = this.googleMap.addMarker(markerOptions);
            marker.setTag(id);
            markerMap.put(Integer.toString(id), marker);
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.radius(200);
            circleOptions.strokeColor(Color.argb(255, 255, 0,0));
            circleOptions.fillColor(Color.argb(64, 255, 0,0));
            circleOptions.strokeWidth(4);
            Circle circle = this.googleMap.addCircle(circleOptions);
            circleMap.put(Integer.toString(id), circle);

        }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        this.googleMap = googleMap;
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                modify = false;
                CustomBottomSheetAdd bottomSheetFragment = new CustomBottomSheetAdd(mContext);
                bottomSheetFragment.setLatLong(latLng.latitude, latLng.longitude);
                bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), "bottomSheet");

            }
        });

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                modify = true;
                ContactModel contact = contactsManager.getContactById((int)marker.getTag());
                CustomBottomSheetDialog bottomSheetFragment = new CustomBottomSheetDialog();
                bottomSheetFragment.setContact(contact);
                bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), "bottomSheet");
                return false;
            }
        });


        // When map is loaded
        for (ContactModel contact : this.contatti) {

            LatLng latLng = new LatLng(contact.getLatitude(), contact.getLongitude());

            addMarkerOnAddMap(latLng, contact.getNome(), contact.getId());

        }
    }

    private void observeLogData() {
        this.contactsManager.getLogLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<ContactModel>>() {
            @Override

            public void onChanged(List<ContactModel> logDescriptors) {

                if (is_first_entry) {
                if (logDescriptors.size() != 0) {

                        ContactModel tmp = logDescriptors.get(0);
                        addMarkerOnAddMap(new LatLng(tmp.getLatitude(), tmp.getLongitude()), tmp.getNome(), tmp.getId());

                }
                } else {

                    is_first_entry = true;

                }

            }
        });
    }


}