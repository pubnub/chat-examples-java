package resourcecenterdemo.pubnub;

import com.google.common.base.Objects;

import androidx.annotation.Nullable;
import resourcecenterdemo.model.Users;
import resourcecenterdemo.prefs.Prefs;

public class User {

    private Users.User user;
    private String status, displayName;

    private User() {
    }

    private User(Builder builder) {
        user = builder.user;
        status = builder.status;
        status = "Available";
        displayName = user.getDisplayName();
        if (isMe()) {
            displayName += " (You)";
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Users.User user;
        private String status;

        private Builder() {
        }

        public Builder user(Users.User user) {
            this.user = user;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public Users.User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public boolean isMe() {
        return Prefs.get().uuid().equals(user.getUuid());
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof User) {
            return ((User) obj).user.getUuid().equals(user.getUuid());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this);
    }
}
