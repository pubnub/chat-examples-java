package resourcecenterdemo.model;

import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

import resourcecenterdemo.App;
import resourcecenterdemo.R;

public class Users {

    private static List<User> users;

    private Users() {
    }

    static {
        users = new ArrayList<>();

        // addData();

        addRichData();
    }

    private static void addRichData() {
        String[] firstNames = App.get().getResources().getStringArray(R.array.first_names);
        String[] lastNames = App.get().getResources().getStringArray(R.array.last_names);
        TypedArray images = App.get().getResources().obtainTypedArray(R.array.images);

        for (int i = 0; i < firstNames.length; i++) {
            users.add(User.newBuilder()
                    .firstName(firstNames[i])
                    .lastName(lastNames[i])
                    .profilePictureUrl(images.getResourceId(i, 0))
                    .uuid("u-0000" + i)
                    .build());
        }

        images.recycle();
    }

    private static void addData() {
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

        private String firstName, lastName, uuid;
        private String displayName;
        private Integer profilePictureUrl;

        private User(Builder builder) {
            firstName = builder.firstName;
            lastName = builder.lastName;
            uuid = builder.uuid;
            profilePictureUrl = builder.profilePictureUrl;
            displayName = firstName + " " + lastName;
        }

        static Builder newBuilder() {
            return new Builder();
        }

        public static final class Builder {

            private String firstName;
            private String lastName;
            private String uuid;
            private Integer profilePictureUrl;

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

            public Builder profilePictureUrl(Integer profilePictureUrl) {
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

        public Integer getProfilePictureUrl() {
            return profilePictureUrl;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
