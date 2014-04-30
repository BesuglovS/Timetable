package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "lessons")
public class Lesson {
    @DatabaseField(generatedId = true)
    public Integer LessonId;
    @DatabaseField
    public String IsActive;
    @DatabaseField
    public Integer TeacherForDisciplineId;
    @DatabaseField
    public Integer CalendarId;
    @DatabaseField
    public Integer RingId;
    @DatabaseField
    public Integer AuditoriumId;

    public Lesson() {
    }
}
