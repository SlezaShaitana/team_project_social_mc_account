package com.social.mc_account.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccountPageDTO {
    private long totalElements;
    private int totalPages;
    private SortDTO sortDTO;
    private int numberOfElements;
    private PageableDTO pageable;
    private boolean first;
    private boolean last;
    private int size;
    private AccountMeDTO accountMeDTO;
    private int number;
    private boolean empty;
}