# Free hosting guide — Library Management System

This package is based on the uploaded project: Java 11, JSP, Servlets (`javax.servlet`), Maven WAR, Tomcat 9 and MySQL 8.

## Hosting plan (free tiers)

- **GitHub**: store the source code.
- **Render Free Web Service**: run the Docker/Tomcat application and provide a free `onrender.com` URL.
- **TiDB Cloud Starter (MySQL-compatible)**: host the database.

Free-tier limits apply. Render free web services sleep after 15 minutes without inbound traffic and typically take about a minute to wake. Render may restart free services; the local filesystem is ephemeral. Do not store library records in local files. The external TiDB database is where records are stored. TiDB's free MySQL-compatible Starter quota is currently 5 GiB row storage, 5 GiB columnar storage and 50 million Request Units per month for eligible free instances. Once the quota is reached, new connections/operations can be restricted until the monthly reset or a spending-limit change. Check current provider terms before deployment.

## A. Prepare GitHub

1. Extract this ZIP.
2. Open the `LibraryManagementSystem` folder in VS Code.
3. Create a **private** GitHub repository (recommended while the project contains demo credentials).
4. In the VS Code terminal, run:

   ```bash
   git init
   git add .
   git commit -m "Prepare library system for free cloud hosting"
   git branch -M main
   git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
   git push -u origin main
   ```

Replace the remote URL with your own repository URL. Do not upload `.env` files or real passwords.

## B. Create the free TiDB database

1. Sign up at https://tidbcloud.com/ and create a **MySQL-compatible TiDB Cloud Starter** instance.
2. Keep the monthly spending limit at **0** if you want to stay within the free quota. Do not enable consumption-based billing.
3. Select a region reasonably close to your Render service region, if available.
4. Open the instance's **Connect** panel and copy the MySQL connection details. TiDB commonly uses port `4000`; use the actual host, port and username shown in your dashboard.
5. Create a database named `library_db` if it does not already exist:

   ```sql
   CREATE DATABASE IF NOT EXISTS library_db;
   ```

6. Import `database/library_db_cloud_import.sql` into that database using the TiDB SQL Editor or a MySQL-compatible client. The file creates the tables and sample data. Import into a new/empty `library_db`; it intentionally does not drop an existing database. If you already imported tables, do not run it again because the CREATE TABLE statements will conflict.
7. For JDBC, build the connection URL in this form, using the exact host from TiDB:

   ```text
   jdbc:mysql://YOUR_TIDB_HOST:4000/library_db?sslMode=VERIFY_IDENTITY&serverTimezone=UTC
   ```

   If TiDB's Connect panel provides a different port, CA certificate, or SSL parameters, follow those exact instructions. Keep TLS verification enabled.

## C. Deploy on Render Free

1. Sign up at https://render.com/ and connect your GitHub account.
2. In Render, choose **New → Blueprint** and select your repository. Render reads `render.yaml` and proposes the web service.
3. Review the service and choose the **Free** plan. Do not add paid services, disks, or databases.
4. Complete creation. The Dockerfile builds the Maven WAR and runs it on Tomcat 9. The container adjusts Tomcat's HTTP connector to Render's `PORT`.
5. When the service is created, open its **Environment** settings and add:
   - `DB_URL` = the JDBC URL above
   - `DB_USER` = the TiDB username
   - `DB_PASSWORD` = the TiDB password
6. Save changes and trigger a deploy/redeploy.
7. Wait for the deploy logs to show the application started. In Render's Settings/Networking, generate a free `onrender.com` domain if one was not generated automatically.
8. Open `https://YOUR-SERVICE.onrender.com/` in a browser. The app's root page redirects to `/login`.

## D. Test the application

Use the sample accounts only for an initial demonstration:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Librarian | `librarian` | `lib123` |
| Student | `student` | `stud123` |

These are known demo credentials. Do not use them for a public real-world library. The supplied project currently hashes passwords with plain SHA-256 rather than a password-specific algorithm such as BCrypt/Argon2. Treat this as a student demonstration only; do not store real student personal data until authentication, password hashing, authorization, backups and privacy controls have been reviewed and improved.

## E. Update the site later

Make code changes locally, then run:

```bash
git add .
git commit -m "Update library application"
git push origin main
```

Render will automatically redeploy when auto-deploy is enabled.

## Troubleshooting

- **Build fails**: open Render → Logs. Check Maven errors. This project needs Java 11 and Tomcat 9; do not switch to Tomcat 10 because the code uses `javax.servlet`.
- **HTTP 502 / service unavailable**: check deploy logs and confirm Tomcat is listening on Render's `PORT`.
- **Database connection refused**: verify TiDB host/port, public network access, and any IP allowlist settings. Ensure the DB URL contains the correct database name and TLS settings.
- **Access denied**: re-check `DB_USER` and `DB_PASSWORD` in Render environment variables.
- **Unknown database**: create `library_db` in TiDB before importing the SQL.
- **Table already exists**: do not re-import the initialization script over an existing database. Use a fresh database or carefully migrate data.
- **First visit is slow**: Render Free sleeps after 15 minutes idle. This is expected; the first request wakes the service.
- **Database quota reached**: check TiDB usage. Keep within free quota; do not add a spending limit if you require zero cost.

## Official references

- Render free service limitations: https://render.com/docs/free
- Render Blueprint configuration: https://render.com/docs/blueprint-spec
- TiDB Cloud plan and free quota: https://docs.pingcap.com/tidbcloud/select-cluster-tier/
- TiDB MySQL compatibility: https://docs.pingcap.com/tidbcloud/mysql-compatibility/

**Cost reminder:** This deployment uses free-tier services only when you select Render Free and keep TiDB's spending limit at zero. Free tiers have quotas and restrictions and are not a guarantee of always-on production hosting.
