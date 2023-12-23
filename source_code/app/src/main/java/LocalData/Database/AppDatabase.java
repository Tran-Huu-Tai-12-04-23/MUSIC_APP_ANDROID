package LocalData.Database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import LocalData.DTO.SearchHistoryDAO;
import LocalData.DTO.SongDAO;
import LocalData.DTO.StatePlayMusicDao;
import LocalData.DTO.UserDAO;
import LocalData.Entity.SearchHistoryEntity;
import LocalData.Entity.SongEntity;
import LocalData.Entity.StatePlayMusic;
import LocalData.Entity.UserEntity;


@Database(entities = {StatePlayMusic.class, UserEntity.class, SongEntity.class, SearchHistoryEntity.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract StatePlayMusicDao statePlayMusicDao();
    public abstract UserDAO userDAO();
    public abstract SongDAO songDAO();
    public abstract SearchHistoryDAO searchHistoryDAO();

}
