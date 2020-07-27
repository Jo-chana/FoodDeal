package com.hankki.fooddeal.ui.home.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.retrofit.BoardController;
import com.hankki.fooddeal.ui.MainActivity;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;

import java.util.ArrayList;


public class ExchangeAndShare extends Fragment {

    View view;
    RecyclerView recyclerView;
    CardView cv_postWrite, cv_showExchange, cv_showShare;
    FrameLayout fl_exchange, fl_share;
    Button btn_filter;
    SetRecyclerViewOption setRecyclerViewOption;
    String category = "INGREDIENT EXCHANGE";
    TextView tv_exchange_chip, tv_share_chip;
    ArrayList<PostItem> postItems;

    /**@Enum pageFrom {Main, My, Dib}*/
    String pageFrom = "Main";

    public ExchangeAndShare(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_exchange, container, false);

        if(pageFrom.equals("Main")) {
            setShowLists();
            setRecyclerView();
            setPostWrite();
        } else {
            setMyPostOption();
            setShowLists();
        }
        filterButtonClickListener();

        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_exchange);
        cv_postWrite = view.findViewById(R.id.cv_post);
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            cv_postWrite.setClickable(false);
            cv_postWrite.setVisibility(View.INVISIBLE);
        }
        setRecyclerViewOption = new SetRecyclerViewOption(recyclerView, cv_postWrite,view,getContext(),R.layout.community_item);

        postItems = BoardController.getBoardList(getContext(), category);
        setRecyclerViewOption.setPostItems(postItems);
        setRecyclerViewOption.setTag("Main");
        setRecyclerViewOption.build(0);
    }

    public void setShowLists(){
        cv_showExchange = view.findViewById(R.id.cv_exchange);
        cv_showShare = view.findViewById(R.id.cv_share);
        fl_exchange = view.findViewById(R.id.fl_exchange);
        fl_share = view.findViewById(R.id.fl_share);
        tv_exchange_chip = view.findViewById(R.id.tv_exchange_chip);
        tv_share_chip = view.findViewById(R.id.tv_share_chip);
        /**교환 게시글 보이기*/
        cv_showExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "INGREDIENT EXCHANGE";
                fl_exchange.setBackgroundResource(R.drawable.cardview_unselector);
                tv_exchange_chip.setTextColor(getResources().getColor(R.color.original_white));
                fl_share.setBackgroundResource(R.drawable.cardview_selector);
                tv_share_chip.setTextColor(getResources().getColor(R.color.original_black));
                /**교환 게시글 필터링*/
                if(pageFrom.equals("Main")) {
                    setRecyclerView();
                }
                else
                    setMyPostOption();
            }
        });

        cv_showShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "INGREDIENT SHARE";
                fl_exchange.setBackgroundResource(R.drawable.cardview_selector);
                tv_exchange_chip.setTextColor(getResources().getColor(R.color.original_black));
                fl_share.setBackgroundResource(R.drawable.cardview_unselector);
                tv_share_chip.setTextColor(getResources().getColor(R.color.original_white));
                /**나눔 게시글 필터링*/
                if(pageFrom.equals("Main")) {
                    setRecyclerView();
                }
                else
                    setMyPostOption();
            }
        });
    }

    public void setPostWrite(){

        cv_postWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**글 쓰기 버튼 클릭 이벤트*/
                Intent intent = new Intent(getContext(),PostActivity.class);
                intent.putExtra("page",0);
                intent.putExtra("mode","write");
                intent.putExtra("category",category);
                startActivity(intent);
            }
        });
    }

    public void distanceSorting(){
        setRecyclerViewOption.sortPostItems();
    }

    public void fromMyPageOption(String tag){
        pageFrom = tag;
    }

    public void setMyPostOption(){
        cv_postWrite = view.findViewById(R.id.cv_post);
        cv_postWrite.setClickable(false);
        cv_postWrite.setVisibility(View.INVISIBLE);

        recyclerView = view.findViewById(R.id.rv_exchange);
        setRecyclerViewOption = new SetRecyclerViewOption(recyclerView, null,view,getContext(),R.layout.community_item);
        if(pageFrom.equals("My")) {
            setRecyclerViewOption.setPostItems(BoardController.getExchangeShareBoardWriteList(getContext(),category));
        }
        else if (pageFrom.equals("Dib"))
            setRecyclerViewOption.setPostItems(BoardController.getExchangeShareBoardLikeList(getContext(),category));
        setRecyclerViewOption.setTag(pageFrom);
        setRecyclerViewOption.build(0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void filterButtonClickListener(){
        btn_filter = view.findViewById(R.id.btn_filter);
        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(getContext(),v);
                ((MainActivity)MainActivity.mainContext).getMenuInflater().inflate(R.menu.menu_filter_posts,p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        if(title.equals("시간순 정렬")){
                            Toast.makeText(getContext(),"최근의 게시글을 먼저 보여줍니다", Toast.LENGTH_SHORT).show();
                        } else if (title.equals("거리순 정렬")){
                            Toast.makeText(getContext(), "가까운 곳의 게시글을 먼저 보여줍니다", Toast.LENGTH_SHORT).show();
                            distanceSorting();
                        }
                        return true;
                    }
                });
                p.show();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        if(pageFrom.equals("Main"))
            setRecyclerViewOption.update();
    }

}
