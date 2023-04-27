package zerobase.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {
	
	@Bean
	public OpenAPI api() {
		Info info = new Info().title("날씨 일기 프로젝트").version("1.0").description("날씨 일기를 CRUD 할 수 있는 백앤드 API 입니다");
		
		return new OpenAPI().components(new Components()).info(info);
	}
}
