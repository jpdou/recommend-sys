package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.ViewedHistory;

public interface ViewedHistoryRepository extends CrudRepository<ViewedHistory, Integer> {

    ViewedHistory findByCustomerId(int customerId);

}
