<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">T</div>
        <div>
          <strong>Teacher Management System</strong>
          <span>Campus Administration</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="Main navigation">
        <button
          v-for="item in modules"
          :key="item.key"
          class="nav-item"
          :class="{ active: activeModule === item.key }"
          @click="setActiveModule(item.key)"
        >
          <span class="nav-icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </button>
      </nav>
    </aside>

    <main class="main-panel">
      <header class="topbar">
        <div>
          <p class="eyebrow">{{ activeConfig.kicker }}</p>
          <h1>{{ activeConfig.title }}</h1>
        </div>
        <div class="top-actions">
          <input v-model="keyword" class="search-input" type="search" :placeholder="searchPlaceholder" />
          <button v-if="activeConfig.fields" class="primary-button" @click="openCreateDialog">
            Add {{ activeConfig.short }}
          </button>
          <button v-else-if="activeModule === 'reports'" class="primary-button" @click="exportReportSummary">
            Export report
          </button>
          <button v-else-if="activeModule === 'resources'" class="primary-button" @click="refreshStudyRooms">
            Refresh status
          </button>
        </div>
      </header>

      <section v-if="activeModule === 'dashboard'" class="dashboard">
        <div class="hero-panel">
          <div>
            <p class="eyebrow">Spring Semester 2026</p>
            <h2>Daily teaching administration in one place</h2>
            <p>Manage teacher profiles, student records, courses, classes, scores, user permissions, and learning resources.</p>
          </div>
          <div class="hero-badge">
            <strong>{{ totalPeople }}</strong>
            <span>People records</span>
          </div>
        </div>

        <div class="stats-grid">
          <button v-for="card in statCards" :key="card.key" class="stat-card" type="button" @click="setActiveModule(card.key)">
            <span>{{ card.label }}</span>
            <strong>{{ card.value }}</strong>
            <small>{{ card.note }}</small>
          </button>
        </div>

        <div class="content-grid">
          <section class="panel">
            <div class="section-title">
              <h3>Today's Tasks</h3>
              <span>6 items</span>
            </div>
            <ul class="todo-list">
              <li>Review new teacher profile documents</li>
              <li>Enter mathematics scores for Grade 10 Class 2</li>
              <li>Update English Speaking classroom resources</li>
              <li>Check student account activation status</li>
            </ul>
          </section>
          <section class="panel">
            <div class="section-title">
              <h3>Score Distribution</h3>
              <span>Excellent rate {{ scoreSummary.excellentRate }}%</span>
            </div>
            <div class="bars">
              <div v-for="bar in scoreBars" :key="bar.label">
                <span>{{ bar.label }}</span>
                <div><i :style="{ width: bar.value + '%' }"></i></div>
                <b>{{ bar.value }}%</b>
              </div>
            </div>
          </section>
          <section class="panel">
            <div class="section-title">
              <h3>Department Coverage</h3>
              <span>{{ departmentSummary.length }} departments</span>
            </div>
            <ul class="todo-list">
              <li v-for="department in departmentSummary" :key="department.name">
                {{ department.name }}: {{ department.count }} teacher{{ department.count === 1 ? '' : 's' }}
              </li>
            </ul>
          </section>
          <section class="panel">
            <div class="section-title">
              <h3>Recent Activity</h3>
              <span>Updated today</span>
            </div>
            <ul class="todo-list">
              <li>New course Robotics Lab was published</li>
              <li>Grade 11 Class 2 attendance list was refreshed</li>
              <li>Two score records need final review</li>
              <li>One teacher account is pending approval</li>
            </ul>
          </section>
        </div>
      </section>

      <section v-else-if="activeModule === 'resources'" class="study-room-page">
        <div class="service-header">
          <div>
            <p class="eyebrow">Learning Resources</p>
            <h2>Computer Lab Availability</h2>
            <p>
              Track bookable workstations and teaching lab resources across the campus.
              Available workstations can be reserved for lessons, self-study, or project work.
            </p>
          </div>
          <div class="live-card">
            <span>Last refreshed</span>
            <strong>{{ liveUpdatedLabel }}</strong>
            <small>{{ online ? 'Connected to backend service' : 'Showing local demo data' }}</small>
          </div>
        </div>

        <div class="status-summary">
          <article>
            <span>Total computers</span>
            <strong>{{ studyRoomStats.total }}</strong>
          </article>
          <article class="available-summary">
            <span>Available now</span>
            <strong>{{ studyRoomStats.available }}</strong>
          </article>
          <article>
            <span>Unavailable</span>
            <strong>{{ studyRoomStats.unavailable }}</strong>
          </article>
          <article>
            <span>Current utilisation</span>
            <strong>{{ studyRoomStats.utilisation }}%</strong>
          </article>
        </div>

        <div class="study-controls">
          <label>
            <span>Location</span>
            <select v-model="selectedLocation">
              <option value="all">All locations</option>
              <option v-for="location in locationOptions" :key="location" :value="location">{{ location }}</option>
            </select>
          </label>
          <label>
            <span>Level</span>
            <select v-model="selectedLevel">
              <option value="all">All levels</option>
              <option v-for="level in levelOptions" :key="level" :value="level">{{ level }}</option>
            </select>
          </label>
          <label>
            <span>Status</span>
            <select v-model="selectedStatus">
              <option value="all">All statuses</option>
              <option value="available">Available</option>
              <option value="unavailable">Unavailable</option>
            </select>
          </label>
          <div class="legend" aria-label="Status legend">
            <span><i class="legend-available"></i>Available and bookable</span>
            <span><i class="legend-unavailable"></i>Unavailable</span>
            <span><i class="legend-maintenance"></i>Service issue</span>
          </div>
        </div>

        <div class="machine-layout">
          <section v-for="group in groupedStudyMachines" :key="group.key" class="machine-zone">
            <div class="machine-zone-header">
              <div>
                <h3>{{ group.location }}</h3>
                <p>{{ group.level }} / {{ group.area }}</p>
              </div>
              <span>{{ group.available }} of {{ group.total }} available</span>
            </div>

            <div class="machine-grid">
              <article
                v-for="machine in group.machines"
                :key="machine.id"
                class="machine-card"
                :class="machineStatusClass(machine)"
              >
                <div class="machine-card-top">
                  <span class="machine-id">{{ machine.pc }}</span>
                  <span class="status-chip">{{ machineStateLabel(machine) }}</span>
                </div>
                <p class="machine-location">{{ machine.zone }}</p>
                <dl class="machine-meta">
                  <div>
                    <dt>Type</dt>
                    <dd>{{ machine.type }}</dd>
                  </div>
                  <div>
                    <dt>Next change</dt>
                    <dd>{{ machine.nextChange }}</dd>
                  </div>
                </dl>
                <button class="reserve-button" :disabled="!isMachineAvailable(machine)">
                  {{ isMachineAvailable(machine) ? 'Reserve' : machineUnavailableLabel(machine) }}
                </button>
              </article>
            </div>
          </section>

          <section v-if="groupedStudyMachines.length === 0" class="empty-state">
            <h3>No computers match the current filters</h3>
            <p>Try another location, level, status, or search term.</p>
          </section>
        </div>

        <section class="booking-panel">
          <div>
            <h3>Resource booking guidance</h3>
            <p>Teachers can reserve lab computers for classes. Students can use available machines for self-study sessions.</p>
          </div>
          <ul>
            <li>Regular study sessions are up to 1 hour.</li>
            <li>Course lab sessions are up to 2 hours.</li>
            <li>Project sessions are up to 3 hours with teacher approval.</li>
          </ul>
        </section>
      </section>

      <section v-else-if="activeModule === 'reports'" class="reports-page">
        <div class="report-hero">
          <div>
            <p class="eyebrow">School Analytics</p>
            <h2>Teaching, payroll, and performance reports</h2>
            <p>
              Review salary payout needs, student-teacher balance, grade performance,
              class outcomes, and teacher-course assignments from one reporting page.
            </p>
          </div>
          <div class="payroll-total">
            <span>Total payroll due</span>
            <strong>{{ formatCurrency(totalPayroll) }}</strong>
            <small>{{ payrollRows.length }} staff records</small>
          </div>
        </div>

        <div class="report-kpi-grid">
          <article>
            <span>Teacher-student ratio</span>
            <strong>1:{{ teacherStudentRatio }}</strong>
            <small>{{ records.teachers.length }} teachers / {{ records.students.length }} students</small>
          </article>
          <article>
            <span>Average score</span>
            <strong>{{ averageScore }}</strong>
            <small>{{ records.scores.length }} score records</small>
          </article>
          <article>
            <span>Open courses</span>
            <strong>{{ openCourseCount }}</strong>
            <small>{{ records.courses.length }} courses total</small>
          </article>
          <article>
            <span>Class capacity</span>
            <strong>{{ totalClassStudents }}</strong>
            <small>{{ records.classes.length }} active classes</small>
          </article>
        </div>

        <div class="report-grid">
          <section class="report-card wide">
            <div class="section-title">
              <div>
                <h3>Payroll by Department</h3>
                <p>Monthly salary amount that needs to be prepared for each department.</p>
              </div>
              <span>{{ formatCurrency(totalPayroll) }}</span>
            </div>
            <div class="chart-bars">
              <div v-for="item in payrollByDepartment" :key="item.department" class="chart-row">
                <span>{{ item.department }}</span>
                <div><i :style="{ width: item.percent + '%' }"></i></div>
                <b>{{ formatCurrency(item.total) }}</b>
              </div>
            </div>
          </section>

          <section class="report-card">
            <div class="section-title">
              <div>
                <h3>Student vs Teacher Ratio</h3>
                <p>Current staffing balance for teaching operations.</p>
              </div>
            </div>
            <div class="ratio-visual">
              <div>
                <strong>{{ records.students.length }}</strong>
                <span>Students</span>
              </div>
              <div>
                <strong>{{ records.teachers.length }}</strong>
                <span>Teachers</span>
              </div>
            </div>
            <p class="report-note">Each teacher currently supports about {{ teacherStudentRatio }} students.</p>
          </section>

          <section class="report-card">
            <div class="section-title">
              <div>
                <h3>Grade Score Overview</h3>
                <p>Average score grouped by grade level.</p>
              </div>
            </div>
            <div class="mini-bars">
              <div v-for="grade in gradeReports" :key="grade.grade">
                <span>{{ grade.grade }}</span>
                <div><i :style="{ height: grade.average + '%' }"></i></div>
                <b>{{ grade.average }}</b>
              </div>
            </div>
          </section>

          <section class="report-card wide">
            <div class="section-title">
              <div>
                <h3>Class Performance Report</h3>
                <p>Class-level averages with head teacher assignment.</p>
              </div>
            </div>
            <div class="report-table-wrap">
              <table class="report-table">
                <thead>
                  <tr>
                    <th>Class</th>
                    <th>Grade</th>
                    <th>Head Teacher</th>
                    <th>Students</th>
                    <th>Average Score</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in classReports" :key="row.className">
                    <td>{{ row.className }}</td>
                    <td>{{ row.grade }}</td>
                    <td>{{ row.headTeacher }}</td>
                    <td>{{ row.students }}</td>
                    <td>{{ row.average }}</td>
                    <td><span class="status-pill">{{ row.status }}</span></td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>

          <section class="report-card wide">
            <div class="section-title">
              <div>
                <h3>Teacher and Course Matching</h3>
                <p>Shows which teachers are assigned to courses and how performance connects back to classes.</p>
              </div>
            </div>
            <div class="teacher-match-grid">
              <article v-for="match in teacherCourseMatrix" :key="match.teacher">
                <strong>{{ match.teacher }}</strong>
                <span>{{ match.department }}</span>
                <p>{{ match.courses.length ? match.courses.join(', ') : 'No course assigned' }}</p>
                <small>{{ match.classCount }} related class{{ match.classCount === 1 ? '' : 'es' }}</small>
              </article>
            </div>
          </section>

          <section class="report-card wide">
            <div class="section-title">
              <div>
                <h3>Payroll Detail Report</h3>
                <p>Salary payout list for finance review.</p>
              </div>
            </div>
            <div class="report-table-wrap">
              <table class="report-table">
                <thead>
                  <tr>
                    <th>Teacher</th>
                    <th>Department</th>
                    <th>Status</th>
                    <th>Monthly Salary</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="row in payrollRows" :key="row.name">
                    <td>{{ row.name }}</td>
                    <td>{{ row.department }}</td>
                    <td><span class="status-pill">{{ row.status }}</span></td>
                    <td>{{ formatCurrency(row.salary) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </section>

      <section v-else class="panel data-panel">
        <div class="section-title">
          <div>
            <h3>{{ activeConfig.title }}</h3>
            <p>{{ activeConfig.description }}</p>
          </div>
          <div class="section-actions">
            <span>{{ filteredRecords.length }} records</span>
            <button class="ghost-button" type="button" @click="exportRecords">Export CSV</button>
          </div>
        </div>

        <div class="table-wrap">
          <table v-if="filteredRecords.length">
            <thead>
              <tr>
                <th v-for="field in activeConfig.fields" :key="field.key">{{ field.label }}</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="record in filteredRecords" :key="record.id">
                <td v-for="field in activeConfig.fields" :key="field.key">
                  <span v-if="field.key === 'status' || field.key === 'level'" class="status-pill">
                    {{ record.fields[field.key] }}
                  </span>
                  <span v-else>{{ record.fields[field.key] }}</span>
                </td>
                <td class="actions-cell">
                  <button class="ghost-button" @click="openEditDialog(record)">Edit</button>
                  <button class="danger-button" @click="deleteRecord(record.id)">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-else class="empty-state table-empty">
            <h3>No records found</h3>
            <p>Try another keyword or add a new {{ activeConfig.short.toLowerCase() }} record.</p>
          </div>
        </div>
      </section>
    </main>

    <div v-if="dialogOpen" class="modal-backdrop" @click.self="closeDialog">
      <form class="modal-card" @submit.prevent="saveRecord">
        <div class="section-title">
          <div>
            <h3>{{ editingRecord ? 'Edit' : 'Add' }} {{ activeConfig.short }}</h3>
            <p>Complete the form and save it to the current module.</p>
          </div>
          <button class="icon-button" type="button" aria-label="Close dialog" @click="closeDialog">x</button>
        </div>

        <label v-for="field in activeConfig.fields" :key="field.key" class="form-field">
          <span>{{ field.label }}</span>
          <input v-model="form[field.key]" :type="field.type || 'text'" :placeholder="'Enter ' + field.label" required />
        </label>

        <div class="modal-actions">
          <button class="ghost-button" type="button" @click="closeDialog">Cancel</button>
          <button class="primary-button" type="submit">Save</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'

const apiBase = 'http://localhost:8080/api'

const modules = [
  { key: 'dashboard', label: 'Dashboard', title: 'Dashboard', short: 'Item', icon: 'D', kicker: 'Teacher Management System' },
  {
    key: 'reports',
    label: 'Reports',
    title: 'Reports & Analytics',
    short: 'Report',
    icon: 'R',
    kicker: 'Analytics Center',
    description: 'Review payroll, staffing ratios, grade performance, class reports, and teacher-course matching.'
  },
  {
    key: 'teachers',
    label: 'Teachers',
    title: 'Teacher Management',
    short: 'Teacher',
    icon: 'T',
    kicker: 'Academic Records',
    description: 'Manage teacher profiles, contact information, departments, and employment status.',
    fields: [
      { key: 'name', label: 'Name' },
      { key: 'department', label: 'Department' },
      { key: 'phone', label: 'Phone' },
      { key: 'email', label: 'Email' },
      { key: 'status', label: 'Status' }
    ]
  },
  {
    key: 'students',
    label: 'Students',
    title: 'Student Management',
    short: 'Student',
    icon: 'S',
    kicker: 'Student Records',
    description: 'Manage student numbers, classes, contact information, and enrollment status.',
    fields: [
      { key: 'name', label: 'Name' },
      { key: 'studentNo', label: 'Student No.' },
      { key: 'className', label: 'Class' },
      { key: 'phone', label: 'Phone' },
      { key: 'status', label: 'Status' }
    ]
  },
  {
    key: 'courses',
    label: 'Courses',
    title: 'Course Management',
    short: 'Course',
    icon: 'C',
    kicker: 'Teaching Operations',
    description: 'Manage course names, assigned teachers, credits, classrooms, and course status.',
    fields: [
      { key: 'name', label: 'Course Name' },
      { key: 'teacher', label: 'Teacher' },
      { key: 'credit', label: 'Credit', type: 'number' },
      { key: 'room', label: 'Room' },
      { key: 'status', label: 'Status' }
    ]
  },
  {
    key: 'classes',
    label: 'Classes',
    title: 'Class Management',
    short: 'Class',
    icon: 'CL',
    kicker: 'Teaching Operations',
    description: 'Manage class names, grades, homeroom teachers, student counts, and rooms.',
    fields: [
      { key: 'name', label: 'Class Name' },
      { key: 'headTeacher', label: 'Homeroom Teacher' },
      { key: 'grade', label: 'Grade' },
      { key: 'students', label: 'Students', type: 'number' },
      { key: 'room', label: 'Room' }
    ]
  },
  {
    key: 'scores',
    label: 'Scores',
    title: 'Score Management',
    short: 'Score',
    icon: 'G',
    kicker: 'Assessment Records',
    description: 'Enter and manage student course scores, terms, and performance levels.',
    fields: [
      { key: 'student', label: 'Student' },
      { key: 'course', label: 'Course' },
      { key: 'score', label: 'Score', type: 'number' },
      { key: 'term', label: 'Term' },
      { key: 'level', label: 'Level' }
    ]
  },
  {
    key: 'users',
    label: 'Accounts',
    title: 'Account Permissions',
    short: 'Account',
    icon: 'A',
    kicker: 'Access Control',
    description: 'Manage system accounts, roles, display names, and account status.',
    fields: [
      { key: 'username', label: 'Username' },
      { key: 'role', label: 'Role' },
      { key: 'name', label: 'Name' },
      { key: 'status', label: 'Status' }
    ]
  },
  {
    key: 'resources',
    label: 'Resources',
    title: 'Learning Resource Status',
    short: 'Resource',
    icon: 'PC',
    kicker: 'Teaching Resources',
    description: 'Track computer lab availability and learning resource status.'
  }
]

const fallbackData = {
  teachers: [
    { id: 1, fields: { name: 'Emma Lin', department: 'Language Arts', phone: '13800000001', email: 'emma.lin@example.com', status: 'Active' } },
    { id: 2, fields: { name: 'Michael Zhou', department: 'Mathematics', phone: '13800000002', email: 'michael.zhou@example.com', status: 'Active' } },
    { id: 3, fields: { name: 'Clara Xu', department: 'English', phone: '13800000003', email: 'clara.xu@example.com', status: 'On Leave' } },
    { id: 18, fields: { name: 'Oliver Reed', department: 'Science', phone: '13800000004', email: 'oliver.reed@example.com', status: 'Active' } },
    { id: 19, fields: { name: 'Sophia Patel', department: 'Computer Science', phone: '13800000005', email: 'sophia.patel@example.com', status: 'Active' } },
    { id: 20, fields: { name: 'Daniel Kim', department: 'Physical Education', phone: '13800000006', email: 'daniel.kim@example.com', status: 'Active' } },
    { id: 21, fields: { name: 'Grace Miller', department: 'Arts', phone: '13800000007', email: 'grace.miller@example.com', status: 'Active' } },
    { id: 22, fields: { name: 'Henry Brown', department: 'History', phone: '13800000008', email: 'henry.brown@example.com', status: 'Pending' } }
  ],
  students: [
    { id: 4, fields: { name: 'Anna Chen', studentNo: 'S2026001', className: 'Grade 10 Class 1', phone: '13900000001', status: 'Enrolled' } },
    { id: 5, fields: { name: 'Leo Li', studentNo: 'S2026002', className: 'Grade 10 Class 2', phone: '13900000002', status: 'Enrolled' } },
    { id: 6, fields: { name: 'Iris Wang', studentNo: 'S2026003', className: 'Grade 11 Class 1', phone: '13900000003', status: 'Enrolled' } },
    { id: 23, fields: { name: 'Noah Smith', studentNo: 'S2026004', className: 'Grade 11 Class 2', phone: '13900000004', status: 'Enrolled' } },
    { id: 24, fields: { name: 'Mia Wilson', studentNo: 'S2026005', className: 'Grade 12 Class 1', phone: '13900000005', status: 'Enrolled' } },
    { id: 25, fields: { name: 'Ethan Davis', studentNo: 'S2026006', className: 'Grade 12 Class 2', phone: '13900000006', status: 'Enrolled' } },
    { id: 26, fields: { name: 'Ava Taylor', studentNo: 'S2026007', className: 'Grade 10 Class 1', phone: '13900000007', status: 'Enrolled' } },
    { id: 27, fields: { name: 'Lucas Martin', studentNo: 'S2026008', className: 'Grade 11 Class 1', phone: '13900000008', status: 'Transfer Review' } }
  ],
  courses: [
    { id: 7, fields: { name: 'Literature Reading', teacher: 'Emma Lin', credit: '3', room: 'A201', status: 'Open' } },
    { id: 8, fields: { name: 'Mathematical Modeling', teacher: 'Michael Zhou', credit: '4', room: 'B305', status: 'Open' } },
    { id: 9, fields: { name: 'English Speaking', teacher: 'Clara Xu', credit: '2', room: 'C102', status: 'Open' } },
    { id: 28, fields: { name: 'Physics Lab', teacher: 'Oliver Reed', credit: '3', room: 'D208', status: 'Open' } },
    { id: 29, fields: { name: 'Robotics Lab', teacher: 'Sophia Patel', credit: '4', room: 'E301', status: 'Open' } },
    { id: 30, fields: { name: 'World History', teacher: 'Henry Brown', credit: '2', room: 'B110', status: 'Draft' } },
    { id: 31, fields: { name: 'Visual Arts Studio', teacher: 'Grace Miller', credit: '2', room: 'F203', status: 'Open' } }
  ],
  classes: [
    { id: 10, fields: { name: 'Grade 10 Class 1', headTeacher: 'Emma Lin', grade: 'Grade 10', students: '42', room: 'A101' } },
    { id: 11, fields: { name: 'Grade 10 Class 2', headTeacher: 'Michael Zhou', grade: 'Grade 10', students: '39', room: 'A102' } },
    { id: 12, fields: { name: 'Grade 11 Class 1', headTeacher: 'Clara Xu', grade: 'Grade 11', students: '41', room: 'B201' } },
    { id: 32, fields: { name: 'Grade 11 Class 2', headTeacher: 'Oliver Reed', grade: 'Grade 11', students: '40', room: 'B202' } },
    { id: 33, fields: { name: 'Grade 12 Class 1', headTeacher: 'Sophia Patel', grade: 'Grade 12', students: '37', room: 'C301' } },
    { id: 34, fields: { name: 'Grade 12 Class 2', headTeacher: 'Daniel Kim', grade: 'Grade 12', students: '36', room: 'C302' } }
  ],
  scores: [
    { id: 13, fields: { student: 'Anna Chen', course: 'Literature Reading', score: '92', term: 'Spring 2026', level: 'Excellent' } },
    { id: 14, fields: { student: 'Leo Li', course: 'Mathematical Modeling', score: '88', term: 'Spring 2026', level: 'Good' } },
    { id: 15, fields: { student: 'Iris Wang', course: 'English Speaking', score: '95', term: 'Spring 2026', level: 'Excellent' } },
    { id: 35, fields: { student: 'Noah Smith', course: 'Physics Lab', score: '91', term: 'Spring 2026', level: 'Excellent' } },
    { id: 36, fields: { student: 'Mia Wilson', course: 'Robotics Lab', score: '86', term: 'Spring 2026', level: 'Good' } },
    { id: 37, fields: { student: 'Ethan Davis', course: 'World History', score: '79', term: 'Spring 2026', level: 'Needs Work' } },
    { id: 38, fields: { student: 'Ava Taylor', course: 'Visual Arts Studio', score: '93', term: 'Spring 2026', level: 'Excellent' } }
  ],
  users: [
    { id: 16, fields: { username: 'admin', role: 'Administrator', name: 'System Admin', status: 'Enabled' } },
    { id: 17, fields: { username: 'teacher01', role: 'Teacher', name: 'Emma Lin', status: 'Enabled' } },
    { id: 39, fields: { username: 'teacher02', role: 'Teacher', name: 'Michael Zhou', status: 'Enabled' } },
    { id: 40, fields: { username: 'student01', role: 'Student', name: 'Anna Chen', status: 'Enabled' } },
    { id: 41, fields: { username: 'reviewer01', role: 'Academic Reviewer', name: 'Grace Miller', status: 'Pending' } }
  ],
  resources: [
    { id: 101, fields: { pc: 'KE-L2-01', location: 'Kate Edger Information Commons', level: 'Level 2', area: 'East study room', zone: 'Silent study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 102, fields: { pc: 'KE-L2-02', location: 'Kate Edger Information Commons', level: 'Level 2', area: 'East study room', zone: 'Silent study', status: 'occupied', type: 'Windows desktop', nextChange: 'Available 2:30 PM' } },
    { id: 103, fields: { pc: 'KE-L2-03', location: 'Kate Edger Information Commons', level: 'Level 2', area: 'East study room', zone: 'Silent study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 104, fields: { pc: 'KE-L2-04', location: 'Kate Edger Information Commons', level: 'Level 2', area: 'East study room', zone: 'Silent study', status: 'maintenance', type: 'Windows desktop', nextChange: 'Service desk check' } },
    { id: 105, fields: { pc: 'KE-L2-05', location: 'Kate Edger Information Commons', level: 'Level 2', area: 'East study room', zone: 'Silent study', status: 'available', type: 'Dual monitor', nextChange: 'Bookable now' } },
    { id: 106, fields: { pc: 'KE-L2-06', location: 'Kate Edger Information Commons', level: 'Level 2', area: 'East study room', zone: 'Silent study', status: 'reserved', type: 'Dual monitor', nextChange: 'Reserved until 3:00 PM' } },
    { id: 107, fields: { pc: 'KE-L3-01', location: 'Kate Edger Information Commons', level: 'Level 3', area: 'North study room', zone: 'Quiet study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 108, fields: { pc: 'KE-L3-02', location: 'Kate Edger Information Commons', level: 'Level 3', area: 'North study room', zone: 'Quiet study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 109, fields: { pc: 'KE-L3-03', location: 'Kate Edger Information Commons', level: 'Level 3', area: 'North study room', zone: 'Quiet study', status: 'occupied', type: 'Windows desktop', nextChange: 'Available 4:00 PM' } },
    { id: 110, fields: { pc: 'KE-L3-04', location: 'Kate Edger Information Commons', level: 'Level 3', area: 'North study room', zone: 'Quiet study', status: 'available', type: 'Dual monitor', nextChange: 'Bookable now' } },
    { id: 111, fields: { pc: 'GL-L1-01', location: 'General Library', level: 'Level 1', area: 'Learning commons', zone: 'Individual study', status: 'occupied', type: 'Windows desktop', nextChange: 'Available 1:45 PM' } },
    { id: 112, fields: { pc: 'GL-L1-02', location: 'General Library', level: 'Level 1', area: 'Learning commons', zone: 'Individual study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 113, fields: { pc: 'GL-L1-03', location: 'General Library', level: 'Level 1', area: 'Learning commons', zone: 'Individual study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 114, fields: { pc: 'GL-L1-04', location: 'General Library', level: 'Level 1', area: 'Learning commons', zone: 'Individual study', status: 'offline', type: 'Windows desktop', nextChange: 'Network issue' } },
    { id: 115, fields: { pc: 'GL-L2-01', location: 'General Library', level: 'Level 2', area: 'South study room', zone: 'Postgraduate study', status: 'available', type: 'Dual monitor', nextChange: 'Bookable now' } },
    { id: 116, fields: { pc: 'GL-L2-02', location: 'General Library', level: 'Level 2', area: 'South study room', zone: 'Postgraduate study', status: 'reserved', type: 'Dual monitor', nextChange: 'Reserved until 2:00 PM' } },
    { id: 117, fields: { pc: 'GL-L2-03', location: 'General Library', level: 'Level 2', area: 'South study room', zone: 'Postgraduate study', status: 'available', type: 'Windows desktop', nextChange: 'Bookable now' } },
    { id: 118, fields: { pc: 'OGGB-L2-01', location: 'Owen G Glenn Building', level: 'Level 2', area: 'Computer lab 240A', zone: 'Business study', status: 'occupied', type: 'Finance workstation', nextChange: 'Available 3:15 PM' } },
    { id: 119, fields: { pc: 'OGGB-L2-02', location: 'Owen G Glenn Building', level: 'Level 2', area: 'Computer lab 240A', zone: 'Business study', status: 'available', type: 'Finance workstation', nextChange: 'Bookable now' } },
    { id: 120, fields: { pc: 'OGGB-L2-03', location: 'Owen G Glenn Building', level: 'Level 2', area: 'Computer lab 240A', zone: 'Business study', status: 'available', type: 'Finance workstation', nextChange: 'Bookable now' } },
    { id: 121, fields: { pc: 'ENG-L3-01', location: 'Engineering Building 405', level: 'Level 3', area: 'Collaboration room', zone: 'Project work', status: 'available', type: 'CAD workstation', nextChange: 'Bookable now' } },
    { id: 122, fields: { pc: 'ENG-L3-02', location: 'Engineering Building 405', level: 'Level 3', area: 'Collaboration room', zone: 'Project work', status: 'maintenance', type: 'CAD workstation', nextChange: 'Software update' } },
    { id: 123, fields: { pc: 'ENG-L3-03', location: 'Engineering Building 405', level: 'Level 3', area: 'Collaboration room', zone: 'Project work', status: 'available', type: 'CAD workstation', nextChange: 'Bookable now' } },
    { id: 124, fields: { pc: 'ENG-L3-04', location: 'Engineering Building 405', level: 'Level 3', area: 'Collaboration room', zone: 'Project work', status: 'occupied', type: 'CAD workstation', nextChange: 'Available 5:00 PM' } }
  ]
}

const salaryByDepartment = {
  'Language Arts': 6800,
  Mathematics: 7200,
  English: 6600,
  Science: 7400,
  'Computer Science': 7800,
  'Physical Education': 6200,
  Arts: 6100,
  History: 6400
}

const activeModule = ref('dashboard')
const keyword = ref('')
const dialogOpen = ref(false)
const editingRecord = ref(null)
const form = reactive({})
const records = reactive(JSON.parse(JSON.stringify(fallbackData)))
const online = ref(false)
const liveUpdatedAt = ref(new Date())
const selectedLocation = ref('all')
const selectedLevel = ref('all')
const selectedStatus = ref('all')
let liveTimer

const activeConfig = computed(() => modules.find((item) => item.key === activeModule.value))
const totalPeople = computed(() => (records.teachers?.length || 0) + (records.students?.length || 0))
const searchPlaceholder = computed(() => {
  if (activeModule.value === 'resources') {
    return 'Search PC, building, level, or workstation type...'
  }
  if (activeModule.value === 'reports') {
    return 'Search reports, teachers, grades, or departments...'
  }
  return 'Search names, courses, classes...'
})

const statCards = computed(() => [
  { key: 'teachers', label: 'Teachers', value: records.teachers.length, note: `Across ${departmentSummary.value.length} departments` },
  { key: 'students', label: 'Students', value: records.students.length, note: 'Managed by class' },
  { key: 'courses', label: 'Courses', value: records.courses.length, note: 'Clear course status' },
  { key: 'resources', label: 'Lab PCs', value: studyRoomStats.value.available, note: 'Available now' },
  { key: 'users', label: 'Accounts', value: records.users.length, note: online.value ? 'Backend connected' : 'Local demo data' }
])

const scoreSummary = computed(() => {
  const scores = records.scores || []
  const total = scores.length || 1
  const countByLevel = scores.reduce((summary, record) => {
    const level = record.fields.level || 'Unmarked'
    summary[level] = (summary[level] || 0) + 1
    return summary
  }, {})
  return {
    excellentRate: Math.round(((countByLevel.Excellent || 0) / total) * 100),
    countByLevel,
    total
  }
})

const scoreBars = computed(() =>
  ['Excellent', 'Good', 'Needs Work'].map((label) => ({
    label,
    value: Math.round(((scoreSummary.value.countByLevel[label] || 0) / scoreSummary.value.total) * 100)
  }))
)

const departmentSummary = computed(() => {
  const departments = records.teachers.reduce((summary, record) => {
    const department = record.fields.department || 'Unassigned'
    summary[department] = (summary[department] || 0) + 1
    return summary
  }, {})
  return Object.entries(departments)
    .map(([name, count]) => ({ name, count }))
    .sort((left, right) => left.name.localeCompare(right.name))
})

const payrollRows = computed(() =>
  records.teachers.map((record) => ({
    name: record.fields.name,
    department: record.fields.department,
    status: record.fields.status,
    salary: salaryByDepartment[record.fields.department] || 6000
  }))
)

const totalPayroll = computed(() => payrollRows.value.reduce((total, row) => total + row.salary, 0))

const payrollByDepartment = computed(() => {
  const totals = payrollRows.value.reduce((summary, row) => {
    summary[row.department] = (summary[row.department] || 0) + row.salary
    return summary
  }, {})
  const max = Math.max(...Object.values(totals), 1)
  return Object.entries(totals)
    .map(([department, total]) => ({
      department,
      total,
      percent: Math.round((total / max) * 100)
    }))
    .sort((left, right) => right.total - left.total)
})

const teacherStudentRatio = computed(() => {
  if (!records.teachers.length) {
    return 0
  }
  return Math.round(records.students.length / records.teachers.length)
})

const averageScore = computed(() => {
  const values = records.scores.map((record) => Number(record.fields.score)).filter((score) => !Number.isNaN(score))
  if (!values.length) {
    return 0
  }
  return Math.round(values.reduce((sum, score) => sum + score, 0) / values.length)
})

const openCourseCount = computed(() => records.courses.filter((record) => record.fields.status === 'Open').length)

const totalClassStudents = computed(() =>
  records.classes.reduce((total, record) => total + Number(record.fields.students || 0), 0)
)

const classReports = computed(() =>
  records.classes.map((record, index) => {
    const scoreRecords = records.scores.filter((score, scoreIndex) => scoreIndex % records.classes.length === index)
    const average = averageFromRecords(scoreRecords)
    return {
      className: record.fields.name,
      grade: record.fields.grade,
      headTeacher: record.fields.headTeacher,
      students: record.fields.students,
      average,
      status: average >= 90 ? 'Excellent' : average >= 84 ? 'Good' : 'Review'
    }
  })
)

const gradeReports = computed(() => {
  const groups = classReports.value.reduce((summary, row) => {
    if (!summary[row.grade]) {
      summary[row.grade] = []
    }
    summary[row.grade].push(row.average)
    return summary
  }, {})
  return Object.entries(groups).map(([grade, scores]) => ({
    grade,
    average: Math.round(scores.reduce((sum, score) => sum + score, 0) / scores.length)
  }))
})

const teacherCourseMatrix = computed(() =>
  records.teachers.map((teacher) => {
    const courses = records.courses
      .filter((course) => course.fields.teacher === teacher.fields.name)
      .map((course) => course.fields.name)
    const classCount = records.classes.filter((item) => item.fields.headTeacher === teacher.fields.name).length
    return {
      teacher: teacher.fields.name,
      department: teacher.fields.department,
      courses,
      classCount
    }
  })
)

const filteredRecords = computed(() => {
  const list = records[activeModule.value] || []
  if (!keyword.value.trim()) {
    return list
  }
  const value = keyword.value.trim().toLowerCase()
  return list.filter((record) => Object.values(record.fields).join(' ').toLowerCase().includes(value))
})

const studyMachines = computed(() => {
  return (records.resources || []).map((record) => ({
    id: record.id,
    ...record.fields
  }))
})

const locationOptions = computed(() => uniqueSorted(studyMachines.value.map((machine) => machine.location)))
const levelOptions = computed(() => uniqueSorted(studyMachines.value.map((machine) => machine.level)))

const filteredStudyMachines = computed(() => {
  const search = keyword.value.trim().toLowerCase()
  return studyMachines.value.filter((machine) => {
    const isAvailable = isMachineAvailable(machine)
    const matchesLocation = selectedLocation.value === 'all' || machine.location === selectedLocation.value
    const matchesLevel = selectedLevel.value === 'all' || machine.level === selectedLevel.value
    const matchesStatus =
      selectedStatus.value === 'all' ||
      (selectedStatus.value === 'available' && isAvailable) ||
      (selectedStatus.value === 'unavailable' && !isAvailable)
    const matchesSearch =
      !search ||
      [machine.pc, machine.location, machine.level, machine.area, machine.zone, machine.type]
        .join(' ')
        .toLowerCase()
        .includes(search)

    return matchesLocation && matchesLevel && matchesStatus && matchesSearch
  })
})

const groupedStudyMachines = computed(() => {
  const groups = new Map()
  filteredStudyMachines.value.forEach((machine) => {
    const key = `${machine.location}-${machine.level}-${machine.area}`
    if (!groups.has(key)) {
      groups.set(key, {
        key,
        location: machine.location,
        level: machine.level,
        area: machine.area,
        machines: []
      })
    }
    groups.get(key).machines.push(machine)
  })

  return Array.from(groups.values()).map((group) => ({
    ...group,
    total: group.machines.length,
    available: group.machines.filter(isMachineAvailable).length
  }))
})

const studyRoomStats = computed(() => {
  const total = studyMachines.value.length
  const available = studyMachines.value.filter(isMachineAvailable).length
  const unavailable = total - available
  const utilisation = total ? Math.round((unavailable / total) * 100) : 0
  return { total, available, unavailable, utilisation }
})

const liveUpdatedLabel = computed(() =>
  new Intl.DateTimeFormat('en-NZ', {
    hour: 'numeric',
    minute: '2-digit',
    second: '2-digit',
    hour12: true,
    timeZoneName: 'short'
  }).format(liveUpdatedAt.value)
)

onMounted(async () => {
  await loadAll()
  liveTimer = window.setInterval(() => {
    liveUpdatedAt.value = new Date()
  }, 15000)
})

onBeforeUnmount(() => {
  window.clearInterval(liveTimer)
})

async function loadAll() {
  await Promise.all(modules.filter((item) => item.key !== 'dashboard').map((item) => loadModule(item.key)))
}

async function request(path, options) {
  const response = await fetch(`${apiBase}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  })
  if (!response.ok) {
    throw new Error('Request failed')
  }
  const result = await response.json()
  online.value = true
  return result.data
}

async function loadModule(module) {
  try {
    const data = await request(`/${module}`)
    if (data.length) {
      records[module] = data
    }
  } catch {
    online.value = false
  }
}

function setActiveModule(module) {
  activeModule.value = module
  keyword.value = ''
}

function resetForm() {
  Object.keys(form).forEach((key) => delete form[key])
  activeConfig.value.fields?.forEach((field) => {
    form[field.key] = ''
  })
}

function openCreateDialog() {
  if (activeModule.value === 'dashboard') {
    activeModule.value = 'teachers'
  }
  editingRecord.value = null
  resetForm()
  dialogOpen.value = true
}

function openEditDialog(record) {
  editingRecord.value = record
  resetForm()
  Object.assign(form, record.fields)
  dialogOpen.value = true
}

function closeDialog() {
  dialogOpen.value = false
}

async function saveRecord() {
  const payload = { ...form }
  const module = activeModule.value
  try {
    if (editingRecord.value) {
      const updated = await request(`/${module}/${editingRecord.value.id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      })
      replaceRecord(module, updated)
    } else {
      const created = await request(`/${module}`, {
        method: 'POST',
        body: JSON.stringify(payload)
      })
      records[module].push(created)
    }
  } catch {
    if (editingRecord.value) {
      replaceRecord(module, { id: editingRecord.value.id, fields: payload })
    } else {
      records[module].push({ id: Date.now(), fields: payload })
    }
  }
  closeDialog()
}

async function deleteRecord(id) {
  const module = activeModule.value
  try {
    await request(`/${module}/${id}`, { method: 'DELETE' })
  } catch {
    online.value = false
  }
  records[module] = records[module].filter((record) => record.id !== id)
}

function replaceRecord(module, nextRecord) {
  records[module] = records[module].map((record) => (record.id === nextRecord.id ? nextRecord : record))
}

function exportRecords() {
  const fields = activeConfig.value.fields || []
  const header = fields.map((field) => field.label)
  const rows = filteredRecords.value.map((record) => fields.map((field) => record.fields[field.key] ?? ''))
  const csv = [header, ...rows].map((row) => row.map(escapeCsvValue).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${activeModule.value}-records.csv`
  link.click()
  URL.revokeObjectURL(url)
}

function exportReportSummary() {
  const rows = [
    ['Metric', 'Value'],
    ['Total payroll due', formatCurrency(totalPayroll.value)],
    ['Teacher-student ratio', `1:${teacherStudentRatio.value}`],
    ['Average score', averageScore.value],
    ['Open courses', openCourseCount.value],
    ['Class capacity', totalClassStudents.value]
  ]
  downloadCsv('reports-summary.csv', rows)
}

function downloadCsv(filename, rows) {
  const csv = rows.map((row) => row.map(escapeCsvValue).join(',')).join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

function escapeCsvValue(value) {
  const text = String(value)
  if (/[",\n]/.test(text)) {
    return `"${text.replaceAll('"', '""')}"`
  }
  return text
}

function averageFromRecords(scoreRecords) {
  if (!scoreRecords.length) {
    return averageScore.value
  }
  const total = scoreRecords.reduce((sum, record) => sum + Number(record.fields.score || 0), 0)
  return Math.round(total / scoreRecords.length)
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 0
  }).format(value)
}

function refreshStudyRooms() {
  liveUpdatedAt.value = new Date()
}

function isMachineAvailable(machine) {
  return machine.status === 'available'
}

function machineStateLabel(machine) {
  return isMachineAvailable(machine) ? 'Available' : 'Unavailable'
}

function machineUnavailableLabel(machine) {
  if (machine.status === 'maintenance' || machine.status === 'offline') {
    return 'Service issue'
  }
  return 'Unavailable'
}

function machineStatusClass(machine) {
  if (isMachineAvailable(machine)) {
    return 'is-available'
  }
  if (machine.status === 'maintenance' || machine.status === 'offline') {
    return 'is-maintenance'
  }
  return 'is-unavailable'
}

function uniqueSorted(values) {
  return Array.from(new Set(values.filter(Boolean))).sort((a, b) => a.localeCompare(b, 'en-NZ'))
}
</script>
