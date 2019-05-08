package resourcecenterdemo.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import resourcecenterdemo.R;
import resourcecenterdemo.pubnub.Message;
import resourcecenterdemo.util.AndroidUtils;
import resourcecenterdemo.util.GlideApp;
import resourcecenterdemo.util.Helper;

import static resourcecenterdemo.util.ChatItem.TYPE_OWN_END;
import static resourcecenterdemo.util.ChatItem.TYPE_OWN_HEADER;
import static resourcecenterdemo.util.ChatItem.TYPE_OWN_MIDDLE;
import static resourcecenterdemo.util.ChatItem.TYPE_REC_END;
import static resourcecenterdemo.util.ChatItem.TYPE_REC_HEADER;
import static resourcecenterdemo.util.ChatItem.TYPE_REC_MIDDLE;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final String mChannel;

    private List<Message> mItems;

    public ChatAdapter(String channel) {
        mChannel = channel;
        mItems = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getType();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_OWN_HEADER:
            case TYPE_OWN_MIDDLE:
            case TYPE_OWN_END:
                View sentMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new MessageViewHolder(sentMessageView, viewType);
            case TYPE_REC_HEADER:
            case TYPE_REC_MIDDLE:
            case TYPE_REC_END:
                View receivedMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new MessageViewHolder(receivedMessageView, viewType);
        }
        throw new IllegalStateException("No applicable viewtype found.");
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bindData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getTimetoken();
    }

    public void update(List<Message> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, mItems));
        diffResult.dispatchUpdatesTo(this);
        mItems.clear();
        mItems.addAll(newData);
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_date)
        TextView mDateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(Long key) {
            mDateTextView.setText(Helper.parseDateTime(key));
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private int mType;

        @BindView(R.id.message_avatar)
        ImageView mAvatar;

        @BindView(R.id.message_sender)
        TextView mSender;

        @BindView(R.id.message_bubble)
        TextView mBubble;

        @BindView(R.id.message_timestamp)
        TextView mTimestamp;

        @OnClick(R.id.root)
        void rootClick(View v) {
            showMessageInfoDialog(v.getContext(), mMessage);
        }

        Message mMessage;

        MessageViewHolder(View itemView, int type) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            this.mType = type;

        }

        void bindData(Message message) {
            this.mMessage = message;

            handleType();

            mBubble.setText(mMessage.getText());

            mSender.setText(mMessage.getUser().getDisplayName());

            mTimestamp.setText(mMessage.getTimestamp());

            GlideApp.with(this.itemView)
                    .load(mMessage.getUser().getProfilePictureUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);

        }

        private void handleType() {
            switch (mType) {
                case TYPE_OWN_HEADER:
                case TYPE_REC_HEADER:
                    mAvatar.setVisibility(View.VISIBLE);
                    mSender.setVisibility(View.VISIBLE);
                    mTimestamp.setVisibility(View.GONE);
                    break;
                case TYPE_OWN_MIDDLE:
                case TYPE_REC_MIDDLE:
                    mAvatar.setVisibility(View.INVISIBLE);
                    mSender.setVisibility(View.GONE);
                    mTimestamp.setVisibility(View.GONE);
                    break;
                case TYPE_OWN_END:
                case TYPE_REC_END:
                    mAvatar.setVisibility(View.INVISIBLE);
                    mSender.setVisibility(View.GONE);
                    mTimestamp.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    class DiffCallback extends DiffUtil.Callback {

        List<Message> newMessages;
        List<Message> oldMessages;

        DiffCallback(List<Message> newMessages, List<Message> oldMessages) {
            this.newMessages = newMessages;
            this.oldMessages = oldMessages;
        }

        @Override
        public int getOldListSize() {
            return oldMessages.size();
        }

        @Override
        public int getNewListSize() {
            return newMessages.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            return oldMessages.get(i).getTimetoken() == newMessages.get(i1).getTimetoken();
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            return oldMessages.get(i).getType() == newMessages.get(i1).getType();
        }

    }

    private void showMessageInfoDialog(Context context, Message message) {

        StringBuilder contentBuilder = new StringBuilder("");
        contentBuilder.append(AndroidUtils.emphasizeText("Sender: "));
        contentBuilder.append(message.getSenderId());
        contentBuilder.append(AndroidUtils.newLine());
        contentBuilder.append(AndroidUtils.emphasizeText("Date time: "));
        contentBuilder.append(Helper.parseDateTime(message.getTimetoken() / 10_000L));
        contentBuilder.append(AndroidUtils.newLine());
        contentBuilder.append(AndroidUtils.emphasizeText("Relative: "));
        contentBuilder.append(Helper.getRelativeTime(message.getTimetoken() / 10_000L));
        contentBuilder.append(AndroidUtils.newLine());
        contentBuilder.append(AndroidUtils.emphasizeText("Own message: "));
        contentBuilder.append(message.isOwnMessage());
        contentBuilder.append(AndroidUtils.newLine());
        contentBuilder.append(AndroidUtils.emphasizeText("Type: "));
        contentBuilder.append(message.getType());

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.message_info)
                .content(Html.fromHtml(contentBuilder.toString()))
                .positiveText(android.R.string.ok)
                .build();
        materialDialog.show();
    }

}
