package com.xuexiang.mymvvm.view;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.xuexiang.mymvvm.R;
import com.xuexiang.mymvvm.base.BaseActivity;
import com.xuexiang.mymvvm.databinding.ActivityMainBinding;
import com.xuexiang.mymvvm.viewmodel.MainModel;

@Route(path = "/ui/main")
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Autowired(name = "user_name")
    String UserName;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        ARouter.getInstance().inject(this);
    }

    /**
     * 绑定控件
     */
    @Override
    protected void bindViews() {
        binding.setMainModel(new MainModel()
                .setLoginName(UserName)
                .setDisplayContent("欢迎来到MVVM的世界！"));
    }

    /**
     * 初始化监听
     */
    @Override
    protected void initListener() {

    }


}
