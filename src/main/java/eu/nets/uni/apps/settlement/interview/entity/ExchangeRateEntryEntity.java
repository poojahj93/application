package eu.nets.uni.apps.settlement.interview.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateEntryEntity {

	private String currency;
	private BigDecimal rate;
}
