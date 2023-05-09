package com.example.demo.service.user;

import com.example.demo.domain.UserEntity;
import com.example.demo.dto.SaveUserDto;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.smtp.SmtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SmtpService smtpService;


    public void temporaryNumbers(String mail){
        UserEntity existedUser = userRepository.findByEmail(mail);
        if (existedUser != null){
            throw new UserAlreadyExistsException();
        }
        else{
            int minRange = 100000;
            int maxRange = 999999;
            String randomNumber = String.valueOf(new Random().nextInt((maxRange - minRange) + 1) + minRange);
            redisTemplate.opsForValue().set(mail, randomNumber, 5, TimeUnit.MINUTES);
            smtpService.sendMail(mail, "인증번호", randomNumber);
            redisTemplate.opsForValue().get(mail);
        }
    }

    public void save(SaveUserDto inputUser) {
        if(userRepository.findByEmail(inputUser.getEmail()) != null){
            throw new UserAlreadyExistsException();
        }
        else if (inputUser.getAuthenticationCode().equals(redisTemplate.opsForValue().get(inputUser.getEmail()))){
            UserEntity newUser = new UserEntity();
            newUser.setEmail(inputUser.getEmail());
            newUser.setPassword(passwordEncoder.encode(inputUser.getPassword()));
            userRepository.save(newUser);
        }
    }
}
