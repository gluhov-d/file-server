package com.github.gluhov.util;

import com.github.gluhov.model.User;
import com.github.gluhov.to.UserTo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtil {

    public static User updateFromTo(User user, UserTo userTo) {
        user.setName(userTo.getName());
        return user;
    }

    public static UserTo createTo(User user) {
        return new UserTo(user.getId(), user.getName(), EventUtil.createTos(user.getEvents()));
    }
}