/**
 * wechatgaotong
 */
package config;

import bean.UserInfo;
import im.model.IMMessage;
import tools.Logger;
import tools.StringUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import java.util.List;

import db.DBManager;
import db.SQLiteTemplate;
import db.SQLiteTemplate.RowMapper;

/**
 *
 * 朋友历史记录
 *
 */
public class FriendManager {

	private static FriendManager friendManager = null;
	private static DBManager manager = null;
	
	private FriendManager(Context context) {
		
		String databaseName = ((AppActivity)context).appContext.getLoginUid();
        Log.d("tong test","FriendManager open database : " + databaseName);
		Logger.i(databaseName);
		manager = DBManager.getInstance(context, databaseName);
	}
	
	public static FriendManager getInstance(Context context) {
		if (friendManager == null) {
			friendManager = new FriendManager(context);
		}
		return friendManager;
	}
	
	public static void destroy() {
		friendManager = null;
		manager = null;
	}
	/**
	 * 
	 * 保存朋友信息 or 更新朋友信息
	 * 
	 * @param user
	 */
	public void saveOrUpdateFriend(UserInfo user) {
        Log.i("tong test","saveOrUpdateFriend! user:" + user);
        
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		ContentValues contentValues = new ContentValues();
		contentValues.put("nickName", user.nickName);
		contentValues.put("avatar", user.userHead);
		contentValues.put("description", user.description);
		int s = st.update("im_friend", contentValues, " userId=? ", new String[]{user.userId});
		if (s == 0) {
			contentValues.put("userId", StringUtils.doEmpty(user.userId));
			st.insert("im_friend", contentValues);
            Log.i("tong test","saveOrUpdateFriend insert success! user:" + user);
        }
	}
	
	/**
	 * 查找朋友
	 * 
	 * @param userId
	 * @return
	 */
	public UserInfo getFriend(final String userId) {
		if (StringUtils.empty(userId)) {
			return null;
		}
		SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
		UserInfo friend = st.queryForObject(new RowMapper<UserInfo>() {
			@Override
			public UserInfo mapRow(Cursor cursor, int index) {
				UserInfo user = new UserInfo();
				user.userId = userId;
				user.nickName = cursor.getString(cursor.getColumnIndex("nickName"));
				user.userHead = cursor.getString(cursor.getColumnIndex("avatar"));
				user.description = cursor.getString(cursor.getColumnIndex("description"));
				return user;
			}
		}, "select nickName, avatar, description from im_friend where userId=?", new String[]{userId});
		return friend;
	}

    public List<UserInfo> getAllFriend() {
        SQLiteTemplate st = SQLiteTemplate.getInstance(manager, false);
        
        List list = st.queryForList(new RowMapper<UserInfo>() {
            @Override
            public UserInfo mapRow(Cursor cursor, int index) {
                UserInfo user = new UserInfo();
                user.userId = cursor.getString(cursor.getColumnIndex("userId"));
                user.nickName = cursor.getString(cursor.getColumnIndex("nickName"));
                user.userHead = cursor.getString(cursor.getColumnIndex("avatar"));
                user.description = cursor.getString(cursor.getColumnIndex("description"));
                return user;
            }
        }, "select userId,nickName, avatar, description from im_friend", new String[]{});
        Log.d("tong test",this.getClass() + "getAllFriend : " + list);
        return list;
    }
}
