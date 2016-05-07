package cc.guoxingnan.wmgank.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.guoxingnan.wmgank.R;
import cc.guoxingnan.wmgank.WebViewActivity;
import cc.guoxingnan.wmgank.adapter.AndroidRecyclerViewAdapter;
import cc.guoxingnan.wmgank.adapter.AndroidRecyclerViewAdapter.OnItemClickListener;
import cc.guoxingnan.wmgank.entity.Android;
import cc.guoxingnan.wmgank.view.SpacesItemDecoration;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class AndroidFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

	public static final String BASE_URL = "http://gank.io/api/data/Android/20/";
	private static AndroidFragment mAndroidFragment;
	private Context mContext;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private RecyclerView mRecyclerView;
	private ArrayList<Android> data;
	private AndroidRecyclerViewAdapter adapter;
	private LayoutManager mLayoutManager;
	private int page = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_android, container, false);
		mContext = getActivity();
		
		initView(view);
		initData();
		initListener();

		return view;
	}


	private void initView(View view) {
		mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_android);
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_view);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.yellow, R.color.green);
		adapter = new AndroidRecyclerViewAdapter(mContext, data);
		mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(mLayoutManager);
		
		mRecyclerView.setAdapter(adapter);
		
		//一些设置
		mRecyclerView.setHasFixedSize(true);
		SpacesItemDecoration decoration = new SpacesItemDecoration(12);
		mRecyclerView.addItemDecoration(decoration);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
	}


	private void initListener() {
		
		mSwipeRefreshLayout.setOnRefreshListener(this);
		
		adapter.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemLongClick(View view, int position) {
			}

			@Override
			public void onItemClick(View view, int position) {
				Android a = data.get(position);
				Intent intent = new Intent();
				intent.putExtra("click", a.getUrl());
				intent.putExtra("title", a.getDesc());
				intent.setClass(mContext, WebViewActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		mSwipeRefreshLayout.postDelayed(new Runnable() {
			@Override
			public void run() {
				mSwipeRefreshLayout.setRefreshing(true);
				page = 1;
				getData(page);
			}
		}, 300);

	}


	public void getData(int page){
		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		StringRequest stringRequest = new StringRequest(BASE_URL+page, 
				new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					data = jieXi(response);
					adapter.reflash(data);
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
				mSwipeRefreshLayout.setRefreshing(false);
			}
		});
		mQueue.add(stringRequest);
	}


	/**
	 * 解析JSON数据
	 */
	private ArrayList<Android> jieXi(String response) throws JSONException {
		ArrayList<Android> list = new ArrayList<Android>();
		JSONArray array = new JSONObject(response).getJSONArray("results");
		for (int i = 0; i < array.length(); i++) {
			JSONObject o = array.getJSONObject(i);
			Android a=new Android();
			a.setDesc(o.getString("desc"));
			a.setTime(o.getString("publishedAt"));
			a.setWho(o.getString("who"));
			a.setUrl(o.getString("url"));
			a.setFrom(o.getString("source"));
			list.add(a);
		}
		return list;
	}


	public static AndroidFragment newInstance() {
		if (mAndroidFragment == null) {
			mAndroidFragment = new AndroidFragment();
		}
		return mAndroidFragment;
	}

	@Override
	public void onRefresh() {
		page ++;
		getData(page);
	}


}
