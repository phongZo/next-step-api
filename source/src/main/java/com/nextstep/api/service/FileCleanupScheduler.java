package com.nextstep.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileCleanupScheduler {

    @Autowired
    private FileService fileService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupTemporaryFolders() {
        log.info("Starting scheduled cleanup of temporary folders...");
        try {
            fileService.cleanupTemporaryFolders();
            log.info("Completed scheduled cleanup of temporary folders");
        } catch (Exception e) {
            log.error("Error during scheduled cleanup of temporary folders: {}", e.getMessage(), e);
        }
    }
} 