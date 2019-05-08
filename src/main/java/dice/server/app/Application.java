package dice.server.app;

import java.text.SimpleDateFormat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import dice.DieBatchRepo;
import dice.DieFaceRepo;
import dice.DieJobRepo;
import dice.DieOrderRepo;
import dice.DieRepo;
import dice.DieTemplateRepo;
import dice.server.storage.AWSFileSystemStorageService;
import dice.server.store.CustomerRepo;

@SpringBootApplication
@ComponentScan(basePackages = { "dice" })
public class Application extends SpringBootServletInitializer {
	public static AWSFileSystemStorageService storageService;
	public static DieRepo dieRepo;
	public static DieTemplateRepo dieTemplateRepo;
	public static DieFaceRepo dieFaceRepo;
	public static DieJobRepo dieJobRepo;
	public static DieBatchRepo dieBatchRepo;
	public static DieOrderRepo dieOrderRepo;
	public static CustomerRepo customerRepo;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	@Bean
	CommandLineRunner init(AWSFileSystemStorageService ss, DieRepo repository, DieTemplateRepo dtrepository, DieFaceRepo dfrepository, DieJobRepo djrepository, DieBatchRepo dbrepository,
			DieOrderRepo dorepo, CustomerRepo crepo) {
		dieRepo = repository;
		dieTemplateRepo = dtrepository;
		dieFaceRepo = dfrepository;
		dieJobRepo = djrepository;
		dieBatchRepo = dbrepository;
		dieOrderRepo = dorepo;
		customerRepo = crepo;
		storageService = ss;
		return (args) -> {
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
		System.out.println("DELETING all uploaded files");
		storageService.deleteAll();
	}
}
