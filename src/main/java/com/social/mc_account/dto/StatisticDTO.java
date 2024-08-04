package com.social.mc_account.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StatisticDTO {
    private LocalDate date;
    private int count;
    private CountPerAgeDTO countPerAgeDTO;
    private CountPerMonthDTO countPerMonthDTO;
}