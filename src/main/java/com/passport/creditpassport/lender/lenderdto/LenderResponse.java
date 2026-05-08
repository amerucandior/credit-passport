package com.passport.creditpassport.lender.lenderdto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LenderResponse {
    private String apiKey;
    private String message;
}
