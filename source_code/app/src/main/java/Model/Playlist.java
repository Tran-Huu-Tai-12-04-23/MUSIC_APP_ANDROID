package Model;


import java.io.Serializable;
import java.util.Date;

public class Playlist implements Serializable {
    private Long id;

    private String title;
    private String thumbnails;
    private Date createAt;
    private Long countSong;

    private User user;

    private Boolean isPrivate;

    private Boolean isSelected;

    public Boolean getSelected() {
        return isSelected != null ? isSelected : false;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Playlist() {
    }

    public Playlist(Long id, String title, String thumbnails, Date createAt, Long countSong, User user) {
        this.id = id;
        this.title = title;
        this.thumbnails = thumbnails;
        this.createAt = createAt;
        this.countSong = countSong;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Long getCountSong() {
        return countSong;
    }

    public void setCountSong(Long countSong) {
        this.countSong = countSong;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
