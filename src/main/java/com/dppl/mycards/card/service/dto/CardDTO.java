package com.dppl.mycards.card.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private String bankName;
    private String cardNumber;
    private String expiry;
    private String issueDate;
    private String cardType; // debit/credit
    private String cardName; // platinum/moneyback/simplySave etc.
    
}
