package com.example.applicazionefinale.persistence;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.applicazionefinale.MainActivity;
import com.example.applicazionefinale.helper.GeofenceHelper;
import com.example.applicazionefinale.model.ContactModel;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Created by Marco Picone (picone.m@gmail.com) 20/03/2020
 * Singleton to manage Log Storage using Room Library
 */
public class ContactsManager {

    private Context context = null;
    private AppDatabase db = null;
    private ContactDao contactDao = null;

    private GeofenceHelper geofenceHelper = null;
    private GeofencingClient geofencingClient;
    private int RADIUS = 200;


    /*
     * The instance is static so it is shared among all instances of the class. It is also private
     * so it is accessible only within the class.
     */
    private static ContactsManager instance = null;

    /*
     * The constructor is private so it is accessible only within the class.
     */
    private ContactsManager(Context context){
        //Log.d(MainActivity.TAG,"Number Manager Created !");

        this.context = context;

        this.db = Room.databaseBuilder(context, AppDatabase.class, "contacts-database").allowMainThreadQueries().build();
        this.contactDao = this.db.contactDao();
        this.geofenceHelper = new GeofenceHelper(context);
        this.geofencingClient = LocationServices.getGeofencingClient(context);

    }

    public static ContactsManager getInstance(Context context){
        if(instance == null)
            instance = new ContactsManager(context);
        return instance;

    }

    public void addContact(ContactModel contact){
        this.contactDao.insert(contact);
    }

    public void addContactToHead(ContactModel contact){

        this.addContact(contact);
        addGeofence(new LatLng(contact.getLatitude(), contact.getLongitude()), RADIUS, this.getContactsList().get(0).getId());

    }

    public void removeContacts(ContactModel contact){
        removeGeoFance(contact.getId());
        this.contactDao.delete(contact);
    }

    public List<ContactModel> getContactsList(){
        return this.contactDao.getAll();
    }

    public ContactModel getContactById(int usId) {return this.contactDao.getUserById(usId);}

    public void updateContact (int usId, double c_lat, double c_lon, String c_nome, String c_desc, long c_timestamp) {
        removeGeoFance(usId);
        this.contactDao.updateContact(usId, c_lat, c_lon, c_nome, c_desc, c_timestamp);
        addGeofence(new LatLng(c_lat, c_lon), RADIUS, usId);

    }
    public LiveData<List<ContactModel>> getLogLiveDataList(){
        return this.contactDao.getAllLiveData();
    }

    private void addGeofence(LatLng latLng, float radius, int id) {


        Geofence geofence = geofenceHelper.getGeofence(String.valueOf(id), latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER);

        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Geofencing has started", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Toast.makeText(context, "Geofencing no is started" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeGeoFance (int id) {


        geofencingClient.removeGeofences(Collections.singletonList(Integer.toString(id)))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Geofencing is removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Toast.makeText(context, "Geofencing no removed" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}