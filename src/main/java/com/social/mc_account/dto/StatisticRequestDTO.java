package com.social.mc_account.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StatisticRequestDTO {
    private Date date;
    private Date firstMonth;
    private Date lastMonth;
}
