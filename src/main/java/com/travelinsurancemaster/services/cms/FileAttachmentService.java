package com.travelinsurancemaster.services.cms;

import com.travelinsurancemaster.model.dto.cms.attachment.FileAttachment;
import com.travelinsurancemaster.repository.cms.FileAttachmentRepository;
import com.travelinsurancemaster.util.SecurityHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Chernov Artur on 29.07.15.
 */

@Service
@Transactional(readOnly = true)
public class FileAttachmentService {
    private static final Logger log = LoggerFactory.getLogger(FileAttachmentService.class);

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    public FileAttachment getFileAttachment(Long id) {
        FileAttachment file = fileAttachmentRepository.findOne(id);
        file.getContent();
        return file;
    }

    public FileAttachment getFileAttachmentWithContent(String uid) {
        FileAttachment file = fileAttachmentRepository.findByFileUid(uid);
        file.getContent();
        return file;
    }

    public List<FileAttachment> getAll() {
        return fileAttachmentRepository.findByDeleted(false);
    }

    @Transactional(readOnly = false)
    public FileAttachment save(FileAttachment attachment) {
        FileAttachment newAttachment;
        attachment.setModifiedDate(new Date());
        if (attachment.getId() != null && (newAttachment = fileAttachmentRepository.findOne(attachment.getId())) != null) {
            newAttachment.setName(attachment.getName());
            newAttachment.setModifiedDate(attachment.getModifiedDate());
            if (attachment.getContent() == null) {
                return fileAttachmentRepository.save(newAttachment);
            }
            newAttachment.setFileName(attachment.getFileName());
            newAttachment.setContent(attachment.getContent());
            newAttachment.setSize(attachment.getSize());
            newAttachment.setMimeType(attachment.getMimeType());
            return fileAttachmentRepository.save(newAttachment);
        }
        attachment.setFileUid(UUID.randomUUID().toString());
        attachment.setCreateDate(new Date());
        attachment.setAuthor(SecurityHelper.getCurrentUser());
        return fileAttachmentRepository.save(attachment);
    }

    @Transactional(readOnly = false)
    public void delete(Long id) {
        FileAttachment attachment = fileAttachmentRepository.findOne(id);
        attachment.setDeleted(true);
        save(attachment);
    }

    public byte[] getFile(String fileUid) {
        if (StringUtils.isEmpty(fileUid)) {
            return null;
        }
        FileAttachment attachment = getFileAttachmentWithContent(fileUid);
        if (attachment == null) {
            return null;
        }
        return attachment.getContent();
    }

    @Transactional(readOnly = false)
    public FileAttachment updateFile(Long id, byte[] bytes) {
        if (id == null || bytes == null) {
            return null;
        }
        FileAttachment attachment = getFileAttachment(id);
        if (attachment == null) {
            return null;
        }
        attachment.setContent(bytes);
        return save(attachment);
    }

}
