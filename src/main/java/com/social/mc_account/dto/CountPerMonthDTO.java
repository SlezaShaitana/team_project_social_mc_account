package com.social.mc_account.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CountPerMonthDTO {
    private LocalDate date;
    private int count;
}