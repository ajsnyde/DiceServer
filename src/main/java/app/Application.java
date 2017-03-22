package app;

import java.text.SimpleDateFormat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import dice.DieBatchRepo;
import dice.DieFaceRepo;
import dice.DieJobRepo;
import dice.DieOrderRepo;
import dice.DieRepo;
import storage.StorageProperties;
import storage.StorageService;
import store.CustomerRepo;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@ComponentScan(basePackages = { "app", "storage", "dice", "store" })
public class Application {

  StorageService storageService;
  public static DieRepo dieRepo;
  public static DieFaceRepo dieFaceRepo;
  public static DieJobRepo dieJobRepo;
  public static DieBatchRepo dieBatchRepo;
  public static DieOrderRepo dieOrderRepo;
  public static CustomerRepo customerRepo;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  CommandLineRunner init(StorageService storageService, DieRepo repository, DieFaceRepo dfrepository, DieJobRepo djrepository, DieBatchRepo dbrepository, DieOrderRepo dorepo, CustomerRepo crepo) {
    dieRepo = repository;
    dieFaceRepo = dfrepository;
    dieJobRepo = djrepository;
    dieBatchRepo = dbrepository;
    dieOrderRepo = dorepo;
    customerRepo = crepo;
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

  @Scheduled(fixedDelay = 86400000) // Delete all uploaded files every hour
  public void restartStorage() {
    storageService.deleteAll();
    storageService.init();
    System.out.println("DELETING");
  }
}
