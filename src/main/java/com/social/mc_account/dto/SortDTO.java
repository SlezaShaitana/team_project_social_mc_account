package com.social.mc_account.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SortDTO {
    private boolean unsorted;
    private boolean sorted;
    private boolean empty;
}