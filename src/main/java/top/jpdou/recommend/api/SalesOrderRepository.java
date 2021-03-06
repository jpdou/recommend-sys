package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.SalesOrder;

public interface SalesOrderRepository extends CrudRepository<SalesOrder, Integer> {

    Iterable<SalesOrder> findByCustomerId(int customerId);

}
