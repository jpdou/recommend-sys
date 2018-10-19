package top.jpdou.recommend.model;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.OrderItem;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {

}
