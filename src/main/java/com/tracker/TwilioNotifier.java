package com.tracker;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioNotifier {

    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioNotifier(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    public void sendNotification(String itemName, String expiryDate) {
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(twilioConfig.getDefaultRecipient()),
                new com.twilio.type.PhoneNumber(twilioConfig.getFromNumber()),
                "⚠️ Reminder: " + itemName + " is expiring on " + expiryDate
        ).create();

        System.out.println("SMS sent: " + message.getSid());
    }
}
