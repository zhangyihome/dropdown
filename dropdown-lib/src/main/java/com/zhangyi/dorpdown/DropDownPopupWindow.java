package com.zhangyi.dorpdown;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangyi.dorpdown.adapter.DropDownViewRecycleViewAdapter;
import com.zhangyi.dorpdown.bean.KeyValues;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DropDownPopupWindow extends PopupWindow implements DropDownViewRecycleViewAdapter.OnItemViewClickListener {
    private List<KeyValues> mDataList;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private int gridColumns;
    private int itemWidth = 100;
    private DropDownView dropDownView;
    private static final String TAG = "DropDownView";
    private View view;
    private DropDownViewRecycleViewAdapter myRecycleViewAdapter;
    private OnItemSelectListener onItemSelectListener;


    public DropDownPopupWindow(Context context, List<KeyValues> dataList, int gridColumns, int itemWidth) {
        super(context);
        mDataList = dataList;
        mContext = context;
        this.gridColumns = gridColumns;
        this.itemWidth = itemWidth;
        initView();
    }

    public DropDownPopupWindow(Context context, List<KeyValues> dataList, int itemWidth) {
        this(context, dataList, 0, itemWidth);
    }

    public void setDataList(List<KeyValues> mDataList) {
        this.mDataList = mDataList;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_popupwindow_dropdown, null);
        // id_pop_tv= (TextView) view.findViewById(R.id.id_pop_tv);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_rv);

        if (myRecycleViewAdapter == null) {
            myRecycleViewAdapter = new DropDownViewRecycleViewAdapter(mContext, mDataList, itemWidth);
        }
        if (gridColumns == 0) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, gridColumns));
        }

        myRecycleViewAdapter.setOnItemViewClickListener(this);
        //??????Item?????????????????????
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(myRecycleViewAdapter);
        //??????PopupWindow???View
        this.setContentView(view);
        //??????PopupWindow??????????????????
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //??????PopupWindow??????????????????
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));//????????????  ps:xml bg??????????????????
        this.setFocusable(true);//?????????  ???????????????????????????popupWindow
        //id_pop_tv.setOnClickListener(this);
    }


    public void setAdapter(DropDownViewRecycleViewAdapter adapter) {
        if (adapter != null) {
            myRecycleViewAdapter = adapter;
            adapter.setOnItemViewClickListener(this);
            mRecyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void onItemViewClick(KeyValues kv, int position, int realPosition) {
        this.dismiss();
        if (position == 0) {
            onItemSelectListener.onItemSelect(null, position, realPosition);
        } else {
            KeyValues nowMap = mDataList.get(realPosition);
            onItemSelectListener.onItemSelect(nowMap, position, realPosition);
        }


    }


    public interface OnItemSelectListener {
        void onItemSelect(KeyValues map, int pos, int realPos);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }


    public void showAsDropDownBelwBtnView(View btnView) {
        int anchor_w = btnView.getWidth();
        this.itemWidth = anchor_w;
        this.dropDownView = (DropDownView) btnView;
//        initView();

        int allHeight1 = this.getMaxAvailableHeight(view);
        int allHeight2 = this.getMaxAvailableHeight(btnView);
        Log.d(TAG, "showAsDropDownBelwBtnView: allHeight1:" + allHeight1);//1920    ?????????????????????
        Log.d(TAG, "showAsDropDownBelwBtnView: allHeight2:" + allHeight2);// ????????????  ??????????????????   ?????????popwindow???????????????????????????????????????????????????????????????????????????

        int btnViewHeight = btnView.getHeight();
        Log.d(TAG, "showAsDropDownBelwBtnView: btnViewHeight:" + btnViewHeight);
        int mRecyclerViewHeight = getTargetHeight(mRecyclerView);
        Log.d(TAG, "showAsDropDownBelwBtnView: mRecyclerView:" + mRecyclerViewHeight);

        int[] position = new int[2];
        btnView.getLocationInWindow(position);
        int x = position[0];
        int y = position[1];
        Log.d(TAG, "showAsDropDownBelwBtnView: BtnView x:" + x);
        Log.d(TAG, "showAsDropDownBelwBtnView: BtnView y:" + y);


        if ((allHeight1 - y - btnViewHeight) < mRecyclerViewHeight && y > mRecyclerViewHeight) {
            this.showAsDropDown(btnView, 0, -(mRecyclerViewHeight + btnViewHeight));
        } else {
            this.showAsDropDown(btnView);
        }


    }

    /**
     * ?????????????????????View?????????????????????
     *
     * @param v
     * @return
     */
    private int getTargetHeight(View v) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class,
                    int.class);
            m.setAccessible(true);
            m.invoke(v, View.MeasureSpec.makeMeasureSpec(
                    ((View) v.getParent()).getMeasuredWidth(),
                    View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
        } catch (Exception e) {

        }
        return v.getMeasuredHeight();
    }

}
