package com.example.mkalinova.app.order.service;

import java.util.UUID;


public interface PdfService {
	byte[] generateOrderPdf(UUID id);
}
