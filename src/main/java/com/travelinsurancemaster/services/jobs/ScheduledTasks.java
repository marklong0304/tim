package com.travelinsurancemaster.services.jobs;

import com.travelinsurancemaster.model.dto.QuoteStorage;
import com.travelinsurancemaster.repository.QuoteStorageRepository;
import com.travelinsurancemaster.services.PurchaseService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Chernov Artur on 01.06.15.
 */
@Service
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Value("${schedule.quote.timeToLive.inDays}")
    private int quoteLiveDuration;

    @Autowired
    private QuoteStorageRepository quoteStorageRepository;

    @Autowired
    private PurchaseService purchaseService;

    /**
     * Set flag to deleted for old quotes
     */
    @Scheduled(cron = "${schedule.quote.cron.expression}")
    public void removeOldQuotes() {
        log.debug("job start: set deleted for old quotes");
        DateTime dateTime = new DateTime().minusDays(quoteLiveDuration);
        List<QuoteStorage> quoteStorages = quoteStorageRepository.findByDeletedFalseAndSavedFalseAndCreateDateBefore(dateTime.toDate());
        for (QuoteStorage quoteStorage : quoteStorages) {
            quoteStorage.setDeleted(true);
            quoteStorageRepository.save(quoteStorage);
        }
    }

    @Scheduled(cron = "${schedule.trip.end.cron.expression}")
    public void endTripEmail() {
        log.debug("job start: endTripEmail");
        purchaseService.sendEndTripEmails();
    }
}