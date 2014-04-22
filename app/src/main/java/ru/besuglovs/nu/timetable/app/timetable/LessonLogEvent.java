package ru.besuglovs.nu.timetable.app.timetable;

import java.util.Date;

/**
 * Created by bs on 21.04.2014.
 */
public class LessonLogEvent {
    public Integer LessonLogEventId;
    public Integer OldLessonId;
    public Integer NewLessonId;
    public Date DateTime;
    public String Comment;
}
