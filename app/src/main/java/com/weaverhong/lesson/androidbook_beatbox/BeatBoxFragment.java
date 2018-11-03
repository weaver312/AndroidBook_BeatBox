package com.weaverhong.lesson.androidbook_beatbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weaverhong.lesson.androidbook_beatbox.databinding.FragmentBeatBoxBinding;
import com.weaverhong.lesson.androidbook_beatbox.databinding.ListItemSoundBinding;

import java.util.List;

// 控制器
public class BeatBoxFragment extends Fragment {

    private BeatBox mBeatBox;

    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取本地资源
        mBeatBox = new BeatBox(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 其实Binding类本质就是，通过
        // 1. 在gradle里配置databinding
        // 2. 将xml根标签设为layout
        // 这两步，把xml配置文件变成了OO对象，使得以后可以。如果只用这个OO对象的getRoot方法就相当于走MVC的老路了
        // 这里对fragment的binding配置其实就是走的MVC老路，因为对fragment的视图没什么可管理的。。
        // 一个RecyclerView，有啥可管理的，不就是设个LayoutManager（原先给传的是ListLayoutManager），
        // 外加设个Adapter么

        // 给View指定渲染的文件。AS只负责帮我们生成View对应的Binding类，但不负责帮我们绑定到View上
        // 虽然这步是钦定好的：即 肯定是这个binding类对应这个View，但还是要写一下。
        // 所以这步和传统MVC步骤中要给fragment做LayoutInflater一样，也要做inflate，才能指定Binding类和View之间的关系
        FragmentBeatBoxBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_beat_box, container, false);

        // 给fragment下的唯一View（理论上也可以有其它View，但我们也知道，几乎没有这种情况，
        // 一般都是一个fragment对应一个最基本的小布局）指定好RecyclerView内列表的布局形式和其适配器
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        binding.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        // 这里binding.getRoot和binding.recyclerView的区别是，getRoot会返回layout内的根标签，
        // 而recyclerView就是返回recyclerView。
        // 这里用getRoot是为了显示出普适性。在本例子里，由于fragment里只有recyclerView一个，没有什么
        // LinearLayout啊，FrameLayout之类的嵌套布局，比较简单。换言之，本例子这里的getRoot其实可以换成
        // binding.recyclerView
        return binding.getRoot();
    }

    private class SoundHolder extends RecyclerView.ViewHolder {
        // 同样的，这里对
        private ListItemSoundBinding mBinding;

        private SoundHolder(ListItemSoundBinding binding) {
            super(binding.getRoot());
            mBinding = binding;

            // 其实只有这里没特别懂，为什么要给VM传整个大ModelLab过去。
            // 毕竟后面Adapter和Holder.bind已经把每个Sound传过去了呀。
            mBinding.setViewModel(new SoundViewModel(mBeatBox));
        }

        // 终于到MVVM的精华部分了。这里的bind和以前的bind区别很大。
        // 这里的bind非常简洁，只有两步：
        // 1. 给VM传过去它需要的所有Model，即实体数据
        // 2. 告诉VM，我给你传完数据了，你自己更新吧（之后，在UI线程上，V会自己调用那些带@Binding的方法来更新）

        // 所以对MVVM的总结就是，它就是大大简化了C中负责衔接M与V的代码部分，把衔接M与V的具体行为放进了ViewModel类
        // 而如何告诉V，你自己去找VM来做你的动态渲染，而不是等着C来亲自渲染呢？那就是通过前面onCreateView里说的两步：
        // 1. 在gradle里配置databinding
        // 2. 将xml根标签设为layout
        // 然后再配上第三步，在有动态数据的xml里（本例就是item_holder这个xml），写一点lambda表达式，
        // 例如倒数第二行：
        //     <data>
        //         <variable
        //             name="viewModel"
        //             type="com.weaverhong.lesson.androidbook_beatbox.SoundViewModel"/>
        //     </data>
        //     <Button
        //         android:layout_width="match_parent"
        //         android:layout_height="120dp"
        //         android:text="@{viewModel.title}"
        //         tools:text="Sound name"/>
        // part A:
        // 当然，这个活就放手交给做布局的人来干，他们和C完全分离，相当于前端。他们和后端（C）之间，
        // 就通过VM中的getset方法来交流，而且交流很少，比如在本例中，后端直接甩给前端一个大Sound对象
        // 前端自己在VM中可以随意处置Sound对象，获取名字啊、专辑照片啊、时长啊。只要前端想改界面，改完了之后，
        // 前端就可以随意对Sound对象处置，从其中获取自己想要的title、time等属性，然后自己修改xml，
        // 添加一些自己想要的lambda表达式，从而获得极大的前端自由度，对View处于一种可以随意修改的状态。
        // part B:
        // 而后端C，和原先差别并不大，只是减少了不少工作量。C只需要知道，我需要给哪个V传M，传什么M，就可以了
        // 在本例中，C只需要知道，哦，我需要
        // 1. 给一个ListView传（bind）一列表的Sound
        // 2. 给一个ListViewItem传（bind）一个Sound
        // （其实这两句话说的是一个意思，至于为什么要写两点，见SoundHolder构造方法中的注释）
        // 并通知VM，“我已经给你传数据了哈~” 就完事了，连传的是啥数据都不用告诉view，就告诉你：
        // "我传完了，你该更新啥自己心里有B数，我不管你了"
        // 就完成了C的所有工作。
        // VM得到通知后，就会在自己身上找@Binding标签，然后让View重新调取这些方法，从而View自己就更新了。

        // 有个图挺好的，说明白了VM这个双向干活的关系
        // https://images2018.cnblogs.com/blog/1048550/201807/1048550-20180701105302049-745020808.png
        // part A就相当于从右往左，Model往View里传值的方式，由VM的get/set方法实现了。
        // part B相当于从左往右，View实时监听Model中哪些值得到更新了，由VM的Observable可观察形态和@Binding标签实现了。

        public void bind(Sound sound) {
            mBinding.getViewModel().setSound(sound);
            mBinding.executePendingBindings();
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        // 对Adapter，还是老样子，两部分：
        // 1. 实体数据
        // 2. 布局的三个回调的，@Override的方法：新建一个holder，索引获取指定项，获取总数

        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Adapter比较传统，它又不认识你用的是MVVM还是MVC，它只认LayoutInflater
            // 所以我们的改造方式就是，在返回的SoundHolder中的bind方法中，进行binding改造

            // 第一步依旧是Adapter本职工作，获取inflater
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            // 第二部就是获取视图binding，把binding传过去，让bind方法不再像以前那样
            // 还要对每个button啊、view啊挨个来setText。因为在MVVM中，这种大量setXXX，与视图密切相关的废话
            // 就直接写在xml中了（为什么感觉有点像jsp的<%%>短表达式。。。）
            // 所以一会去看bind方法，就会发现bind方法出奇的简洁，只要把asset/model/实体数据文件一股脑儿地
            // 甩给binding/VM类，让VM类自己设计，应该更新model中的哪些值，渲染到哪部分，就完事儿了。
            ListItemSoundBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.list_item_sound, parent, false);
            return new SoundHolder(binding);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bind(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
}
