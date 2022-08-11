package eu.nets.uni.apps.settlement.interview.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvPojo {

	    private String baseCurrency;
	    private String timeStamp;
	    private String currency;
	    private BigDecimal avgRate;
}
