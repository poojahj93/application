package eu.nets.uni.apps.settlement.interview.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateEntry {
    private String currency;
    private BigDecimal rate;
}
