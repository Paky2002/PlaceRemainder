package com.example.applicazionefinale.ui.home;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applicazionefinale.adapter.RecyclerViewAdapter;
import com.example.applicazionefinale.R;
import com.example.applicazionefinale.persistence.ContactsManager;
import com.example.applicazionefinale.model.ContactModel;


/**
 * Created by Marco Picone (picone.m@gmail.com) 20/03/2020
 * Fragment for Logs History
 */
public  class HomeFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private RecyclerView mRecyclerView = null;
    private LinearLayoutManager mLayoutManager = null;
    private RecyclerViewAdapter mAdapter = null;
    private ImageButton addButton = null;
    private Context mContext = null;
    private ContactsManager contactsManager = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        this.mContext = getContext();
        this.contactsManager = ContactsManager.getInstance(mContext);

        init(rootView);
        observeLogData();

        return rootView;
    }
    private void init(View rootView){

        mRecyclerView  = (RecyclerView)rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        // use a linear layout manager
        mLayoutManager  = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);

        mRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mAdapter  = new RecyclerViewAdapter(mContext);

        mAdapter.setOnButtonInfoClickListener(new RecyclerViewAdapter.OnButtonInfoClickListener() {
            @Override
            public void onButtonClicked(ContactModel contact) {
                // Gestisci l'evento del click del bottone
                // Utilizza l'oggetto ContactModel specifico
                // Apri il Bottom Sheet con i dettagli del contatto
                CustomBottomSheetDialog bottomSheetFragment = new CustomBottomSheetDialog();
                // Passa eventuali dati o parametri al tuo BottomSheetFragment
                bottomSheetFragment.setContact(contact);
                // Mostra il tuo BottomSheetFragment
                bottomSheetFragment.show(requireActivity().getSupportFragmentManager(), "bottomSheet");
            }
        });

        mAdapter.setdeleteListener(new RecyclerViewAdapter.deleteListener() {
            @Override
            public void onButtonClicked (ContactModel contact) {
                contactsManager.removeContacts(contact);
            }
        });

        mRecyclerView.setAdapter(mAdapter);

    }

    private void observeLogData(){
        this.contactsManager.getLogLiveDataList().observe(getViewLifecycleOwner(), new Observer<List<ContactModel>>() {
            @Override
            public void onChanged(List<ContactModel> logDescriptors) {
                if(logDescriptors != null){
                    Log.d(TAG, "Update Log List Received ! List Size: " + logDescriptors.size());
                    refreshRecyclerView(logDescriptors, 0);
                }
                else
                    Log.e(TAG, "Error observing Log List ! Received a null Object !");
            }
        });
    }

    private void refreshRecyclerView(List<ContactModel> updatedList, int scrollPosition){
        mAdapter.setDataset(updatedList);
        mAdapter.notifyDataSetChanged();
        if(scrollPosition >= 0)
            mLayoutManager.scrollToPosition(scrollPosition);
    }



}