package hello;

import hello.storage.StorageProperties;
import hello.storage.StorageService;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {

  StorageService storageService;
  public static DieRepo dieRepo;
  public static DieFaceRepo dieFaceRepo;
  public static DieJobRepo dieJobRepo;
  public static DieBatchRepo dieBatchRepo;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner init(StorageService storageService, DieRepo repository, DieFaceRepo dfrepository, DieJobRepo djrepository, DieBatchRepo dbrepository) {
    dieRepo = repository;
    dieFaceRepo = dfrepository;
    dieJobRepo = djrepository;
    dieBatchRepo = dbrepository;
    this.storageService = storageService;
    Stripe.apiKey = "sk_test_KxLUHNW5j4SgB2IKOgRnGPwK"; // TEST ONLY - MUST BE REPLACED IN PROD

    Map<String, Object> tokenParams = new HashMap<String, Object>();
    Map<String, Object> cardParams = new HashMap<String, Object>();
    cardParams.put("number", "4242424242424242");
    cardParams.put("exp_month", 3);
    cardParams.put("exp_year", 2018);
    cardParams.put("cvc", "314");
    tokenParams.put("card", cardParams);
    Token token = null;
    try {
      token = Token.create(tokenParams);
    } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e1) {
      e1.printStackTrace();
    }

    Map<String, Object> chargeParams = new HashMap<String, Object>();
    chargeParams.put("amount", 2200);
    chargeParams.put("currency", "usd");
    chargeParams.put("description", "Charge for liam.davis@example.com");
    chargeParams.put("source", token.getId());

    RequestOptions options = RequestOptions.builder().setIdempotencyKey(new BigInteger(130, new SecureRandom()).toString(32)).build();

    try {
      Charge.create(chargeParams, options);
    } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
      e.printStackTrace();
    }

    return (args) -> {
      restartStorage();
    };
  }

  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {
    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
    builder.indentOutput(true);
    builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    return builder;
  }

  @Scheduled(fixedDelay = 86400000) // Delete all every hour
  public void restartStorage() {
    storageService.deleteAll();
    storageService.init();
    System.out.println("DELETING");
  }
}
