package com.springbootfinal.app.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResultDto {
 
    private String resultCode;
    private String message;
    private Object resultData;
    
    @Builder
    public ResultDto (String resultCode, String message, Object resultData, String url) {
        this.resultCode = resultCode;
        this.message    = message;
        this.resultData = resultData;
    }
    
}
