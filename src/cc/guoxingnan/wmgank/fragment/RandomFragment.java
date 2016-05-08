package cc.guoxingnan.wmgank.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cc.guoxingnan.wmgank.R;
import cc.guoxingnan.wmgank.WebViewActivity;
import cc.guoxingnan.wmgank.adapter.RandomAdapter;
import cc.guoxingnan.wmgank.adapter.RandomAdapter.OnItemClickListener;
import cc.guoxingnan.wmgank.entity.GankModel;
import cc.guoxingnan.wmgank.entity.ResultModel;
import cc.guoxingnan.wmgank.http.GankHttpClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class RandomFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static RandomFragment mRandomFragment;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ArrayList<GankModel> mGankModels;
    private RandomAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_random, container, false);
        mContext = getActivity();
        initView(view);
        initData();
        initListener();
        return view;
    }


    public void initData() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getData();
            }
        }, 300);
        mRecyclerView.smoothScrollToPosition(0);
    }


    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red, R.color.blue, R.color.yellow, R.color.green);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        
        mGankModels = new ArrayList<GankModel>();
        adapter = new RandomAdapter(mContext, mGankModels, null);
        mRecyclerView.setAdapter(adapter);
    }

    private void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        
        adapter.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemLongClick(View view, int position) {
				adapter.addData(position);
				adapter.removeData(position+1);
			}
			
			@Override
			public void onItemClick(View view, int position) {
				GankModel model = mGankModels.get(position);
				Intent intent = new Intent();
				intent.putExtra("click", model.getUrl());
				intent.putExtra("title", model.getDesc());
				intent.setClass(mContext, WebViewActivity.class);
				startActivity(intent);
			}
		});
    }

    public static RandomFragment newInstance() {
        if (mRandomFragment == null) {
            mRandomFragment = new RandomFragment();
        }
        return mRandomFragment;
    }

    private void getData() {
    	// 取0-7之间的随机数，作为数组下标
        int index = new Random().nextInt(7);
        final String type = getResources().getStringArray(R.array.type)[index];
        GankHttpClient.getRandomData(type, new Callback<ResultModel>() {
            @Override
            public void success(ResultModel resultModel, Response response) {
                mGankModels.clear();
                mGankModels.addAll(resultModel.getResults());
                adapter.setType(type);
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        getData();
    }
    
    /**
	 * 获取列表控件
	 * @return
	 */
	public RecyclerView getListView(){
		return mRecyclerView;
	}
}
