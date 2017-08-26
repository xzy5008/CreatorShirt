package com.example.yf.creatorshirt.mvp.model.detaildesign;

/**
 * Created by yangfang on 2017/8/19.
 * 最小的style，example：
 * "name": "碎花",
 * "file": "pattern_2.png"
 *
 */

public class DetailColorStyle {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DetailColorStyle{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}