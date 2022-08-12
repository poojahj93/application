package eu.nets.uni.apps.settlement.interview.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "exchange_rate_DB")
public class ExchangeRateEntity {


	private Instant timestamp;
	private String baseCurrency;
	private List<ExchangeRateEntryEntity> exchangeRateEntries;

}
