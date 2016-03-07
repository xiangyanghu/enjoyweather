package com.xyh.enjoyweather.util;

/**
 * Created by 向阳湖 on 2016/3/7.
 */
public interface HttpCallbackListener {
    void onFinish(String reponse);

    void onError(Exception e);
}
