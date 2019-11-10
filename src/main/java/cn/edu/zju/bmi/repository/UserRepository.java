package cn.edu.zju.bmi.repository;
import cn.edu.zju.bmi.entity.DAO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByUserName(String name);
    List<User> findTop1ByOrderByIdDesc();

    Optional<User> findByUserName(String accountName);

    Optional<User> findById(long id);
}
