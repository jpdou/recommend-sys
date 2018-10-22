package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {

}
