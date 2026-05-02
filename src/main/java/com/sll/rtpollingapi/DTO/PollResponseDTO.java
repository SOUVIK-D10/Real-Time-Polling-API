package com.sll.rtpollingapi.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record PollResponseDTO(
    int id,
    LocalDateTime expiryDate,
    LocalDateTime createdAt,
    String topic,
    List<OptionDTO> options
) {
    
}
