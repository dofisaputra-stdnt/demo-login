
# 🚀 Demo Login

Spring Boot-based authentication and authorization project using JWT and PostgreSQL.

## 🧠 Fitur Utama

- ✅ Login & Register menggunakan JWT
- ✅ Struktur kode bersih dan modular (controller-service-repository)
- ✅ PostgreSQL sebagai database
- ✅ Docker Compose untuk setup database
- ✅ Contoh konfigurasi via `application.properties.example`

---

## 🛠️ Teknologi

- Java 17
- Spring Boot
- Spring Security
- JSON Web Token (JWT)
- PostgreSQL
- Docker + Docker Compose
- Gradle

---

## ⚙️ Cara Menjalankan Project

### 1. Jalankan PostgreSQL via Docker Compose

```bash
docker-compose -f db/docker-compose.yml up -d
```

> Pastikan port, username, dan password-nya **sesuai** dengan yang ada di `application.properties`.

---

### 2. Salin dan Sesuaikan Konfigurasi

Buat file konfigurasi dari contoh:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Lalu edit `application.properties` sesuai kebutuhan kamu:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/namadb
spring.datasource.username=postgres
spring.datasource.password=postgres
jwt.secret=rahasiaSuperSecret
```

---

### 3. Jalankan Aplikasi

Gunakan Gradle wrapper:

```bash
./gradlew bootRun
```

Atau kalau kamu pakai IDE seperti IntelliJ IDEA, bisa langsung jalankan kelas `DemoLoginApplication`.

---

## 📂 Struktur Project

```
demo-login
├── db/
│   └── docker-compose.yml        # Konfigurasi PostgreSQL
├── src/
│   └── main/
│       ├── java/com.cloudify.demologin/
│       │   ├── config/           # Konfigurasi security, Web, dll
│       │   ├── controller/       # Endpoint REST
│       │   ├── dto/              # Data Transfer Object
│       │   ├── entity/           # Entity JPA
│       │   ├── repository/       # Repository untuk database
│       │   ├── security/         # JWT dan konfigurasi security
│       │   ├── service/          # Business logic
│       │   └── util/             # Utility/helper
│       └── resources/
│           ├── static/
│           ├── templates/
│           ├── application.properties
│           └── application.properties.example
├── .gitignore
├── build.gradle
└── settings.gradle
```

---

## 🧪 Build & Test

Build project:

```bash
./gradlew build
```

Jalankan test:

```bash
./gradlew test
```

---

## 🧾 Contoh Endpoint API

| Method | Endpoint           | Deskripsi              |
|--------|--------------------|------------------------|
| POST   | `/api/auth/login`  | Login dan ambil JWT    |
| POST   | `/api/auth/signup` | Registrasi user baru   |
| GET    | `/api/home`        | Menampilkan user login |

---
