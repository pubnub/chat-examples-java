package com.pubnub.crc.chat_examples_java.pubnub;

import com.pubnub.crc.chat_examples_java.model.Users;

public class User {

    private Users.User user;
    private String status;

    private User(Builder builder) {
        user = builder.user;
        status = builder.status;
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
}
