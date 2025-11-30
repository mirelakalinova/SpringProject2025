package com.example.mkalinova.app.scheduler;

import com.example.mkalinova.app.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class OrderCleanUp {
	
	
	private static final Logger log = LoggerFactory.getLogger(OrderCleanUp.class);
	private final OrderService orderService;
	
	public OrderCleanUp(OrderService orderService) {
		this.orderService = orderService;
	}
	
	
	@Scheduled(cron = "${app.scheduling.cleanup.cron}")
		public void runCleanupJob() {
			try {
				LocalDateTime limit = LocalDateTime.now().minusDays(30);
				log.info("Starting cleanup scheduled job for orders before {}", limit);
				int deleted = orderService.cleanOrder(limit);
				log.info("Cleanup scheduled job finished - deleted {} orders", deleted);
			} catch (Exception ex) {
				log.error("Cleanup scheduled job failed", ex);
			}
		}
	
	
}
