package com.jeckep.chat.controller;

import com.jeckep.chat.util.Path;
import com.jeckep.chat.util.ViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Controller
public class PaymentsController {
    private static final String FORM_URL = "https://money.yandex.ru/quickpay/shop-widget?account=410013589656448&quickpay=shop&payment-type-choice=on&mobile-payment-type-choice=on&writer=seller&button-text=01&comment=on&successURL=jeckep.online";

    @GetMapping(Path.Web.PAYMENTS)
    public String index(Map<String, Object> model, HttpServletRequest request) throws UnsupportedEncodingException {
        final String formUrl = FORM_URL
                 + "&sum=321"
                + "&default-sum=123"
                + "&label=12345678"
                + "&targets=" + URLEncoder.encode("На шлюх и наркотики Женьку", "UTF8")
                + "&hint=" + URLEncoder.encode("Впишите сюда что-нибудь", "UTF8")
                ;
        model.put("nav_active", "payments");
        model.put("formUrl", formUrl);
        ViewUtil.putLayoutVars(request, model);
        return Path.Template.PAYMENTS;
    }
}
