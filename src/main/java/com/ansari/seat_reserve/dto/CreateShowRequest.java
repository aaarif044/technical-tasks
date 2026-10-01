package com.ansari.seat_reserve.dto;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateShowRequest(@NotBlank String name, @NotEmpty List<@NotBlank String> seats, @PositiveOrZero long price_paise, @Min(1) Integer per_user_limit) {
	
}
