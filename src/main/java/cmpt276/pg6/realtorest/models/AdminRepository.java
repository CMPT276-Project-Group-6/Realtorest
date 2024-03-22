package cmpt276.pg6.realtorest.models;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    List<Admin> findByEmailAndPassword(String email, String password);
}
