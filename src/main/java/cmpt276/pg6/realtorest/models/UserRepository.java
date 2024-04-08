package cmpt276.pg6.realtorest.models;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByEmailAndPassword(String email, String password);

    List<User> findByEmail(String email);

    List<User> findByIsOnMailingList(boolean isOnMailingList);

    List<User> findByEmailAndResetToken(String email, String resetToken);

    List<User> findByUsername(String username);
}
