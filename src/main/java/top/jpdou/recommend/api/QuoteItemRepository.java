package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.QuoteItem;

public interface QuoteItemRepository extends CrudRepository<QuoteItem, Integer> {

    Iterable<QuoteItem> findByParentId(int parentId);

}
