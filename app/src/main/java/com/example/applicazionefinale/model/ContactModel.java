package com.example.applicazionefinale.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Entity(tableName = "contacts_new")
public class ContactModel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "timestamp")
    private long timestamp = 0;

    @ColumnInfo(name = "latitude")
    private double latitude = 0.0;

    @ColumnInfo(name = "longitude")
    private double longitude = 0.0;

    @ColumnInfo(name = "nome")
    private String nome = null;

    @ColumnInfo(name = "descrizione")
    private String descrizione = null;

    public ContactModel() {
    }

    @Ignore
    public ContactModel(double latitude, double longitude, String nome) {
        super();
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
        this.nome = nome;
        this.descrizione = "";
    }

    @Ignore
    public ContactModel(double latitude, double longitude, String nome, String desc) {
        super();
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
        this.nome = nome;
        this.descrizione = desc;
    }

    public void setDescrizione (String desc) {this.descrizione = desc;}

    public String getDescrizione() {
        return this.descrizione;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimeStampDateFormat () {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(timestamp);

        return dateString;
    }

    public String getTimeStampTimeFormat () {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String timeString = dateFormat.format(timestamp);

        return timeString;
    }

    @Override
    public String toString() {
        return "Timestamp: "+timestamp+" Lat: "+ latitude+" Lon:"+longitude+" Nome :"+nome;
    }

    @Override
    public boolean equals(Object o) {
        ContactModel cModelObj = (ContactModel)o;
        if(cModelObj.timestamp == this.timestamp)
            return true;
        else
            return false;
    }

}