package com.example.applicazionefinale.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applicazionefinale.R;
import com.example.applicazionefinale.helper.GeoCodeHelper;
import com.example.applicazionefinale.model.ContactModel;
import com.example.applicazionefinale.ui.map.CustomBottomSheetAdd;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CustomBottomSheetDialog extends BottomSheetDialogFragment {

    private ContactModel contact = null;
    private Button buttonModify = null;

    private Context mContext = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);
        buttonModify = v.findViewById(R.id.modifyButton);
        this.mContext = getContext();
        buttonModify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ContactModel tmp = getContact();
                CustomBottomSheetAdd bottomSheetFragment = new CustomBottomSheetAdd(mContext, tmp);
                bottomSheetFragment.setLatLong(tmp.getLatitude(), tmp.getLongitude());
                bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), "bottomSheet");
                dismiss();

            }

        });
        return v;
    }

    public void setContact (ContactModel contact) {
        this.contact = contact;
    }

    public ContactModel getContact () {
        return this.contact;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {



        TextView nomeText = (TextView) view.findViewById(R.id.welcomeTextView_bottom_sheet);
        nomeText.setText(this.contact.getNome());

        TextView latText = (TextView) view.findViewById(R.id.loc_text);
        try {
            latText.setText(GeoCodeHelper.getInstance(getContext()).get_location_from_latlng(new LatLng(this.contact.getLatitude(), this.contact.getLongitude() )));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TextView dataText = (TextView) view.findViewById(R.id.data_text);

        // Format the LocalDateTime using the formatter
        dataText.setText(contact.getTimeStampDateFormat());

        TextView oraText = (TextView) view.findViewById(R.id.ora_text);
        oraText.setText(contact.getTimeStampTimeFormat());

        TextView descText = (TextView) view.findViewById(R.id.desc_text);

        if (!(contact.getDescrizione().matches(""))) {
            descText.setText("Descrizione : " + contact.getDescrizione());
        } else {
            descText.setVisibility(View.GONE);
        }

    }
}