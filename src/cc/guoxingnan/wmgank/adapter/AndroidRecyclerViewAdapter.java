package cc.guoxingnan.wmgank.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import cc.guoxingnan.wmgank.R;
import cc.guoxingnan.wmgank.adapter.AndroidRecyclerViewAdapter.AndroidViewHolder;
import cc.guoxingnan.wmgank.entity.Android;

public class AndroidRecyclerViewAdapter extends Adapter<AndroidViewHolder> {
	private Context context;
	private ArrayList<Android> data;

	
	public AndroidRecyclerViewAdapter(Context context, ArrayList<Android> data) {
		super();
		this.context = context;
		if (data==null) data = new ArrayList<Android>();
		this.data = data;
	}


	public void reflash(ArrayList<Android> list){
		data = list;
		notifyDataSetChanged();
	}


	@Override
	public int getItemCount() {
		return data.size();
	}


	@Override
	public AndroidViewHolder onCreateViewHolder(ViewGroup parent, int position) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_android, parent, false);
		return new AndroidViewHolder(view);
	}


	@Override
	public void onBindViewHolder(final AndroidViewHolder holder, int position) {
		Android a = data.get(position);
		holder.tvTime.setText(a.getTime().trim().substring(0, 10));
		holder.tvDesc.setText(a.getDesc());

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

	class AndroidViewHolder extends RecyclerView.ViewHolder{
		TextView tvTime;
		TextView tvDesc;

		public AndroidViewHolder(View itemView) {
			super(itemView);
			tvTime = (TextView) itemView.findViewById(R.id.tv_android_item_time);
			tvDesc = (TextView) itemView.findViewById(R.id.tv_android_item_desc);
		}

	}

	/**
	 * ����¼��ӿ�
	 */
	private OnItemClickListener listener;

	public interface OnItemClickListener{
		void onItemClick(View view, int position);
		void onItemLongClick(View view, int position);
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		this.listener = onItemClickListener;
	}

}
