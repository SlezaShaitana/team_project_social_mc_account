package com.social.mc_account.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    @Min(value = 0, message = "The page value must be greater than or equal to 0.")
    private int page;

    @Min(value = 1, message = "the page value must be greater than or equal to 1.")
    private int size;

    private List<String> sort;
}