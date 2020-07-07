package com.hankki.fooddeal.ux.recyclerview;

import android.content.ClipData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;

import java.util.ArrayList;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.InputViewHolder> {
    private Context context;
    private ArrayList<InputItem> inputItems;
    private InputViewHolder viewHolder;

    public InputAdapter(Context context, ArrayList<InputItem> itemList){
        this.context = context;
        this.inputItems = itemList;
    }


    @NonNull
    @Override
    public InputViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.register_input_item,null);
        viewHolder = new InputViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InputViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.tv_input_type);
        String title = inputItems.get(position).getTitle();
        textView.setText(title);
    }

    @Override
    public int getItemCount() {
        return inputItems.size();
    }


    public class InputViewHolder extends RecyclerView.ViewHolder{
        private InputAdapter mAdapter;

        public InputViewHolder(@NonNull View itemView, InputAdapter inputAdapter){
            super(itemView);
            mAdapter = inputAdapter;
        }
    }
}
