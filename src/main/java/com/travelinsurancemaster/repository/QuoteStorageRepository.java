package com.travelinsurancemaster.repository;

import com.travelinsurancemaster.model.dto.QuoteStorage;
import com.travelinsurancemaster.model.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by Chernov Artur on 06.05.15.
 */

@Repository
@Transactional
public interface QuoteStorageRepository extends JpaRepository<QuoteStorage, String>, JpaSpecificationExecutor<QuoteStorage> {

    QuoteStorage findByIdAndDeletedFalse(String id);

    List<QuoteStorage> findByDeletedFalseAndSavedFalseAndCreateDateBefore(Date date);

    QuoteStorage findTop1ByUidAndDeletedFalseOrderByCreateDateDesc(String uid);

    QuoteStorage findTop1ByUserAndDeletedFalseOrderByCreateDateDesc(User user);

    List<QuoteStorage> findAllByDeletedFalseAndSavedTrueAndUserOrderByCreateDateDesc(User user);

    List<QuoteStorage> findAllByDeletedFalseAndSavedTrueAndUserAndCreateDateGreaterThanEqualAndCreateDateLessThanEqualOrderByCreateDateDesc(User user, Date dateFrom, Date dateTo);

    List<QuoteStorage> findAllByDeletedFalseAndSavedTrueAndUserAndCreateDateGreaterThanEqualOrderByCreateDateDesc(User user, Date dateFrom);

    List<QuoteStorage> findAllByDeletedFalseAndSavedTrueAndUserAndCreateDateLessThanEqualOrderByCreateDateDesc(User user, Date dateTo);

    @Modifying
    @Query(value="UPDATE quote_storage SET quote_request_json = "
            + " CASE"
            + "  WHEN CAST(quote_request_json AS json)#>CAST(?2 AS text[]) IS NULL"
            + "  THEN jsonb_set(CAST(quote_request_json AS jsonb), CAST(?2 AS text[]), CAST(?5 AS jsonb))"
            + "  ELSE jsonb_set(CAST(quote_request_json AS jsonb), CAST(?3 AS text[]), CAST(?4 AS jsonb))"
            + " END"
            + " WHERE id = ?1"
            , nativeQuery = true)
    void insertVendorPolicyMap(String quoteId, String path, String pathWithPolicy, String mapJson, String mapWithPolicyJson);
}