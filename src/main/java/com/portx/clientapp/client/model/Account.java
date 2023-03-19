package com.portx.clientapp.client.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Account {

    private Long id;
    private String accountType;
    private String accountNumber;
}
