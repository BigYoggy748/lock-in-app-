package com.lockin.dashboard.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.lockin.dashboard.data.model.DayLog;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DayLogDao_Impl implements DayLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DayLog> __insertionAdapterOfDayLog;

  private final SharedSQLiteStatement __preparedStmtOfUpdateScratchpad;

  public DayLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDayLog = new EntityInsertionAdapter<DayLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `day_log` (`dateKey`,`scratchpad`,`updatedAt`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayLog entity) {
        statement.bindString(1, entity.getDateKey());
        statement.bindString(2, entity.getScratchpad());
        statement.bindLong(3, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfUpdateScratchpad = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE day_log SET scratchpad = ?, updatedAt = ? WHERE dateKey = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertIfAbsent(final DayLog dayLog, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDayLog.insert(dayLog);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateScratchpad(final String dateKey, final String text, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateScratchpad.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, text);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
        _stmt.bindString(_argIndex, dateKey);
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
          __preparedStmtOfUpdateScratchpad.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DayLog> getByDate(final String dateKey) {
    final String _sql = "SELECT * FROM day_log WHERE dateKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dateKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"day_log"}, new Callable<DayLog>() {
      @Override
      @Nullable
      public DayLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDateKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dateKey");
          final int _cursorIndexOfScratchpad = CursorUtil.getColumnIndexOrThrow(_cursor, "scratchpad");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final DayLog _result;
          if (_cursor.moveToFirst()) {
            final String _tmpDateKey;
            _tmpDateKey = _cursor.getString(_cursorIndexOfDateKey);
            final String _tmpScratchpad;
            _tmpScratchpad = _cursor.getString(_cursorIndexOfScratchpad);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new DayLog(_tmpDateKey,_tmpScratchpad,_tmpUpdatedAt);
          } else {
            _result = null;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
