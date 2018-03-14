# MyMVVM
ARouter + DataBinding + MVVM 的使用演示

## 关于我
[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 演示效果（请star支持）
![](https://github.com/xuexiangjys/MyMVVM/blob/master/img/mvvm.gif)

## 如何使用DataBinding实现MVVM框架

###1.在Android Studio上使用，需要在module级别的build.gradle上添加对DataBinding的支持：

```
android {
    ....
    dataBinding {
        enabled = true
    }
}
```

###2.编写ViewModel

ViewModel实质是一个POJO类，继承了BaseObservable，通过Bindale注解绑定一个getter，当data属性发生改变在setter中发出通知，可以实现UI的实时刷新。

下面的示例代码是登录界面的ViewModel， 实现了ILoginModel接口，这样ViewModel不仅可以绑定数据，也可以绑定事件了。

```
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
        ToastUtil.showToast("点击登陆按钮，用户名：" + loginModel.getLoginName() + "，密码：" + loginModel.getLoginPassword());
    }

}
```

###3.编写layout files, 绑定ViewModel

DataBinding的layout files和普通的非DataBinding布局文件是有一些区别的，下面是一个基础的使用了DataBinding的布局文件：

（1）注册ViewModel

```
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">

    <data>
        <variable
            name="LoginModel"
            type="com.xuexiang.mymvvm.viewmodel.LoginModel" />
    </data>

    <LinearLayout...>

</layout>
```

（2）ViewModel绑定数据
使用@={}实现双向绑定， @{}只能实现单向绑定
```
<EditText
    android:id="@+id/loginname"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:ems="10"
    android:hint="请输入用户名"
    android:text="@={LoginModel.loginName}"
    android:inputType="textPersonName" />

```

（3）ViewModel绑定监听事件
使用lambda表达式进行事件的绑定
```
 <Button
    android:id="@+id/btn_login"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:onClick="@{() -> LoginModel.login(LoginModel)}"
    android:text="登陆" />

```

###4.在Activity或者Fragment中进行绑定

当编写完layout files后， 对应的Activity或者Fragment会自动生成Binding类，例如LoginActivity对应就是ActivityLoginBinding。使用DataBindingUtil进行绑定

```
//Activity
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, getLayoutId());
    initArgs();
    bindViews();
    initListeners();
}

//Fragment
@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(), getLayoutId(), container, false);
    initArgs();
    bindViews();
    initListeners();
    return binding.getRoot();
}

```

###5.最后向layout files中注入ViewModel
使用binding进行绑定，编译器会自动生成相应的setter方法。

```
/**
 * 绑定ViewModel
 */
@Override
protected void bindViews() {
    binding.setLoginModel(new LoginModel().attachV(this));
}
```


##最终的效果就是，Activity中没有任何多余代码
如下就是登录界面Activity的类
```
public class LoginActivity extends BaseActivity<ActivityLoginBinding> implements ILoginView {
    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    /**
     * 绑定ViewModel
     */
    @Override
    protected void bindViews() {
        binding.setLoginModel(new LoginModel().attachV(this));
    }

    @Override
    public void onFinished() {
        finish();
    }
}
```

