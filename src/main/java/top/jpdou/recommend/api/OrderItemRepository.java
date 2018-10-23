package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.OrderItem;

public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {

    Iterable<OrderItem> findByParentId(int parentId);

}
