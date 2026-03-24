package com.project.ecommerce.configuration;

import com.project.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MailTemplate {

    public String verificationCode(String code, long minutes) {
        return """
                Your verification code is: %s
                This code will expire in %d minutes.
                """.formatted(code, minutes);
    }

    public String forgotPassword(String code, long minutes) {
        return """
                Reset password code: %s
                This code will expire in %d minutes.
                """.formatted(code, minutes);
    }

    public String orderSuccess(String customerName,
                               List<OrderItem> items,
                               BigDecimal total) {

        String itemText = items.stream()
                .map(i -> "- %s x %d".formatted(
                        i.getProduct().getName(),
                        i.getQuantity()))
                .collect(Collectors.joining("\n"));

        return """
                Hello %s,

                Your order has been placed successfully!

                Items:
                %s

                Total amount: %s

                Thank you for shopping with us!
                """.formatted(customerName, itemText, total);
    }
}
