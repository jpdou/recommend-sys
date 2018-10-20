package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.Quote;

public interface QuoteRepository extends CrudRepository<Quote, Integer> {

}
