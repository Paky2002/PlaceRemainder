package com.example.applicazionefinale.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.applicazionefinale.persistence.ContactsManager;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GeoCodeHelper {

    private Context mContext;

    private static GeoCodeHelper instance = null;

    private Geocoder coder = null;
    public GeoCodeHelper (Context context) {

        mContext = context;
        this.coder = new Geocoder(mContext);

    }

    public static GeoCodeHelper getInstance(Context context){

        if(instance == null)
            instance = new GeoCodeHelper(context);
        return instance;
    }

    public String get_location_from_latlng (LatLng latLng) throws IOException {

        List<Address> place = coder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        return place.get(0).getLocality();

    }

    public HashMap<String, String> get_info_from_latlng (LatLng latLng) throws IOException {

        List<Address> place = coder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        HashMap<String, String> addressMap = new HashMap<>();
        if (!place.isEmpty()) {
            Address address = place.get(0);

            int addressLineCount = address.getMaxAddressLineIndex();
            StringBuilder fullAddress = new StringBuilder();
            for (int i = 0; i <= addressLineCount; i++) {
                fullAddress.append(address.getAddressLine(i));
                if (i < addressLineCount) {
                    fullAddress.append(", ");
                }
            }

            addressMap.put("latitude", String.valueOf(address.getLatitude()));
            addressMap.put("longitude", String.valueOf(address.getLongitude()));
            addressMap.put("full_address", fullAddress.toString());
            addressMap.put("locality", address.getLocality());
            addressMap.put("country", address.getCountryName());
        }

        return addressMap;

    }

    public Iterator<Address> get_latlng_from_string (String indirizzo, String citta, String nazione) throws IOException {

        Iterator<Address> list_places = coder.getFromLocationName(indirizzo + " " + citta + " " + nazione, 10).iterator();
        Toast.makeText(mContext, indirizzo, Toast.LENGTH_SHORT).show();

        return list_places;

    }

}
