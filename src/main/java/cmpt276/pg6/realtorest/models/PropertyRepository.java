package cmpt276.pg6.realtorest.models;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PropertyRepository extends JpaRepository<Property, Integer> {
    List<Property> findByPid(int pid);

    List<Property> findByCityAndBrCountAndBaCountAndNameAllIgnoreCase(String city, Integer brCount, Integer baCount, String name);

    List<Property> findByNameContainingIgnoreCase(String name, Sort sort);

    List<Property> findByCity(String city, Sort sort);

    List<Property> findByBrCount(Integer brCount, Sort sort);

    List<Property> findByBaCount(Integer baCount, Sort sort);

   List<Property> findByBrCountGreaterThanEqual( Integer brCount, Sort sort);

    List<Property> findByBaCountGreaterThanEqual(Integer baCount, Sort sort);

    List<Property> findByFeatured(boolean featured);
}
