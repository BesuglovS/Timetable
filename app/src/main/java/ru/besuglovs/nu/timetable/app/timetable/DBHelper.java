package ru.besuglovs.nu.timetable.app.timetable;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "Timetable.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private Dao<Auditorium, Integer> AuditoriumDao = null;
    private Dao<Calendar, Integer> CalendarDao = null;
    private Dao<ConfigOption, Integer> ConfigOptionDao = null;
    private Dao<Discipline, Integer> DisciplineDao = null;
    private Dao<Lesson, Integer> LessonDao = null;
    private Dao<LessonLogEvent, Integer> LessonLogEventDao = null;
    private Dao<Ring, Integer> RingDao = null;
    private Dao<Student, Integer> StudentDao = null;
    private Dao<StudentGroup, Integer> StudentGroupDao = null;
    private Dao<StudentsInGroups, Integer> StudentsInGroupsDao = null;
    private Dao<Teacher, Integer> TeacherDao = null;
    private Dao<TeacherForDiscipline, Integer> TeacherForDisciplineDao = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DBHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Auditorium.class);
            TableUtils.createTable(connectionSource, Calendar.class);
            TableUtils.createTable(connectionSource, ConfigOption.class);
            TableUtils.createTable(connectionSource, Discipline.class);
            TableUtils.createTable(connectionSource, Lesson.class);
            TableUtils.createTable(connectionSource, LessonLogEvent.class);
            TableUtils.createTable(connectionSource, Ring.class);
            TableUtils.createTable(connectionSource, Student.class);
            TableUtils.createTable(connectionSource, StudentGroup.class);
            TableUtils.createTable(connectionSource, StudentsInGroups.class);
            TableUtils.createTable(connectionSource, Teacher.class);
            TableUtils.createTable(connectionSource, TeacherForDiscipline.class);

        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

        // here we try inserting data in the on-create as a test
        // RuntimeExceptionDao<SimpleData, Integer> dao = getSimpleDataDao();
        // long millis = System.currentTimeMillis();
        // create some entries in the onCreate
        // SimpleData simple = new SimpleData(millis);
        // dao.create(simple);
        // simple = new SimpleData(millis + 1);
        // dao.create(simple);
        // Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DBHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Auditorium.class, true);
            TableUtils.dropTable(connectionSource, Calendar.class, true);
            TableUtils.dropTable(connectionSource, ConfigOption.class, true);
            TableUtils.dropTable(connectionSource, Discipline.class, true);
            TableUtils.dropTable(connectionSource, Lesson.class, true);
            TableUtils.dropTable(connectionSource, LessonLogEvent.class, true);
            TableUtils.dropTable(connectionSource, Ring.class, true);
            TableUtils.dropTable(connectionSource, Student.class, true);
            TableUtils.dropTable(connectionSource, StudentGroup.class, true);
            TableUtils.dropTable(connectionSource, StudentsInGroups.class, true);
            TableUtils.dropTable(connectionSource, Teacher.class, true);
            TableUtils.dropTable(connectionSource, TeacherForDiscipline.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Auditorium, Integer> getAuditoriumDao() throws SQLException {
        if (AuditoriumDao == null) {
            AuditoriumDao = getDao(Auditorium.class);
        }
        return AuditoriumDao;
    }
    public Dao<Calendar, Integer> getCalendarDao() throws SQLException {
        if (CalendarDao == null) {
            CalendarDao = getDao(Calendar.class);
        }
        return CalendarDao;
    }
    public Dao<ConfigOption, Integer> getConfigOptionDao() throws SQLException {
        if (ConfigOptionDao == null) {
            ConfigOptionDao = getDao(ConfigOption.class);
        }
        return ConfigOptionDao;
    }
    public Dao<Discipline, Integer> getDisciplineDao() throws SQLException {
        if (DisciplineDao == null) {
            DisciplineDao = getDao(Discipline.class);
        }
        return DisciplineDao;
    }
    public Dao<Lesson, Integer> getLessonDao() throws SQLException {
        if (LessonDao == null) {
            LessonDao = getDao(Lesson.class);
        }
        return LessonDao;
    }
    public Dao<LessonLogEvent, Integer> getLessonLogEventDao() throws SQLException {
        if (LessonLogEventDao == null) {
            LessonLogEventDao = getDao(LessonLogEvent.class);
        }
        return LessonLogEventDao;
    }
    public Dao<Ring, Integer> getRingDao() throws SQLException {
        if (RingDao == null) {
            RingDao = getDao(Ring.class);
        }
        return RingDao;
    }
    public Dao<Student, Integer> getStudentDao() throws SQLException {
        if (StudentDao == null) {
            StudentDao = getDao(Student.class);
        }
        return StudentDao;
    }
    public Dao<StudentGroup, Integer> getStudentGroupDao() throws SQLException {
        if (StudentGroupDao == null) {
            StudentGroupDao = getDao(StudentGroup.class);
        }
        return StudentGroupDao;
    }
    public Dao<StudentsInGroups, Integer> getStudentsInGroupsDao() throws SQLException {
        if (StudentsInGroupsDao == null) {
            StudentsInGroupsDao = getDao(StudentsInGroups.class);
        }
        return StudentsInGroupsDao;
    }
    public Dao<Teacher, Integer> getTeacherDao() throws SQLException {
        if (TeacherDao == null) {
            TeacherDao = getDao(Teacher.class);
        }
        return TeacherDao;
    }
    public Dao<TeacherForDiscipline, Integer> getTeacherForDisciplineDao() throws SQLException {
        if (TeacherForDisciplineDao == null) {
            TeacherForDisciplineDao = getDao(TeacherForDiscipline.class);
        }
        return TeacherForDisciplineDao;
    }
}
