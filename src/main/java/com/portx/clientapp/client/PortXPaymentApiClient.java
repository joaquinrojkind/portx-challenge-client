package com.portx.clientapp.client;

import com.portx.clientapp.client.model.Payment;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PortXPaymentApiClient {

    @POST("payments")
    Call<Payment> acceptPayment(@Body Payment payment, @Header(value = "Idempotency-Key") String idempotencyKey);
}
