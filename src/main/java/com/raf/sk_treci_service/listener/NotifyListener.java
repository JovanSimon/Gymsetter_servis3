package com.raf.sk_treci_service.listener;

import com.raf.sk_treci_service.listener.helper.MessageHelper;
import org.springframework.stereotype.Component;

@Component
public class NotifyListener {
    private MessageHelper messageHelper;

    public NotifyListener(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

}
