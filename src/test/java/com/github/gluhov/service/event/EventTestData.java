package com.github.gluhov.service.event;

import com.github.gluhov.model.Event;

import static com.github.gluhov.service.file.FileTestData.file1;
import static com.github.gluhov.service.file.FileTestData.file3;
import static com.github.gluhov.service.user.UserTestData.user1;
import static com.github.gluhov.service.user.UserTestData.user3;

public class EventTestData {
    public static final long EVENT_ID = 1;
    public static final long EVENT_NOT_FOUND_ID = 100;
    public static final Event event1 = new Event(EVENT_ID, user1, file3);
    public static final Event event3 = new Event(EVENT_ID + 2, user3, file1);

}
