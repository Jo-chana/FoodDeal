package com.hankki.fooddeal.ui.chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticChatRoom;
import com.hankki.fooddeal.ux.recyclerview.MessageItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**채팅방 상세화면*/
public class ChatDetail extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText messageEdit;
    Button sendButton;
    ArrayList<MessageItem> messageItems;
    static HashMap<Integer,ArrayList<MessageItem>> messageMap;
    chatAdapter mAdapter;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        index = getIntent().getIntExtra("index",-1);
        setMessageItems();
        bindComponents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageMap.put(index, messageItems);
    }

    public void setMessageItems(){
        if(messageMap==null){
            messageMap = new HashMap<>();
            messageItems = new ArrayList<MessageItem>();
            messageMap.put(index,messageItems);
        } else {
            if(messageMap.get(index)==null){
                messageItems = new ArrayList<MessageItem>();
                messageMap.put(index,messageItems);
            } else {
                messageItems = messageMap.get(index);
            }
        }
    }

    public void bindComponents(){
        recyclerView = findViewById(R.id.rv_chat_detail);
        messageEdit = findViewById(R.id.et_chat_message);
        sendButton = findViewById(R.id.btn_chat_message);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(messageItems.size());
                        }
                    }, 100);
                }
            }
        });
        /**리사이클러 뷰 어댑터 설정*/
        setAdapter();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEdit.getText().toString();
                messageEdit.setText("");
                String time = getTime();
                MessageItem item = new MessageItem("User",message,time);
                messageItems.add(item);
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageItems.size());
                new StaticChatRoom().getChatItems().get(index).information = message;
            }
        });
    }

    public String getTime(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfnow = new SimpleDateFormat("HH:mm");
        String timeData = sdfnow.format(date);
        return timeData;
    }

    public void setAdapter(){
        mAdapter = new chatAdapter(messageItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        recyclerView.setAdapter(mAdapter);
    }


    public class chatAdapter extends RecyclerView.Adapter<chatViewHolder>{
        ArrayList<MessageItem> messageItems;

        public chatAdapter(ArrayList<MessageItem> items){
            messageItems = items;
        }

        @NonNull
        @Override
        public chatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(),R.layout.message_item,null);
            chatViewHolder viewHolder = new chatViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull chatViewHolder holder, int position) {
            MessageItem item = messageItems.get(position);
            holder.userName.setText(item.getUserName());
            holder.message.setText(item.getMessage());
            holder.time.setText(item.getTime());
        }

        @Override
        public int getItemCount() {
            return messageItems.size();
        }
    }

    public class chatViewHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView message;
        TextView time;

        public chatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            message = itemView.findViewById(R.id.chat_message);
            time = itemView.findViewById(R.id.message_time);
        }
    }
}