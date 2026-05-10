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
          <p class="eyebrow">Spring Semester 2026</p>
          <h1>{{ activeConfig.title }}</h1>
        </div>
        <div class="top-actions">
          <input v-model="keyword" class="search-input" type="search" placeholder="Search names, courses, classes..." />
          <button class="primary-button" @click="openCreateDialog">Add {{ activeConfig.short }}</button>
        </div>
      </header>

      <section v-if="activeModule === 'dashboard'" class="dashboard">
        <div class="hero-panel">
          <div>
            <p class="eyebrow">Campus Data Overview</p>
            <h2>Manage daily teaching work with clarity</h2>
            <p>Centralized management for teachers, students, courses, classes, scores, and user accounts.</p>
          </div>
          <div class="hero-badge">
            <strong>{{ totalPeople }}</strong>
            <span>People Records</span>
          </div>
        </div>

        <div class="stats-grid">
          <article v-for="card in statCards" :key="card.key" class="stat-card">
            <span>{{ card.label }}</span>
            <strong>{{ card.value }}</strong>
            <small>{{ card.note }}</small>
          </article>
        </div>

        <div class="content-grid">
          <section class="panel">
            <div class="section-title">
              <h3>Today's Tasks</h3>
              <span>6 items</span>
            </div>
            <ul class="todo-list">
              <li>Review new teacher profile documents</li>
              <li>Enter math scores for Class 2</li>
              <li>Update the English speaking classroom</li>
              <li>Check active student account status</li>
            </ul>
          </section>
          <section class="panel">
            <div class="section-title">
              <h3>Score Distribution</h3>
              <span>Excellent rate 67%</span>
            </div>
            <div class="bars">
              <div v-for="bar in scoreBars" :key="bar.label">
                <span>{{ bar.label }}</span>
                <div><i :style="{ width: bar.value + '%' }"></i></div>
                <b>{{ bar.value }}%</b>
              </div>
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
          <span>{{ filteredRecords.length }} records</span>
        </div>

        <div class="table-wrap">
          <table>
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
          <button class="icon-button" type="button" @click="closeDialog">×</button>
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
import { computed, onMounted, reactive, ref } from 'vue'

const apiBase = 'http://localhost:8080/api'

const modules = [
  { key: 'dashboard', label: 'Dashboard', title: 'Dashboard', short: 'Item', icon: '⌂' },
  {
    key: 'teachers',
    label: 'Teachers',
    title: 'Teacher Management',
    short: 'Teacher',
    icon: 'T',
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
    description: 'Manage system accounts, roles, display names, and account status.',
    fields: [
      { key: 'username', label: 'Username' },
      { key: 'role', label: 'Role' },
      { key: 'name', label: 'Name' },
      { key: 'status', label: 'Status' }
    ]
  }
]

const fallbackData = {
  teachers: [
    { id: 1, fields: { name: 'Emma Lin', department: 'Language Arts', phone: '13800000001', email: 'emma.lin@example.com', status: 'Active' } },
    { id: 2, fields: { name: 'Michael Zhou', department: 'Mathematics', phone: '13800000002', email: 'michael.zhou@example.com', status: 'Active' } },
    { id: 3, fields: { name: 'Clara Xu', department: 'English', phone: '13800000003', email: 'clara.xu@example.com', status: 'On Leave' } }
  ],
  students: [
    { id: 4, fields: { name: 'Anna Chen', studentNo: 'S2026001', className: 'Grade 10 Class 1', phone: '13900000001', status: 'Enrolled' } },
    { id: 5, fields: { name: 'Leo Li', studentNo: 'S2026002', className: 'Grade 10 Class 2', phone: '13900000002', status: 'Enrolled' } },
    { id: 6, fields: { name: 'Iris Wang', studentNo: 'S2026003', className: 'Grade 11 Class 1', phone: '13900000003', status: 'Enrolled' } }
  ],
  courses: [
    { id: 7, fields: { name: 'Literature Reading', teacher: 'Emma Lin', credit: '3', room: 'A201', status: 'Open' } },
    { id: 8, fields: { name: 'Mathematical Modeling', teacher: 'Michael Zhou', credit: '4', room: 'B305', status: 'Open' } },
    { id: 9, fields: { name: 'English Speaking', teacher: 'Clara Xu', credit: '2', room: 'C102', status: 'Open' } }
  ],
  classes: [
    { id: 10, fields: { name: 'Grade 10 Class 1', headTeacher: 'Emma Lin', grade: 'Grade 10', students: '42', room: 'A101' } },
    { id: 11, fields: { name: 'Grade 10 Class 2', headTeacher: 'Michael Zhou', grade: 'Grade 10', students: '39', room: 'A102' } },
    { id: 12, fields: { name: 'Grade 11 Class 1', headTeacher: 'Clara Xu', grade: 'Grade 11', students: '41', room: 'B201' } }
  ],
  scores: [
    { id: 13, fields: { student: 'Anna Chen', course: 'Literature Reading', score: '92', term: 'Spring 2026', level: 'Excellent' } },
    { id: 14, fields: { student: 'Leo Li', course: 'Mathematical Modeling', score: '88', term: 'Spring 2026', level: 'Good' } },
    { id: 15, fields: { student: 'Iris Wang', course: 'English Speaking', score: '95', term: 'Spring 2026', level: 'Excellent' } }
  ],
  users: [
    { id: 16, fields: { username: 'admin', role: 'Administrator', name: 'System Admin', status: 'Enabled' } },
    { id: 17, fields: { username: 'teacher01', role: 'Teacher', name: 'Emma Lin', status: 'Enabled' } }
  ]
}

const activeModule = ref('dashboard')
const keyword = ref('')
const dialogOpen = ref(false)
const editingRecord = ref(null)
const form = reactive({})
const records = reactive(JSON.parse(JSON.stringify(fallbackData)))
const online = ref(false)

const activeConfig = computed(() => modules.find((item) => item.key === activeModule.value))
const totalPeople = computed(() => (records.teachers?.length || 0) + (records.students?.length || 0))

const statCards = computed(() => [
  { key: 'teachers', label: 'Teachers', value: records.teachers.length, note: 'Across 3 departments' },
  { key: 'students', label: 'Students', value: records.students.length, note: 'Managed by class' },
  { key: 'courses', label: 'Courses', value: records.courses.length, note: 'Clear course status' },
  { key: 'scores', label: 'Scores', value: records.scores.length, note: 'Fast score entry' },
  { key: 'users', label: 'Accounts', value: records.users.length, note: online.value ? 'Backend connected' : 'Local demo data' }
])

const scoreBars = [
  { label: 'Excellent', value: 67 },
  { label: 'Good', value: 25 },
  { label: 'Needs Work', value: 8 }
]

const filteredRecords = computed(() => {
  const list = records[activeModule.value] || []
  if (!keyword.value.trim()) {
    return list
  }
  const value = keyword.value.trim().toLowerCase()
  return list.filter((record) => Object.values(record.fields).join(' ').toLowerCase().includes(value))
})

onMounted(async () => {
  await loadAll()
})

async function loadAll() {
  await Promise.all(modules.filter((item) => item.fields).map((item) => loadModule(item.key)))
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
    records[module] = await request(`/${module}`)
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
</script>
