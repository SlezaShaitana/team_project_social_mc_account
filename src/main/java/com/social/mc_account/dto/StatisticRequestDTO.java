package com.social.mc_account.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StatisticRequestDTO {
    private LocalDate date;
    private LocalDate firstMonth;
    private LocalDate lastMonth;
}