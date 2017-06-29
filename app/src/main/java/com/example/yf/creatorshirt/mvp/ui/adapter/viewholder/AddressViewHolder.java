package com.example.yf.creatorshirt.mvp.ui.adapter.viewholder;

import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yf.creatorshirt.R;
import com.example.yf.creatorshirt.mvp.ui.adapter.base.BaseViewHolder;

/**
 * Created by yang on 29/06/2017.
 */

public class AddressViewHolder extends BaseViewHolder{
    public TextView mReceiverName;
    public TextView mReceiverPhone;
    public TextView mAddress;
    public AddressViewHolder(ViewGroup parent, @LayoutRes int resId) {
        super(parent, resId);
        mReceiverName = getView(R.id.address_name);
        mReceiverPhone = getView(R.id.address_phone);
        mAddress = getView(R.id.address);
    }
}
