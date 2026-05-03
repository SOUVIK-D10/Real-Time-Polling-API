package com.sll.rtpollingapi.DTO;

import java.time.LocalDateTime;

public record PollHeaderDTO(
    int id,
    LocalDateTime expiryDate,
    String topic
) {
    
}
