package com.weaverhong.lesson.androidbook_beatbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

// 将只装有一个Fragment的Activity抽象出来。它的继承类只要重写createFragment方法就可以了。
// 这个写法可能是叫回调，就是构造中传入的类的某些方法被私有方法使用。从而最终效果就好像传入了一个方法

public abstract class SingleFragmentActivity extends AppCompatActivity{
    // 只要重写这个方法就行了。这个写法好像是类似回调。
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
