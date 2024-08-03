package com.social.mc_account.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class StatisticRequestDTO {
    private LocalDate date;
    private LocalDate firstMonth;
    private LocalDate lastMonth;
}
