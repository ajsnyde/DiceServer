package hello;

import hello.storage.StorageProperties;
import hello.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {

  StorageService storageService;
  public static DieRepo dieRepo;
  public static DieFaceRepo dieFaceRepo;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner init(StorageService storageService, DieRepo repository, DieFaceRepo dfrepository) {
    dieRepo = repository;
    dieFaceRepo = dfrepository;
    this.storageService = storageService;
    return (args) -> {
      restartStorage();
    };
  }

  @Scheduled(fixedDelay = 86400000) // Delete all every hour
  public void restartStorage() {
    storageService.deleteAll();
    storageService.init();
    System.out.println("DELETING");

  }
}
