package ru.besuglovs.nu.timetable.app.timetable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by bs on 21.04.2014.
 */
@DatabaseTable(tableName = "teacherForDisciplines")
public class TeacherForDiscipline {
    @DatabaseField(generatedId = true)
    public Integer TeacherForDisciplineId;
    @DatabaseField
    public Integer TeacherId;
    @DatabaseField
    public Integer DisciplineId;

    public TeacherForDiscipline() {
    }
}
