package resourcecenterdemo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import resourcecenterdemo.R;
import resourcecenterdemo.pubnub.User;
import resourcecenterdemo.util.GlideApp;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final String mChannel;

    private List<User> mItems;

    public UserAdapter(String channel, List<User> items) {
        mChannel = channel;
        mItems = items;
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

            mUsername.setText(this.mUser.getUser().getDisplayName());
            mStatus.setText(this.mUser.getStatus());

            GlideApp.with(this.itemView)
                    .load(user.getUser().getProfilePictureUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(mAvatar);
        }
    }
}
