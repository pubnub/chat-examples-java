package resourcecenterdemo.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import resourcecenterdemo.R;
import resourcecenterdemo.pubnub.Message;
import resourcecenterdemo.util.AndroidUtils;
import resourcecenterdemo.util.GlideApp;
import resourcecenterdemo.util.Helper;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final int TYPE_TXT_OWN = 0;
    private final int TYPE_TXT_REC = 1;

    private final String mChannel;

    private List<Message> mItems;

    private Handler mainHandler;

    public ChatAdapter(String channel, List<Message> items) {
        mainHandler = new Handler(Looper.getMainLooper());
        mChannel = channel;
        mItems = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).isOwnMessage())
            return TYPE_TXT_OWN;
        return TYPE_TXT_REC;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TXT_OWN:
                View sentMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new MessageViewHolder(sentMessageView);
            case TYPE_TXT_REC:
                View receivedMessageView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new MessageViewHolder(receivedMessageView);
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

    class MessageViewHolder extends RecyclerView.ViewHolder {

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

        Timer mTimer;
        TimerTask mTimerTask;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (mTimer == null) {
                mTimer = new Timer();
            }

            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (mMessage != null)
                            mainHandler.post(() -> mTimestamp.setText(Helper.getRelativeTime(mMessage.getTimetoken() / 10_000L)));
                    }
                };
                // mTimer.scheduleAtFixedRate(mTimerTask, 0, TimeUnit.SECONDS.toMillis(1));
            }
        }

        void bindData(Message message) {
            this.mMessage = message;

            mBubble.setText(mMessage.getText());
            mSender.setText(mMessage.getUser().getDisplayName());
            mTimestamp.setText(mMessage.getTimestamp());

            GlideApp.with(this.itemView)
                    .load(mMessage.getUser().getProfilePictureUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
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

        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.message_info)
                .content(Html.fromHtml(contentBuilder.toString()))
                .positiveText(android.R.string.ok)
                .build();
        materialDialog.show();
    }

}
