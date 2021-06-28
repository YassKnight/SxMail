package com.yknight.mail2.modules.write;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lodz.android.pandora.base.activity.AbsActivity;
import com.yknight.mail2.R;

import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;

/**
 * @ProjectName: Mail2
 * @Package: com.yknight.mail2.modules.write
 * @ClassName: WriteMailActivity
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/6/21
 * @description: 写信页
 */
public class WriteMailActivity extends AbsActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, WriteMailActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getAbsLayoutId() {
        return R.layout.activity_write_mail;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);
        ButterKnife.bind(this);
    }
}
