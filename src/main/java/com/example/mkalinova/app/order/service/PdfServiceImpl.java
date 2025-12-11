package com.example.mkalinova.app.order.service;

import com.example.mkalinova.app.exception.NoSuchResourceException;
import com.example.mkalinova.app.order.data.entity.Order;
import com.example.mkalinova.app.order.repo.OrderRepository;
import com.example.mkalinova.app.orderPart.data.OrderPart;
import com.example.mkalinova.app.orderPart.service.OrderPartService;
import com.example.mkalinova.app.orderRepair.data.OrderRepair;
import com.example.mkalinova.app.orderRepair.service.OrderRepairService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService {
	private final SpringTemplateEngine templateEngine;
	private final OrderRepository orderRepository;
	private final OrderPartService orderPartService;
	private final OrderRepairService orderRepairService;
	
	
	public PdfServiceImpl(SpringTemplateEngine templateEngine, OrderRepository orderRepository, OrderPartService orderPartService, OrderRepairService orderRepairService) {
		this.templateEngine = templateEngine;
		this.orderRepository = orderRepository;
		this.orderPartService = orderPartService;
		this.orderRepairService = orderRepairService;
	}
	
	
	@Override
	public byte[] generateOrderPdf(UUID id) {
		log.info("Attempt to generate pdf order with id:" + id);
		Optional<Order> order = orderRepository.findById(id);
		if(order.isEmpty()){
			throw new NoSuchResourceException("Няма намерена поръчка с #" + id);
		}
		List<OrderPart> partList = orderPartService.findAllByOrderId(id);
		List<OrderRepair> repairList = orderRepairService.findAllByOrderId(id);
		Context context = new Context();
		context.setVariable("order", order.get());
		context.setVariable("partList", partList);
		context.setVariable("repairList", repairList);
		
		String htmlContent = templateEngine.process("order/pdf", context);
		
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ClassPathResource resource = new ClassPathResource("static/css/font/RobotoCondensed-Regular.ttf");
			File fontFile =  resource.getFile();
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.withHtmlContent(htmlContent, null);
			builder.useFont(fontFile, "Roboto Condensed");
			
			builder.toStream(os);
			builder.run();
			log.info("Successfully generated pdf order with id:" + id);
			return os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Unsuccessfully generated pdf order with id:" + id);
			throw new RuntimeException("Грешка при генериране на PDF", e);
		}
	}
}
