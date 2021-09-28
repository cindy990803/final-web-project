package com.project.bokduck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import net.nurigo.java_sdk.api.Message;
import org.springframework.stereotype.Service;


import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    public void certifiedPhoneNumber(String tel, String cerNum) {

        String api_key = "NCS3FIWDKUN7BOFH";
        String api_secret = "VUJPRWWWIUT3JZEQARBBSYIZRYHNV2B3";
        Message coolsms = new Message(api_key, api_secret);

        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", tel);    // 수신전화번호"
        params.put("from", "01052586394" );    // 발신전화번호. 본인 번호
        params.put("type", "SMS");
        params.put("text", "[북덕북덕] 인증번호는" + "["+cerNum+"]를 입력해주세요");
        params.put("app_version", "test app 1.2"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }
}
