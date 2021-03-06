package com.example.yf.creatorshirt.mvp.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.example.yf.creatorshirt.R;
import com.example.yf.creatorshirt.app.App;
import com.example.yf.creatorshirt.common.UserInfoManager;
import com.example.yf.creatorshirt.http.DataManager;
import com.example.yf.creatorshirt.http.HttpResponse;
import com.example.yf.creatorshirt.mvp.listener.CommonListener;
import com.example.yf.creatorshirt.mvp.model.ShareInfoEntity;
import com.example.yf.creatorshirt.mvp.model.orders.OrderBaseInfo;
import com.example.yf.creatorshirt.mvp.model.orders.OrderType;
import com.example.yf.creatorshirt.mvp.model.orders.SaveStyleEntity;
import com.example.yf.creatorshirt.mvp.model.orders.TextureEntity;
import com.example.yf.creatorshirt.mvp.presenter.base.RxPresenter;
import com.example.yf.creatorshirt.mvp.presenter.contract.SizeOrShareContract;
import com.example.yf.creatorshirt.utils.Constants;
import com.example.yf.creatorshirt.utils.GsonUtils;
import com.example.yf.creatorshirt.utils.RxUtils;
import com.example.yf.creatorshirt.utils.ShareContentUtil;
import com.example.yf.creatorshirt.utils.SharedPreferencesUtil;
import com.example.yf.creatorshirt.utils.ToastUtil;
import com.example.yf.creatorshirt.utils.Utils;
import com.example.yf.creatorshirt.widget.CommonSubscriber;
import com.google.gson.Gson;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * Created by yangfang on 2017/8/28.
 */

