package top.jpdou.recommend.model;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.SalesOrder;

public interface SalesOrderRepository extends CrudRepository<SalesOrder, Integer> {

}
