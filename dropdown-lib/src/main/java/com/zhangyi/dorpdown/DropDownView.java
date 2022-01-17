package com.zhangyi.dorpdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.zhangyi.dorpdown.adapter.DropDownViewRecycleViewAdapter;
import com.zhangyi.dorpdown.bean.KeyValues;
import com.zhangyi.dorpdown.tools.SizeTool;
import com.zhangyi.dorpdown.tools.StringTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropDownView extends AppCompatTextView implements View.OnClickListener {

    private Context mContext;
    private static final String TAG = "DropDownView";
    public static final String NUSELETED_SHOW_NAME = "请选择";
    private List<KeyValues> dataList = new ArrayList<>();

    private View nowClickView;
    private DropDownViewRecycleViewAdapter dropDownViewRecycleViewAdapter;

    public String defaultText;
    public int defaultBthWidth = 0;

    public boolean canSelect=true;
    private DropDownPopupWindow myPopupwindow;

    public String getDefaultText() {
        return defaultText;
    }


    public void setDataList(List<KeyValues> dataList) {
        this.dataList.clear();
        if (dataList != null) {
            this.dataList.addAll(dataList);
            Log.d(TAG, "setNameStateList: " + this.dataList.size());
            configMaxItemWidth();
        }

    }


    public DropDownView(Context context) {
        this(context,null,0);
    }

    public DropDownView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DropDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.DropDownView);
        canSelect = typedArray.getBoolean(R.styleable.DropDownView_canSelect, true);
        typedArray.recycle();
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        if (this.getText() == null || StringTool.isNullOrNullStrOrBlankStr(this.getText().toString())) {
            this.setText(NUSELETED_SHOW_NAME);
        }
        defaultText = this.getText().toString();//
        // this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (this.getPaddingTop() == 0 && this.getPaddingBottom() == 0 && this.getPaddingLeft() == 0 && this.getPaddingRight() == 0) {
            int paddingLeft_Right = SizeTool.dp2px(mContext, 8);
            int paddingTop_Bottom = SizeTool.dp2px(mContext, 5);
            this.setPadding(paddingLeft_Right, paddingTop_Bottom, paddingLeft_Right, paddingTop_Bottom);
        }
        this.setOnClickListener(this);
        if (canSelect) {
            this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_blue_grey_400_18dp, 0);
        }
        this.setBackgroundResource(R.drawable.shape_list);
        this.setSingleLine();
        /**
         * setup min width
         */
        // int text_width= (int) this.getPaint().measureText("请选择");//当前画笔测量三个字的width
        // int text_ScaleX= (int) this.getTextScaleX();//字间距
        //
        // final int text_count=230;
        int temp_min_width = SizeTool.dp2px(mContext, 60);//三个字 大概
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (this.getMinWidth() != -1 && this.getMinWidth() != 0) {
                temp_min_width = this.getMinWidth();
            }
        }
        this.setMinWidth(temp_min_width);

        /**
         * setup max width
         */
        // final int text_count_MAX=10;
        int temp_max_width = SizeTool.dp2px(mContext, 130);//九个字 大概
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (this.getMaxWidth() != -1 && this.getMaxWidth() != 0 && this.getMaxWidth() != Integer.MAX_VALUE) {
                temp_max_width = this.getMaxWidth();
            }
        }
        this.setMaxWidth(temp_max_width);
        this.setEllipsize(TextUtils.TruncateAt.END);


        defaultBthWidth = SizeTool.getMeasuredWidthMy(this);
        configMaxItemWidth();
    }


    /**
     * 2016年9月20日09:48:07
     */
    private void configMaxItemWidth() {
        //初始化w
        int newItemWidth = dealItemMaxWidth(dataList, defaultBthWidth, this.getTextSize());
        if (canSelect) {
            newItemWidth+=26;
        }
        this.setWidth(newItemWidth);
        Log.i(TAG, "configMaxItemWidth:newItemWidth:" + newItemWidth);
        myPopupwindow = new DropDownPopupWindow(mContext, dataList,newItemWidth);
        myPopupwindow.setDataList(dataList);

        myPopupwindow.setAdapter(dropDownViewRecycleViewAdapter);
        myPopupwindow.setOnItemSelectListener(new DropDownPopupWindow.OnItemSelectListener() {
            @Override
            public void onItemSelect(KeyValues map, int pos, int realPos) {
                Log.d(TAG, "onItemSelect:"+pos);

                if (map == null) {
                    ((DropDownView) nowClickView).setText(NUSELETED_SHOW_NAME);
                } else {
                    ////
                    if (map.getKey() != null) {
                        ((DropDownView) nowClickView).setText(map.getKey());
                    }

                }
                if (realPos < 0) return;
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(map, pos, realPos);
                }
            }
        });

    }

    /**
     * 2016年9月20日09:48:07
     *
     * @param dataList
     * @param btnWidth
     * @param textSize
     * @return
     */
    private int dealItemMaxWidth(List<KeyValues> dataList, int btnWidth, float textSize) {
        int tempWidth = 0;
        int maxLen = 0;
        String maxName = "";
        for (int i = 0; i < dataList.size(); i++) {
            String nowName = dataList.get(i).getKey();
            if (nowName.length() > maxLen) {
                maxLen = nowName.length();
                maxName = nowName;
            }
        }
        if (maxLen > DropDownView.NUSELETED_SHOW_NAME.length()) {
            int defaultTextWidth = StringTool.getAllTextWidth(DropDownView.NUSELETED_SHOW_NAME, textSize);
            int maxNameTextWidth = StringTool.getAllTextWidth(maxName, textSize);

            tempWidth = btnWidth - defaultTextWidth + maxNameTextWidth;
        } else {
            tempWidth = btnWidth;
        }

        return tempWidth;
    }


    public int dealTextMyW(int text_width, int text_ScaleX, int text_count) {
        return text_width * text_count + text_ScaleX * (text_count - 1);
    }


    @Override
    public void onClick(View v) {
        if (!canSelect) {
            return;
        }

        nowClickView = v;

        if (dataList != null && dataList.size() > 0) {
            myPopupwindow.showAsDropDownBelwBtnView(nowClickView);
        }
    }

    public void setAdapter(DropDownViewRecycleViewAdapter adapter) {
        dropDownViewRecycleViewAdapter=adapter;
        myPopupwindow.setAdapter(dropDownViewRecycleViewAdapter);

    }

    public interface OnItemClickListener {
        void onItemClick(KeyValues map, int pos, int realPos);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    OnItemClickListener onItemClickListener;

    /**
     * 得到所选的key
     */
    public String getSelectKey() {
        String key = NUSELETED_SHOW_NAME;
        if (this.getText() == null || StringTool.isNullOrNullStrOrBlankStr(this.getText().toString())) {
            //
        } else {
            key = this.getText().toString();
        }
        return key;
    }


    /**
     * 通过key得到所选的index
     */
    public int getPositionByKey(String key) {
        int pos = -1;
        if (key != null && !key.equals("")) {

            for (int i = 0; i < dataList.size(); i++) {
                if (key.equals(dataList.get(i).getKey())) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }



    /**
     * 通过index得到所选的key
     */
    public String getKeyByPosition(int position) {
        return String.valueOf(dataList.get(position).getKey());
    }

    /**
     * 通过position得到所选的value
     */
    public Object getValueByPosition(int position) {
        Object value = String.valueOf(dataList.get(position).getValue());
        return value;
    }


}
