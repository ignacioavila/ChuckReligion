package com.ignacioavila.chuckreligion;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.JokeViewHolder> {
    private List<Joke> data;
    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;

    public JokeAdapter(@NonNull List<Joke> data, RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
        this.data = data;
        this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
    }

    @Override
    public JokeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        JokeViewHolder jvh = new JokeViewHolder(row, recyclerViewOnItemClickListener);
        return jvh;
    }

    @Override
    public void onBindViewHolder(JokeViewHolder holder, int position) {
        Joke joke = data.get(position);

        String arr[] = joke.getJoke().split(" ", 4);
        String firstWord = arr[0];
        String secondWord = arr[1];
        holder.getTitleTextView().setText(arr[0] + " " + arr[1] + " " + arr[2] + " ...");
        holder.getSubtitleTextView().setText(joke.getJoke());
        holder.getPersonPhoto().setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class JokeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView titleTextView;
        private TextView subtitleTextView;
        private ImageView personPhoto;

        private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;


        public JokeViewHolder(View itemView, RecyclerViewOnItemClickListener recyclerViewOnItemClickListener) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitleTextView);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            this.recyclerViewOnItemClickListener = recyclerViewOnItemClickListener;
            itemView.setOnClickListener(this);
        }

        public TextView getTitleTextView() {return titleTextView;}

        public TextView getSubtitleTextView() {
            return subtitleTextView;
        }

        public ImageView getPersonPhoto(){ return personPhoto;}

        @Override
        public void onClick(View v) {
            recyclerViewOnItemClickListener.onClick(v, getAdapterPosition());
        }
    }

}
