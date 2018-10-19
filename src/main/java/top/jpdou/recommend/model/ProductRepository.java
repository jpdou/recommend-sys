package top.jpdou.recommend.model;

import org.springframework.data.repository.CrudRepository;
import top.jpdou.recommend.model.entity.Product;

public interface ProductRepository extends CrudRepository<Product, String> {

}
