package com.hankki.fooddeal.ui.chatting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticChatRoom;
import com.hankki.fooddeal.ux.itemtouchhelper.ItemTouchHelperCallback;
import com.hankki.fooddeal.ux.itemtouchhelper.ItemTouchHelperExtension;
import com.hankki.fooddeal.ux.itemtouchhelper.MainRecyclerAdapter;
import com.hankki.fooddeal.ux.itemtouchhelper.ChatRoomItem;

import java.util.ArrayList;

/**채팅 화면*/
public class ChattingFragment extends Fragment {

    RecyclerView recyclerView;
    MainRecyclerAdapter mAdapter;
    ItemTouchHelperExtension.Callback mCallback;
    ItemTouchHelperExtension mItemTouchHelper;
    public static ArrayList<ChatRoomItem> chatRoomItems;

    Context mContext;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        mContext = getContext();

        setRecyclerView(view);
        return view;
    }

    public void setRecyclerView(View v){
        recyclerView = v.findViewById(R.id.rv_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCallback = new ItemTouchHelperCallback();
        mAdapter =  new MainRecyclerAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mAdapter.updateData(getChatRoomData());
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter.setItemTouchHelperExtension(mItemTouchHelper);
    }

    public ArrayList<ChatRoomItem> getChatRoomData() {
        StaticChatRoom staticChatRoom = new StaticChatRoom();
        chatRoomItems = staticChatRoom.getChatItems();
        return chatRoomItems;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.updateData(getChatRoomData());
    }
}
