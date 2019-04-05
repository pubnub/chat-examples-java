package com.pubnub.crc.chat_examples_java.model;

import com.pubnub.crc.chat_examples_java.util.Helper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class Users {

    private static List<User> users;

    private Users() {
    }

    static {
        users = new ArrayList<>();

        users.add(User.newBuilder()
                .firstName("Finny")
                .lastName("Fish")
                .uuid("u-00000")
                .build());

        users.add(User.newBuilder()
                .firstName("Daniel")
                .lastName("Dog")
                .uuid("u-00001")
                .build());

        users.add(User.newBuilder()
                .firstName("Bernie")
                .lastName("Bear")
                .uuid("u-00002")
                .build());

        users.add(User.newBuilder()
                .firstName("Carl")
                .lastName("Cat")
                .uuid("u-00003")
                .build());

        users.add(User.newBuilder()
                .firstName("Uri")
                .lastName("Unicorn")
                .uuid("u-00004")
                .build());

        users.add(User.newBuilder()
                .firstName("Monty")
                .lastName("Monkey")
                .uuid("u-00005")
                .build());

        users.add(User.newBuilder()
                .firstName("Ollie")
                .lastName("Owl")
                .uuid("u-00006")
                .build());

        users.add(User.newBuilder()
                .firstName("Larry")
                .lastName("Lion")
                .uuid("u-00007")
                .build());

    }

    public static List<User> all() {
        return users;
    }

    public static User getUserById(String id) {
        for (User user : users) {
            if (user.uuid.equals(id))
                return user;
        }
        return null;
    }

    public static class User {

        private String firstName, lastName, uuid, profilePictureUrl;
        private String displayName;

        private User(Builder builder) {
            firstName = builder.firstName;
            lastName = builder.lastName;
            uuid = builder.uuid;
            profilePictureUrl = builder.profilePictureUrl;
            displayName = firstName + " the " + lastName;
            if (profilePictureUrl == null) {
                String hash = Helper.md5(uuid);
                profilePictureUrl = new HttpUrl.Builder()
                        .scheme("https")
                        .host("gravatar.com")
                        .addPathSegment("avatar")
                        .addPathSegment(hash)
                        .addQueryParameter("s", "400")
                        .addQueryParameter("d", "identicon")
                        .build().toString();
            }
        }

        static Builder newBuilder() {
            return new Builder();
        }

        public static final class Builder {

            private String firstName;
            private String lastName;
            private String uuid;
            private String profilePictureUrl;

            private Builder() {
            }

            Builder firstName(String firstName) {
                this.firstName = firstName;
                return this;
            }

            Builder lastName(String lastName) {
                this.lastName = lastName;
                return this;
            }

            Builder uuid(String uuid) {
                this.uuid = uuid;
                return this;
            }

            public Builder profilePictureUrl(String profilePictureUrl) {
                this.profilePictureUrl = profilePictureUrl;
                return this;
            }

            User build() {
                return new User(this);
            }
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getUuid() {
            return uuid;
        }

        public String getProfilePictureUrl() {
            return profilePictureUrl;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
