# MyMVVM
ARouter + DataBinding + MVVM 的使用演示

## 关于我
[![github](https://img.shields.io/badge/GitHub-xuexiangjys-blue.svg)](https://github.com/xuexiangjys)   [![csdn](https://img.shields.io/badge/CSDN-xuexiangjys-green.svg)](http://blog.csdn.net/xuexiangjys)

## 演示效果（请star支持）
![](https://github.com/xuexiangjys/MyMVVM/blob/master/img/mvvm.gif)

## 何为MVVM框架

MVVM目前是主流的前端框架，它是Model-View-ViewModel的简写。它的演化过程：MVC-> MVP -> MVVM。

MVVM与之前的MVC和MVP最大的不同之处就在于它使用了数据绑定技术，通过binding层，可以轻松地构建UI和渲染UI。

在Android中：

- Model：作为数据的来源,可以是本地存储数据（数据库、本地文件）、内存数据、网络请求数据等
- View：相当于我们写的layout(XML）和Activity/Fragment文件
- ViewModel：存放UI相关的数据和进行逻辑处理

## 为何选择MVVM框架

先让我们来总结一下MVC和MVP的优缺点，再来讨论MVVM。

### MVC

   在Android中，在项目不大的时候，我们绝大多数使用的就是这种。但是带来的问题就是，Activity既作为V，又作为C,等项目逐渐变大了之后，Activity便会变得异常地庞大和复杂，有时一个Activity可能会有上千行的代码，这对于维护而言简直就是噩梦。
  
### MVP
    
   当项目逐渐变得庞大和复杂之后，为了解决Activity的臃肿问题，我们才引入了P，P的出现就是为了解决Activity既作为V又作为C这种尴尬的情况，将所有的逻辑处理从Activity中剥离到P中进行处理。P作为V和M直接的中介者来进行调度。
   
   但是在实际的使用过程中，我发现，P虽然作为中介者来调度M和V，但是实际操作依然都是M和V进行，这就意味着，P会持有M和V的实例，M产生出数据后，还需要P去通知View进行UI渲染，而V产生事件后，又需要先通知P，然后P再更具情况去判断是否调用M去进行数据修改。实际上，如果项目变得非常大的时候，P也会像Activity一样变得越来越复杂。而且由于V存在生命周期，P也不具备感知V生命周期的能力。当页面变得非常多时，会产生无数多的P，那么项目的可维护性就大大降低。
   
### MVVM
   
   针对MVP使用过程中最大的痛点--“M产生出数据后，还需要P去通知View进行UI渲染，而V产生事件后，又需要先通知P，然后P再更具情况去判断是否调用M去进行数据修改”，Android引进了目前前端最火的MVVM框架来解决这一问题。通过MVVM，ViewModel实现了和View的绑定，当ViewModel中存放的来自Model的数据发生变化时，View会自动刷新UI，而View产生事件后，又会立即通知ViewModel进行处理。这样，View层就可以专心做UI渲染的工作，而ViewModel就可以专心做业务处理工作，也不会产生剪不断理还乱这种忧伤。
   
   但是MVVM对于XML布局的书写要求非常高，它实质是将之前MVP中M和P的关系转移到了XML布局中。但凡XML中有一处表达式拼写错误，排查问题来还是非常需要技术的。

## 如何使用DataBinding实现MVVM框架

### 1.在Android Studio上使用，需要在module级别的build.gradle上添加对DataBinding的支持：

```
android {
    ....
    dataBinding {
        enabled = true
    }
}
```
DataBinding详细使用请参见：https://blog.csdn.net/QDJdeveloper/article/details/62236309

### 2.编写ViewModel

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

### 3.编写layout files, 绑定ViewModel

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

### 4.在Activity或者Fragment中进行绑定

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

### 5.最后向layout files中注入ViewModel
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


## 最终的效果就是，Activity中没有任何多余代码
如下就是登录界面Activity的类，所有的View都在xml中，逻辑和数据都存放在ViewModel中，Activity专心做UI的渲染工作。
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

## 更多框架演示

- [MVP](https://github.com/xuexiangjys/MyMVP)
- [MVVM](https://github.com/xuexiangjys/MyMVVM)
- [Google Architecture Components](https://github.com/xuexiangjys/GoogleComponentsDemo)

