package ru.besuglovs.nu.timetable.app.timetable;

import java.util.List;

/**
 * Created by bs on 21.04.2014.
 */
public class Timetable {
    public List<Auditorium> auditoriums;
    public List<Calendar> calendars;
    public List<Discipline> disciplines;
    public List<Lesson> lessons;
    public List<Ring> rings;
    public List<Student> students;
    public List<StudentGroup> studentGroups;
    public List<StudentsInGroups> studentsInGroups;
    public List<Teacher> teachers;
    public List<TeacherForDiscipline> teacherForDisciplines;

    public List<ConfigOption> configOptions;
    public List<LessonLogEvent> lessonLogEvents;

}
