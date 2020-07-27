package com.hankki.fooddeal.data.retrofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hankki.fooddeal.data.CommentItem;
import com.hankki.fooddeal.data.PostItem;
import com.hankki.fooddeal.data.PreferenceManager;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.BoardListResponse;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.CommentListResponse;
import com.hankki.fooddeal.data.retrofit.retrofitDTO.MemberResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;

public class BoardController {
    public static APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

    public static ArrayList<PostItem> getBoardList(Context context, String boardCode) {

        ArrayList<PostItem> items = new ArrayList<>();
//
//        PreferenceManager.setString(context, "region1Depth", "서울");
//        PreferenceManager.setString(context, "region2Depth", "광진");
//        PreferenceManager.setString(context, "region3Depth", "화양");

        String regionFirst = PreferenceManager.getString(context, "region1Depth");
        String regionSecond = PreferenceManager.getString(context, "region2Depth");
        String regionThird = PreferenceManager.getString(context, "region3Depth");

        Call<BoardListResponse> boardListCall = apiInterface.getBoardList(regionFirst, regionSecond, regionThird, boardCode);
        try {
            items = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    final ArrayList<PostItem> postItems = new ArrayList<>();
                    try {
                        BoardListResponse boardListResponse = boardListCall.execute().body();
                        assert boardListResponse != null;
                        List<BoardListResponse.BoardResponse> boardResponses = boardListResponse.getBoardList();
                        for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                            String url = getThumbnailUrl(boardResponse.getInsertDate());
                            PostItem item = new PostItem();
                            item.onBindBoardApi(boardResponse, url);

                            if (boardResponse.getDelYn().equals("N")) {
                                postItems.add(item);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return postItems;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static boolean boardWrite(Context context, PostItem item) {
        boolean complete = false;
        HashMap<String, String> body = item.onBindBodyApi(context);

        Call<MemberResponse> boardWrite = apiInterface.boardWrite(body);

        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    boolean finalComplete = false;
                    try {
                        MemberResponse response = boardWrite.execute().body();
                        if (response != null && response.getResponseCode() == 400) {
                            finalComplete = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return complete;
    }

    public static boolean boardRevise(Context context, PostItem item) {
        boolean complete = false;
        HashMap<String, String> body = item.onBindBodyApi(context);
        body.put("BOARD_SEQ", String.valueOf(item.getBoardSeq()));
        Call<MemberResponse> responseCall = apiInterface.boardRevise(body);
        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                boolean finalComplete = false;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        MemberResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 402) {
                            finalComplete = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return complete;
    }

    public static boolean boardDelete(Context context, PostItem item) {
        boolean complete = false;
        HashMap<String, String> body = new HashMap<>();
        body.put("BOARD_SEQ", String.valueOf(item.getBoardSeq()));
        body.put("USER_TOKEN", PreferenceManager.getString(context, "userToken"));
        Call<MemberResponse> responseCall = apiInterface.boardDelete(body);
        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                boolean finalComplete = false;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        MemberResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 420) {
                            finalComplete = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return complete;
    }


    public static ArrayList<CommentItem> getBoardCommentList(PostItem postItem) {
        ArrayList<CommentItem> commentItems = new ArrayList<>();
        Call<CommentListResponse> commentListResponseCall = apiInterface.getBoardCommentList(postItem.getBoardSeq());
        try {
            commentItems = new AsyncTask<Void, Void, ArrayList<CommentItem>>() {
                final ArrayList<CommentItem> items = new ArrayList<>();
                @Override
                protected ArrayList<CommentItem> doInBackground(Void... voids) {
                    try {
                        CommentListResponse commentListResponse = commentListResponseCall.execute().body();
                        assert commentListResponse != null;
                        List<CommentListResponse.CommentResponse> commentResponses = commentListResponse.getBoardCommentList();
                        for (CommentListResponse.CommentResponse comment : commentResponses) {
                            CommentItem item = new CommentItem();
                            item.onBindCommentApi(comment);

                            if(item.getDelYn().equals("N")){
                                items.add(item);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentItems;
    }

    public static boolean commentWrite(Context context, CommentItem comment) {
        boolean complete = false;
        HashMap<String, String> body = comment.onBindBodyApi(context);
        Call<MemberResponse> responseCall = apiInterface.commentWrite(body);
        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                boolean finalComplete = false;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        MemberResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 800) {
                            finalComplete = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complete;
    }

    public static boolean commentDelete(Context context, CommentItem item){
        boolean complete = false;
        HashMap<String, String> body = new HashMap<>();
        body.put("COMMENT_SEQ",String.valueOf(item.getCommentSeq()));
        body.put("USER_TOKEN",PreferenceManager.getString(context,"userToken"));
        body.put("BOARD_SEQ",String.valueOf(item.getBoardSeq()));
        Call<MemberResponse> responseCall = apiInterface.commentDelete(body);
        try{
            complete = new AsyncTask<Void, Void, Boolean>() {
                boolean finalComplete = false;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try{
                        MemberResponse response = responseCall.execute().body();
                        if(response != null && response.getResponseCode()==820){
                            finalComplete = true;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();
        } catch (Exception e){
            e.printStackTrace();
        }
        return complete;
    }

    public static boolean childCommentWrite(Context context,CommentItem parentComment, CommentItem comment) {
        boolean complete = false;
        HashMap<String, String> body = comment.onBindBodyApi(context);
        body.put("PARENT_COMMENT_SEQ",String.valueOf(parentComment.getCommentSeq()));
        Call<MemberResponse> responseCall = apiInterface.commentWrite(body);
        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                boolean finalComplete = false;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        MemberResponse response = responseCall.execute().body();
                        if(response != null && response.getResponseCode()==800){
                            finalComplete = true;
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();
        } catch (Exception e){
            e.printStackTrace();
        }
        return complete;
    }

    public static boolean boardLikePlus(Context context, PostItem item) {
        boolean complete = false;
        HashMap<String, String> body = new HashMap<>();
        body.put("BOARD_SEQ", String.valueOf(item.getBoardSeq()));
        body.put("USER_TOKEN", PreferenceManager.getString(context, "userToken"));
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfnow = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");
        String timeData = sdfnow.format(date);
        body.put("LIKE_DATE", timeData);

        Call<MemberResponse> responseCall = apiInterface.boardLikePlus(body);
        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                boolean finalComplete = false;
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        MemberResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 100) {
                            finalComplete = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complete;
    }

    public static boolean boardLikeMinus(Context context, PostItem item) {
        boolean complete = false;
        HashMap<String, String> body = new HashMap<>();
        body.put("BOARD_SEQ", String.valueOf(item.getBoardSeq()));
        body.put("USER_TOKEN", PreferenceManager.getString(context, "userToken"));

        Call<MemberResponse> responseCall = apiInterface.boardLikeMinus(body);
        try {
            complete = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    boolean finalComplete = false;
                    try {
                        MemberResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 102) {
                            finalComplete = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return finalComplete;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return complete;
    }

    public static ArrayList<PostItem> getBoardWriteList(Context context) {
        ArrayList<PostItem> myBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardWriteList(PreferenceManager.getString(context, "userToken"));
        try {
            myBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 410) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getBoardList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y")) {
                                    items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myBoardList;
    }

    public static ArrayList<PostItem> getExchangeShareBoardWriteList(Context context, String category){
        ArrayList<PostItem> myBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardWriteList(PreferenceManager.getString(context, "userToken"));
        try {
            myBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 410) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getBoardList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y")) {
                                    if (item.getCategory().equals(category))
                                        items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myBoardList;
    }

    public static ArrayList<PostItem> getRecipeBoardWriteList(Context context) {
        ArrayList<PostItem> myBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardWriteList(PreferenceManager.getString(context, "userToken"));
        try {
            myBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 410) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getBoardList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y") && item.getCategory().equals("RECIPE")) {
                                    items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myBoardList;
    }

    public static ArrayList<PostItem> getFreeBoardWriteList(Context context) {
        ArrayList<PostItem> myBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardWriteList(PreferenceManager.getString(context, "userToken"));
        try {
            myBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 410) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getBoardList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y") && item.getCategory().equals("FREE")) {
                                    items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return myBoardList;
    }

    public static ArrayList<PostItem> getBoardLikeList(Context context) {
        ArrayList<PostItem> likeBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardLikeList(PreferenceManager.getString(context, "userToken"));
        try {
            likeBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 110) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getLikeList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y")) {
                                    items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return likeBoardList;
    }

    public static ArrayList<PostItem> getExchangeShareBoardLikeList(Context context, String category){
        ArrayList<PostItem> likeBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardLikeList(PreferenceManager.getString(context, "userToken"));
        try {
            likeBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 110) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getLikeList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y")) {
                                    if (item.getCategory().equals(category))
                                        items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return likeBoardList;
    }

    public static ArrayList<PostItem> getRecipeBoardLikeList(Context context) {
        ArrayList<PostItem> likeBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardLikeList(PreferenceManager.getString(context, "userToken"));
        try {
            likeBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 110) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getLikeList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y") && item.getCategory().equals("RECIPE")) {
                                    items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return likeBoardList;
    }

    public static ArrayList<PostItem> getFreeBoardLikeList(Context context) {
        ArrayList<PostItem> likeBoardList = new ArrayList<>();
        Call<BoardListResponse> responseCall = apiInterface.getBoardLikeList(PreferenceManager.getString(context, "userToken"));
        try {
            likeBoardList = new AsyncTask<Void, Void, ArrayList<PostItem>>() {
                ArrayList<PostItem> items = new ArrayList<>();

                @Override
                protected ArrayList<PostItem> doInBackground(Void... voids) {
                    try {
                        BoardListResponse response = responseCall.execute().body();
                        if (response != null && response.getResponseCode() == 110) {
                            List<BoardListResponse.BoardResponse> boardResponses = response.getLikeList();
                            for (BoardListResponse.BoardResponse boardResponse : boardResponses) {
                                PostItem item = new PostItem();
                                item.onBindBoardApi(boardResponse, getThumbnailUrl(boardResponse.getInsertDate()));
                                if (!item.getDelYN().equals("Y") && item.getCategory().equals("FREE")) {
                                    items.add(item);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return items;
                }
            }.execute().get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return likeBoardList;
    }

    public static boolean isLikedBoard(Context context, PostItem item) {
        ArrayList<PostItem> likedPosts = getBoardLikeList(context);
        if (likedPosts != null && likedPosts.size() > 0) {
            for (PostItem postItem : likedPosts) {
                if (postItem.getBoardSeq() == item.getBoardSeq() &&
                        postItem.getCategory().equals(item.getCategory())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getTime() {
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        TimeZone time = TimeZone.getTimeZone("Asia/Seoul");
        format.setTimeZone(time);
        return format.format(date);
    }

    private static String getThumbnailUrl(String timestamp) {
        String result = "";

        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("postPhotos").document(timestamp);
        Task<DocumentSnapshot> task = documentReference.get();
        try {
            DocumentSnapshot documentSnapshot = Tasks.await(task);
            result = documentSnapshot.getString("0");
            Log.w("############", result);
        } catch (Exception e) {
            result = "NONE";
        }

        return result;
    }
}
