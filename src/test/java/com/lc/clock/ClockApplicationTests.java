package com.lc.clock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@SpringBootTest
class ClockApplicationTests {

    public static void main(String[] args) {
        SpringApplication.run(ClockApplication.class, args);
    }


}


