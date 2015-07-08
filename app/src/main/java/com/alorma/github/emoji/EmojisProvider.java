package com.alorma.github.emoji;

import android.content.Context;

import com.alorma.github.basesdk.client.BaseClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 08/07/2015.
 */
public class EmojisProvider implements BaseClient.OnResultCallback<HashMap<String, String>> {

    private Context context;
    private EmojisCallback emojisCallback;
    private Realm instance;

    public void getEmojis(Context context, EmojisCallback emojisCallback) {
        this.context = context;
        this.emojisCallback = emojisCallback;
        instance = Realm.getInstance(context);

        RealmQuery<EmojiVO> query = instance.where(EmojiVO.class);

        RealmResults<EmojiVO> result = query.findAll();

        if (result.size() > 0) {
            if (emojisCallback != null) {
                emojisCallback.onEmojisLoaded(transform(result));
            }
        } else {
            EmojisClient emojisClient = new EmojisClient(context);
            emojisClient.setOnResultCallback(this);
            emojisClient.execute();
        }
        instance.close();
        instance = null;
    }

    @Override
    public void onResponseOk(HashMap<String, String> emojis, Response r) {
        instance = Realm.getInstance(context);
        if (emojis.size() > 0) {
            RealmList<EmojiVO> emojiVOs = new RealmList<>();
            for (String key : emojis.keySet()) {
                EmojiVO emojiVO = new EmojiVO(key, emojis.get(key));
                emojiVOs.add(emojiVO);
            }
            instance.beginTransaction();
            instance.copyToRealmOrUpdate(emojiVOs);
            List<EmojiVO> copyEmojiList = new ArrayList<>(emojiVOs);

            if (emojisCallback != null) {
                emojisCallback.onEmojisLoaded(transform(copyEmojiList));
            }

            instance.commitTransaction();
            instance.close();
            instance = null;
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    public List<Emoji> transform(List<EmojiVO> vos) {
        List<Emoji> emojis = new ArrayList<>();
        if (vos != null) {
            for (EmojiVO vo : vos) {
                emojis.add(new Emoji(vo.getKey(), vo.getValue()));
            }
        }
        return emojis;
    }

    public void setEmojisCallback(EmojisCallback emojisCallback) {
        this.emojisCallback = emojisCallback;
    }

    public interface EmojisCallback {
        void onEmojisLoaded(List<Emoji> emojis);

        void onEmojisLoadFail();
    }
}
