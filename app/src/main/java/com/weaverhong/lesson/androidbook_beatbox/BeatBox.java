package com.weaverhong.lesson.androidbook_beatbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// 相当于以前的Lab，可以视为SoundLab，专门用来管理一类assets，比如这里的sample_sounds，
// 而Sound则相当于Model
// 其实这里的BeatBox和Sound都是广义上的Model
public class BeatBox {
    private static final String TAG = "BeatBox";

    private static final String SOUNDS_FOLDER = "sample_sounds";

    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();

    // 虽然这个构造方法有context，但实际使用时其实不关心是哪个context，
    // 因为所有context其实管理了同一套assets资源
    public BeatBox(Context context) {
        mAssets = context.getAssets();
        loadSounds();
    }

    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAssets.list(SOUNDS_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + "sounds");
        } catch (Exception e) {
            Log.e(TAG, "Could not list assets", e);
            return;
        }

        for (String filename : soundNames) {
            String assetPath = SOUNDS_FOLDER + "/" + filename;
            Sound sound = new Sound(assetPath);
            mSounds.add(sound);
        }
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
