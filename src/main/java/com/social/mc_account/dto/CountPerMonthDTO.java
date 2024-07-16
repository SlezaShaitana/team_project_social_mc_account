package com.social.mc_account.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CountPerMonthDTO {
    private Date date;
    private int count;
}
