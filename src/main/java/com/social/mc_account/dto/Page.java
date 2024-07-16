package com.social.mc_account.dto;

import lombok.Data;

import java.util.List;

@Data
public class Page {
    private int page;
    private int size;
    private List<String> sort;
}
