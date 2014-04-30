package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "disciplines")
public class Discipline {
    @DatabaseField(generatedId = true)
    public Integer DisciplineId;
    @DatabaseField
    public String Name;
    @DatabaseField
    public String Attestation; // 0 - ничего; 1 - зачёт; 2 - экзамен; 3 - зачёт и экзамен
    @DatabaseField
    public String AuditoriumHours;
    @DatabaseField
    public String LectureHours;
    @DatabaseField
    public String PracticalHours;
    @DatabaseField
    public Integer StudentGroupId;

    public Discipline() {
    }
}
