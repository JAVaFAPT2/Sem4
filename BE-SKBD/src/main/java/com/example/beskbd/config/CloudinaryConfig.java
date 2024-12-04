package com.example.beskbd.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "ddqtgl9tj");
        config.put("api_key","239668941537347" );
        config.put("api_secret","6MK0gWF79r0QLIsTSGfpAk3IsNQ");
//        CLOUDINARY_NAME=ddqtgl9tj
//        CLOUDINARY_API_KEY=239668941537347
//        CLOUDINARY_API_SECRET=6MK0gWF79r0QLIsTSGfpAk3IsNQ
        return new Cloudinary(config);
    }
}
