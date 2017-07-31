package com.example.yf.creatorshirt.mvp.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yf.creatorshirt.R;
import com.example.yf.creatorshirt.mvp.ui.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 订单详情页面
 */

public class MyOrderActivity extends BaseActivity {

    @BindView(R.id.order_receiver_address)
    TextView mChoiceAddress;
    @BindView(R.id.pay_for_money)
    Button mPayfor;

    @Override
    protected void inject() {

    }

    @Override
    protected void initView() {
        mAppBarTitle.setText(R.string.my_order);
        mAppBarBack.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.order_receiver_address, R.id.pay_for_money})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_receiver_address:
                Intent intent = new Intent();
                intent.setClass(this, AddressActivity.class);
                startActivity(intent);
                break;
            case R.id.pay_for_money:
                //// TODO: 30/06/2017 跳转到支付宝或者微信去支付
                startCommonActivity(this,null,SuccessPayActivity.class);
                break;
        }
    }


    @Override
    public void initData() {
        super.initData();
    }

    @Override
    protected int getView() {
        return R.layout.activity_my_order;
    }
}
