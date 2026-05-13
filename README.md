# Teacher Management System

A teacher management system built with Java, Spring MVC, Vue and MySQL.

## Tech Stack

- Java
- Spring Boot
- Spring MVC
- Vue
- MySQL
- Maven

## Modules

- Teacher management
- Student management
- Course management
- Class management
- Score management
- Account permissions
- Learning resource availability
- CSV export
- Dashboard statistics
- Reports and analytics

## Reports

The Reports page includes:

- Payroll amount that needs to be paid to teachers.
- Student-to-teacher ratio.
- Average score overview.
- Grade and class performance reports.
- Teacher and course matching.
- Payroll detail table for finance review.
- CSV export for the report summary.

## Contribution Test Note

Recent commits use the verified project author email so GitHub can associate repository activity with the correct profile.

## Local Frontend Preview

```bash
cd frontend
npm install
npm run dev
```

Open the Vite URL shown in the terminal, usually `http://localhost:5173`.

## Current Improvements

- Richer demo data for teachers, students, courses, classes, scores, accounts, and resources.
- Dynamic dashboard summaries for departments and score distribution.
- Searchable management tables with add, edit, delete, and CSV export.
- Empty-state handling for filtered tables.
- Pink professional admin layout for a polished project presentation.

## Project Structure

```text
teacher-management-system/
|-- backend/
|-- frontend/
|-- database/
|-- .gitignore
`-- README.md
```

## Development Plan

1. Build database tables.
2. Create Spring MVC REST APIs.
3. Create Vue pages and call backend APIs.
4. Add login and role permissions.
5. Deploy or publish the final project.
