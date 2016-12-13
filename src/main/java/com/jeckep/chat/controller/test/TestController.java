package com.jeckep.chat.controller.test;

import com.jeckep.chat.domain.money.Payment;
import com.jeckep.chat.service.money.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jeckep on 13.12.16.
 */

@Slf4j
@RestController
public class TestController {

    @Autowired
    PaymentService paymentService;

    @GetMapping("createpayment")
    public void createpayment(){
        Payment p = paymentService.create(1, 100);
        System.out.println();
    }
}
