package com.social.mc_account.dto;

import lombok.Data;
import java.util.Date;

@Data
public class StatisticDTO {
    private Date date;
    private int count;
    private CountPerAgeDTO countPerAgeDTO;
    private CountPerMonthDTO countPerMonthDTO;
}