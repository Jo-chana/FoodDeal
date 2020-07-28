package com.hankki.fooddeal.ui.home.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hankki.fooddeal.R;
import com.hankki.fooddeal.data.retrofit.BoardController;
import com.hankki.fooddeal.ux.recyclerview.SetRecyclerViewOption;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RecipeShare extends Fragment {
    View view;
    RecyclerView recyclerView;
    CardView cv_post;
    SetRecyclerViewOption setRecyclerViewOption;
    String category = "RECIPE";

    /**@Enum pageFrom {Main, My, Dib}*/
    String pageFrom = "Main";

    Disposable disposable;

    ProgressBar progressBar;

    public RecipeShare(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_recipe, container, false);

        progressBar = view.findViewById(R.id.customDialog_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        disposable = Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception { return false; }
        })

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object result) throws Exception {
                        if(pageFrom.equals("Main")) {
                            setRecyclerView();
                            setPostWrite();
                        } else {
                            setMyPostOption();
                        }
                        progressBar.setVisibility(View.GONE);
//                        customAnimationDialog.dismiss();
                        disposable.dispose();
                    }
                });

        // 다이얼로그 이전 버전
        /*if(pageFrom.equals("Main")) {
            setRecyclerView();
            setPostWrite();
        } else {
            setMyPostOption();
        }*/
        return view;
    }

    public void setRecyclerView(){
        recyclerView = view.findViewById(R.id.rv_recipe);
        cv_post = view.findViewById(R.id.cv_post);
        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            cv_post.setClickable(false);
            cv_post.setVisibility(View.INVISIBLE);
        }
        setRecyclerViewOption = new SetRecyclerViewOption(
                recyclerView, cv_post, view, getContext(), R.layout.community_item );
        setRecyclerViewOption.setPostItems(BoardController.getBoardList(getContext(),category));
        setRecyclerViewOption.setTag("Main");
        setRecyclerViewOption.build(1);
    }
    public void setPostWrite(){
        cv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**글 쓰기 버튼 클릭 이벤트*/
                Intent intent = new Intent(getContext(),PostActivity.class);
                intent.putExtra("page",1);
                intent.putExtra("mode","write");
                intent.putExtra("category","RECIPE");
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
        cv_post = view.findViewById(R.id.cv_post);
        cv_post.setClickable(false);
        cv_post.setVisibility(View.INVISIBLE);

        recyclerView = view.findViewById(R.id.rv_recipe);
        setRecyclerViewOption = new SetRecyclerViewOption(recyclerView, null,view,getContext(),R.layout.community_item);
        if(pageFrom.equals("My")) {
            setRecyclerViewOption.setPostItems(BoardController.getRecipeBoardWriteList(getContext()));
        }
        else if (pageFrom.equals("Dib")) {
            setRecyclerViewOption.setPostItems(BoardController.getRecipeBoardLikeList(getContext()));
        }
        setRecyclerViewOption.setTag(pageFrom);
        setRecyclerViewOption.build(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(pageFrom.equals("Main"))
            setRecyclerViewOption.update();
    }
}
