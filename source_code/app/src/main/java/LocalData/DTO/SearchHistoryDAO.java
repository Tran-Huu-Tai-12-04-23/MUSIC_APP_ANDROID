package LocalData.DTO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import LocalData.Entity.SearchHistoryEntity;
import LocalData.Entity.UserEntity;

@Dao
public interface SearchHistoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchHistory(SearchHistoryEntity searchHistory);
    @Delete
    void deleteSearchHistory(SearchHistoryEntity searchHistory);
    @Update
    void updateSearchHistory(SearchHistoryEntity searchHistory);

    @Query("SELECT * FROM searchhistory WHERE userId = :userId")
    List<SearchHistoryEntity> getAllSearchHistoryByUser(long userId);
}
