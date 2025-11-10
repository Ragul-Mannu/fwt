package com.tracker;

import com.tracker.model.User;
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
        System.out.println("✅ Twilio initialized successfully!");
    }

    /**
     * Send SMS notification to a specific user.
     *
     * @param itemName   Food item name
     * @param expiryDate Expiry date string
     * @param user       User object (for personalized phone number)
     */
    public void sendNotification(String itemName, String expiryDate, User user) {
        if (user == null || user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            System.out.println("⚠️ Skipping SMS: user phone number not available");
            return;
        }

        String messageText = "⚠️ Reminder: Your food item '" + itemName + "' will expire on " + expiryDate;
        System.out.println("📤 Sending SMS to " + user.getPhoneNumber() + ": " + messageText);

        try {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(user.getPhoneNumber()), // recipient
                    new com.twilio.type.PhoneNumber(twilioConfig.getFromNumber()), // sender
                    messageText
            ).create();

            System.out.println("✅ SMS sent successfully! SID: " + message.getSid());
        } catch (Exception e) {
            System.out.println("❌ Failed to send SMS: " + e.getMessage());
        }
    }
}
