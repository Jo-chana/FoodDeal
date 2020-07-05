package com.hankki.fooddeal.ui.chatting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;
import com.hankki.fooddeal.ux.itemtouchhelper.ItemTouchHelperCallback;
import com.hankki.fooddeal.ux.itemtouchhelper.ItemTouchHelperExtension;
import com.hankki.fooddeal.ux.itemtouchhelper.MainRecyclerAdapter;
import com.hankki.fooddeal.ux.itemtouchhelper.TestModel;
import com.hankki.fooddeal.ux.recyclerview.PostAdapter;
import com.hankki.fooddeal.ux.recyclerview.PostItem;

import java.util.ArrayList;
import java.util.List;

/**채팅 화면*/
public class ChattingFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<PostItem> postItems;
    MainRecyclerAdapter mAdapter;
    ItemTouchHelperExtension.Callback mCallback;
    ItemTouchHelperExtension mItemTouchHelper;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        setRecyclerView(view);
        return view;
    }

    public void setChatItems(){
        /**Recycler View 에 나타낼 채팅방 아이템 셋업*/
        for(int i=0;i<30;i++){
            PostItem item = new PostItem();
            postItems.add(i,item);
        }
    }

    public void setRecyclerView(View v){
        recyclerView = v.findViewById(R.id.rv_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCallback = new ItemTouchHelperCallback();
        mAdapter =  new MainRecyclerAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mAdapter.updateData(createTestDatas());
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
        mAdapter.setItemTouchHelperExtension(mItemTouchHelper);
    }

    private List<TestModel> createTestDatas() {
        List<TestModel> result = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            TestModel testModel= new TestModel(i,":Item Swipe Action Button Container Width");
            if (i == 1) {
                testModel = new TestModel(i, "Item Swipe with Action container width and no spring");
            }
            if (i == 2) {
                testModel = new TestModel(i, "Item Swipe with RecyclerView Width");
            }
            if (i == 3) {
                testModel = new TestModel(i, "Item No swipe");
            }
            result.add(testModel);
        }
        return result;
    }

}
