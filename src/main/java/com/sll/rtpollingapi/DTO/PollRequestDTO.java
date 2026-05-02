package com.sll.rtpollingapi.DTO;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PollRequestDTO(
    @Future(message = "The expiry should be a future date not present")
    LocalDateTime expiryDate,
    @NotBlank
    @Size(min = 3, max = 100, message = "topic size is Min 3 to Max 100")
    String topic,
    @Size(min = 2, max = 10, message = "Options Min 2 to Max 10")
    List<String> options
) {
}
