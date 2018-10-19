package top.jpdou.recommend.model;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.Wishlist;

public interface WishlistRepository extends CrudRepository<Wishlist, Integer> {

}
