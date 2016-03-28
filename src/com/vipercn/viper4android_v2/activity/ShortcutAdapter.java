package com.vipercn.viper4android_v2.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.vipercn.viper4android_v2.R;

public class ShortcutAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private String[] mEqualizerPreset;
    private int[] mPicDataset;

    private int mSelect;

    private Context context;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    private MainActivity mLauncher;

    class viewHolder extends RecyclerView.ViewHolder {

        public TextView nameTv;
        public ImageView picTv;
        public ImageView accept;

        public viewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.info_text);
            picTv = (ImageView) itemView.findViewById(R.id.pic);
            accept = (ImageView) itemView.findViewById(R.id.accept);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int Position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShortcutAdapter(Context mContext, String[] myDataset, int[] myPicDataset) {
        context = mContext;
        mEqualizerPreset = myDataset;
        mPicDataset = myPicDataset;
        mLauncher = (MainActivity) context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        view.setOnClickListener(this);
        return new viewHolder(view);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder holder = (viewHolder) viewHolder;
        holder.itemView.setTag(i);
        holder.nameTv.setText(mEqualizerPreset[i]);
        holder.picTv.setBackgroundResource(mPicDataset[i]);
        mSelect = mLauncher.getPrefs("settings").getInt("home.sound.select", 0);
        if (i == mSelect) holder.accept.setVisibility(View.VISIBLE);
        else holder.accept.setVisibility(View.GONE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mEqualizerPreset.length;
    }
}
