package com.lockin.dashboard.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.lockin.dashboard.data.db.Converters;
import com.lockin.dashboard.data.model.DayTaskSummary;
import com.lockin.dashboard.data.model.Priority;
import com.lockin.dashboard.data.model.Task;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TaskDao_Impl implements TaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Task> __insertionAdapterOfTask;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Task> __updateAdapterOfTask;

  private final SharedSQLiteStatement __preparedStmtOfSetCompleted;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public TaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTask = new EntityInsertionAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `task` (`id`,`dateKey`,`orderIndex`,`title`,`priority`,`estMinutes`,`isCompleted`,`durationDays`,`endDateKey`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDateKey());
        statement.bindLong(3, entity.getOrderIndex());
        statement.bindString(4, entity.getTitle());
        final String _tmp = __converters.fromPriority(entity.getPriority());
        statement.bindString(5, _tmp);
        statement.bindLong(6, entity.getEstMinutes());
        final int _tmp_1 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getDurationDays());
        statement.bindString(9, entity.getEndDateKey());
      }
    };
    this.__updateAdapterOfTask = new EntityDeletionOrUpdateAdapter<Task>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `task` SET `id` = ?,`dateKey` = ?,`orderIndex` = ?,`title` = ?,`priority` = ?,`estMinutes` = ?,`isCompleted` = ?,`durationDays` = ?,`endDateKey` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Task entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDateKey());
        statement.bindLong(3, entity.getOrderIndex());
        statement.bindString(4, entity.getTitle());
        final String _tmp = __converters.fromPriority(entity.getPriority());
        statement.bindString(5, _tmp);
        statement.bindLong(6, entity.getEstMinutes());
        final int _tmp_1 = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp_1);
        statement.bindLong(8, entity.getDurationDays());
        statement.bindString(9, entity.getEndDateKey());
        statement.bindString(10, entity.getId());
      }
    };
    this.__preparedStmtOfSetCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE task SET isCompleted = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM task WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Task task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTask.insert(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Task task, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTask.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setCompleted(final String id, final boolean completed,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetCompleted.acquire();
        int _argIndex = 1;
        final int _tmp = completed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Task>> getTasksForDate(final String dateKey) {
    final String _sql = "SELECT * FROM task WHERE (dateKey = ? OR (dateKey <= ? AND endDateKey >= ?)) ORDER BY isCompleted ASC, orderIndex ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dateKey);
    _argIndex = 2;
    _statement.bindString(_argIndex, dateKey);
    _argIndex = 3;
    _statement.bindString(_argIndex, dateKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"task"}, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfEstMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "estMinutes");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfDurationDays = CursorUtil.getColumnIndexOrThrow(_cursor, "durationDays");
          final int _cursorIndexOfEndDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateKey");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Priority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toPriority(_tmp);
            final int _tmpEstMinutes;
            _tmpEstMinutes = _cursor.getInt(_cursorIndexOfEstMinutes);
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final int _tmpDurationDays;
            _tmpDurationDays = _cursor.getInt(_cursorIndexOfDurationDays);
            final String _tmpEndDateKey;
            _tmpEndDateKey = _cursor.getString(_cursorIndexOfEndDateKey);
            _item = new Task(_tmpId,_tmpDateKey,_tmpOrderIndex,_tmpTitle,_tmpPriority,_tmpEstMinutes,_tmpIsCompleted,_tmpDurationDays,_tmpEndDateKey);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTasksForRange(final String from, final String to,
      final Continuation<? super List<Task>> $completion) {
    final String _sql = "SELECT * FROM task WHERE NOT (endDateKey < ? OR dateKey > ?) ORDER BY dateKey ASC, isCompleted ASC, orderIndex ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, from);
    _argIndex = 2;
    _statement.bindString(_argIndex, to);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Task>>() {
      @Override
      @NonNull
      public List<Task> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "orderIndex");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfEstMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "estMinutes");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfDurationDays = CursorUtil.getColumnIndexOrThrow(_cursor, "durationDays");
          final int _cursorIndexOfEndDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "endDateKey");
          final List<Task> _result = new ArrayList<Task>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Task _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final Priority _tmpPriority;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPriority);
            _tmpPriority = __converters.toPriority(_tmp);
            final int _tmpEstMinutes;
            _tmpEstMinutes = _cursor.getInt(_cursorIndexOfEstMinutes);
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final int _tmpDurationDays;
            _tmpDurationDays = _cursor.getInt(_cursorIndexOfDurationDays);
            final String _tmpEndDateKey;
            _tmpEndDateKey = _cursor.getString(_cursorIndexOfEndDateKey);
            _item = new Task(_tmpId,_tmpDateKey,_tmpOrderIndex,_tmpTitle,_tmpPriority,_tmpEstMinutes,_tmpIsCompleted,_tmpDurationDays,_tmpEndDateKey);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDaySummariesFrom(final String fromDate,
      final Continuation<? super List<DayTaskSummary>> $completion) {
    final String _sql = "\n"
            + "        SELECT dateKey,\n"
            + "               COUNT(*) as total,\n"
            + "               SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed\n"
            + "        FROM task\n"
            + "        WHERE dateKey >= ?\n"
            + "        GROUP BY dateKey\n"
            + "        ORDER BY dateKey ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fromDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DayTaskSummary>>() {
      @Override
      @NonNull
      public List<DayTaskSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateKey = 0;
          final int _cursorIndexOfTotal = 1;
          final int _cursorIndexOfCompleted = 2;
          final List<DayTaskSummary> _result = new ArrayList<DayTaskSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayTaskSummary _item;
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final int _tmpTotal;
            _tmpTotal = _cursor.getInt(_cursorIndexOfTotal);
            final int _tmpCompleted;
            _tmpCompleted = _cursor.getInt(_cursorIndexOfCompleted);
            _item = new DayTaskSummary(_tmpDateKey,_tmpTotal,_tmpCompleted);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getDatesWithCompletions(final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT dateKey FROM task WHERE isCompleted = 1 ORDER BY dateKey DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countForDate(final String dateKey,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM task WHERE (dateKey = ? OR (dateKey <= ? AND endDateKey >= ?))";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dateKey);
    _argIndex = 2;
    _statement.bindString(_argIndex, dateKey);
    _argIndex = 3;
    _statement.bindString(_argIndex, dateKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
