package eu.nets.uni.apps.settlement.interview.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRates {
    private Double timestamp;
    private String baseCurrency;
    private List<ExchangeRateEntry> exchangeRateEntries;
}
