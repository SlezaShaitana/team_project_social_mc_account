package com.social.mc_account.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class StatisticDTO {
    private LocalDate date;
    private int count;
    private CountPerAgeDTO countPerAgeDTO;
    private CountPerMonthDTO countPerMonthDTO;
}