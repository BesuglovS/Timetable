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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ru.besuglovs.nu.timetable.app.timetable.Auditorium;
import ru.besuglovs.nu.timetable.app.timetable.Calendar;
import ru.besuglovs.nu.timetable.app.timetable.ConfigOption;
import ru.besuglovs.nu.timetable.app.timetable.DBHelper;
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


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<InputStream> {

    static final int LOADER_ID = 1;
    static final String Log_TAG = "myLogTag";

    private DBHelper dbHelper = null;

    private DBHelper getHelper() {
        if (dbHelper == null) {
            dbHelper =
                    OpenHelperManager.getHelper(this, DBHelper.class);
        }
        return dbHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(Log_TAG, "onCreate start downloding");
        DownloadTimeTable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
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
    public Loader<InputStream> onCreateLoader(int id, Bundle args) {
        Loader<InputStream> loader = null;
        if (id == LOADER_ID) {
            loader = new TimeTableLoader(this);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<InputStream> loader, InputStream data) {
        Log.d(Log_TAG, "Done");
        DecodeJSONTask decodeTask = new DecodeJSONTask();
        decodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        int eprst = 999;
    }

    class DecodeJSONTask extends AsyncTask<InputStream, Void, Timetable> {
        @Override
        protected Timetable doInBackground(InputStream... params) {
            Log.d(Log_TAG, "JSON start");

            //result = JacksonOneLineObjectMapperParser(params[0]);
            //result = GSON(params[0]);

            Timetable result = null;
            try {
                result = JacksonJParserDecode(params[0]);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Log.d(Log_TAG, "JSON finish");

            Integer eprst = 999;
            return result;
        }

        private Timetable GSON(InputStream inputStream) {
            final Gson gson = new Gson();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Timetable result = gson.fromJson(reader, Timetable.class);
            return result;
        }

        private Timetable JacksonOneLineObjectMapperParser(InputStream inputStream) throws SQLException {
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
            Timetable result = null;
            try {
                File file = new File(getFilesDir(), "json.tmp");

                result = mapper.readValue(file, Timetable.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            WriteToDatabase(result);

            return result;
        }

        private void WriteToDatabase(Timetable result) throws SQLException {
            Dao<Auditorium, Integer> AudDAO = getHelper().getAuditoriumDao();
            for (Auditorium aud : result.auditoriums) {
                AudDAO.create(aud);
            }

            Dao<Calendar, Integer> CalendarDAO = getHelper().getCalendarDao();
            for (Auditorium aud : result.auditoriums) {
                AudDAO.create(aud);
            }

            Dao<Discipline, Integer> DisciplinesDAO = getHelper().getDisciplineDao();
            for (Discipline discipline : result.disciplines) {
                DisciplinesDAO.create(discipline);
            }

            Dao<Lesson, Integer> LessonDAO = getHelper().getLessonDao();
            for (Lesson lesson : result.lessons) {
                LessonDAO.create(lesson);
            }

            Dao<Ring, Integer> RingDAO = getHelper().getRingDao();
            for (Ring ring : result.rings) {
                RingDAO.create(ring);
            }

            Dao<Student, Integer> StudentDAO = getHelper().getStudentDao();
            for (Student student : result.students) {
                StudentDAO.create(student);
            }

            Dao<StudentGroup, Integer> StudentGroupDAO = getHelper().getStudentGroupDao();
            for (StudentGroup studentGroup : result.studentGroups) {
                StudentGroupDAO.create(studentGroup);
            }

            Dao<StudentsInGroups, Integer> StudentsInGroupsDAO = getHelper().getStudentsInGroupsDao();
            for (StudentsInGroups studentsInGroups : result.studentsInGroups) {
                StudentsInGroupsDAO.create(studentsInGroups);
            }

            Dao<Teacher, Integer> TeacherDAO = getHelper().getTeacherDao();
            for (Teacher teacher : result.teachers) {
                TeacherDAO.create(teacher);
            }

            Dao<TeacherForDiscipline, Integer> TeacherForDisciplineDAO =
                    getHelper().getTeacherForDisciplineDao();
            for (TeacherForDiscipline teacherForDiscipline : result.teacherForDisciplines) {
                TeacherForDisciplineDAO.create(teacherForDiscipline);
            }

            Dao<ConfigOption, Integer> ConfigOptionDAO = getHelper().getConfigOptionDao();
            for (ConfigOption configOption : result.configOptions) {
                ConfigOptionDAO.create(configOption);
            }

            Dao<LessonLogEvent, Integer> LessonLogEventDAO = getHelper().getLessonLogEventDao();
            for (LessonLogEvent lessonLogEvent : result.lessonLogEvents) {
                LessonLogEventDAO.create(lessonLogEvent);
            }
        }

        private Timetable JacksonJParserDecode(InputStream inputStream) throws SQLException {
            Timetable result = new Timetable();

            JsonFactory jfactory = new JsonFactory();

            JsonParser jParser = null;
            try {
                File file = new File(getFilesDir(), "json.tmp");

                jParser = jfactory.createParser(file);

                while (jParser.nextToken() != null) {
                    if (jParser.getCurrentToken() == JsonToken.START_ARRAY)
                    {
                        String TableName = jParser.getCurrentName();
                        if (TableName.equals("auditoriums"))
                        {
                            Dao<Auditorium, Integer> AudDAO = getHelper().getAuditoriumDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Auditorium newAuditorium = new Auditorium();

                                jParser.nextToken();
                                newAuditorium.AuditoriumId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newAuditorium.Name = jParser.nextTextValue();

                                AudDAO.create(newAuditorium);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON auditoriums finished");
                        }

                        if (TableName.equals("calendars"))
                        {
                            Dao<Calendar, Integer> CalendarDAO = getHelper().getCalendarDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Calendar newCalendar = new Calendar();

                                jParser.nextToken();
                                newCalendar.CalendarId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                newCalendar.Date = jParser.nextTextValue();

                                CalendarDAO.create(newCalendar);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON calendars finished");
                        }

                        if (TableName.equals("disciplines"))
                        {
                            Dao<Discipline, Integer> DisciplinesDAO = getHelper().getDisciplineDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Discipline newDiscipline = new Discipline();

                                jParser.nextToken();
                                newDiscipline.DisciplineId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newDiscipline.Name = jParser.nextTextValue();

                                jParser.nextToken();
                                newDiscipline.Attestation = jParser.nextTextValue();

                                jParser.nextToken();
                                newDiscipline.AuditoriumHours = jParser.nextTextValue();

                                jParser.nextToken();
                                newDiscipline.LectureHours = jParser.nextTextValue();

                                jParser.nextToken();
                                newDiscipline.PracticalHours = jParser.nextTextValue();

                                jParser.nextToken();
                                newDiscipline.StudentGroupId = Integer.parseInt(jParser.nextTextValue());

                                DisciplinesDAO.create(newDiscipline);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON disciplines finished");
                        }

                        if (TableName.equals("lessons"))
                        {
                            Dao<Lesson, Integer> LessonDAO = getHelper().getLessonDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Lesson newLesson = new Lesson();

                                jParser.nextToken();
                                newLesson.LessonId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.IsActive = jParser.nextTextValue();

                                jParser.nextToken();
                                newLesson.TeacherForDisciplineId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.CalendarId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.RingId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newLesson.AuditoriumId = Integer.parseInt(jParser.nextTextValue());

                                LessonDAO.create(newLesson);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON lessons finished");
                        }

                        if (TableName.equals("rings"))
                        {
                            Dao<Ring, Integer> RingDAO = getHelper().getRingDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Ring newRing = new Ring();

                                jParser.nextToken();
                                newRing.RingId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                newRing.Time = jParser.nextTextValue();

                                RingDAO.create(newRing);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON rings finished");
                        }
                        if (TableName.equals("students"))
                        {
                            Dao<Student, Integer> StudentDAO = getHelper().getStudentDao();

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
                                newStudent.Starosta = jParser.nextTextValue();

                                jParser.nextToken();
                                newStudent.NFactor = jParser.nextTextValue();

                                jParser.nextToken();
                                newStudent.Expelled = jParser.nextTextValue();

                                StudentDAO.create(newStudent);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON students finished");
                        }
                        if (TableName.equals("studentGroups"))
                        {
                            Dao<StudentGroup, Integer> StudentGroupDAO = getHelper().getStudentGroupDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                StudentGroup newStudentGroup = new StudentGroup();

                                jParser.nextToken();
                                newStudentGroup.StudentGroupId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudentGroup.Name = jParser.nextTextValue();

                                StudentGroupDAO.create(newStudentGroup);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON studentGroups finished");
                        }
                        if (TableName.equals("studentsInGroups"))
                        {
                            Dao<StudentsInGroups, Integer> StudentsInGroupsDAO = getHelper().getStudentsInGroupsDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                StudentsInGroups newStudentInGroup = new StudentsInGroups();

                                jParser.nextToken();
                                newStudentInGroup.StudentsInGroupsId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudentInGroup.StudentId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newStudentInGroup.StudentGroupId = Integer.parseInt(jParser.nextTextValue());

                                StudentsInGroupsDAO.create(newStudentInGroup);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON studentsInGroups finished");
                        }
                        if (TableName.equals("teachers"))
                        {
                            Dao<Teacher, Integer> TeacherDAO = getHelper().getTeacherDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                Teacher newTeacher = new Teacher();

                                jParser.nextToken();
                                newTeacher.TeacherId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newTeacher.FIO = jParser.nextTextValue();

                                TeacherDAO.create(newTeacher);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON teachers finished");
                        }
                        if (TableName.equals("teacherForDisciplines"))
                        {
                            Dao<TeacherForDiscipline, Integer> TeacherForDisciplineDAO =
                                    getHelper().getTeacherForDisciplineDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                TeacherForDiscipline newTeacherForDiscipline = new TeacherForDiscipline();

                                jParser.nextToken();
                                newTeacherForDiscipline.TeacherForDisciplineId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newTeacherForDiscipline.TeacherId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newTeacherForDiscipline.DisciplineId = Integer.parseInt(jParser.nextTextValue());

                                TeacherForDisciplineDAO.create(newTeacherForDiscipline);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON teacherForDisciplines finished");
                        }
                        if (TableName.equals("configOptions"))
                        {
                            Dao<ConfigOption, Integer> ConfigOptionDAO = getHelper().getConfigOptionDao();

                            while (jParser.nextToken() == JsonToken.START_OBJECT) {
                                ConfigOption newConfigOption = new ConfigOption();

                                jParser.nextToken();
                                newConfigOption.ConfigOptionId = Integer.parseInt(jParser.nextTextValue());

                                jParser.nextToken();
                                newConfigOption.Key = jParser.nextTextValue();

                                jParser.nextToken();
                                newConfigOption.Value = jParser.nextTextValue();

                                ConfigOptionDAO.create(newConfigOption);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON configOptions finished");
                        }
                        if (TableName.equals("lessonLogEvents"))
                        {
                            Dao<LessonLogEvent, Integer> LessonLogEventDAO = getHelper().getLessonLogEventDao();

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
                                newLessonLogEvent.DateTime = jParser.nextTextValue();

                                jParser.nextToken();
                                newLessonLogEvent.Comment = jParser.nextTextValue();

                                LessonLogEventDAO.create(newLessonLogEvent);
                                jParser.nextToken();
                            }
                            Log.d(Log_TAG, "JSON lessonLogEvents finished");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    jParser.close();
                } catch (IOException e) {
                }

            }
            return result;
        }
    }

    @Override
    public void onLoaderReset(Loader<InputStream> loader) {

    }
}
