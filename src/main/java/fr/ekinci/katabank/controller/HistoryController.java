package fr.ekinci.katabank.controller;

import fr.ekinci.katabank.model.History;
import fr.ekinci.katabank.service.HistoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
class HistoryController {
	private final HistoryService historyService;

	HistoryController(HistoryService historyService) {
		this.historyService = historyService;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/history")
	Mono<History> getHistory() {
		return historyService.getHistory();
	}
}
