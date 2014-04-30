package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "auditoriums")
public class Auditorium {
    @DatabaseField(generatedId = true)
    public Integer AuditoriumId;
    @DatabaseField
    public String Name;

    public Auditorium() {
    }
}
