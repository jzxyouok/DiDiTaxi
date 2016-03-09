package com.cuit.diditaxi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuit.diditaxi.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/3/8.
 */
public class EndRecommendAdapter extends RecyclerView.Adapter<EndRecommendAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mDataList;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public EndRecommendAdapter(Context context, List<String> dataList) {
        mContext = context;
        mDataList = dataList;

        mInflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void itemLongClick(View view, int position);
    }

    public void replaceData(List<String> newDataList){
        mDataList.clear();
        mDataList.addAll(newDataList);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.list_item_end_recommend, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String option = mDataList.get(position);
        holder.mTv.setText(option);

        if (mOnItemClickListener!=null){
            holder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickPosition = holder.getLayoutPosition();
                    mOnItemClickListener.itemLongClick(v,clickPosition);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layout_list_item_end_recommend)
        LinearLayout mLayout;
        @Bind(R.id.tv_list_item_end_recommend)
        TextView mTv;


        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
