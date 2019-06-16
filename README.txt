1. Build and run locally (powershell):
	mvn clean install spring-boot:run -D'spring.profiles.active=dev'

2. Deploy to tomcat server:
	mvn tomcat7:[re|un|]deploy