package cmpt276.pg6.realtorest.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface AdminRepository extends JpaRepository<User, Integer>{
    List<Admin> findByEmailAndPassword(String email, String password);
}
