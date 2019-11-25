package com.travelinsurancemaster.model.dto;

import com.travelinsurancemaster.model.security.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

/**
 * Created by Chernov Artur on 26.08.15.
 */

@Embeddable
public class AffiliateLinkPurchaser implements Serializable {
    private static final long serialVersionUID = -5210503587866807832L;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
