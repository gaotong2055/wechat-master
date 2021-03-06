package ui.adapter;

import java.util.List;
import java.util.concurrent.ExecutionException;

import android.view.Gravity;
import android.widget.*;
import tools.StringUtils;
import ui.FindFriend;
import ui.Friend;

import com.donal.wechat.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import config.CommonValue;

import bean.UserInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class StrangerAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<UserInfo> cards;
	
	static class CellHolder {
		TextView alpha;
		ImageView avatarImageView;
		TextView titleView;
		TextView desView;
	}
	
	public StrangerAdapter(Context context, List<UserInfo> cards) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.cards = cards;
	}
	
	@Override
	public int getCount() {
		return cards.size();
	}

	@Override
	public Object getItem(int arg0) {
		return cards.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
        final UserInfo model = cards.get(position);
        if(model != null && !model.userId.equals("empty")){
            CellHolder cell = null;
            if (convertView == null) {
                cell = new CellHolder();
                convertView = inflater.inflate(R.layout.friend_card_cell, null);
                cell.alpha = (TextView) convertView.findViewById(R.id.alpha);
                cell.avatarImageView = (ImageView) convertView.findViewById(R.id.avatarImageView);
                cell.titleView = (TextView) convertView.findViewById(R.id.title);
                cell.desView = (TextView) convertView.findViewById(R.id.des);
                convertView.setTag(cell);
            }
            else {
                cell = (CellHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(CommonValue.BASE_URL+model.userHead, cell.avatarImageView, CommonValue.DisplayOptions.default_options);
            cell.titleView.setText(model.nickName);
            //model.mLang;
            cell.desView.setText("母语："+ CommonValue.getLangStrings(model.mLang) );
            cell.alpha.setVisibility(View.GONE);
            convertView.setOnClickListener( new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    ((FindFriend)context).show2OptionsDialog(new String[]{CommonValue.Operation.addFriend}, model);
                }
            });
            return convertView;
        }else{
            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setPadding(30,30,30,30);
            textView.setTextSize(16);
            textView.setText("暂无可推荐的小伙伴");
            return textView;
        }
		
	}
	
}
