package com.example.playpal;

import com.example.playpal.auth.model.entity.UserEntity;
import com.example.playpal.auth.repository.UserRepository;
import com.example.playpal.common.security.UserType;
import com.example.playpal.game.model.entity.GameEntity;
import com.example.playpal.game.repository.GameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class PlayPalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlayPalApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository userRepo, GameRepository gameRepo, PasswordEncoder encoder) {
        return args -> {
            // Default admin
            if (userRepo.findUserEntityByEmail("admin@playpal.com").isEmpty()) {
                userRepo.save(UserEntity.builder()
                        .email("admin@playpal.com")
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .lastLogout(new Date(0))
                        .userType(UserType.ADMIN.getValue()).build());
            }

            // Default games
            List<String> games = List.of("League of Legends", "Valorant", "Counter-Strike 2", "Overwatch 2");
            for (String title : games) {
                if (gameRepo.findByTitle(title).isEmpty()) {
                    gameRepo.save(GameEntity.builder().title(title).build());
                }
            }
        };
    }
}
