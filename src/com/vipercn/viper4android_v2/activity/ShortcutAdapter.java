package com.vipercn.viper4android_v2.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import com.vipercn.viper4android_v2.R;
import com.vipercn.viper4android_v2.FxApplication;
import com.vipercn.viper4android_v2.misc.ItemInfo;
import com.vipercn.viper4android_v2.misc.LruImageCache;

import java.util.LinkedList;

public class ShortcutAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private LinkedList<ItemInfo> mData;

    private int mSelect;

    private Context mContext;

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    private FxApplication mFxApplication;

    private MainActivity mLauncher;

    private LruImageCache mLruImageCache;

    private ImageLoader mImageLoader;

    private String url = "http://192.168.199.248/pic/";

    class viewHolder extends RecyclerView.ViewHolder {

        public TextView nameTv;
        public NetworkImageView picTv;
        public ImageView accept;

        public viewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.info_text);
            picTv = (NetworkImageView) itemView.findViewById(R.id.pic);
            accept = (ImageView) itemView.findViewById(R.id.accept);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int Position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShortcutAdapter(Context context, LinkedList<ItemInfo> data) {
        mContext = context;
        mData = data;
        mFxApplication = FxApplication.getFxApplication();
        mLauncher = (MainActivity) context;
        mLruImageCache = LruImageCache.instance();
        mImageLoader = new ImageLoader(mFxApplication.getQueue(), mLruImageCache);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(mContext);
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
        holder.nameTv.setText(mData.get(i).getUser());
        holder.picTv.setDefaultImageResId(R.drawable.icon);
        holder.picTv.setErrorImageResId(R.drawable.icon);
        holder.picTv.setImageUrl(url + mData.get(i).getPicture(), mImageLoader);	
        //mSelect = mLauncher.getPrefs("settings").getInt("home.sound.select", 0);
        //if (i == mSelect) holder.accept.setVisibility(View.VISIBLE);
        //else holder.accept.setVisibility(View.GONE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
