# Projekt: EduTutor – Learning Management System (Backend Only)

## Cel projektu
System LMS dedykowany prywatnym korepetycjom. Dwóch nauczycieli-korepetytorów
chce zarządzać kursami i materiałami dla swoich uczniów w jednym miejscu.
Na tym etapie implementujemy WYŁĄCZNIE backend – REST API zwracające JSON.

---

## Stack technologiczny
- Język: Java
- Framework: Spring Boot
- ORM: Hibernate (JPA)
- Baza danych: MySQL 8.x
- API: REST (JSON)
- Build tool: Maven
- Bezpieczeństwo: Spring Security + JWT
- Serwer: Apache Tomcat (embedded w Spring Boot)

---

## Diagram klas (model domenowy)

User (abstrakcyjny)
├── Student
├── Teacher
└── Admin

Relacje:
- Teacher (1) → Course (1..*) : tworzy
- Category (1) → Course (0..*) : zawiera
- Course (1) → Section (1..*) : posiada
- Section (1) → Resource (1..*) : zawiera
- Student → Enrollment (zapisanie się do kursu)
- Student → Progress (śledzenie ukończonych zasobów)
- Admin → Enrollment (zarządza)

---

## Encje JPA (tabele bazy danych)

### User (dziedziczenie JOINED)
- id: Long (PK)
- name: String
- email: String (UNIQUE)
- passwordHash: String
- role: Enum { STUDENT, TEACHER, ADMIN }
- createdAt: LocalDateTime
- isActive: Boolean

### Category
- id: Long (PK)
- name: String
- description: String
- icon: String (opcjonalnie)
- createdBy: Long (FK → User)

### Course
- id: Long (PK)
- title: String
- description: String
- status: Enum { DRAFT, PUBLISHED }
- coverImage: String (ścieżka lub URL)
- categoryId: Long (FK → Category)
- teacherId: Long (FK → User)
- createdAt: LocalDateTime

### Section
- id: Long (PK)
- courseId: Long (FK → Course)
- title: String
- orderIndex: Int

### Resource
- id: Long (PK)
- sectionId: Long (FK → Section)
- title: String
- type: Enum { FILE, VIDEO, LINK, NOTE }
- url: String (dla VIDEO i LINK)
- filePath: String (dla FILE)
- content: Text (dla NOTE)
- orderIndex: Int

### Enrollment
- id: Long (PK)
- studentId: Long (FK → User)
- courseId: Long (FK → Course)
- enrolledAt: LocalDateTime

### Progress
- id: Long (PK)
- studentId: Long (FK → User)
- resourceId: Long (FK → Resource)
- completedAt: LocalDateTime

---

## Role i uprawnienia

| Funkcja                         | Guest | Student | Teacher | Admin |
|---------------------------------|-------|---------|---------|-------|
| Publiczny katalog kursów        |  ✅   |   ✅    |   ✅    |  ✅   |
| Rejestracja / logowanie         |  ✅   |   —     |   —     |  —    |
| Dashboard studenta              |  ❌   |   ✅    |   ✅    |  ✅   |
| Przeglądanie kursów/zasobów     |  ❌   |   ✅    |   ✅    |  ✅   |
| Oznaczanie zasobu jako ukończ.  |  ❌   |   ✅    |   ❌    |  ✅   |
| Dashboard nauczyciela           |  ❌   |   ❌    |   ✅    |  ✅   |
| Tworzenie/edycja kursów         |  ❌   |   ❌    |   ✅    |  ✅   |
| Upload/zarządzanie zasobami     |  ❌   |   ❌    |   ✅    |  ✅   |
| Zarządzanie zapisami studentów  |  ❌   |   ❌    |   ✅    |  ✅   |
| Zarządzanie kategoriami         |  ❌   |   ❌    |   ✅    |  ✅   |
| Panel admina (użytkownicy)      |  ❌   |   ❌    |   ❌    |  ✅   |
| Logi systemowe                  |  ❌   |   ❌    |   ❌    |  ✅   |

---

## Endpointy REST

### Publiczne (Guest)
POST   /api/auth/register                        – rejestracja studenta
POST   /api/auth/login                           – logowanie, zwraca JWT
GET    /api/courses                              – lista opublikowanych kursów
GET    /api/categories                           – lista kategorii

