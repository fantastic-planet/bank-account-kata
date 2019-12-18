package fr.ekinci.katabank.service;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class TimeService {

	public LocalDateTime currentLocalDateTime() {
		return LocalDateTime.now();
	}
}
