package ru.practicum.stats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class StatsApp {

	public static void main(String[] args) {
		SpringApplication.run(StatsApp.class, args);
		log.warn("     ***********************************  Запущен Stats  ***********************************     ");
	}

}
