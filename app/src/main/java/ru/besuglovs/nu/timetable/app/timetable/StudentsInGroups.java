package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "studentInGroups")
public class StudentsInGroups {
    @DatabaseField(generatedId = true)
    public Integer StudentsInGroupsId;
    @DatabaseField
    public Integer StudentId;
    @DatabaseField
    public Integer StudentGroupId;

    public StudentsInGroups() {
    }
}
