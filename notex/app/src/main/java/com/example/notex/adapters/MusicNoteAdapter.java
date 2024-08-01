package com.example.notex.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notex.models.MusicNote;
import java.util.List;

public class MusicNoteAdapter extends RecyclerView.Adapter<MusicNoteAdapter.ViewHolder> {

    private List<MusicNote> musicNotes;

    public MusicNoteAdapter(List<MusicNote> musicNotes) {
        this.musicNotes = musicNotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MusicNote musicNote = musicNotes.get(position);
        holder.pitchTextView.setText(musicNote.getPitch());
        holder.durationTextView.setText(String.valueOf(musicNote.getDuration()));
    }

    @Override
    public int getItemCount() {
        return musicNotes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pitchTextView;
        public TextView durationTextView;

        public ViewHolder(View view) {
            super(view);
            pitchTextView = view.findViewById(android.R.id.text1);
            durationTextView = view.findViewById(android.R.id.text2);
        }
    }
}