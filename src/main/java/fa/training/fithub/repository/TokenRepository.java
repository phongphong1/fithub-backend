package fa.training.fithub.repository;

import fa.training.fithub.entity.Token;
import fa.training.fithub.entity.User;

import java.util.List;
import java.util.Optional;

import fa.training.fithub.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenAndType(String token, TokenType type);

    List<Token> findAllByUserAndType(User user, TokenType type);
}
