package com.yknight.mail2.modules.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lodz.android.pandora.widget.rv.recycler.BaseRecyclerViewAdapter;
import com.yknight.mail2.R;
import com.yknight.mail2.bean.MessageTableBean;
import com.yknight.mail2.modules.definite.EmailDetailShowActivity;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import jakarta.mail.MessagingException;


/**
 * @ProjectName: SXMail
 * @Package: com.snxun.sxmail.modules.main
 * @ClassName: MessageListAdapter
 * @CreateAuthor: yKnight
 * @CreateDate: 2021/5/17
 * @description: 主页邮件列表适配器
 */
public class MessageListAdapter extends BaseRecyclerViewAdapter<MessageTableBean> {

    public MessageListAdapter(@NotNull Context context) {
        super(context);
    }

    @Override
    public void onBind(@NotNull RecyclerView.ViewHolder viewHolder, int i) {
        MessageTableBean bean = getItem(i);
        if (bean == null) {
            return;
        }
        try {
            showMessageItem((MessageViewHolder) viewHolder, bean);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void showMessageItem(MessageViewHolder holder, MessageTableBean bean) throws MessagingException {

        holder.mMessageTitle.setText(bean.subject);
        holder.mMessageContent.setText(bean.content);
        holder.mMessageDate.setText(String.valueOf(bean.sentData));
        holder.mMessageIconText.setText(bean.from.isEmpty() ? "" : bean.from.charAt(0) + "");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailDetailShowActivity.start(getContext(), bean);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(getLayoutView(parent, R.layout.item_message_list));
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        /**
         * 头像文本
         */
        @BindView(R.id.message_icon_text_tv)
        TextView mMessageIconText;
        /**
         * 邮件标题
         */
        @BindView(R.id.message_title_tv)
        TextView mMessageTitle;
        /**
         * 邮件正文
         */
        @BindView(R.id.message_content_tv)
        TextView mMessageContent;
        /**
         * 邮件发送时间
         */
        @BindView(R.id.message_date_tv)
        TextView mMessageDate;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
