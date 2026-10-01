package com.ansari.seat_reserve.dto;
import jakarta.validation.constraints.*;
import java.util.List;

public record ReserveRequest(@NotEmpty List<@NotBlank String> seats, @NotBlank String idempotency_key) {}
