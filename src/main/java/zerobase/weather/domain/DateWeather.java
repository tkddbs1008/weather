package zerobase.weather.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name ="date_weather")
public class DateWeather {
	@Id
	private LocalDate date;
	private String weather;
	private String icon;
	private double temperature;
	
}
