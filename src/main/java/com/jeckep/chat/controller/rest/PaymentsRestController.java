package com.jeckep.chat.controller.rest;

import com.jeckep.chat.util.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jeckep on 11.12.16.
 */

@Slf4j
@RestController
public class PaymentsRestController {
    @Value("${yandex.money.secret}")
    private String secret;

    public static final String VERIFY_SRING_FORMAT = "notification_type&operation_id&amount&currency&datetime&sender&codepro&notification_secret&label";

    @PostMapping(Path.Web.PAYMENTS + "callback")
    public String callback(@RequestParam(required = false) String withdraw_amount
                         , @RequestParam(required = false) String notification_type
                         , @RequestParam(required = false) String operation_id
                         , @RequestParam(required = false) String amount
                         , @RequestParam(required = false) String currency
                         , @RequestParam(required = false) String datetime
                         , @RequestParam(required = false) String sender
                         , @RequestParam(required = false) String codepro
                         , @RequestParam(required = false) String label
                         , @RequestParam(required = false) String sha1_hash
    ){

        if(isValid(notification_type, operation_id, amount, currency, datetime, sender, codepro, label, sha1_hash)){
            log.info("Payment is valid.");
        }else{
            throw new RuntimeException("Payment callback is not valid");
        }

        log.info("label=" + label
                + "\n" + "amount=" + amount
                + "\n" + "withdraw_amount=" + withdraw_amount
                + "\n" + "datetime=" + datetime
                + "\n" + "operation_id=" + operation_id);


        return null;
    }

    private boolean isValid(String notification_type, String operation_id, String amount, String currency, String datetime,
                            String sender, String codepro, String label, String sha1_hash) {

        final String data =
                notification_type + "&"
                + operation_id + "&"
                + amount + "&"
                + currency + "&"
                + datetime + "&"
                + sender + "&"
                + codepro + "&"
                + secret + "&"
                + label
                ;

        final String hex = DigestUtils.sha1Hex(data);

        return sha1_hash.equals(hex);
    }

}
