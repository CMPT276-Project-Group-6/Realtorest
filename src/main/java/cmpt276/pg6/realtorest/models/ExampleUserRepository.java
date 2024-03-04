package cmpt276.pg6.realtorest.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface ExampleUserRepository extends JpaRepository<ExampleUser, Integer> {
    List<ExampleUser> findBySize(int size);

    List<ExampleUser> findByNameAndPassword(String name, String password);
}
