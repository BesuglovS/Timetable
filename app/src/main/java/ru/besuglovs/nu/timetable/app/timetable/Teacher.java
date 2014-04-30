package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "teachers")
public class Teacher {
    @DatabaseField(generatedId = true)
    public Integer TeacherId;
    @DatabaseField
    public String FIO;

    public Teacher() {
    }
}
