package com.example.yf.creatorshirt.mvp.presenter.contract;

import android.support.v4.util.ArrayMap;

import com.example.yf.creatorshirt.mvp.model.detaildesign.DetailColorStyle;
import com.example.yf.creatorshirt.mvp.model.detaildesign.DetailPatterStyle;
import com.example.yf.creatorshirt.mvp.model.detaildesign.DetailStyleBean;
import com.example.yf.creatorshirt.mvp.model.detaildesign.StyleBean;
import com.example.yf.creatorshirt.mvp.presenter.base.BasePresenter;
import com.example.yf.creatorshirt.mvp.view.BaseView;

import java.util.List;

/**
 * Created by yangfang on 2017/8/19.
 */

public interface DetailDesignContract {

    interface DetailDesignView extends BaseView {
        void showSuccessData(DetailStyleBean detailStyleBean);

        void showSuccessData(List<StyleBean> newList, List<String> clotheKey, ArrayMap<String, List<DetailColorStyle>> mColorData, ArrayMap<String, List<DetailPatterStyle>> mPatternData, ArrayMap<String, List<DetailColorStyle>> mSignatureData);


        void showBackData(String neck, String arm, String ornament);
    }

    interface Presenter extends BasePresenter<DetailDesignView> {
        void getDetailDesign(String gender, String type);
    }

}
