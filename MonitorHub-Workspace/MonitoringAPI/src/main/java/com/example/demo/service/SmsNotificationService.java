package com.example.demo.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private final String TWILIO_NUMBER;

    // 1. Initialize Twilio ONCE via constructor injection when the application boots up
    public SmsNotificationService(
            @Value("${twilio.account.sid}") String accountSid,
            @Value("${twilio.auth.token}") String authToken,
            @Value("${twilio.phone.number}") String twilioNumber) {
        
        this.TWILIO_NUMBER = twilioNumber;
        
        try {
            Twilio.init(accountSid, authToken);
            System.out.println("🔑 Twilio SMS client successfully initialized.");
        } catch (Exception e) {
            System.err.println("❌ Failed to initialize Twilio client core: " + e.getMessage());
        }
    }

    public void sendSms(String toPhoneNumber, String body) {
        if (toPhoneNumber == null || toPhoneNumber.trim().isEmpty()) {
            System.err.println("⚠️ Skipping SMS: Target phone number is empty.");
            return;
        }

        // Twilio requires phone numbers to be in E.164 format (e.g., +1234567890)
        String formattedToNumber = toPhoneNumber.startsWith("+") ? toPhoneNumber : "+" + toPhoneNumber;

        try {
            Message message = Message.creator(
                new PhoneNumber(formattedToNumber), // Recipient
                new PhoneNumber(TWILIO_NUMBER),     // Sender (Your Twilio number)
                body)
                .create();

            System.out.println("✅ SMS sent successfully to " + formattedToNumber + ". SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("❌ Failed to send SMS to " + formattedToNumber + ": " + e.getMessage());
        }
    }
}