package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.ViewedHistory;

import java.util.Optional;

public interface ViewedHistoryRepository extends CrudRepository<ViewedHistory, Integer> {

    Optional findByCustomerIdAndProductId(int customerId, int productId);

    Iterable<ViewedHistory> findByCustomerId(int customerId);
}
