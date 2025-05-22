package com.nextstep.api.dto.candidate;

import lombok.Data;

@Data
public class GoogleVerifyDto {
    public CandidateDto candidate;
    public boolean isNew;
}
