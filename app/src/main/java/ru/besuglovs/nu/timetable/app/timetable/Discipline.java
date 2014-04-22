package ru.besuglovs.nu.timetable.app.timetable;

/**
 * Created by bs on 21.04.2014.
 */
public class Discipline {
    public Integer DisciplineId;
    public String Name;
    public Integer Attestation; // 0 - ничего; 1 - зачёт; 2 - экзамен; 3 - зачёт и экзамен
    public Integer AuditoriumHours;
    public Integer LectureHours;
    public Integer PracticalHours;
    public Integer StudentGroupId;
}
