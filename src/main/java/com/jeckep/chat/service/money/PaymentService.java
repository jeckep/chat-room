package com.jeckep.chat.service.money;

import com.jeckep.chat.domain.money.Payment;
import com.jeckep.chat.domain.money.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jeckep on 13.12.16.
 */

@Service
@Transactional
public class PaymentService {

    @Autowired
    PaymentRepository rep;

    public Payment create(int userId, long amount){
        return rep.saveAndFlush(new Payment(userId, amount, false));
    }
}
