package com.example.yf.creatorshirt.mvp.presenter;

import android.util.Log;

import com.example.yf.creatorshirt.http.DataManager;
import com.example.yf.creatorshirt.http.HttpResponse;
import com.example.yf.creatorshirt.http.TestRequestServer;
import com.example.yf.creatorshirt.mvp.model.address.AddressEntity;
import com.example.yf.creatorshirt.mvp.presenter.base.RxPresenter;
import com.example.yf.creatorshirt.mvp.presenter.contract.AddressContract;
import com.example.yf.creatorshirt.utils.GsonUtils;
import com.example.yf.creatorshirt.utils.SharedPreferencesUtil;

import javax.inject.Inject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yang on 07/08/2017.
 */

public class AddressPresenter extends RxPresenter<AddressContract.AddressView> implements AddressContract.Presenter {

    private DataManager mDataManager;
    private AddressEntity saveEntity;

    @Inject
    public AddressPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void getAddressData() {
        Log.e("tag", "fffffff");
//        addSubscribe(mDataManager.getAddressData()
//                .compose(RxUtils.<HttpResponse<List<AddressBean>>>rxSchedulerHelper())
//                .compose(RxUtils.<List<AddressBean>>handleResult())
//                .subscribeWith(new CommonSubscriber<List<AddressBean>>(mView, "数据请求错误") {
//                    @Override
//                    public void onNext(List<AddressBean> addressBeen) {
//                        Log.e("TAG", "FUCK YOU");
//                        mView.showSuccess(addressBeen);
//                    }
//                })
//        );

        TestRequestServer.getInstance().getAddressData(SharedPreferencesUtil.getUserToken(),GsonUtils.getGson(saveEntity)).enqueue(new Callback<HttpResponse>() {
            @Override
            public void onResponse(Call<HttpResponse> call, Response<HttpResponse> response) {
                Log.e("Address", "dddd" + response.body().getResult());
            }

            @Override
            public void onFailure(Call<HttpResponse> call, Throwable t) {
                Log.e("ADDRESS", "FAILURE");
            }
        });
    }

    public void setAddressInfo(String receiverName, String receiverPhone, String receiverEmail, String reciverCity, String receiverAddress) {
        saveEntity= new AddressEntity();
        saveEntity.setAddress(receiverAddress);
        saveEntity.setCity(reciverCity);
        saveEntity.setMobile(receiverPhone);
        saveEntity.setZipcode(receiverEmail);
        saveEntity.setUserName(receiverName);
    }

    public void saveAddressData() {
        RequestBody body = GsonUtils.getGson(saveEntity);
//        mDataManager.saveAddress(SharedPreferencesUtil.getUserToken(),body);
    }
}
