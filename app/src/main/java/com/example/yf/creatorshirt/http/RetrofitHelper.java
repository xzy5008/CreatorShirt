package com.example.yf.creatorshirt.http;

import com.example.yf.creatorshirt.mvp.model.BombStyleBean;
import com.example.yf.creatorshirt.mvp.model.GirlData;
import com.example.yf.creatorshirt.mvp.model.HotDesignsBean;
import com.example.yf.creatorshirt.mvp.model.LoginBean;
import com.example.yf.creatorshirt.mvp.model.NewsSummary;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by yang on 17/05/2017.
 * Retrofit2获取数据辅助类
 */

public class RetrofitHelper implements HttpHelper {
    private RequestApi mRequestApi;

    /**
     * Dagger2直接注入对象
     *
     * @param requestApiService
     */
    @Inject
    public RetrofitHelper(RequestApi requestApiService) {
        mRequestApi = requestApiService;
    }

    /**
     * RxJava2抓取数据
     *
     * @return
     */
    @Override
    public Flowable<NewsSummary> getDataNewsSummary() {
        return mRequestApi.getNewsSummary();
    }

    /**
     * 获取照片
     *
     * @param size
     * @param page
     * @return
     */
    public Flowable<GirlData> getPhotoList(int size, int page) {
        return mRequestApi.getPhotoList(size, page);
    }

    /**
     * phone login
     *
     * @param phone
     * @param password
     * @return
     */
    @Override
    public Observable<LoginBean> login(String phone, String password) {
        return mRequestApi.loginPhone(phone, password);
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @Override
    public Observable<LoginBean> getVerifyCode(String phone) {
        return mRequestApi.getCode(phone);
    }

    @Override
    public Observable getUserInfo() {
        return mRequestApi.getUserInfo();
    }

    @Override
    public Flowable<HttpResponse<List<BombStyleBean>>> getBombData() {
        return mRequestApi.getBombData();
    }

    @Override
    public Flowable<HotDesignsBean> getHotDesign() {
        return mRequestApi.getHotDesign();
    }

}