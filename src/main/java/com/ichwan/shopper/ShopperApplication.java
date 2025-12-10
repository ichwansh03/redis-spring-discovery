package com.ichwan.shopper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

//https://chatgpt.com/s/t_69398b506b18819195bf2c1957764deb
@SpringBootApplication
@EnableCaching
public class ShopperApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopperApplication.class, args);
	}

}
