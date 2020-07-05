package com.hankki.fooddeal.ux.itemtouchhelper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hankki.fooddeal.R;

import java.util.ArrayList;
import java.util.List;

/**Item Touch Helper Extension Library
 * Main Recycler Adapter
 * 리사이클러 뷰 어댑터 Extension 버전
 * Swipe to Delete 에 사용
 *
 * !!라이브러리 불안정함
 * 라이브러리 바꾸거나, 보수 필요*/
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ItemBaseViewHolder> {

    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    private List<TestModel> mDatas;
    private Context mContext;
    private ItemTouchHelperExtension mItemTouchHelperExtension;

    public MainRecyclerAdapter(Context context) {
        mDatas = new ArrayList<>();
        mContext = context;
    }

    public void setDatas(List<TestModel> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
    }

    public void updateData(List<TestModel> datas) {
        setDatas(datas);
        notifyDataSetChanged();
    }

    public void setItemTouchHelperExtension(ItemTouchHelperExtension itemTouchHelperExtension) {
        mItemTouchHelperExtension = itemTouchHelperExtension;
    }

    private LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ItemBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_item_main, parent, false);
        return new ItemBaseViewHolder(view);
    }

    @Override /**View Holder 와 layout 바인딩*/
    public void onBindViewHolder(@NonNull ItemBaseViewHolder holder, int position) {
        holder.bind(mDatas.get(position));
        holder.mViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Item Content click: #" + holder.getAdapterPosition()
                        , Toast.LENGTH_SHORT).show();
                /**스와이프 되어있는 상태일 경우*/
                if(holder.mViewContent.getTranslationX()!=0) {
                    holder.mViewContent.setTranslationX(0);
                    notifyDataSetChanged();
                }
                /**스와이프 안되어있는 상태일 경우
                 * 채팅방 들어가기*/
            }
        });

        holder.mActionViewDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doDelete(holder.getAdapterPosition());
                    }
                }
            );}

    private void doDelete(int adapterPosition) {
        mDatas.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void move(int from, int to) {
        TestModel prev = mDatas.remove(from);
        mDatas.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }



    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE_ACTION_WIDTH;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ItemBaseViewHolder extends RecyclerView.ViewHolder implements Extension{
        View mViewContent;
        View mActionContainer;
        TextView mTextTitle;
        TextView mTextIndex;
        View mActionViewDelete;

        public ItemBaseViewHolder(View itemView) {
            super(itemView);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_list_main_title);
            mTextIndex = (TextView) itemView.findViewById(R.id.text_list_main_index);
            mViewContent = itemView.findViewById(R.id.view_list_main_content);
            mActionContainer = itemView.findViewById(R.id.view_list_repo_action_container);
            mActionViewDelete = itemView.findViewById(R.id.view_list_repo_action_delete);
        }

        @Override
        public float getActionWidth() {
            return mActionContainer.getWidth();
        }

        public void bind(TestModel testModel) {
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        mItemTouchHelperExtension.startDrag(ItemBaseViewHolder.this);
                    }
                    return true;
                }
            });
        }
    }
}
