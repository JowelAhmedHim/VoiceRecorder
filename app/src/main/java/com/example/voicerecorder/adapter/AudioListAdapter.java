package com.example.voicerecorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voicerecorder.R;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewholder> {

    private File[] allFile;

    private onItemClick onItemClick;

    public AudioListAdapter(File[] allFile, onItemClick onItemClick) {
        this.allFile = allFile;
        this.onItemClick=onItemClick;
    }

    @NonNull
    @Override
    public AudioViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        AudioViewholder vh= new AudioViewholder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewholder holder, int position) {

        holder.filename.setText(allFile[position].getName());
        holder.filedate.setText(allFile[position].lastModified()+"");

    }

    @Override
    public int getItemCount() {
        return allFile.length;
    }

    public class AudioViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView filename;
        private TextView filedate;
        public AudioViewholder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.audioPlay);
            filename=itemView.findViewById(R.id.audioName);
            filedate=itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClick.onClickListener(allFile[getAdapterPosition()],getAdapterPosition());

        }
    }

    public interface onItemClick{
        void onClickListener(File file,int position);
    }
}
