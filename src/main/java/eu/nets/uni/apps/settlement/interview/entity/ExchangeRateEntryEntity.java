package eu.nets.uni.apps.settlement.interview.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateEntryEntity {

	private String currency;
	private BigDecimal rate;
}
