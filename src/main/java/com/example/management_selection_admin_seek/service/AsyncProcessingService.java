package com.example.management_selection_admin_seek.service;

import com.example.management_selection_admin_seek.entity.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Simple Async Processing Service
 * Handles background tasks for client operations
 */
@Service
@Slf4j
public class AsyncProcessingService {

    /**
     * Process new client registration asynchronously
     * Simulates background tasks like notifications, reports, etc.
     */
    @Async("taskExecutor")
    public void processNewClient(Client client) {
        log.info("🚀 [ASYNC] Starting background processing for client: {} {}", 
                 client.getName(), client.getLastName());
        
        try {
            log.info("📧 [ASYNC TASK] Sending welcome notification to: {}", 
                     client.getName() + " " + client.getLastName());
            Thread.sleep(100);
            log.info("✅ [ASYNC TASK] Welcome notification sent successfully");
            
            log.info("📊 [ASYNC TASK] Generating initial client report");
            String clientSummary = generateClientSummary(client);
            Thread.sleep(150);
            log.info("✅ [ASYNC TASK] Client report generated: {}", clientSummary);

            log.info("📈 [ASYNC TASK] Updating system statistics");
            Thread.sleep(50);
            log.info("✅ [ASYNC TASK] System statistics updated");
            
            log.info("🎉 [ASYNC] All background processing completed for client: {}", client.getId());
            
        } catch (Exception e) {
            log.error("❌ [ASYNC] Error processing client {}: {}", client.getId(), e.getMessage());
        }
    }
    
    /**
     * Generate a simple client summary for the report
     */
    private String generateClientSummary(Client client) {
        int currentYear = java.time.LocalDate.now().getYear();
        int birthYear = client.getBirthDate().getYear();
        int calculatedAge = currentYear - birthYear;
        
        return String.format("Client[ID=%d, Name=%s %s, Age=%d, Generation=%s]", 
                client.getId(),
                client.getName(), 
                client.getLastName(),
                calculatedAge,
                getGeneration(calculatedAge));
    }
    
    /**
     * Determine generation based on age
     */
    private String getGeneration(int age) {
        if (age >= 18 && age <= 28) return "Gen-Z";
        if (age >= 29 && age <= 43) return "Millennial"; 
        if (age >= 44 && age <= 58) return "Gen-X";
        return "Boomer+";
    }
}
