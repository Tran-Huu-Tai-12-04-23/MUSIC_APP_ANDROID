package LocalData.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import LocalData.Service.DateConverter;

@Entity(tableName = "SearchHistory")
@TypeConverters(DateConverter.class)
public class SearchHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long userId;
    private String key;
    private Date searchDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(Date searchDate) {
        this.searchDate = searchDate;
    }
}
