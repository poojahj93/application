package eu.nets.uni.apps.settlement.interview.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntity;

@Repository
public interface ExchangeRateRepository extends MongoRepository<ExchangeRateEntity, Integer>{

	List<ExchangeRateEntity> findByBaseCurrency(String baseCurrency);
	

}
