package resourcecenterdemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import resourcecenterdemo.R;
import resourcecenterdemo.pubnub.User;
import resourcecenterdemo.util.GlideApp;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final String mChannel;

    private List<User> mItems;

    public UserAdapter(String channel) {
        mChannel = channel;
        mItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View receivedMessageView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(receivedMessageView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bindData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void update(List<User> newData) {
        Collections.sort(newData, (o1, o2) -> Boolean.compare(o1.isMe(), o2.isMe()) * (-1));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(newData, mItems));
        diffResult.dispatchUpdatesTo(this);
        mItems.clear();
        mItems.addAll(newData);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_avatar)
        ImageView mAvatar;

        @BindView(R.id.user_username)
        TextView mUsername;

        @BindView(R.id.user_status)
        TextView mStatus;

        @OnClick(R.id.root)
        void rootClick(View v) {

        }

        User mUser;

        UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(User user) {
            this.mUser = user;

            mUsername.setText(this.mUser.getDisplayName());
            mStatus.setText(this.mUser.getUser().getDesignation());

            GlideApp.with(this.itemView)
                    .load(user.getUser().getProfilePictureUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
        }
    }

    class DiffCallback extends DiffUtil.Callback {

        List<User> newUsers;
        List<User> oldUsers;

        public DiffCallback(List<User> newChats, List<User> oldChats) {
            this.newUsers = newChats;
            this.oldUsers = oldChats;
        }

        @Override
        public int getOldListSize() {
            return oldUsers.size();
        }

        @Override
        public int getNewListSize() {
            return newUsers.size();
        }

        @Override
        public boolean areItemsTheSame(int i, int i1) {
            return oldUsers.get(i).equals(newUsers.get(i1));
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            return areItemsTheSame(i, i1);
        }

    }
}
