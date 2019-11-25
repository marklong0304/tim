package com.travelinsurancemaster.repository.cms;

import com.travelinsurancemaster.model.dto.cms.attachment.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Chernov Artur on 29.07.15.
 */

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
    List<FileAttachment> findByName(String name);
    FileAttachment findByFileUid(String uid);
    List<FileAttachment> findByDeleted(boolean deleted);
}
