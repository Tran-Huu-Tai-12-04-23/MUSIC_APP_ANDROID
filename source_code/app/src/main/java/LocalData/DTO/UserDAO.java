package LocalData.DTO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import LocalData.Entity.SongEntity;
import LocalData.Entity.UserEntity;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUSer(UserEntity user);

    @Update
    void updateUser(UserEntity user);

    @Delete
    void deleteUser(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserByID(long userId);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUser();

}
