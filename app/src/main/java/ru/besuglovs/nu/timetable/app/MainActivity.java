package ru.besuglovs.nu.timetable.app;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.besuglovs.nu.timetable.app.timetable.Auditorium;
import ru.besuglovs.nu.timetable.app.timetable.Calendar;
import ru.besuglovs.nu.timetable.app.timetable.ConfigOption;
import ru.besuglovs.nu.timetable.app.timetable.Discipline;
import ru.besuglovs.nu.timetable.app.timetable.Lesson;
import ru.besuglovs.nu.timetable.app.timetable.LessonLogEvent;
import ru.besuglovs.nu.timetable.app.timetable.Ring;
import ru.besuglovs.nu.timetable.app.timetable.Student;
import ru.besuglovs.nu.timetable.app.timetable.StudentGroup;
import ru.besuglovs.nu.timetable.app.timetable.StudentsInGroups;
import ru.besuglovs.nu.timetable.app.timetable.Teacher;
import ru.besuglovs.nu.timetable.app.timetable.TeacherForDiscipline;
import ru.besuglovs.nu.timetable.app.timetable.Timetable;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<String> {

    static final int LOADER_ID = 1;
    static final String Log_TAG = "myLogTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Log_TAG, "onCreate start downloding");
        DownloadTimeTable();
    }

    private void DownloadTimeTable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Load timetable
            LoaderManager loaderManager =  getLoaderManager();
            Log.d(Log_TAG, "initLoader");
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            // Нету интернета
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Loader<String> loader = null;
        if (id == LOADER_ID) {
            loader = new TimeTableLoader(this);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        Log.d(Log_TAG, "Done");
        DecodeJSONTask decodeTask = new DecodeJSONTask();
        decodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        int eprst = 999;
    }

    class DecodeJSONTask extends AsyncTask<String, Void, Timetable> {

        @Override
        protected Timetable doInBackground(String... params) {
            Log.d(Log_TAG, "JSON start");

            Timetable result = new Timetable();

            JsonFactory jfactory = new JsonFactory();

            JsonParser jParser = null;
            try {
                jParser = jfactory.createParser(params[0]);

                while (jParser.nextToken() != null) {
                    if (jParser.getCurrentToken() == JsonToken.START_ARRAY)
                    {
                        String TableName = jParser.getCurrentName();
                        if (TableName.equals("auditoriums"))
                        {
                            result.auditoriums = new ArrayList<Auditorium>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Auditorium newAuditorium = new Auditorium();

                                jParser.nextToken();
                                newAuditorium.AuditoriumId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newAuditorium.Name = jParser.nextTextValue();

                                result.auditoriums.add(newAuditorium);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON auditoriums finished");
                        }

                        if (TableName.equals("calendars"))
                        {
                            result.calendars = new ArrayList<Calendar>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Calendar newCalendar = new Calendar();

                                jParser.nextToken();
                                newCalendar.CalendarId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                newCalendar.Date = formatter.parse(jParser.nextTextValue());

                                result.calendars.add(newCalendar);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON calendars finished");
                        }

                        if (TableName.equals("disciplines"))
                        {
                            result.disciplines = new ArrayList<Discipline>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Discipline newDiscipline = new Discipline();

                                jParser.nextToken();
                                newDiscipline.DisciplineId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newDiscipline.Name = jParser.nextTextValue();

                                jParser.nextToken();
                                newDiscipline.Attestation = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newDiscipline.AuditoriumHours = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newDiscipline.LectureHours = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newDiscipline.PracticalHours = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newDiscipline.StudentGroupId = Integer.parseInt(jParser.nextTextValue());

                                result.disciplines.add(newDiscipline);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON disciplines finished");
                        }

                        if (TableName.equals("lessons"))
                        {
                            result.lessons = new ArrayList<Lesson>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Lesson newLesson = new Lesson();

                                jParser.nextToken();
                                newLesson.LessonId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.IsActive = jParser.nextTextValue().equals("1");

                                jParser.nextToken();
                                newLesson.TeacherForDisciplineId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.CalendarId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.RingId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.AuditoriumId = Integer.parseInt(jParser.nextTextValue());

                                result.lessons.add(newLesson);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON lessons finished");
                        }

                        if (TableName.equals("rings"))
                        {
                            result.rings = new ArrayList<Ring>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Ring newRing = new Ring();

                                jParser.nextToken();
                                newRing.RingId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                newRing.Time = sdf.parse(jParser.nextTextValue());

                                result.rings.add(newRing);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON rings finished");
                        }
                        if (TableName.equals("students"))
                        {
                            result.students = new ArrayList<Student>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Student newStudent = new Student();

                                jParser.nextToken();
                                newStudent.StudentId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudent.F = jParser.nextTextValue();

                                jParser.nextToken();
                                newStudent.I = jParser.nextTextValue();

                                jParser.nextToken();
                                newStudent.O = jParser.nextTextValue();

                                jParser.nextToken();
                                newStudent.Starosta = jParser.nextTextValue().equals("1");

                                jParser.nextToken();
                                newStudent.NFactor = jParser.nextTextValue().equals("1");

                                jParser.nextToken();
                                newStudent.Expelled = jParser.nextTextValue().equals("1");

                                result.students.add(newStudent);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON students finished");
                        }
                        if (TableName.equals("studentGroups"))
                        {
                            result.studentGroups = new ArrayList<StudentGroup>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                StudentGroup newStudentGroup = new StudentGroup();

                                jParser.nextToken();
                                newStudentGroup.StudentGroupId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudentGroup.Name = jParser.nextTextValue();

                                result.studentGroups.add(newStudentGroup);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON studentGroups finished");
                        }
                        if (TableName.equals("studentsInGroups"))
                        {
                            result.studentsInGroups = new ArrayList<StudentsInGroups>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                StudentsInGroups newStudentInGroup = new StudentsInGroups();

                                jParser.nextToken();
                                newStudentInGroup.StudentsInGroupsId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudentInGroup.StudentId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudentInGroup.StudentGroupId = Integer.parseInt(jParser.nextTextValue());

                                result.studentsInGroups.add(newStudentInGroup);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON studentsInGroups finished");
                        }
                        if (TableName.equals("teachers"))
                        {
                            result.teachers = new ArrayList<Teacher>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Teacher newTeacher = new Teacher();

                                jParser.nextToken();
                                newTeacher.TeacherId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newTeacher.FIO = jParser.nextTextValue();

                                result.teachers.add(newTeacher);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON teachers finished");
                        }
                        if (TableName.equals("teacherForDisciplines"))
                        {
                            result.teacherForDisciplines = new ArrayList<TeacherForDiscipline>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                TeacherForDiscipline newTeacherForDiscipline = new TeacherForDiscipline();

                                jParser.nextToken();
                                newTeacherForDiscipline.TeacherForDisciplineId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newTeacherForDiscipline.TeacherId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newTeacherForDiscipline.DisciplineId = Integer.parseInt(jParser.nextTextValue());

                                result.teacherForDisciplines.add(newTeacherForDiscipline);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON teacherForDisciplines finished");
                        }
                        if (TableName.equals("configOptions"))
                        {
                            result.configOptions = new ArrayList<ConfigOption>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                ConfigOption newConfigOption = new ConfigOption();

                                jParser.nextToken();
                                newConfigOption.ConfigOptionId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newConfigOption.Key = jParser.nextTextValue();

                                jParser.nextToken();
                                newConfigOption.Value = jParser.nextTextValue();

                                result.configOptions.add(newConfigOption);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON configOptions finished");
                        }
                        if (TableName.equals("lessonLogEvents"))
                        {
                            result.lessonLogEvents = new ArrayList<LessonLogEvent>();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                LessonLogEvent newLessonLogEvent = new LessonLogEvent();

                                jParser.nextToken();
                                newLessonLogEvent.LessonLogEventId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLessonLogEvent.OldLessonId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLessonLogEvent.NewLessonId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                newLessonLogEvent.DateTime = formatter.parse(jParser.nextTextValue());

                                jParser.nextToken();
                                newLessonLogEvent.Comment = jParser.nextTextValue();

                                result.lessonLogEvents.add(newLessonLogEvent);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON lessonLogEvents finished");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                try {
                    jParser.close();
                } catch (IOException e) {
                }

            }



            Log.d(Log_TAG, "JSON finish");
            return result;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
