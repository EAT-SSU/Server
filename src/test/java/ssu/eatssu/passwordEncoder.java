package ssu.eatssu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class passwordEncoder {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void encoderTwice() {
        String rawString = "password";
        String encodedString = passwordEncoder.encode(rawString);
        String doubleEncodedString = passwordEncoder.encode(encodedString);

        System.out.println("rawString = " + rawString);
        System.out.println("encodedString = " + encodedString);
        System.out.println("doubleEncodedString = " + doubleEncodedString);
    }
}
