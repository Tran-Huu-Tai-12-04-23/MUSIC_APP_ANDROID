package Model;

import java.io.Serializable;
import java.util.Date;

public class Song implements Serializable {
    private long id;
    private String title;
    private String thumbnails;
    private String songLink;
    private double duration;
    private Date uploadDate;
    private String genre;
    private User userUpload;
    private Boolean isPrivate;

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Song() {
    }

    public Song(long id, String title, String thumbnails, String songLink, double duration, Date uploadDate, String genre, User userUpload) {
        this.id = id;
        this.title = title;
        this.thumbnails = thumbnails;
        this.songLink = songLink;
        this.duration = duration;
        this.uploadDate = uploadDate;
        this.genre = genre;
        this.userUpload = userUpload;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public User getUserUpload() {
        return userUpload;
    }

    public void setUserUpload(User userUpload) {
        this.userUpload = userUpload;
    }
}