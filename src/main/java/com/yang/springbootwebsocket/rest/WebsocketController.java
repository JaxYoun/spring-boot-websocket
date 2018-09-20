package com.yang.springbootwebsocket.rest;

import com.yang.springbootwebsocket.config.WebsocketServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author: Yang
 * @date: 2018/9/20 23:07
 * @description:
 */
@RestController
@RequestMapping("/websocket")
public class WebsocketController {

    @GetMapping("/send")
    public Object send(String id) {
        try {
            new WebsocketServer().sendInfo("biz", "entiy", "-----------");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "SUCCESS";
    }

}
