package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.Wishlist;

public interface WishlistRepository extends CrudRepository<Wishlist, Integer> {

    Iterable<Wishlist> findByCustomerId(int customerId);

}
