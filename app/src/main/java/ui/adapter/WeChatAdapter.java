package ui.adapter;

import im.WeChat;
import im.model.HistoryChatBean;

import java.util.List;

import tools.DateUtil;
import tools.StringUtils;

import bean.JsonMessage;
import bean.UserDetail;
import bean.UserInfo;

import com.donal.wechat.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import config.ApiClent;
import config.ApiClent.ClientCallback;
import config.CommonValue;
import config.FriendManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeChatAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<HistoryChatBean> chatBeans;
	private Context context;
	private OnClickListener contacterOnClick;
	private OnLongClickListener contacterOnLongClick;

	static class CellHolder {
		TextView alpha;
		ImageView avatarImageView;
		TextView titleView;
		TextView timeView;
		TextView desView;
		TextView paopao;
		TextView newDate;
	}
	
	public WeChatAdapter(Context context, List<HistoryChatBean> ChatBeans) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.chatBeans = ChatBeans;
	}

	public void setNoticeList(List<HistoryChatBean> inviteUsers) {
		this.chatBeans = inviteUsers;
	}

	@Override
	public int getCount() {
		return chatBeans.size();
	}

	@Override
	public Object getItem(int position) {
		return chatBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HistoryChatBean notice = chatBeans.get(position);
		CellHolder cell = null;
		if (convertView == null) {
			cell = new CellHolder();
			convertView = mInflater.inflate(R.layout.friend_card_cell, null);
			cell.alpha = (TextView) convertView.findViewById(R.id.alpha);
			cell.avatarImageView = (ImageView) convertView.findViewById(R.id.avatarImageView);
			cell.timeView = (TextView) convertView.findViewById(R.id.time);
			cell.titleView = (TextView) convertView.findViewById(R.id.title);
			cell.desView = (TextView) convertView.findViewById(R.id.des);
			cell.paopao = (TextView) convertView.findViewById(R.id.paopao);
			convertView.setTag(cell);
		} else {
			cell = (CellHolder) convertView.getTag();
		}
		String jid = notice.getFrom();
		cell.desView.setTag(notice);
		getUserInfo(jid, cell, notice);
		convertView.setOnClickListener(contacterOnClick);
		convertView.setOnLongClickListener(contacterOnLongClick);
		return convertView;
	}

	public void setOnClickListener(OnClickListener contacterOnClick) {

		this.contacterOnClick = contacterOnClick;
	}
	
	public void setOnLongClickListener(OnLongClickListener contacterOnLongClick ) {
        this.contacterOnLongClick = contacterOnLongClick;
    }

    private void getUserInfo(final String userId, final CellHolder holder, HistoryChatBean notice) {
        Log.i("tong test",this.getClass() + " getUserInfo userId for display user's icon(other):" + userId);
		holder.timeView.setText(DateUtil.wechat_time(notice.getNoticeTime()));
		Integer ppCount = notice.getNoticeSum();
		if (ppCount != null && ppCount > 0) {
			holder.paopao.setText(ppCount + "");
			holder.paopao.setVisibility(View.VISIBLE);
		} else {
			holder.paopao.setVisibility(View.GONE);
		}
		
		final String content = notice.getContent();
		try {
			JsonMessage msg = JsonMessage.parse(content);
			holder.desView.setText(msg.text);
		} catch(Exception e) {
			holder.desView.setText(content);
		}
		UserInfo friend = FriendManager.getInstance(context).getFriend(userId.split("@")[0]);
		if (friend != null ) {
            Log.d("tong test","getUserInfo displayImage : " + CommonValue.BASE_URL+friend.userHead);
            if(StringUtils.notEmpty(friend.userHead))
			    ImageLoader.getInstance().displayImage(CommonValue.BASE_URL+friend.userHead, holder.avatarImageView, CommonValue.DisplayOptions.default_options);
			else
                ImageLoader.getInstance().displayImage(CommonValue.BASE_URL+friend.userHead, holder.avatarImageView, CommonValue.DisplayOptions.default_options);

            holder.titleView.setText(friend.nickName);
			return;
		}
		SharedPreferences sharedPre = context.getSharedPreferences(
				CommonValue.LOGIN_SET, Context.MODE_PRIVATE);
		String apiKey = sharedPre.getString(CommonValue.APIKEY, null);
		
		ApiClent.getUserInfo(apiKey, userId.split("@")[0], new ClientCallback() {
			
			@Override
			public void onSuccess(Object data) {
				UserDetail userInfo = (UserDetail) data;
//                String myid = ((WeChat)context).appContext.getLoginUid();
//                if(myid != null)
                Log.d("tong test","get user info from server : " + userInfo);
				    FriendManager.getInstance(context).saveOrUpdateFriend(userInfo.userDetail);
				holder.titleView.setText(userInfo.userDetail.nickName);
				ImageLoader.getInstance().displayImage(CommonValue.BASE_URL+userInfo.userDetail.userHead, holder.avatarImageView, CommonValue.DisplayOptions.default_options);
			}
			
			@Override
			public void onFailure(String message) {
			}
			
			@Override
			public void onError(Exception e) {
			}
		});
	}
}