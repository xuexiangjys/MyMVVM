package com.xuexiang.mymvvm.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.xuexiang.mymvvm.BR;

/**
 * @author xuexiang
 * @date 2018/3/14 下午1:04
 */
public class MainModel extends BaseObservable {

    /**
     * 登录名
     */
    private String DisplayContent = "欢迎登录！";

    /**
     * 登录名
     */
    private String LoginName = "登录的用户名：";

    @Bindable
    public String getLoginName() {
        return LoginName;
    }

    public MainModel setLoginName(String loginName) {
        LoginName = loginName;
        notifyPropertyChanged(BR.loginName);
        return this;
    }

    @Bindable
    public String getDisplayContent() {
        return DisplayContent;
    }

    public MainModel setDisplayContent(String displayContent) {
        DisplayContent = displayContent;
        notifyPropertyChanged(BR.displayContent);
        return this;
    }
}
