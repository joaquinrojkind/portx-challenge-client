package com.portx.clientapp.service;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.portx.clientapp.client.PortXPaymentApiClient;
import com.portx.clientapp.client.model.Account;
import com.portx.clientapp.client.model.Payment;
import com.portx.clientapp.client.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class PortXPaymentServiceImpl implements PortXPaymentService {

    @Autowired
    private PortXPaymentApiClient portXPaymentApiClient;

    private static final int PAYMENT_REQUEST_COUNT_DEFAULT = 12;
    private static final int PAYMENT_REQUEST_COUNT_MAX = 30;

    @PostConstruct
    private void run() {
        acceptPayments();
    }

    @Override
    public void acceptPayments() {
        String paymentRequestCount = System.getProperty("paymentRequestCount");
        Integer paymentCount;
        if (StringUtils.isNotBlank(paymentRequestCount)) {
            paymentCount = Integer.valueOf(paymentRequestCount) > PAYMENT_REQUEST_COUNT_MAX ?
                    PAYMENT_REQUEST_COUNT_MAX : Integer.valueOf(paymentRequestCount);
        } else {
            paymentCount = PAYMENT_REQUEST_COUNT_DEFAULT;
        }
        log.info("\n");
        log.info("Executing the payment api to create payments, request count is {}", paymentCount);
        List<Payment> payments = buildPaymentList(paymentCount);
        for (int i = 0; i < paymentCount; i++) {
            log.info("\n");
            log.info("Executing request number {}", i + 1);
            Call<Void> call = portXPaymentApiClient.acceptPayment(payments.get(i), null);
            log.info("Request url is {}", call.request().url());
            log.info("Request body is: {}", new Gson().toJson(payments.get(i)));
            Response response;
            try {
                response = call.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("Response http status is {}", response.code());
        }
        log.info("\n");
        log.info("Terminating application with status 0");
        Runtime.getRuntime().halt(0);
    }

    private List<Payment> buildPaymentList(Integer count) {
        List<Payment> payments = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        Faker faker = new Faker();
        String[] currencies = {"USD", "CAD", "EUR"};
        String[] accountTypes = {"checking", "savings"};
        for (int i = 0; i < count; i++) {
            Payment payment = Payment.builder()
                    .currency(currencies[new Random().nextInt(currencies.length)])
                    .amount(Double.valueOf(decimalFormat.format(Math.random() * 100)))
                    .originator(User.builder()
                            .name(faker.name().fullName())
                            .build())
                    .beneficiary(User.builder()
                            .name(faker.name().fullName())
                            .build())
                    .sender(Account.builder()
                            .accountType(accountTypes[new Random().nextInt(accountTypes.length)])
                            .accountNumber(String.valueOf(new Random().ints(10000000, 99999999).findFirst().getAsInt()))
                            .build())
                    .receiver(Account.builder()
                            .accountType(accountTypes[new Random().nextInt(accountTypes.length)])
                            .accountNumber(String.valueOf(new Random().ints(10000000, 99999999).findFirst().getAsInt()))
                            .build())
                    .build();
            payments.add(payment);
        }
        return payments;
    }
}
