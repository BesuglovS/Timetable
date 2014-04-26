package ru.besuglovs.nu.timetable.app.timetable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bs on 21.04.2014.
 */
public class Timetable {
    public ArrayList<Auditorium> auditoriums;
    public ArrayList<Calendar> calendars;
    public ArrayList<Discipline> disciplines;
    public ArrayList<Lesson> lessons;
    public ArrayList<Ring> rings;
    public ArrayList<Student> students;
    public ArrayList<StudentGroup> studentGroups;
    public ArrayList<StudentsInGroups> studentsInGroups;
    public ArrayList<Teacher> teachers;
    public ArrayList<TeacherForDiscipline> teacherForDisciplines;

    public ArrayList<ConfigOption> configOptions;
    public ArrayList<LessonLogEvent> lessonLogEvents;
}