### Student (rola STUDENT lub wyżej)
GET    /api/dashboard                            – podsumowanie: kursy + postęp
GET    /api/my-courses                           – kursy studenta
GET    /api/courses/{id}                         – szczegóły kursu (sekcje + zasoby)
GET    /api/courses/{id}/resources/{resourceId}  – konkretny zasób
POST   /api/progress/{resourceId}/complete       – oznacz zasób jako ukończony
GET    /api/profile                              – dane profilu
PUT    /api/profile                              – edycja profilu

### Teacher (rola TEACHER)
GET    /api/teacher/dashboard                            – statystyki nauczyciela
GET    /api/teacher/analytics                            – dane do wykresów (Chart.js)

GET    /api/teacher/categories                           – lista kategorii
POST   /api/teacher/categories                           – nowa kategoria
PUT    /api/teacher/categories/{id}                      – edycja kategorii
DELETE /api/teacher/categories/{id}                      – usunięcie kategorii

GET    /api/teacher/courses                              – kursy nauczyciela
POST   /api/teacher/courses                              – utwórz kurs
GET    /api/teacher/courses/{id}                         – szczegóły kursu
PUT    /api/teacher/courses/{id}                         – edycja kursu
DELETE /api/teacher/courses/{id}                         – usunięcie kursu
POST   /api/teacher/courses/{id}/publish                 – zmień status na PUBLISHED

GET    /api/teacher/courses/{id}/sections                – sekcje kursu
POST   /api/teacher/courses/{id}/sections                – dodaj sekcję
PUT    /api/teacher/courses/{id}/sections/{sid}          – edycja sekcji
DELETE /api/teacher/courses/{id}/sections/{sid}          – usunięcie sekcji

GET    /api/teacher/sections/{sid}/resources             – zasoby sekcji
POST   /api/teacher/sections/{sid}/resources             – dodaj zasób
PUT    /api/teacher/resources/{rid}                      – edycja zasobu
DELETE /api/teacher/resources/{rid}                      – usunięcie zasobu
POST   /api/teacher/resources/upload                     – upload pliku (multipart/form-data)

GET    /api/teacher/courses/{id}/students                – lista zapisanych studentów
POST   /api/teacher/courses/{id}/students/{sid}          – zapisz studenta
DELETE /api/teacher/courses/{id}/students/{sid}          – wypisz studenta

### Admin (rola ADMIN)
GET    /api/admin/users                          – wszyscy użytkownicy
PUT    /api/admin/users/{id}/role                – zmiana roli
PUT    /api/admin/users/{id}/suspend             – zawieszenie konta
DELETE /api/admin/users/{id}                     – usunięcie konta
GET    /api/admin/stats                          – statystyki systemowe
GET    /api/admin/logs                           – logi systemowe

---

## Wymagania techniczne

- Hasła hashowane BCrypt
- Walidacja wejść przez @Valid + Bean Validation
- Globalny handler błędów: @ControllerAdvice z ErrorResponse { status, message, timestamp }
- Kody HTTP: 200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found
- Pliki (PDF, obrazy) przechowywane lokalnie, ścieżka konfigurowana w application.properties
- Endpointy analityczne Teacher: enrollment chart, activity timeline (30 dni), completion rate per course, top resources
- Endpointy statystyk Admin: liczba użytkowników, kursów, zasobów, miesięczni aktywni użytkownicy

---

## Struktura pakietów

com.edututor
├── config/          – SecurityConfig, JwtConfig
├── controller/      – AuthController, CourseController, TeacherController, AdminController
├── service/         – interfejsy i implementacje logiki biznesowej
├── repository/      – interfejsy JpaRepository
├── entity/          – User, Student, Teacher, Admin, Course, Section, Resource, Enrollment, Progress, Category
├── dto/             – klasy Request/Response (bez eksponowania encji)
├── security/        – JwtFilter, UserDetailsServiceImpl
├── exception/       – GlobalExceptionHandler, custom exceptions
└── util/            – FileStorageService, ProgressCalculator

---

## Kolejność implementacji

1. Inicjalizacja projektu Maven + Spring Boot + konfiguracja MySQL
2. Encje JPA + schemat bazy danych (DDL)
3. Repozytoria JpaRepository
4. Spring Security + JWT (filtr, generowanie i walidacja tokena)
5. AuthController: /register, /login
6. Endpointy Studenta
7. Endpointy Nauczyciela: kategorie → kursy → sekcje → zasoby → studenci
8. Upload plików (FileStorageService)
9. Endpointy analityczne (Teacher dashboard)
10. Endpointy Admina + logowanie systemowe

---

Zacznij od kroku 1: wygeneruj pom.xml i application.properties z pełną konfiguracją.
