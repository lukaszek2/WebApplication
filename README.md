# EduTutor — Learning Management System

System zarządzania nauczaniem (LMS) dla prywatnych korepetycji. Backend REST (Spring Boot + Hibernate + MySQL, JWT) oraz frontend w czystym HTML/CSS/JavaScript serwowany przez aplikację.

## Stack
- Java 17, Spring Boot 2.7, Spring Security + JWT, Hibernate/JPA
- MySQL 8
- Frontend: HTML + CSS + JavaScript (vanilla), wykresy w Chart.js
- Build: Maven

## Konfiguracja bazy danych
W pliku `src/main/resources/application.properties` uzupełnij dane dostępu:

```
spring.datasource.url=jdbc:mysql://<host>:3306/<DB_NAME>?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=<USERNAME>
spring.datasource.password=<PASSWORD>
```

Hibernate sam utworzy tabele (`spring.jpa.hibernate.ddl-auto=update`).

## Uruchomienie
```bash
./mvnw spring-boot:run
```
Aplikacja startuje na `http://localhost:8080`. Frontend dostępny jest pod tym samym adresem (`/`), API pod `/api/...`.

## Konta startowe
Przy pierwszym uruchomieniu (pusta baza) tworzone są konta testowe oraz przykładowy kurs:

| Rola    | Email                | Hasło      |
|---------|----------------------|------------|
| Admin   | admin@edututor.pl    | admin123   |
| Teacher | teacher@edututor.pl  | teacher123 |
| Student | student@edututor.pl  | student123 |

> Zmień te hasła przed wdrożeniem produkcyjnym.

## Struktura
```
src/main/java/com/edututor   – backend (controller, service, repository, entity, dto, security)
src/main/resources/static    – frontend (index.html, css/, js/)
```

## Role i dostęp
- **Guest** – publiczny katalog kursów, rejestracja/logowanie
- **Student** – dashboard, zapisane kursy, przeglądanie materiałów, oznaczanie ukończenia, profil
- **Teacher** – wszystko co student + zarządzanie kategoriami, kursami, sekcjami, zasobami, studentami oraz dashboard analityczny (wykresy)
- **Admin** – wszystko + zarządzanie użytkownikami i statystyki systemu
