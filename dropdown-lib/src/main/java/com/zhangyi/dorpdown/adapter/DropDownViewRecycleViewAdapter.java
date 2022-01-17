package com.zhangyi.dorpdown.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zhangyi.dorpdown.DropDownView;
import com.zhangyi.dorpdown.R;
import com.zhangyi.dorpdown.bean.KeyValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropDownViewRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "DropDownView";
    private int itemWidth;
    private KeyValues checkedKv;


    public DropDownViewRecycleViewAdapter(Context context, List<KeyValues> dataList, int itemWidth) {
        mContext = context;
        this.mDataList = dataList;
        this.itemWidth = itemWidth;
    }

    private List<KeyValues> mDataList;
    private Context mContext;


    /**
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dropdown_item, parent, false);//不false  在popupwindow里报错
        //设置Item的宽
        view.setLayoutParams(new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        Log.d(TAG, "itemWidth:"+itemWidth);

        MyRecyclerViewHolder myRecyclerViewHolder = new MyRecyclerViewHolder(view);
        return myRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyRecyclerViewHolder myRecyclerViewHolder = (MyRecyclerViewHolder) holder;
        myRecyclerViewHolder.id_ll_item.setBackgroundResource(R.drawable.selector_shape_list_item_notop);

        int realPos = position - 1;
        KeyValues currentKV=null;
        if (position == 0) {
            ///
            myRecyclerViewHolder.mTextView.setText(DropDownView.NUSELETED_SHOW_NAME);

        } else {
            ///
            currentKV=mDataList.get(realPos);
            boolean isShowAbove = currentKV==checkedKv;
            if (isShowAbove) {
                myRecyclerViewHolder.id_ll_item.setBackgroundResource(R.drawable.selector_shape_list_item_nobottom);
            }
            ///
            if (currentKV.getKey() != null) {
                myRecyclerViewHolder.mTextView.setText(currentKV.getKey());
            }
        }

        KeyValues finalCurrentKV = currentKV;
        myRecyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedKv=finalCurrentKV;
                mOnItemViewClickListener.onItemViewClick(finalCurrentKV, position, realPos);
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDataList.size() + 1;
    }

    class MyRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout id_ll_item;

        public MyRecyclerViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.id_tv);
            id_ll_item = (LinearLayout) itemView.findViewById(R.id.id_ll_item);

        }
    }


    public interface OnItemViewClickListener {
        void onItemViewClick(KeyValues kv, int position, int realPosition);
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        mOnItemViewClickListener = onItemViewClickListener;
    }

    private OnItemViewClickListener mOnItemViewClickListener;


}
