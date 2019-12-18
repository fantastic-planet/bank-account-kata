package fr.ekinci.katabank.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TransactionIdGenerator {
	public String generate() {
		return UUID.randomUUID().toString();
	}
}
