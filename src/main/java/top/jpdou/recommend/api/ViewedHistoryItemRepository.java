package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.ViewedHistoryItem;

public interface ViewedHistoryItemRepository extends CrudRepository<ViewedHistoryItem, Integer> {

    ViewedHistoryItem findByParentIdAndProductId(int parentId, int productId);

}
