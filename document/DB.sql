-- Bảng cho người dùng (user)
CREATE TABLE Users (
    id INT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    phoneNumber VARCHAR(255)
);

-- Bảng cho hồ sơ người dùng (profile)
CREATE TABLE Profile (
    id INT PRIMARY KEY,
    userId INT,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    avatar VARCHAR(255),
    region VARCHAR(255),
    dateOfBirth DATE,
    FOREIGN KEY (userId) REFERENCES Users(id)
);

-- Bảng cho cài đặt (setting)
CREATE TABLE Setting (
    id INT PRIMARY KEY,
    userId INT,
    autoPlayNext BOOLEAN,
    randomPlay BOOLEAN,
    soundPower INT,
    showArtistInProfile INT,
    hiddenProfile BOOLEAN,
    qualityDownload INT,
    notification BOOLEAN,
    FOREIGN KEY (userId) REFERENCES Users(id)
);

-- Bảng cho theo dõi (follow)
CREATE TABLE Follow (
    followerId INT,
    followeeId INT,
    PRIMARY KEY (followerId, followeeId),
    FOREIGN KEY (followerId) REFERENCES Users(id),
    FOREIGN KEY (followeeId) REFERENCES Users(id)
);

-- Bảng cho bài hát (song)
CREATE TABLE Song (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    thumbnails VARCHAR(255),
    songLink VARCHAR(255),
    duration DOUBLE,
    uploadDate DATE,
    genre VARCHAR(255)
);

-- Bảng cho xác minh OTP (OTP_verify)
CREATE TABLE OTP_verify (
    id INT PRIMARY KEY,
    userId INT,
    OTP VARCHAR(255),
    expireTime INT,
    FOREIGN KEY (userId) REFERENCES Users(id)
);

-- Bảng cho lịch sử (history)
CREATE TABLE History (
    id INT PRIMARY KEY,
    userId INT,
    songId INT,
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (songId) REFERENCES Song(id)
);

-- Bảng cho danh sách phát (playlist)
CREATE TABLE Playlist (
    title VARCHAR(255),
    thumbnails VARCHAR(255),
    createAt DATE,
    countSong INT
);

-- Bảng chi tiết danh sách phát (detail_playlist)
CREATE TABLE Detail_playlist (
    userId INT,
    songId INT,
    playlistId INT,
    FOREIGN KEY (userId) REFERENCES Users(id),
    FOREIGN KEY (songId) REFERENCES Song(id),
    FOREIGN KEY (playlistId) REFERENCES Playlist(id)
);

-- Bảng cho bình luận (comment)
CREATE TABLE Comment (
    id INT PRIMARY KEY,
    userId INT,
    songId INT,
    content TEXT,
    commentDate DATE
);
