package com.github.worldoneo.ciphelet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.worldoneo.ciphelet.R;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.Contact> {
    Context context;

    public ContactListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Contact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.contact_row, parent, false);
        return new Contact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Contact holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class Contact extends RecyclerView.ViewHolder{
        public Contact(@NonNull View itemView) {
            super(itemView);

        }
    }
}
