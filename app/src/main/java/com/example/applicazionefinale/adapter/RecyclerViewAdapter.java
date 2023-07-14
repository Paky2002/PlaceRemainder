package com.example.applicazionefinale.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.applicazionefinale.model.ContactModel;
import com.example.applicazionefinale.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<ContactModel> mDataset;
    private Context mContext = null;

    public interface OnButtonInfoClickListener {
        void onButtonClicked(ContactModel contact);

    }

    public interface deleteListener {
        void onButtonClicked(ContactModel contact);

    }

    private OnButtonInfoClickListener onButtonClickListener;
    private deleteListener onButtonClickListenerD;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View v = null;
        public Button button_info;
        public Button button_delete;
        public ViewHolder(View v) {
            super(v);
            this.v = v;
            this.button_info = v.findViewById(R.id.infoButton);
            this.button_delete = v.findViewById(R.id.deleteButton);

            this.button_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onButtonClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ContactModel contact = mDataset.get(position);
                            onButtonClickListener.onButtonClicked(contact);
                        }
                    }
                }
            });

            this.button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onButtonClickListenerD != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ContactModel contact = mDataset.get(position);
                            onButtonClickListenerD.onButtonClicked(contact);
                        }
                    }
                }
            });
        }

        public void setText(String text){
            TextView tView = (TextView)v.findViewById(R.id.myTextView);
            tView.setText(text);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(List<ContactModel> myDataset, Context context) {
        mDataset = myDataset;
        mContext  = context;
    }

    public void setOnButtonInfoClickListener(OnButtonInfoClickListener listener) {
        this.onButtonClickListener = listener;
    }

    public void setdeleteListener(deleteListener listener) {
        this.onButtonClickListenerD = listener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(Context context) {
        mDataset = new ArrayList<>();
        mContext  = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_element, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setText(mDataset.get(position).getNome());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<ContactModel> getmDataset() {
        return mDataset;
    }

    public void setDataset(List<ContactModel> mDataset) {
        this.mDataset = mDataset;
    }
}