package com.xuexiang.mymvvm.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xuexiang.mymvvm.BR;
import com.xuexiang.mymvvm.model.ILoginModel;
import com.xuexiang.mymvvm.util.ToastUtil;
import com.xuexiang.mymvvm.view.ILoginView;

/**
 * @author xuexiang
 * @date 2018/3/14 上午11:36
 */
public class LoginModel extends BaseObservable implements ILoginModel<ILoginView> {

    /**
     * 登录名
     */
    private String LoginName = "";
    /**
     * 登录密码
     */
    private String LoginPassword = "";

    private ILoginView mILoginView;

    @Override
    public LoginModel attachV(ILoginView view) {
        mILoginView = view;
        return this;
    }

    @Override
    public void detachV() {
        mILoginView = null;
    }

    @Bindable
    public String getLoginName() {
        return LoginName;
    }

    public LoginModel setLoginName(String loginName) {
        LoginName = loginName;
        notifyPropertyChanged(BR.loginName);
        return this;
    }

    @Bindable
    public String getLoginPassword() {
        return LoginPassword;
    }

    public LoginModel setLoginPassword(String loginPassword) {
        LoginPassword = loginPassword;
        notifyPropertyChanged(BR.loginPassword);
        return this;
    }

    /**
     * 登录
     *
     * @param loginModel
     */
    @Override
    public void login(LoginModel loginModel) {
        if ("xuexiang".equals(loginModel.getLoginName()) && "123456".equals(loginModel.getLoginPassword())) {
            ARouter.getInstance().build("/ui/main").withString("user_name", loginModel.getLoginName()).navigation();
            if (mILoginView != null) {
                mILoginView.onFinished();
            }
        } else {
            ToastUtil.showToast("用户名或者密码错误！");
        }
    }

    /**
     * 注册
     *
     * @param loginModel
     */
    @Override
    public void register(LoginModel loginModel) {
        ToastUtil.showToast("点击注册按钮，用户名：" + loginModel.getLoginName() + "，密码：" + loginModel.getLoginPassword());
    }

}
