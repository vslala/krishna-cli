package org.containers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PythonContainerRunnerTest {
    String pythonCode = """
            import requests
                        
            # OpenWeatherMap API Key
            api_key = '46ed2f214d4e499619d261f4caf99ba1'
                        
            # Consett city information
            city_name = 'Consett'
            country_code = 'GB'
                        
            # API endpoint URL
            url = f'http://api.openweathermap.org/data/2.5/weather?q={city_name},{country_code}&appid={api_key}&units=metric'
                        
            # Send the request
            response = requests.get(url)
                        
            # Check if the request was successful
            if response.status_code == 200:
                data = response.json()
               \s
                # Extract relevant weather information
                weather = data['weather'][0]['description']
                temperature = data['main']['temp']
                humidity = data['main']['humidity']
                wind_speed = data['wind']['speed']
               \s
                # Print the weather details
                print(f'Weather in {city_name}, {country_code}:')
                print(f'Conditions: {weather}')
                print(f'Temperature: {temperature}°C')
                print(f'Humidity: {humidity}%')
                print(f'Wind Speed: {wind_speed} m/s')
            else:
                print('Failed to fetch weather data.')
            """;
    private PythonContainerRunner containerRunner;

    @BeforeEach
    void beforeEach() {
        containerRunner = new PythonContainerRunner();
    }

    @Test
    void should_copy_requirements_and_python_files_in_the_docker_directory() {
        String requirements = "requests~=2.22.0";
//        String pythonCode = "print('Hello World!')";

        boolean result = containerRunner.build(requirements, pythonCode);

        assertTrue(result);
    }

    @Test
    void should_run_the_container_image() {
        String consoleOutput = containerRunner.run();

        System.out.println(consoleOutput);
        assertNotNull(consoleOutput);
    }
}
