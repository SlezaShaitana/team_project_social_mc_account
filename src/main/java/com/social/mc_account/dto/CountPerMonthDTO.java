package com.social.mc_account.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CountPerMonthDTO {
    private LocalDate date;
    private int count;
}
