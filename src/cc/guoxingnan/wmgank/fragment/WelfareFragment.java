package cc.guoxingnan.wmgank.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cc.guoxingnan.wmgank.ImageGalleryActivity;
import cc.guoxingnan.wmgank.R;
import cc.guoxingnan.wmgank.adapter.WelfareAdapter;
import cc.guoxingnan.wmgank.adapter.WelfareAdapter.OnItemClickListener;
import cc.guoxingnan.wmgank.entity.Girl;
import cc.guoxingnan.wmgank.view.SpacesItemDecoration;
import cc.guoxingnan.wmgank.view.UpRefreshRecyclerView;
import cc.guoxingnan.wmgank.view.UpRefreshRecyclerView.UpRefreshListener;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class WelfareFragment extends Fragment implements UpRefreshListener,SwipeRefreshLayout.OnRefreshListener{

	private static WelfareFragment mWelfareFragment;
	private Context mContext;
	private UpRefreshRecyclerView mRecyclerView;
	private WelfareAdapter adapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ArrayList<Girl> girls;
	private int page = 1;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_welfare, container, false);
		mContext = getActivity();
		initView(view);
		initData();
		initListener();
		return view;
	}


	private void initView(View view) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_view);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.yellow, R.color.green);

		mRecyclerView = (UpRefreshRecyclerView) view.findViewById(R.id.rv_welfare);
		StaggeredGridLayoutManager layoutManager= new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);

		girls = new ArrayList<Girl>();
		adapter = new WelfareAdapter(mContext, girls);
		mRecyclerView.setAdapter(adapter);

		//设置item之间的间隔
		SpacesItemDecoration decoration=new SpacesItemDecoration(12);
		mRecyclerView.addItemDecoration(decoration);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());

		//点击事件
		adapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemLongClick(View view, int position) {
				//替换当前图片为志玲
				adapter.addData(position);
				adapter.removeData(position+1);
			}

			@Override
			public void onItemClick(View view, int position) {
				ImageGalleryActivity.launch(mContext, girls, position);
			}
		});

	}



	public static WelfareFragment newInstance() {
		if (mWelfareFragment == null) {
			mWelfareFragment = new WelfareFragment();
		}
		return mWelfareFragment;
	}


	public void initData() {
		mSwipeRefreshLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(true);
				page = 1;
				getData(page);
			}
		}, 300);
		mRecyclerView.smoothScrollToPosition(0);
	}

	private void initListener() {
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mRecyclerView.setUpRefreshListener(this);
	}


	private void getData(final int page) {
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		StringRequest stringRequest = new StringRequest("http://gank.io/api/data/福利/10/"+page, 
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					if (page == 1) {
	                    girls.clear();
	                }
					girls.addAll(parseGirls(response));
					adapter.notifyDataSetChanged();
					
					//更新界面布局
					mRecyclerView.onRefreshFinish();
					mSwipeRefreshLayout.setRefreshing(false);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, 
		new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("Error", error.getMessage(), error);
				//更新界面布局：取消刷新动画
				mRecyclerView.onRefreshFinish();
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
		mQueue.add(stringRequest);
	}


	protected ArrayList<Girl> parseGirls(String respText) throws JSONException {
		ArrayList<Girl> girls = new ArrayList<Girl>();
		JSONObject obj = new JSONObject(respText);

		JSONArray array = obj.getJSONArray("results");
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			Girl girl = new Girl();
			girl.setWho(object.getString("who"));
			girl.setUrl(object.getString("url"));
			girl.setCreatedAt(object.getString("createdAt"));
			girls.add(girl);
		}
		return girls;
	}



	@Override
	public void onRefresh() {
		page = 1;
		getData(page);
	}


	@Override
	public void onUpRefresh() {
		mSwipeRefreshLayout.setRefreshing(true);
		page++;
        getData(page);
	}
	
	/**
	 * 获取列表控件
	 * @return
	 */
	public UpRefreshRecyclerView getListView(){
		return mRecyclerView;
	}
}
