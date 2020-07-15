package com.hankki.fooddeal.data.staticdata;

import com.hankki.fooddeal.data.PostItem;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticPost {
    static HashMap<Integer, ArrayList<PostItem>> postList;

    public StaticPost(){
        if (postList == null){
            postList = new HashMap<>();
        }
    }

    public ArrayList<PostItem> getPostList(int page){
        if(postList==null)
            postList = new HashMap<>();

        if(postList.get(page)==null){
            ArrayList<PostItem> posts = new ArrayList<>();
            postList.put(page,posts);
        }

        return postList.get(page);
    }

    public PostItem getPost(int page, int index){
        ArrayList<PostItem> posts = getPostList(page);
        return posts.get(index);
    }

    public void updatePost(int page, int index, PostItem post){
        postList.get(page).set(index,post);
    }

    public void addPost(int page, PostItem post){
        ArrayList<PostItem> posts = getPostList(page);
        posts.add(0,post);
        postList.put(page, posts);
    }

    public void ExchangeAndShareDefault(){
        if(postList == null)
            postList = new HashMap<>();
        if(postList.get(0) == null){
            ArrayList<PostItem> postItems = new ArrayList<>();
            postItems.add(new PostItem("팡팡이","집에서 엄마가 감자를 보내주셨어요~ \n채팅 들어오세요!"
                    ,"광진구 화양동","감자 나눔해요","3분 전",250,null,null));
            postItems.add(new PostItem("처음처럼","요리하고 나니 양파가 많이 남았는데... \n가져가실 분 있나요?"
                    , "강남구 삼성동","양파를 너무 많이 샀어요","1시간 전",100,null,null));
            postItems.add(new PostItem("겨울나그네","택배로 소시지를 샀는데 ㅋㅋ \n3kg 인줄 모르고 받았더니 ㅋㅋ너무 많네요\n다른 반찬이랑 교환 하쉴?? 챗 ㄱㄱ",
                    "관악구 행운동","아 ㅋㅋ 저 소시지 반찬 교환하실분","3시간 전",15,null,null));
            postList.put(0,postItems);
        }
    }

    public void RecipeShareDefault(){
        if(postList == null)
            postList = new HashMap<>();
        if(postList.get(1) == null){
            ArrayList<PostItem> postItems = new ArrayList<>();
            postItems.add(new PostItem("나누리","카레 만드는 법: 전자렌지 3분 돌린다"
                    ,"광진구 화양동","카레 만드는 법","3분 전",110,null,null));
            postItems.add(new PostItem("가가가가","갈비탕 레시피 아시는 분 채팅좀여 ㅠㅠ"
                    , "강남구 삼성동","갈비탕 레시피 아시는 분?","1시간 전",15,null,null));
            postItems.add(new PostItem("풀잎","가끔씩 레시피들 적혀있음 ㅋㅋ",
                    "관악구 행운동","웹툰 댓글 보세요","3시간 전",25,null,null));
            postList.put(1,postItems);
        }
    }

    public void FreeDefault(){
        if(postList == null)
            postList = new HashMap<>();
        if(postList.get(2) == null){
            ArrayList<PostItem> postItems = new ArrayList<>();
            postItems.add(new PostItem("심심","아 배고파.... 심심해..."
                    ,"광진구 화양동","심심하다 얘들아","3분 전",130,null,null));
            postItems.add(new PostItem("웈끼웈끼","여친 사귀는 꿈 ㅋㅋ"
                    , "강남구 삼성동","내가 어제 꾼 꿈","1시간 전",450,null,null));
            postItems.add(new PostItem("잎새","하루남았다",
                    "관악구 행운동","마지막 잎새","3시간 전",30,null,null));
            postList.put(2,postItems);
        }
    }
}
