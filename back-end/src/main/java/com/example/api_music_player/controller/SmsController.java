package com.example.api_music_player.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@RestController
@RequestMapping("/api/v1/OTP")
public class SmsController {
    @GetMapping("/sendSMS")
    public ResponseEntity<String> sendSMS() {

        Twilio.init("ACf7b929e87ee897894bc1ac4200ec51c0", "41a03c39e60414b80940e941a7aed7bf");

        Message.creator(
                new com.twilio.type.PhoneNumber("+84376100548"),
                new com.twilio.type.PhoneNumber("+12065576352"),
                "Mã OTP lấy lại mật khẩu của bạn là : ")
                .create();

        return new ResponseEntity<String>("Message sent successfully", HttpStatus.OK);
    }
}
