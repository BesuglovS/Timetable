package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "students")
public class Student {
    @DatabaseField(generatedId = true)
    public Integer StudentId;
    @DatabaseField
    public String F;
    @DatabaseField
    public String I;
    @DatabaseField
    public String O;
    @DatabaseField
    public String Starosta;
    @DatabaseField
    public String NFactor;
    @DatabaseField
    public String Expelled;

    public Student() {
    }
}
