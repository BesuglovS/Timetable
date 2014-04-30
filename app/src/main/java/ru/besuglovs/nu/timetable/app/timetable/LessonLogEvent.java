package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "lessonLogEvents")
public class LessonLogEvent {
    @DatabaseField(generatedId = true)
    public Integer LessonLogEventId;
    @DatabaseField
    public Integer OldLessonId;
    @DatabaseField
    public Integer NewLessonId;
    @DatabaseField
    public String DateTime;
    @DatabaseField
    public String Comment;

    public LessonLogEvent() {
    }
}
