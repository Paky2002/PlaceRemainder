package com.example.applicazionefinale.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.applicazionefinale.model.ContactModel;

@Database(entities = {ContactModel.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE contacts_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, timestamp INTEGER NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL, nome TEXT, descrizione TEXT)");
            database.execSQL("INSERT INTO contacts_new (id, timestamp, latitude, longitude, nome, descrizione) SELECT id, timestamp, latitude, longitude, nome, descrizione FROM contacts");
            database.execSQL("DROP TABLE contacts");
            database.execSQL("ALTER TABLE contacts_new RENAME TO contacts");
        }
    };
}