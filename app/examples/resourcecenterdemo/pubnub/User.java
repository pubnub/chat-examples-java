package resourcecenterdemo.pubnub;

import resourcecenterdemo.model.Users;

import androidx.annotation.Nullable;

public class User {

    private Users.User user;
    private String status;

    private User(Builder builder) {
        user = builder.user;
        status = builder.status;
        status = "Available";
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof User) {
            return ((User) obj).user.getUuid().equals(user.getUuid());
        }
        return super.equals(obj);
    }
}
