package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.WishlistItem;

public interface WishlistItemRepository extends CrudRepository<WishlistItem, Integer> {

}
