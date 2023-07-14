package com.example.applicazionefinale.ui.map;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.applicazionefinale.MainActivity;
import com.example.applicazionefinale.helper.GeoCodeHelper;
import com.example.applicazionefinale.persistence.ContactsManager;
import com.google.android.gms.common.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.example.applicazionefinale.R;
import com.example.applicazionefinale.model.ContactModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetAdd extends BottomSheetDialogFragment {

    private double lat = 0;
    private double lon = 0;
    private ContactsManager contactsManager = null;
    private Context mContext = null;

    private boolean modify = false;

    public EditText fieldNome = null;
    public EditText fieldCitta = null;
    public EditText fieldIndirizzo = null;

    public EditText fieldDesc = null;

    private TextView precisionePosizione = null;

    public GeoCodeHelper coder = null;
    public ContactModel contatto = null;

    public Spinner spinnerNazioni = null;



    ProgressBar progressBar = null;
    public CustomBottomSheetAdd () {}
    public CustomBottomSheetAdd (Context context) {
        this.mContext = context;
    }

    public CustomBottomSheetAdd (Context context, ContactModel contatto) {
        this.mContext = context;
        this.contatto = contatto;
        this.modify = true;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_dialog_add, container, false);
        add_marker (v.findViewById(R.id.nomeLabel), "Nome");
        add_marker (v.findViewById(R.id.indirizzoLabel), "Indirizzo");
        Button b = v.findViewById(R.id.confirmButton);
        this.fieldNome = v.findViewById(R.id.nomeField);

        if (contatto != null) {
            this.fieldNome.setText(contatto.getNome());
        }


        this.fieldCitta = v.findViewById(R.id.cittaField);
        this.fieldIndirizzo = v.findViewById(R.id.indirizzoField);
        this.precisionePosizione = v.findViewById(R.id.percentualePrecisione);
        this.spinnerNazioni = v.findViewById(R.id.nationSpinner);
        this.fieldDesc = v.findViewById(R.id.descField);


        if (contatto != null && !(contatto.getDescrizione().matches(""))) {
            this.fieldDesc.setText(contatto.getDescrizione());
        }

        add_text_listener(fieldCitta);
        add_text_listener(fieldIndirizzo);
        progressBar = v.findViewById(R.id.progressBar);
        this.mContext = getContext();
        this.contactsManager = ContactsManager.getInstance(mContext);
        coder = GeoCodeHelper.getInstance(mContext);

        if (this.lat != 0 && this.lon != 0) {
            String localita = "";
            try {
                HashMap<String, String> info = coder.get_info_from_latlng(new LatLng(this.lat, this.lon));

                if (info != null) {

                    fieldCitta.setText(info.get("locality"));

                    String[] nationsArray = getResources().getStringArray(R.array.nations_array);
                    List<String> nationsList = Arrays.asList(nationsArray);
                    int position = nationsList.indexOf(info.get("country"));

                    spinnerNazioni.setSelection(position);

                    fieldIndirizzo.setText(info.get("full_address"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        b.setOnClickListener(new View.OnClickListener() {

            double lat = get_lat();
            double lon = get_lon();

            public void onClick(View v) {

                if (check_if_is_empty(fieldNome) && check_if_is_empty(fieldIndirizzo)){

                    boolean chek_exist = true;

                    if (!(this.lat != 0 && lon != 0) || contatto != null) {

                        try {
                            Iterator<Address> addresses = coder.get_latlng_from_string(fieldIndirizzo.getText().toString(), fieldCitta.getText().toString(), spinnerNazioni.getSelectedItem().toString());
                            if (addresses != null && addresses.hasNext()) {
                                Address loc = addresses.next();
                                lat = loc.getLatitude();
                                lon = loc.getLongitude();
                            } else {
                                chek_exist = false;
                                Toast.makeText(mContext, "Indirizzo non esistente !", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    if (chek_exist) {
                        LocalDate currentDate = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-y");
                        String dateToday = currentDate.format(formatter);

                            if (modify) {
                                contactsManager.updateContact(contatto.getId(), lat, lon, fieldNome.getText().toString(), fieldDesc.getText().toString(), System.currentTimeMillis());
                                Toast.makeText(mContext, "Contatto modificato correttamente !", Toast.LENGTH_SHORT).show();
                            } else {
                                ContactModel tmp = new ContactModel(lat, lon, fieldNome.getText().toString(), fieldDesc.getText().toString());
                                contactsManager.addContactToHead(tmp);
                                Toast.makeText(mContext, "Contatto aggiunto correttamente !" + Integer.toString(tmp.getId()), Toast.LENGTH_SHORT).show();
                            }

                        dismiss();
                    }
                }

            }

        });

        return v;
    }



    public void setLatLong (double lat, double lon) {

        this.lat = lat;
        this.lon = lon;

    }

    private void add_marker (TextView textView, String text) {

        text += " *";
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);

        int asteriskIndex = text.indexOf("*");
        if (asteriskIndex != -1) {
            spannableString.setSpan(colorSpan, asteriskIndex, asteriskIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(spannableString);

    }

    public boolean check_if_is_empty (EditText editText) {

        String text  = editText.getText().toString();
        if (text.matches("")) {
            Toast.makeText(mContext, "Compila tutti i campi obbligatori", Toast.LENGTH_SHORT).show();
            editText.setBackground(ContextCompat.getDrawable(mContext, R.drawable.edittextback_error));
            return false;
        }
        return true;
    }

    public void add_text_listener (EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                update_progress_bar();

            }
        });
    }

    public void update_progress_bar () {

        int progress_status = 0;

        if (!(fieldCitta.getText().toString().matches(""))) {
            progress_status += 35;
        }

        if (!(fieldIndirizzo.getText().toString().matches(""))) {
            progress_status += 50;
        }

        if (!(spinnerNazioni.getSelectedItem().toString().matches("Nazione"))) {
            progress_status += 15;
        }

        progressBar.setProgress(progress_status);
        precisionePosizione.setText("Precisione : " + Integer.toString(progress_status) + " %");

    }

    public Double get_lat () {

        return this.lat;

    }

    public Double get_lon () {

        return this.lon;

    }
}