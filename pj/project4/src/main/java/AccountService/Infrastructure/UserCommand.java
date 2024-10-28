package AccountService.Infrastructure;

import AccountService.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommand extends JpaRepository<User, Integer> {
}