public class SizeOrSharePresenter extends RxPresenter<SizeOrShareContract.SizeOrShareView> implements
        SizeOrShareContract.Presenter, CommonListener.CommonClickListener {

    private static final int WHAT_SUCCESS = 1;
    private static final int WHAT_SUCCESS2 = 2;
    private DataManager manager;
    private String QiniuToken;
    private UploadManager uploadManager = new UploadManager();
    private String userToken;
    private String UserId;
    private OrderBaseInfo mOrderBaseInfo;
    private String imageFrontUrl;
    private String imageBackUrl;
    private String mBack;
    private String size;
    private String styleContext;
    private String type;
    private OrderType mOrderType;
    private String textUre;
    private AsyncTask<String, Integer, Void> asyncTask;

    private Map<String, String> map = new HashMap<>();
    private List<String> mapAvatar = new ArrayList<>();
    private List<String> totalNum = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_SUCCESS) {
                getList("B", mBack);
            }
            if (msg.what == WHAT_SUCCESS2) {
                ToastUtil.cancel();
                if (asyncTask != null) {
                    asyncTask.cancel(true);
                    asyncTask = null;
                }
                sendOrderData();
            }
        }
    };

    @Inject
    SizeOrSharePresenter(DataManager manager) {
        this.manager = manager;
    }

    private void getList(final String kewWord, final String imageUrl) {
        if (TextUtils.isEmpty(QiniuToken)) {
            Log.e("TAG", "没有取到token");
            return;
        }
        UserId = UserInfoManager.getInstance().getLoginResponse().getUserInfo().getUserid() + "_";
        String key = UserId + Utils.getTime() + kewWord;
        uploadManager.put(imageUrl, key, QiniuToken, new UpCompletionHandler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                if (info.isOK()) {
                    Log.e("qiniu_clothes", "UPLOAD SUCCESS" + key + ":" + info + ":" + response);
                    if (kewWord.equals("A")) {
                        try {
                            String imageFrontUrl = Constants.ImageUrl + response.get("key");
                            map.put("A", imageFrontUrl);
                            handler.sendEmptyMessage(WHAT_SUCCESS);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (kewWord.equals("B")) {
                        try {
                            String imageBackUrl = Constants.ImageUrl + response.get("key");
                            map.put("B", imageBackUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (map.size() == 2) {
                        handler.sendEmptyMessage(WHAT_SUCCESS2);
                    }

                } else {
                    Log.e("qiniu_clothes", "UPLOAD fail");
                    mView.showErrorMsg("保存图片失败");
                    if (asyncTask != null) {
                        asyncTask.cancel(true);
                        asyncTask = null;
                    }
                }

            }
        }, null);
    }


    /**
     * 获取七牛token
     */
    public void getToken() {
        userToken = UserInfoManager.getInstance().getToken();
        Log.e("TAG", "CKVM" + userToken);
        if (userToken == null) {
            return;
        }
        addSubscribe(manager.getQiToken(userToken)
                .compose(RxUtils.<HttpResponse<String>>rxSchedulerHelper())
                .compose(RxUtils.<String>handleResult())
                .subscribeWith(new CommonSubscriber<String>(mView) {
                    @Override
                    public void onNext(String s) {
                        QiniuToken = s;
                        mView.showTokenSuccess(s);
                    }
                })
        );
    }

    //直接生成订单
    private void sendOrderData() {
        SaveStyleEntity saveStyleEntity = new SaveStyleEntity();
        if (size != null) {
            String[] newSize = size.split("c");
            String size = newSize[0];
            saveStyleEntity.setSize(Integer.parseInt(size));
            saveStyleEntity.setHeight(Integer.parseInt(size));
            saveStyleEntity.setTexture(textUre);
        }
        //分享不传size;
        imageBackUrl = map.get("B");
        imageFrontUrl = map.get("A");
        String baseColor = mOrderBaseInfo.getColor();
        String[] detail = baseColor.split("#");
        String detailColor = detail[1];
        saveStyleEntity.setGender(mOrderBaseInfo.getGender());
        saveStyleEntity.setBaseId(mOrderBaseInfo.getType());
        saveStyleEntity.setColor(detailColor);
        saveStyleEntity.setOrderType(type);
        saveStyleEntity.setFinishImage(imageFrontUrl);
        saveStyleEntity.setAllImage(imageFrontUrl + "," + imageBackUrl);
        saveStyleEntity.setZipCode("");
        saveStyleEntity.setAddress("");
        saveStyleEntity.setUserId(SharedPreferencesUtil.getUserId());
        saveStyleEntity.setStyleContext(styleContext);

        Gson gson = new Gson();
        String postEntity = gson.toJson(saveStyleEntity);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postEntity);
        addSubscribe(manager.saveOrderData(userToken, body)
                .compose(RxUtils.<HttpResponse<OrderType>>rxSchedulerHelper())
                .compose(RxUtils.<OrderType>handleResult())
                .subscribeWith(new CommonSubscriber<OrderType>(mView) {
                    @Override
                    public void onNext(OrderType s) {
                        if (type != null) {
                            if (type.equals("Check")) {
                                mView.showSuccessData(s);

                            } else if (type.equals("Share")) {
                                mView.showShareSuccessData(s);
                            }
                            mOrderType = s;
                        } else {
                            mView.showErrorMsg("生成订单失败");
                        }
                    }
                }));
        if (map.size() != 0) {
            map.clear();
        }

    }


    /**
     * 设置数据的String格式
     *
     * @param s
     */
    @Override
    public void setStyleContext(String s) {
        this.styleContext = s;
    }

    /**
     * 保存图片
     *
     * @param key
     * @param value
     */
    @SuppressLint("StaticFieldLeak")
    public void request(final String key, final String value) {

        asyncTask = new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                getList(key, value);
                return null;
            }

            @Override
            protected void onPreExecute() {
                ToastUtil.showProgressToast(App.getInstance(), "正在生成订单", R.drawable.progress_icon);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        };
        asyncTask.execute();

    }

    public void setIM(String mBackImageUrl) {
        mBack = mBackImageUrl;
    }

    public void setClothesData(OrderBaseInfo mOrderBaseInfo, String size) {
        this.mOrderBaseInfo = mOrderBaseInfo;
        this.size = size;
    }

    /**
     * 分享方法
     *
     * @param mActivity
     */
    public void getShare(final Activity mActivity) {
        if (mOrderBaseInfo.getFrontUrl() == null) {
            return;
        }
        if (UserInfoManager.getInstance().getUserName() == null) {
            return;
        }
        ShareContentUtil shareContentUtil = new ShareContentUtil(mActivity);
        ShareInfoEntity infoEntity = new ShareInfoEntity();
        infoEntity.setPicUrl(imageFrontUrl);
        infoEntity.setTargetUrl("http://styleweb.man-kang.com?orderid=" + mOrderType.getOrderId());

        infoEntity.setContent("衣秀，做自己的设计师");
        infoEntity.setTitle(UserInfoManager.getInstance().getUserName() + "的原创定制");
        infoEntity.setDefaultImg(R.mipmap.man_t_shirt);//默认图片
        shareContentUtil.setOnClickListener(this);
        shareContentUtil.startShare(infoEntity, 1);

    }


    public void setOrderClothes(OrderBaseInfo mOrderBaseInfo) {
        this.mOrderBaseInfo = mOrderBaseInfo;
    }

    public void setSaveType(String type) {
        this.type = type;
    }

    public void getShareToken() {
        userToken = UserInfoManager.getInstance().getToken();
        if (userToken == null) {
            return;
        }
        addSubscribe(manager.getQiToken(userToken)
                .compose(RxUtils.<HttpResponse<String>>rxSchedulerHelper())
                .compose(RxUtils.<String>handleResult())
                .subscribeWith(new CommonSubscriber<String>(mView) {
                    @Override
                    public void onNext(String s) {
                        if (s != null) {
                            QiniuToken = s;
                            mView.showShareTokenSuccess(s);
                        }
                    }
                })
        );

    }

    @Override
    public void onClickListener() {
        mView.hidePopupWindow();
    }

    /**
     * 分享的保存订单
     *
     * @param texture
     * @param orderId
     * @param size
     */
    public void saveOrdersFromShare(String orderId, String size, String texture) {
        Map<String, String> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("height", size);
        map.put("texture", texture);
        addSubscribe(manager.saveOrdersFromShare(UserInfoManager.getInstance().getLoginResponse().getToken(),
                GsonUtils.getGson(map))
                .compose(RxUtils.<HttpResponse<OrderType>>rxSchedulerHelper())
                .compose(RxUtils.<OrderType>handleResult())
                .subscribeWith(new CommonSubscriber<OrderType>(mView, "生成订单出错") {
                    @Override
                    public void onNext(OrderType orderType) {
                        if (orderType == null) {
                            mView.showErrorMsg("订单生成出错");
                        } else {
                            mView.showSuccessData(orderType);//生成订单
                        }
                    }
                })
        );
    }

    public void getTexture(OrderBaseInfo mOrderBaseInfo) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Gender", mOrderBaseInfo.getGender());
            jsonObject.put("Typeversion", mOrderBaseInfo.getType());
            addSubscribe(manager.getTextUre(GsonUtils.getGson(jsonObject))
                    .compose(RxUtils.<HttpResponse<List<TextureEntity>>>rxSchedulerHelper())
                    .compose(RxUtils.<List<TextureEntity>>handleResult())
                    .subscribeWith(new CommonSubscriber<List<TextureEntity>>(mView) {
                        @Override
                        public void onNext(List<TextureEntity> textureEntity) {
                            if (textureEntity != null) {
                                if (textureEntity.size() != 0) {
                                    mView.showSuccessTextUre(textureEntity);
                                }
                            }
                        }
                    }));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setTextUre(String textUre) {
        if (textUre != null) {
            this.textUre = textUre;
        }
    }

    private void getAvatarList(final String s, final int i) {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final FlowableEmitter<String> e) throws Exception {
                String kewWord = "pattern";
                if (TextUtils.isEmpty(QiniuToken)) {
                    Log.e("TAG", "没有取到token");
                    return;
                }
                UserId = UserInfoManager.getInstance().getLoginResponse().getUserInfo().getUserid() + "_";
                String key = UserId + Utils.getTime() + kewWord + i;
                uploadManager.put(s, key, QiniuToken, new UpCompletionHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {

                        if (info.isOK()) {
                            try {
                                String url = Constants.ImageUrl + response.get("key");
                                e.onNext(url);
                                Log.e("TAG", "avatar_url:" + url);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("qiniu_avatar", "UPLOAD fail" + key + ":" + info + ":" + response);
                            mView.showErrorMsg("保存图片失败");
                        }

                    }
                }, null);
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommonSubscriber<String>(mView) {
                    @Override
                    public void onNext(String s) {
                        totalNum.add(s);
                        if(mapAvatar.size() >=2) {
                            if (totalNum.size() < mapAvatar.size()) {
                                getAvatarList(mapAvatar.get(totalNum.size()), totalNum.size());
                            }
                        }
                    }
                });
    }

    public void saveAvatar(List<String> avatarList) {
        mapAvatar = avatarList;
        getAvatarList(avatarList.get(0), 0);
    }
}
