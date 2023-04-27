package zerobase.weather.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mariadb.jdbc.internal.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

@Service
public class DiaryService {
	
	@Value("${openweathermap.key}")
	private String apiKey;
	
	private final DiaryRepository diaryRepository;
	
	private final DateWeatherRepository dateWeatherRepository;
	
	private static final org.mariadb.jdbc.internal.logging.Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
	
	public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
		this.diaryRepository = diaryRepository;
		this.dateWeatherRepository = dateWeatherRepository;
	}
	
	@Transactional
	@Scheduled(cron = "0 0 1 * * *")
	public void saveWeatherDate() {
		dateWeatherRepository.save(getWeatherFromApi());
	}
	
	private DateWeather getWeatherFromApi() {
		//open weather map에서 날씨 데이터 가져오기
		String weatherData = getWeatherString();
		// 받아온 날씨 json 파싱하기
		Map<String, Object> parseWeather = parseWeather(weatherData);
		//파싱된 데이터 + 일기 값 우리 db 에 넣기
		DateWeather dateWeather = new DateWeather();
		dateWeather.setDate(LocalDate.now());
		dateWeather.setWeather(parseWeather.get("main").toString());
		dateWeather.setIcon(parseWeather.get("icon").toString());
		dateWeather.setTemperature((Double) parseWeather.get("temp"));
		return dateWeather;
	}
	
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public void createDiary(LocalDate date, String text) {
		logger.info("started to create diary");
		//날씨 데이터 가져오기
		DateWeather dateWeather = getDateWeather(date);
		//파싱된 데이터 + 일기 값 우리 db 에 넣기
		Diary nowDiary = new Diary();
		nowDiary.setDateWeather(dateWeather);
		nowDiary.setText(text);
		nowDiary.setDate(date);
		diaryRepository.save(nowDiary);
		logger.info("end to create diary");
	}
	
	private DateWeather getDateWeather(LocalDate localDate) {
		List<DateWeather> dateWeahterListFromDB = 
				dateWeatherRepository.findAllByDate(localDate);
		if(dateWeahterListFromDB.size() == 0) {
			return getWeatherFromApi();
		} else {
			return dateWeahterListFromDB.get(0);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Diary> readDiary(LocalDate date) {
		logger.debug("read diary");
		return diaryRepository.findAllByDate(date);
	}
	
	public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
		return diaryRepository.findAllByDateBetween(startDate, endDate);
	}
	
	public void updateDiary(LocalDate date, String text) {
		Diary nowDiary = diaryRepository.getFirstByDate(date);
		nowDiary.setText(text);
		diaryRepository.save(nowDiary);
	}
	
	public void deleteDiary(LocalDate date) {
		diaryRepository.deleteAllByDate(date);
	}
	
	private String getWeatherString() {
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid="
		+ apiKey;
		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			BufferedReader br;
			if(responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			} else {
				br = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			}
			String inputLine;
			StringBuilder response = new StringBuilder();
			while((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			
			return response.toString();
		} catch(Exception e) {
			return "failed to get response";
		}
	}
	
	private Map<String, Object> parseWeather(String jsonString) {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject;
		
		try {
			jsonObject = (JSONObject) jsonParser.parse(jsonString);
		} catch(ParseException e) {
			throw new RuntimeException(e);
		}
		Map<String, Object> resultMap = new HashMap<>();
		JSONObject mainData = (JSONObject) jsonObject.get("main");
		resultMap.put("temp", mainData.get("temp"));
		JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
		JSONObject weatherData = (JSONObject) weatherArray.get(0);
		resultMap.put("main", weatherData.get("main"));
		resultMap.put("icon", weatherData.get("icon"));
		return resultMap;
	}
}
