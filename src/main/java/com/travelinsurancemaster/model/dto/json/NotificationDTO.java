package com.travelinsurancemaster.model.dto.json;

import java.io.Serializable;

/**
 * @author Artur Chernov
 */
public class NotificationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private NotificationColor color;

    public NotificationDTO() {
    }

    public NotificationDTO(String message, NotificationColor color) {
        this.message = message;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationColor getColor() {
        return color;
    }

    public void setColor(NotificationColor color) {
        this.color = color;
    }
}
