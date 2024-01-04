package com.example.api_music_player;

import com.example.api_music_player.model.DetailPlaylist;
import com.example.api_music_player.model.Playlist;
import com.example.api_music_player.model.Song;
import com.example.api_music_player.model.User;
import com.example.api_music_player.repository.DetailPlaylistRepository;
import com.example.api_music_player.repository.PlaylistRepository;
import com.example.api_music_player.repository.SongRepository;
import com.example.api_music_player.repository.UserRepository;
import com.example.api_music_player.service.ISongService;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.IOException;
import java.net.URL;


@SpringBootApplication
@RequiredArgsConstructor
public class APiMusicPlayerApplication  implements CommandLineRunner {
	private final UserRepository userRepository;
	private final SongRepository songRepository;
	private final PlaylistRepository playlistRepository;
	private final DetailPlaylistRepository detailPlaylistRepository;
	private final ISongService iSongService;

	public final static String authStringApiSongLink = "?authen=exp=1703080753~acl=/32d11d43dcadde252fceb3a2c24a46cd/*~hmac=fd279f5626bccb03a091e83bfa8cbe76";
	public static void main(String[] args) {
		SpringApplication.run(APiMusicPlayerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


//		crawlData("home");

//		User userAdmin = userRepository.findByUsername("ADMIN");
//		if( userAdmin != null) return;
//		User user = new User();
//		user.setUsername("ADMIN");
//		user.setPassword(Util.hashPassword("ADMIN"));
//
//		userRepository.save(user);

//		getData();
//		List<Song> songlist = iSongService.getAll();
//
//		for( Song song : songlist) {
//
//
//			try {
//				int duration  = getAudioDuration(song.getSongLink() + authStringApiSongLink);
//				System.out.println("Duration: " + duration + " seconds");
//				song.setDuration(duration);
//				songRepository.save(song);
//			} catch (IOException | CannotReadException | TagException | InvalidAudioFrameException e) {
//				// Handle exceptions
//				e.printStackTrace();
//				System.out.println("Error retrieving duration.");
//			}
//
//		}

	}



	public static String crawlData(String link){
		try {
			// Create URL object with the API endpoint
			URL url = new URL("http://localhost:3000/api/"+link);
			// Open connection to the API endpoint
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// Set request method
			connection.setRequestMethod("GET");
			// Get response code
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);
			// Read response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

//			saveExcel(response.toString());

			reader.close();



			// Print response
			// System.out.println("Response: " + response.toString());
			// Close connection
			connection.disconnect();
			return response.toString();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public void getData() {
		List<User> listUser = new ArrayList<User>();
		List<String> listPlaylistId = new ArrayList<String>();
		List<DetailPlaylist> listPlayListDetail = new ArrayList<DetailPlaylist>();
		List<Song> listSong = new ArrayList<Song>();


		String JSON = crawlData("home");

		if( JSON == null ) return;
		JSONObject jsonResponse = new JSONObject(JSON);
		JSONObject data = jsonResponse.getJSONObject("data");
		JSONArray items = data.getJSONArray("items");

		if (items == null) {
			return;
		}

		for (int i = 0; i < items.length(); i++) {
			System.out.println("\n-----------------------------------------\n");
			JSONObject item = items.getJSONObject(i);
			if (item.getString("sectionType").equals("playlist")) {
				JSONArray listPlaylistItem = item.getJSONArray("items");

				for( int j = 0; j < listPlaylistItem.length() ; j++ ) {
					listPlaylistId.add(listPlaylistItem.getJSONObject(j).optString("encodeId", null));
				}

			}
		}

		System.out.println(Arrays.toString(new List[]{listPlaylistId}));

		for( int i = 0; i < listPlaylistId.toArray().length ; i++ ) {
			String JSONDetailPlaylist = crawlData("/detailplaylist?id=" + listPlaylistId.get(i));

			JSONObject playlistJSON = new JSONObject(JSONDetailPlaylist).getJSONObject("data");
			Playlist playlist = new Playlist();
			DetailPlaylist detailPlaylist = new DetailPlaylist();

			playlist.setTitle(playlistJSON.optString("title", "null"));
			playlist.setThumbnails(playlistJSON.optString("thumbnail", "null"));

			JSONObject artistPlaylist = playlistJSON.getJSONObject("artist");

			User artistPlaylistInstance = new User();
			artistPlaylistInstance.setUsername(artistPlaylist.optString("name", "null"));
			artistPlaylistInstance.setAvatar(artistPlaylist.optString("thumbnail", "null"));
			artistPlaylistInstance.setPassword("ADMIN");

			User artistPlInstanceExist = userRepository.findByUsername(artistPlaylistInstance.getUsername());

			if( artistPlInstanceExist != null ){
				artistPlaylistInstance = artistPlInstanceExist;
			}else {
				artistPlaylistInstance = userRepository.save(artistPlaylistInstance);
			}

			System.out.println(artistPlaylistInstance.getUsername());
			playlist.setUser(artistPlaylistInstance);

			Playlist playlistExist = playlistRepository.findByTitle(playlist.getTitle());

			if( playlistExist == null ) {
				System.out.println(playlist.toString());
				playlistExist = playlistRepository.save(playlist);
			}

			Long idPlaylist = playlistExist.getId();



			Playlist existPlaylist = playlistRepository.findByTitle(playlist.getTitle());
			JSONArray artistArr = playlistJSON.getJSONArray("artists");
//
			for( int j = 0; j < artistArr.length() ; j ++ ) {
				JSONObject artist = artistArr.getJSONObject(j);
				System.out.println(artist);

				User user = new User();
				user.setUsername(artist.optString("name", ""));
				user.setPassword("ADMIN");
				user.setAvatar(artist.optString("thumbnail", ""));
				user.setTotalFollow(artist.optLong("totalFollow", 0));

				if( userRepository.findByUsername(user.getUsername()) == null ) {
					userRepository.save(user);
				}
			}

			JSONArray listSongArtist = playlistJSON.getJSONObject("song").getJSONArray("items");
			JSONArray genres = playlistJSON.getJSONArray("genres");

			for( int k = 0 ; k < listSongArtist.length() ; k ++ ) {
				JSONObject songJSON = listSongArtist.getJSONObject(k);
				Song song = new Song();
				song.setGenre(genres.optString(1, "v-pop"));
				song.setThumbnails(songJSON.optString("thumbnailM",""));
				song.setTitle(songJSON.optString("alias", ""));

//
//				JSONObject artistsSong = songJSON.getJSONArray("artists").getJSONObject(0);
				if (songJSON.has("artists") && songJSON.getJSONArray("artists").length() > 0) {
					JSONObject artistsSong = songJSON.getJSONArray("artists").getJSONObject(0);
					System.out.println(artistsSong);
					User userSong = new User();
					userSong.setUsername(artistsSong.getString("name"));
					userSong.setAvatar(artistsSong.getString("thumbnailM"));



					User userexit = userRepository.findByUsername(userSong.getUsername());
					if( userexit!= null ) {
						userSong = userexit;
					}else {
						userSong = userRepository.save(userSong);
					}

					Song songExist = songRepository.findByTitle(song.getTitle());
					if( songExist == null ) {
						System.out.println("start add song");

						song.setUserUpload(userSong);
						String jsonDataSongDetail = crawlData("/song?id="+songJSON.getString("encodeId"));
						JSONObject detailSong = new JSONObject(jsonDataSongDetail);
						if (detailSong.has("data")) {
							JSONObject dataJSON = detailSong.getJSONObject("data");
							song.setDuration(detailSong.getLong("timestamp"));
							song.setSongLink(dataJSON.getString("128"));



							Song newSong = songRepository.save(song);
							DetailPlaylist detailPlaylist1 = new DetailPlaylist();
							detailPlaylist1.setPlaylist(playlist);
							detailPlaylist1.setSong(newSong);

							detailPlaylistRepository.save(detailPlaylist1);

						}

					}else {
//						System.out.println(songExist.getTitle());
						System.out.println("--------------------------------------------/n");
					}


				}

//

			}
			System.out.println("TEst" + i);
		}
	}

	public static void saveExcel(String respon) {

		try {
			// Parse JSON string to a JSONObject
			JSONObject jsonResponse = new JSONObject(respon);
			JSONObject data = jsonResponse.getJSONObject("data");

			JSONArray items = data.getJSONArray("items");
			if (items == null) {
				System.out.println(items);
				return;
			}

			// Create a new Excel workbook and sheets
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Sheet1");

			// Write header row for playlist items
			Row headerRow = sheet.createRow(0);
			Cell cell = headerRow.createCell(0);
			cell.setCellValue("encodeId");
			cell = headerRow.createCell(1);
			cell.setCellValue("thumbnail");
			cell = headerRow.createCell(2);
			cell.setCellValue("thumbnailM");
			cell = headerRow.createCell(3);
			cell.setCellValue("link");
			cell = headerRow.createCell(4);
			cell.setCellValue("title");
			cell = headerRow.createCell(5);
			cell.setCellValue("sortDescription");

			// Write data rows for playlist items and artist items
			int rowIndex = 1; // Row index for both playlist and artist items

			for (int i = 0; i < items.length(); i++) {
				System.out.println("\n-----------------------------------------\n");
				JSONObject item = items.getJSONObject(i);
				System.out.println(item.getString("sectionType"));
				if (item.getString("sectionType").equals("playlist")) {
					JSONArray arrayPlaylist = item.getJSONArray("items");

					for (int j = 0; j < arrayPlaylist.length(); j++) {
						JSONObject subItem = arrayPlaylist.getJSONObject(j);

						Row row = sheet.createRow(rowIndex++);
						cell = row.createCell(0);
						cell.setCellValue(subItem.getString("encodeId"));
						cell = row.createCell(1);
						cell.setCellValue(subItem.getString("thumbnail"));
						cell = row.createCell(2);
						cell.setCellValue(subItem.getString("thumbnailM"));
						cell = row.createCell(3);
						cell.setCellValue(subItem.getString("link"));
						cell = row.createCell(4);
						cell.setCellValue(subItem.getString("title"));
						cell = row.createCell(5);
						cell.setCellValue(subItem.getString("sortDescription"));

						JSONArray artistArr = subItem.getJSONArray("artists");

						for (int k = 0; k < artistArr.length(); k++) {
							JSONObject artists = artistArr.getJSONObject(k);
							System.out.println(artists);

							row = sheet.createRow(rowIndex++);
							cell = row.createCell(0);
							cell.setCellValue("                       ");
							cell = row.createCell(1);
							cell.setCellValue(artists.optString("id", "null"));
							cell = row.createCell(2);
							cell.setCellValue(artists.optString("name"));
							cell = row.createCell(3);
							cell.setCellValue(artists.optString("thumbnail"));
							cell = row.createCell(4);
							cell.setCellValue(artists.optString("thumbnailM"));
							cell = row.createCell(5);
							cell.setCellValue(artists.optString("playlistId", "null"));
							cell = row.createCell(6);
							cell.setCellValue(artists.optString("totalFollow", "null"));
						}
					}
				}
			}

			// Write the workbook to a file
			FileOutputStream outputStream = new FileOutputStream("output.xlsx");
			workbook.write(outputStream);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}


}
