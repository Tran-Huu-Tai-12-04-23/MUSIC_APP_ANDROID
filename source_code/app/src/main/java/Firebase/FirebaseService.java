package Firebase;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import LocalData.Entity.CurrentPlaylistEntity;
import LocalData.Entity.SongEntity;
import Model.CurrentPlaylist;
import Model.Song;

public class FirebaseService {
    private static final String CURRENT_PLAYLIST_NODE = "current_playlist";

    private final DatabaseReference databaseReference;
    private Long userId;


    public interface OnSaveCompleteListener {
        void onSaveComplete(boolean isSuccess);
    }

    public FirebaseService(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("LoginData", MODE_PRIVATE);
        String strUserId = preferences.getString("userId", "");
        if( !strUserId.equals("")) {
            userId = Long.parseLong(strUserId);
        }
        this.databaseReference = FirebaseDatabase.getInstance().getReference().child(userId.toString());
    }

    public void saveCurrentSong(CurrentPlaylist CurrentPlaylist, final OnSaveCompleteListener listener) {
        databaseReference
                .setValue(CurrentPlaylist, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            // Operation succeeded
                            if (listener != null) {
                                listener.onSaveComplete(true);
                            }
                        } else {
                            // Operation failed
                            if (listener != null) {
                                listener.onSaveComplete(false);
                            }
                        }
                    }

                });
    }

    public void getCurrentPlaylist(final OnGetPlaylistCompleteListener listener) {
        databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Process the data and convert it into a list of CurrentPlaylist objects
                        CurrentPlaylist currentPlaylist;
                        currentPlaylist = dataSnapshot.getValue(CurrentPlaylist.class);
                        System.out.println("failed");
                        if (listener != null) {
                            listener.onGetPlaylistComplete(currentPlaylist);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        if (listener != null) {
                            listener.onGetPlaylistComplete(null);
                        }
                    }
                });
    }

    public void changeCurrentSong(Song newCurrentSong, final OnSaveCompleteListener listener) {
        databaseReference.child("currentSong").setValue(newCurrentSong, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null && listener != null) {
                    listener.onSaveComplete(true);
                } else if (listener != null) {
                    listener.onSaveComplete(false);
                }
            }
        });
    }

    public void addSongToPlaylist( Song songToAdd, final OnSaveCompleteListener listener) {
        this.getCurrentPlaylist(new OnGetPlaylistCompleteListener() {
            @Override
            public void onGetPlaylistComplete(CurrentPlaylist currentPlaylist) {
                if( currentPlaylist == null || currentPlaylist.getSongs() == null ) {
                    initPlayingSong(songToAdd);
                    listener.onSaveComplete(true);
                }else {
                    List<Song> songs = currentPlaylist.getSongs();
                    boolean isExistSong = songExistList(songToAdd, songs);
                    if( !isExistSong){
                        songs.add(songToAdd);
                        replaceSongsInPlaylist(songs, new OnSaveCompleteListener() {
                            @Override
                            public void onSaveComplete(boolean isSuccess) {
                                listener.onSaveComplete(isSuccess);
                            }
                        });
                    }else {
                        listener.onSaveComplete(true);
                    }

                }
            }
        });
    }

    public void removeSongFromPlaylist(int songId, final OnSaveCompleteListener listener) {
        DatabaseReference songToRemoveRef = databaseReference.child("songs").child(String.valueOf(songId));
        songToRemoveRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null && listener != null) {
                    listener.onSaveComplete(true);
                } else if (listener != null) {
                    listener.onSaveComplete(false);
                }
            }
        });
    }

    public void replaceSongsInPlaylist(List<Song> newSongs, final OnSaveCompleteListener listener) {
        databaseReference.child("songs").setValue(newSongs, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null && listener != null) {
                    listener.onSaveComplete(true);
                } else if (listener != null) {
                    listener.onSaveComplete(false);
                }
            }
        });
    }

    public void addPlaylistChangeListener( final OnPlaylistChangeListener listener) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Process the changed data and notify the listener
                CurrentPlaylist currentPlaylistEntity = dataSnapshot.getValue(CurrentPlaylist.class);
                if (listener != null) {
                    listener.onPlaylistChanged(currentPlaylistEntity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                // You may want to notify the listener about errors as well
            }
        });
    }

    public interface OnPlaylistChangeListener {
        void onPlaylistChanged(CurrentPlaylist currentPlaylist);
    }

    public interface OnGetPlaylistCompleteListener {
        void onGetPlaylistComplete(CurrentPlaylist currentPlaylist);
    }

    private void initPlayingSong(Song song) {
        CurrentPlaylist currentPlaylist = new CurrentPlaylist();

        currentPlaylist.setCurrentSong(song);
        List<Song> songs = new ArrayList<>();
        songs.add(song);
        currentPlaylist.setSongs(songs);

        this.saveCurrentSong(currentPlaylist, new FirebaseService.OnSaveCompleteListener() {
            @Override
            public void onSaveComplete(boolean isSuccess) {

            }
        });



    }


    private boolean songExistList(Song song, List<Song> songs) {
        for( Song songEntity : songs ) {
            if( songEntity.getTitle().equals(song.getTitle())
                    && songEntity.getSongLink().equals(song.getSongLink())
                    && songEntity.getThumbnails().equals(song.getThumbnails())) {
                return true;
            }
        }
        return false;
    }
}
