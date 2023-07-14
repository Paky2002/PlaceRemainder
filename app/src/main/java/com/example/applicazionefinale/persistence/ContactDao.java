package com.example.applicazionefinale.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.applicazionefinale.model.ContactModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Marco Picone (picone.m@gmail.com) 20/03/2020
 * Doom Dao Structure with available methods
 */
@Dao
public interface ContactDao {

    @Query("SELECT * FROM contacts_new ORDER BY timestamp DESC")
    LiveData<List<ContactModel>> getAllLiveData();

    @Query("SELECT * FROM contacts_new")
    List<ContactModel> getAll();

    @Query("SELECT * FROM contacts_new WHERE id IN (:userIds)")
    List<ContactModel> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM contacts_new WHERE id = (:usId)")
    ContactModel getUserById (int usId);

    @Query("UPDATE contacts_new SET nome = (:c_nome), latitude = (:c_lat), longitude = (:c_lon), descrizione = (:c_desc), timestamp = (:c_timestamp) WHERE id = (:usId)")
    int updateContact (int usId, double c_lat, double c_lon, String c_nome, String c_desc, long c_timestamp);

    @Insert
    void insertAll(ContactModel... logs);

    @Insert
    void insert(ContactModel log);


    @Delete
    void delete(ContactModel ContactModel);

}