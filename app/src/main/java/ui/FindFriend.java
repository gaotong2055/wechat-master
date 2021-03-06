/**
 * wechatgaotong
 */
package ui;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import tools.UIHelper;
import ui.adapter.StrangerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import bean.StrangerEntity;
import bean.UserInfo;

import com.donal.wechat.R;

import config.ApiClent;
import config.AppActivity;
import config.CommonValue;
import config.ApiClent.ClientCallback;

/**
 * wechat
 *
 * @author gaotong
 *
 */


public class FindFriend extends AppActivity implements OnScrollListener, OnRefreshListener{
	
	private int lvDataState;
	private int currentPage;
	
	private ListView xlistView;
	private List<UserInfo> datas;
	private StrangerAdapter mAdapter;
	private SwipeRefreshLayout swipeLayout;
   // public TextView noRecommdText;
	//private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findfriend);
		initUI();
		getFriendCardFromCache();
        swipeLayout.post(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(true);
            }
        });
       
        //progressDialog = UIHelper.showProgress(this,"提示","加载中...",true);
	}
	
	private void initUI() {
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.xrefresh);
		swipeLayout.setOnRefreshListener(this);
	    swipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_green_light, 
	            android.R.color.holo_orange_light, 
	            android.R.color.holo_red_light);
		xlistView = (ListView)findViewById(R.id.xlistview);
		xlistView.setOnScrollListener(this);
        xlistView.setDividerHeight(0);
        datas = new ArrayList<UserInfo>();
		mAdapter = new StrangerAdapter(this, datas);
		xlistView.setAdapter(mAdapter);
//        noRecommdText = (TextView) findViewById(R.id.noRecommdText);
	}
	
	private void getFriendCardFromCache() {
		currentPage = 1;
		findFriend(currentPage, "", UIHelper.LISTVIEW_ACTION_REFRESH);
	}
	
	private void findFriend(int page, String nickName, final int action) {
        Log.d("tong test",this.getClass() + ". findFriend");
     //   noRecommdText.setVisibility(View.GONE);
        
		String apiKey = appContext.getLoginApiKey();
		ApiClent.findFriend(appContext, apiKey, page+"", UIHelper.LISTVIEW_COUNT+"", nickName, new ClientCallback() {
			@Override
			public void onSuccess(Object data) {
				StrangerEntity entity = (StrangerEntity)data;
                //UIHelper.dismissProgress(progressDialog);
				switch (entity.status) {
				case 1:
					handleFriends(entity, action);
					break;
				default:
					showToast(entity.msg);
					break;
				}
			}
			
			@Override
			public void onFailure(String message) {
				showToast(message);
			}
			
			@Override
			public void onError(Exception e) {
			}
		});
	}
	
	private void handleFriends(StrangerEntity entity, int action) {
        
		switch (action) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
			datas.clear();
			datas.addAll(entity.userList);
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			datas.addAll(entity.userList);
			break;
		}
        
		if(entity.userList.size() == UIHelper.LISTVIEW_COUNT){					
			lvDataState = UIHelper.LISTVIEW_DATA_MORE;
			mAdapter.notifyDataSetChanged();
		}
		else {
			lvDataState = UIHelper.LISTVIEW_DATA_FULL;
			mAdapter.notifyDataSetChanged();
		}
        Log.d("tong test",this.getClass() + ". handleFriends datas :" + datas);
		if(datas.isEmpty()){
            //空的时候，提示用户无推荐的用户
            //noRecommdText.setVisibility(View.VISIBLE);
			lvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
            UserInfo u = new UserInfo();
            u.userId = "empty";
            datas.add(u);
            mAdapter.notifyDataSetChanged();
		}else{
            //noRecommdText.setVisibility(View.GONE);
        }
		swipeLayout.setRefreshing(false);
	}

	@Override
	public void onBackPressed() {
		isExit();
	}
	
	public void show2OptionsDialog(final String[] arg ,final UserInfo model){
		new AlertDialog.Builder(context).setTitle(null).setItems(arg,
				new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				switch(which){
				case 0:
					addFriend(model);
					break;
				}
			}
		}).show();
	}
	
	private void addFriend(final UserInfo user) {
		ApiClent.addFriend(appContext, appContext.getLoginApiKey(), user.userId, new ClientCallback() {
			
			@Override
			public void onSuccess(Object data) {
				showToast((String)data);
				addFriendBroadcast(user);
			}
			
			@Override
			public void onFailure(String message) {
				showToast(message);
			}
			
			@Override
			public void onError(Exception e) {
				
			}
		});
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (lvDataState != UIHelper.LISTVIEW_DATA_MORE) {
            return;
        }
        if (firstVisibleItem + visibleItemCount >= totalItemCount
                && totalItemCount != 0) {
        	lvDataState = UIHelper.LISTVIEW_DATA_LOADING;
        	currentPage++;
        	findFriend(currentPage, "", UIHelper.LISTVIEW_ACTION_SCROLL);
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onRefresh() {
        
        Log.d("tong test",this.getClass() + ". onRefresh");
		if (lvDataState != UIHelper.LISTVIEW_DATA_LOADING) {
			lvDataState = UIHelper.LISTVIEW_DATA_LOADING;
			currentPage = 1;
			findFriend(currentPage, "", UIHelper.LISTVIEW_ACTION_REFRESH);
		}
		else {
			swipeLayout.setRefreshing(false);
		}
	}
	
	private void addFriendBroadcast(UserInfo user) {
		Intent intent = new Intent(CommonValue.ADD_FRIEND_ACTION);
		intent.putExtra("user", user);
		sendBroadcast(intent);
	}
}
