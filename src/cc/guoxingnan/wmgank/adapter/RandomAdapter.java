package cc.guoxingnan.wmgank.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.guoxingnan.wmgank.R;
import cc.guoxingnan.wmgank.entity.GankModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class RandomAdapter extends RecyclerView.Adapter<RandomAdapter.ViewHolder> {

	private Context mContext;
	private ArrayList<GankModel> mGankModels;
	private String mType;
	private static final int WELFARE_TYPE = 100;
	private static final int OTHER_TYPE = 101;


	public RandomAdapter(Context context, ArrayList<GankModel> gankModels, String type) {
		mContext = context;
		mGankModels = gankModels;
		mType = type;
	}

	public void setType(String type) {
		mType = type;
	}

	@Override
	public RandomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_random, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public int getItemViewType(int position) {
		if (mContext.getResources().getStringArray(R.array.type)[0].equals(mType)) {
			return WELFARE_TYPE;
		} else {
			return OTHER_TYPE;
		}
	}


	@Override
	public void onBindViewHolder(final RandomAdapter.ViewHolder holder, final int position) {
		final GankModel gankModel = mGankModels.get(position);
		if (getItemViewType(position) == WELFARE_TYPE) {
			holder.mOther.setVisibility(View.GONE);
			holder.mImageView.setVisibility(View.VISIBLE);
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
			layoutParams.height = (int) mContext.getResources().getDimension(R.dimen.random_image_height);
			String url = mGankModels.get(position).getUrl();
			Glide.with(mContext).load(url).placeholder(R.color.stay_color).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(holder.mImageView);
		} else {
			holder.mOther.setVisibility(View.VISIBLE);
			holder.mImageView.setVisibility(View.GONE);
			holder.mWho.setText("via: " + gankModel.getWho());
			holder.mDesc.setText(gankModel.getDesc());
			holder.mUrl.setText(gankModel.getUrl());
			holder.mType.setText(gankModel.getType());

			final int pos = holder.getLayoutPosition();
			if (listener != null) {
				holder.itemView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						listener.onItemClick(holder.itemView, pos);
					}

				});

				holder.itemView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						listener.onItemLongClick(holder.itemView, pos);
						return false;
					}
				});
			}
		}
	}

	@Override
	public int getItemCount() {
		return mGankModels.size();
	}


	class ViewHolder extends RecyclerView.ViewHolder {

		ImageView mImageView;
		RelativeLayout mOther;
		TextView mWho;
		TextView mDesc;
		TextView mUrl;
		TextView mType;

		public ViewHolder(View itemView) {
			super(itemView);
			mImageView = (ImageView) itemView.findViewById(R.id.sdv_view);
			mOther = (RelativeLayout) itemView.findViewById(R.id.rl_other);
			mWho = (TextView) itemView.findViewById(R.id.tv_who);
			mDesc = (TextView) itemView.findViewById(R.id.tv_desc);
			mUrl = (TextView) itemView.findViewById(R.id.tv_url);
			mType = (TextView) itemView.findViewById(R.id.tv_type);
		}
	}

	/**
	 * 点击事件接口
	 */
	private OnItemClickListener listener;

	public interface OnItemClickListener{
		void onItemClick(View view, int position);
		void onItemLongClick(View view, int position);
	}


	public void setOnItemClickListener(OnItemClickListener listener){
		this.listener = listener;
	}
	
	
	public void addData(int position){
        mGankModels.add(position,new GankModel("https://github.com/mixinan","万码千钧","小楠","我的GitHub"));        
        notifyItemInserted(position);
    }

    public void removeData(int position){
    	mGankModels.remove(position);
        notifyItemRemoved(position);
    }
}
