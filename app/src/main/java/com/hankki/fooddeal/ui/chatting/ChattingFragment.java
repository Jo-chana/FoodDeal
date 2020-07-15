package com.hankki.fooddeal.ui.chatting;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.staticdata.StaticChatRoom;
import com.hankki.fooddeal.ux.firebasehelper.FirestoreAdapter;
import com.hankki.fooddeal.ux.itemtouchhelper.ItemTouchHelperCallback;
import com.hankki.fooddeal.ux.itemtouchhelper.ItemTouchHelperExtension;
import com.hankki.fooddeal.ux.itemtouchhelper.MainRecyclerAdapter;
import com.hankki.fooddeal.ux.itemtouchhelper.ChatRoomItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**채팅 화면*/
public class ChattingFragment extends Fragment {

    RecyclerView recyclerView;
    MainRecyclerAdapter mAdapter;
    ItemTouchHelperExtension.Callback mCallback;
    ItemTouchHelperExtension mItemTouchHelper;
    public static ArrayList<ChatRoomItem> chatRoomItems;

    Context mContext;

    //TODO firebase 연동해야하는데 왜 안될까.........
    /*이현준*/
    Button setChatButton;
    private FirestoreAdapter firestoreAdapter;
    private WeakHashMap<String, String> selectedUsers = new WeakHashMap<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        mContext = getContext();

        /*이현준*/
        setChatButton = view.findViewById(R.id.setChat);
        setChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 게스트 자신의 UID 입력
                selectedUsers.put("ggj0418", "");

                DocumentReference newRoom = FirebaseFirestore.getInstance().collection("rooms").document();
                CreateChattingRoom(newRoom);
            }
        });


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


    public void CreateChattingRoom(final DocumentReference room) {
        // TODO 게시글 정보에서 글 작성자의 아이디 혹은 UID를 가져와서 uid에 저장
        String uid = "dlguwn13";
        Map<String, Integer> users = new HashMap<>();
        StringBuilder titleStringBuilder = new StringBuilder();
        String title = "";
        for( String key : selectedUsers.keySet()) {
            users.put(key, 0);
            if (title.length() < 20 & !key.equals(uid)) {
                titleStringBuilder.append(selectedUsers.get(key));
                titleStringBuilder.append(", ");
                title = titleStringBuilder.toString();
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("title", title.substring(0, title.length() - 2));
        data.put("users", users);

        room.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "성공!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mAdapter.updateData(getChatRoomData());
    }
}
