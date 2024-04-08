package cmpt276.pg6.realtorest.models;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByPropertyID(int propertyID);
}
