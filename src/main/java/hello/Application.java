package hello;

import hello.storage.StorageProperties;
import hello.storage.StorageService;

import java.text.SimpleDateFormat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {

  StorageService storageService;
  public static DieRepo dieRepo;
  public static DieFaceRepo dieFaceRepo;
  public static DieJobRepo dieJobRepo;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner init(StorageService storageService, DieRepo repository, DieFaceRepo dfrepository, DieJobRepo djrepository) {
    dieRepo = repository;
    dieFaceRepo = dfrepository;
    dieJobRepo = djrepository;
    this.storageService = storageService;
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
