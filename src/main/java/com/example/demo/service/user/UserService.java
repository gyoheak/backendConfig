package com.example.demo.service.user;

import com.example.demo.config.SmtpConfig;
import com.example.demo.domain.UserEntity;
import com.example.demo.dto.LoginUserDto;
import com.example.demo.dto.SaveUserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.smtp.SmtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RedisTemplate<String, String> redisTemplate;
    private final SmtpService smtpService;

    public List<UserEntity> findAll(){
        return userRepository.findAll();
    }

    public void save(SaveUserDto inputUser) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        if (inputUser.getAuthenticationCode().equals(valueOperations.get(inputUser.getEmail()))){
            UserEntity user = new UserEntity();
            user.setEmail(inputUser.getEmail());
            user.setPassword(passwordEncoder.encode(inputUser.getPassword()));
            userRepository.save(user);
        }
    }

    public String temporaryNumbers(String mail){
        UserEntity existedUser = userRepository.findByEmail(mail);
        if (existedUser != null){
            return "";
        }
        else{
            int minRange = 100000;
            int maxRange = 999999;
            String randomNumber = String.valueOf(new Random().nextInt((maxRange - minRange) + 1) + minRange);
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set(mail, randomNumber);
            smtpService.sendMail(mail, "인증번호", randomNumber);
            return randomNumber;
        }
    }


    public Boolean login(LoginUserDto loginUserDto){
        UserEntity existedUser = userRepository.findByEmail(loginUserDto.getEmail());
        if (existedUser != null){
            if (passwordEncoder.matches(loginUserDto.getPassword(), existedUser.getPassword())){
                //여기에 jwt 발행해주는 로직 넣기
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
}
