package com.weaverhong.lesson.androidbook_beatbox;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

// ViewMode类
// 基于BaseObservable类，让VM成为可观察的对象，同时，部署在V（xml）里的data标签也让V成为了观察者，
// 观察那些带有@Bindable的方法。只要发生了notifychange调用，观察者就会重新观察这些bindable方法，
// 刷新对应的变量。

// 其中部署的setSound方法和构造方法，让VM与M融为一体，控制器只承担了一个赋值的行为：
// mBinding.setViewModel(new SoundViewModel(mBeatBox));
// 给数据绑定布局，即Binding（View）赋值一个携带参数Model的SoudnVM
// 这样C就把V和VM和M联到了一起，而且从此以后，C也不用管理V的事了。VM会处理所有V显示M的事项。
public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }

    @Bindable
    public String getTitle() {
        return mSound.getName();
    }

    public Sound getSound() {
        return mSound;
    }

    public void setSound(Sound sound) {
        mSound = sound;
        notifyChange();
    }
}
