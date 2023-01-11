package com.vid.allvideodownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {

    public Context context;
    public List<LanguageModal> languageList;
    private int selectedPosition = -1;

    public LanguageAdapter(Context context, List<LanguageModal> languageList ){
        this.context = context;
        this.languageList = languageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.languageName.setText(languageList.get(position).getLanguageName());
        if (selectedPosition == position) {
            holder.languageBack.setBackground(context.getResources().getDrawable(R.drawable.languageselback));
        } else {
            holder.languageBack.setBackground(context.getResources().getDrawable(R.drawable.languageback));
        }
        holder.languageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView languageName;
        public RelativeLayout languageBack;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            languageName = (TextView) itemLayoutView.findViewById(R.id.languageName);
            languageBack = (RelativeLayout) itemLayoutView.findViewById(R.id.languageBack);
        }
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }
}
