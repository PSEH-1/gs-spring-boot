package hello;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class HelloController {

	private static ObjectMapper objMapper = new ObjectMapper();

	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@RequestMapping(value = "/forecast", method = RequestMethod.GET)
	public Forecast addStudent() throws IOException {
		return getForeCast("london");
	}

	private static Forecast getForeCast(String city) throws IOException {
		final String uri = "http://api.openweathermap.org/data/2.5/forecast?q=" + city
				+ ",us&mode=json&appid=d2929e9483efc82c82c32ee7e02d563e";

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject(uri, String.class);
		JsonNode jsonNode = objMapper.readTree(result).get("list");
		JsonNode weatherNode;
		Forecast forecast = new Forecast();
		List<Day> days = new ArrayList<Day>();
		forecast.setForecasts(days);
		if (jsonNode.isArray()) {
			int indexCount = 1;
			Float min_temp = Float.MAX_VALUE;
			Float max_temp = Float.MIN_VALUE;
			Float temp;
			boolean isRain = false;
			for (final JsonNode objNode : jsonNode) {
				if (indexCount > 24) {
					break;
				}
				System.out.println(objNode);
				JsonNode main = objNode.get("main");
				temp = Float.valueOf(main.get("temp_min").toString());
				if (temp < min_temp) {
					min_temp = temp;
				}
				temp = Float.valueOf(main.get("temp_max").toString());
				if (temp > max_temp) {
					max_temp = temp;
				}
				weatherNode = objNode.get("weather");
				for (final JsonNode weather : weatherNode) {
					if (weather.get("main").toString().equals("Rain")) {
						isRain = true;
					}
				}

				if (indexCount++ % 8 == 0) {
					Day day = new Day();
					day.setMaxTemp(max_temp.toString());
					day.setMinTemp(min_temp.toString());
					if (max_temp > 40 + 273.15)
						day.setMessage("Use sunscreen lotion");
					if (isRain)
						day.setMessage("Carry umbrella");
					days.add(day);
				}
			}

		}

		System.out.println(result);
		return forecast;
	}
}
