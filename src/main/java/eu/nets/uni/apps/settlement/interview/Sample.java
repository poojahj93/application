//package eu.nets.uni.apps.settlement.interview;
//
//import java.io.File;
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.fasterxml.jackson.core.JsonGenerator.Feature;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.csv.CsvMapper;
//import com.fasterxml.jackson.dataformat.csv.CsvSchema;
//
//import eu.nets.uni.apps.settlement.interview.model.CsvPojo;
//
//
//public class Sample {
//
//	public static void main(String[] args) throws ParseException {
//
////		List<CsvPojo> csv= new ArrayList<>();
////		CsvPojo CsvPojo1= new CsvPojo("EUR", "413413431", "USD", new BigDecimal("2312") );
////		CsvPojo CsvPojo2= new CsvPojo("EUR", "413413431", "USD", new BigDecimal("2312") );
////		CsvPojo CsvPojo3= new CsvPojo("EUR", "413413431", "USD", new BigDecimal("2312") );
////		csv.add(CsvPojo1);
////		csv.add(CsvPojo2);
////		csv.add(CsvPojo3);
////		
////		ObjectMapper mapper = new ObjectMapper(); 
////		String json = mapper.writeValueAsString(csv);
////		
////		JsonNode jsonTree = mapper.readTree(json);
////		
////	//	  JsonNode jsonNode = new ObjectMapper().readTree(new File("src/main/resources/data.json"));
////
////		    CsvSchema.Builder builder = CsvSchema.builder()
////		        .addColumn("baseCurrency")
////		        .addColumn("timeStamp")
////		        .addColumn("currency")
////		        .addColumn("avgRate");
////
////		    CsvSchema csvSchema = builder.build().withHeader();
////
////		    CsvMapper csvMapper = new CsvMapper();
////		    csvMapper.configure(Feature.IGNORE_UNKNOWN, true);
////		    csvMapper.writerFor(JsonNode.class)
////		        .with(csvSchema)
////		        .writeValue(new File("src/main/resources/data.csv"), jsonTree);
//	}
//
//}
