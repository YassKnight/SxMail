package com.yknight.mail2.modules.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lodz.android.corekt.utils.ToastUtils;
import com.lodz.android.pandora.base.activity.BaseRefreshActivity;
import com.lodz.android.pandora.rx.subscribe.observer.ProgressObserver;
import com.lodz.android.pandora.rx.utils.RxUtils;
import com.lodz.android.pandora.widget.base.TitleBarLayout;
import com.yknight.mail2.R;
import com.yknight.mail2.bean.MessageTableBean;
import com.yknight.mail2.modules.write.WriteMailActivity;
import com.yknight.mail2.utils.db.DbFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends BaseRefreshActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    /**
     * 邮件列表
     */
    @BindView(R.id.message_list_rv)
    RecyclerView mMessageList;
    /**
     * 标题栏
     */
    @BindView(R.id.main_title_bar_layout)
    TitleBarLayout mTitleBarLayout;

    MessageListAdapter mAdapter;
    List<MessageTableBean> mDataList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews(@Nullable Bundle savedInstanceState) {
        super.findViews(savedInstanceState);
        ButterKnife.bind(this);
        goneTitleBar();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        mAdapter = new MessageListAdapter(getContext());
        mAdapter.setData(mDataList);
        mMessageList.setLayoutManager(manager);
        mMessageList.setHasFixedSize(true);
        mMessageList.setAdapter(mAdapter);
    }

    @Override
    protected void onDataRefresh() {
        //暂时先获取数据库数据
        //正确做法应该要主动获取新邮箱更新至数据中,然后再从数据中在读取新的数据加载
        getDataFromDb();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        //左上角菜单按钮
        mTitleBarLayout.setOnBackBtnClickListener(new Function1<View, Unit>() {
            @Override
            public Unit invoke(View view) {
                //写信
                WriteMailActivity.start(getContext());
                return Unit.INSTANCE;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        getDataFromDb();
    }

    /**
     * 从数据库获取邮件数据
     */
    public void getDataFromDb() {
        DbFactory.create().getMessageListByCatalog("INBOX").rx()
                .compose(RxUtils.ioToMainObservable())
                .subscribe(new ProgressObserver<List<MessageTableBean>>() {
                    @Override
                    public void onPgNext(List<MessageTableBean> messageTableBeans) {
                        ToastUtils.showShort(getContext(), "列表数据获取成功");
                        mDataList.clear();
                        mDataList.addAll(messageTableBeans);
                        mAdapter.notifyDataSetChanged();
                        setSwipeRefreshFinish();
                        showStatusCompleted();
                    }

                    @Override
                    public void onPgError(@NotNull Throwable throwable, boolean b) {
                        ToastUtils.showShort(getContext(), getString(R.string.getting_messages_error));
                        showStatusError();
                        setSwipeRefreshFinish();
                    }
                }.create(getContext(), getString(R.string.getting_messages), false));
    }
}