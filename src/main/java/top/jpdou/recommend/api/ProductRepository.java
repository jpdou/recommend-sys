package top.jpdou.recommend.api;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.Product;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, String> {

    Optional findBySku(String sku);

}
