package config;

import tools.AppManager;
import tools.ImageUtils;
import android.graphics.Bitmap;

import com.donal.wechat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import tools.StringUtils;
import util.Utils;

/**
 * wechat
 *
 * @author gaotong
 *
 */
public class CommonValue {
	public static String PackageName = "com.donal.wechat";
    //public static String host = "192.168.1.101";
    public static String host = "115.47.15.58";
    
	public static String BASE_API = "http://"+host+":8080/wechat/api/";
	public static String BASE_URL = "http://"+host+":8080/wechat/";

    public static final String SHARED_PREFERENCE = "FF_USER";
    
    public static final String ITEMS[] = {"汉语","日语","韩语","英语", "西班牙语", "法语"};

    public static final int kWCMessageTypePlain = 0;
	public static final int kWCMessageTypeImage = 1;
	public static final int kWCMessageTypeVoice =2;
	public static final int kWCMessageTypeLocation=3;
	
	public static final int kWCMessageStatusWait = 1;
	public static final int kWCMessageStatusSending = 2;

    public static boolean[] getCheckedByString(String currentSelected) {
        boolean res[] = new boolean[ITEMS.length];
        if(currentSelected.trim().equals("") || currentSelected == null)
            return res;
        String strArr[] = currentSelected.split(",");
        for(String str:strArr){
            res[ Integer.parseInt(str) ] = true;
        }
        return res;
    }

    // auil options
	public interface DisplayOptions {
		public DisplayImageOptions default_options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageOnLoading(R.drawable.content_image_loading)
				.showImageForEmptyUri(R.drawable.content_image_loading)
				.showImageOnFail(R.drawable.content_image_loading)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) 
				.displayer(new BitmapDisplayer() {
					@Override
					public void display(Bitmap bitmap, ImageAware imageAware,
							LoadedFrom loadedFrom) {
						imageAware.setImageBitmap(bitmap);
					}
				})
				.build();
		
		public DisplayImageOptions avatar_options = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.showImageOnLoading(R.drawable.content_image_loading)
		.showImageForEmptyUri(R.drawable.content_image_loading)
		.showImageOnFail(R.drawable.content_image_loading)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED) 
		.displayer(new RoundedBitmapDisplayer(ImageUtils.dip2px(AppManager.getAppManager().currentActivity(), 30)))
		.build();
	}
	
	public interface Operation {
		String addFriend = "加好友";
		String chatFriend = "发消息";
	}
	
	public static final String NEW_MESSAGE_ACTION = "chat.newmessage";
	public static final String SEND_MESSAGE_ACTION = "chat.sendmessage";
	
	public static final String ADD_FRIEND_ACTION = "add.friend";
	/**
	 * USERINFO
	 */
	public static final String LOGIN_SET = "login_set";
	public static final String USERID = "userId";
	public static final String APIKEY = "apiKey";

	/**
	 * 重连接
	 */
	/**
	 * 重连接状态acttion
	 * 
	 */
	public static final String ACTION_RECONNECT_STATE = "action_reconnect_state";
	/**
	 * 描述重连接状态的关机子，寄放的intent的关键字
	 */
	public static final String RECONNECT_STATE = "reconnect_state";
	/**
	 * 描述重连接，
	 */
	public static final boolean RECONNECT_STATE_SUCCESS = true;
	public static final boolean RECONNECT_STATE_FAIL = false;
	
	/**
	 * 注册
	 */
	public static final int REQUEST_REGISTER_INFO = 1;
	
	/**
	 * 打开会话
	 */
	public static final int REQUEST_OPEN_CHAT = 2;
    
    public static String getLangStrings(String langIds){
        if(StringUtils.notEmpty(langIds)){
            String[] ids = langIds.split(",");
            String res = ITEMS[Integer.parseInt(ids[0])];
            for(int i=1; i<ids.length; i++){
                res += "、"+ ITEMS[Integer.parseInt(ids[i])];
            }
            return res;
        }
        return "";
    }
}
