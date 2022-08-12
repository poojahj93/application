package eu.nets.uni.apps.settlement.interview.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CsvPojo {

	    private String baseCurrency;
	    private String timeStamp;
	    private String currency;
	    private BigDecimal avgRate;
}
