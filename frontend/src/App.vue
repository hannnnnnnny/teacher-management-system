<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">樱</div>
        <div>
          <strong>教师管理平台</strong>
          <span>Sakura Campus</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="主导航">
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
          <p class="eyebrow">2026 春季学期</p>
          <h1>{{ activeConfig.title }}</h1>
        </div>
        <div class="top-actions">
          <input v-model="keyword" class="search-input" type="search" placeholder="搜索姓名、课程、班级..." />
          <button class="primary-button" @click="openCreateDialog">新增{{ activeConfig.short }}</button>
        </div>
      </header>

      <section v-if="activeModule === 'dashboard'" class="dashboard">
        <div class="hero-panel">
          <div>
            <p class="eyebrow">校园数据总览</p>
            <h2>用温柔清晰的方式管理教学日常</h2>
            <p>教师、学生、课程、班级、成绩与账号集中管理，适合课程设计、毕业设计和项目展示。</p>
          </div>
          <div class="hero-badge">
            <strong>{{ totalPeople }}</strong>
            <span>师生档案</span>
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
              <h3>今日待办</h3>
              <span>6 项</span>
            </div>
            <ul class="todo-list">
              <li>审核新入职教师资料</li>
              <li>录入高一二班数学成绩</li>
              <li>更新英语口语课程教室</li>
              <li>检查学生账号启用状态</li>
            </ul>
          </section>
          <section class="panel">
            <div class="section-title">
              <h3>成绩分布</h3>
              <span>优秀率 67%</span>
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
          <span>{{ filteredRecords.length }} 条记录</span>
        </div>

        <div class="table-wrap">
          <table>
            <thead>
              <tr>
                <th v-for="field in activeConfig.fields" :key="field.key">{{ field.label }}</th>
                <th>操作</th>
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
                  <button class="ghost-button" @click="openEditDialog(record)">编辑</button>
                  <button class="danger-button" @click="deleteRecord(record.id)">删除</button>
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
            <h3>{{ editingRecord ? '编辑' : '新增' }}{{ activeConfig.short }}</h3>
            <p>填写后保存到当前模块。</p>
          </div>
          <button class="icon-button" type="button" @click="closeDialog">×</button>
        </div>

        <label v-for="field in activeConfig.fields" :key="field.key" class="form-field">
          <span>{{ field.label }}</span>
          <input v-model="form[field.key]" :type="field.type || 'text'" :placeholder="'请输入' + field.label" required />
        </label>

        <div class="modal-actions">
          <button class="ghost-button" type="button" @click="closeDialog">取消</button>
          <button class="primary-button" type="submit">保存</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

const apiBase = 'http://localhost:8080/api'

const modules = [
  { key: 'dashboard', label: '首页总览', title: '首页总览', short: '事项', icon: '⌂' },
  {
    key: 'teachers',
    label: '教师管理',
    title: '教师管理',
    short: '教师',
    icon: '师',
    description: '维护教师档案、联系方式、所属教研组和任职状态。',
    fields: [
      { key: 'name', label: '姓名' },
      { key: 'department', label: '教研组' },
      { key: 'phone', label: '电话' },
      { key: 'email', label: '邮箱' },
      { key: 'status', label: '状态' }
    ]
  },
  {
    key: 'students',
    label: '学生管理',
    title: '学生管理',
    short: '学生',
    icon: '生',
    description: '管理学生学号、班级、联系方式和在校状态。',
    fields: [
      { key: 'name', label: '姓名' },
      { key: 'studentNo', label: '学号' },
      { key: 'className', label: '班级' },
      { key: 'phone', label: '电话' },
      { key: 'status', label: '状态' }
    ]
  },
  {
    key: 'courses',
    label: '课程管理',
    title: '课程管理',
    short: '课程',
    icon: '课',
    description: '管理课程名称、授课教师、学分、教室和开课状态。',
    fields: [
      { key: 'name', label: '课程名称' },
      { key: 'teacher', label: '授课教师' },
      { key: 'credit', label: '学分', type: 'number' },
      { key: 'room', label: '教室' },
      { key: 'status', label: '状态' }
    ]
  },
  {
    key: 'classes',
    label: '班级管理',
    title: '班级管理',
    short: '班级',
    icon: '班',
    description: '维护班级、年级、班主任、人数与教室信息。',
    fields: [
      { key: 'name', label: '班级名称' },
      { key: 'headTeacher', label: '班主任' },
      { key: 'grade', label: '年级' },
      { key: 'students', label: '人数', type: 'number' },
      { key: 'room', label: '教室' }
    ]
  },
  {
    key: 'scores',
    label: '成绩管理',
    title: '成绩管理',
    short: '成绩',
    icon: '绩',
    description: '录入学生课程成绩、学期和等级。',
    fields: [
      { key: 'student', label: '学生' },
      { key: 'course', label: '课程' },
      { key: 'score', label: '分数', type: 'number' },
      { key: 'term', label: '学期' },
      { key: 'level', label: '等级' }
    ]
  },
  {
    key: 'users',
    label: '账号权限',
    title: '账号权限',
    short: '账号',
    icon: '权',
    description: '管理系统账号、角色、姓名与启用状态。',
    fields: [
      { key: 'username', label: '用户名' },
      { key: 'role', label: '角色' },
      { key: 'name', label: '姓名' },
      { key: 'status', label: '状态' }
    ]
  }
]

const fallbackData = {
  teachers: [
    { id: 1, fields: { name: '林樱', department: '语文组', phone: '13800000001', email: 'linying@example.com', status: '在职' } },
    { id: 2, fields: { name: '周明', department: '数学组', phone: '13800000002', email: 'zhouming@example.com', status: '在职' } },
    { id: 3, fields: { name: '许晴', department: '英语组', phone: '13800000003', email: 'xuqing@example.com', status: '请假' } }
  ],
  students: [
    { id: 4, fields: { name: '陈安安', studentNo: 'S2026001', className: '高一一班', phone: '13900000001', status: '正常' } },
    { id: 5, fields: { name: '李星河', studentNo: 'S2026002', className: '高一二班', phone: '13900000002', status: '正常' } },
    { id: 6, fields: { name: '王若溪', studentNo: 'S2026003', className: '高二一班', phone: '13900000003', status: '正常' } }
  ],
  courses: [
    { id: 7, fields: { name: '语文阅读', teacher: '林樱', credit: '3', room: 'A201', status: '开课' } },
    { id: 8, fields: { name: '数学建模', teacher: '周明', credit: '4', room: 'B305', status: '开课' } },
    { id: 9, fields: { name: '英语口语', teacher: '许晴', credit: '2', room: 'C102', status: '开课' } }
  ],
  classes: [
    { id: 10, fields: { name: '高一一班', headTeacher: '林樱', grade: '高一', students: '42', room: 'A101' } },
    { id: 11, fields: { name: '高一二班', headTeacher: '周明', grade: '高一', students: '39', room: 'A102' } },
    { id: 12, fields: { name: '高二一班', headTeacher: '许晴', grade: '高二', students: '41', room: 'B201' } }
  ],
  scores: [
    { id: 13, fields: { student: '陈安安', course: '语文阅读', score: '92', term: '2026春季', level: '优秀' } },
    { id: 14, fields: { student: '李星河', course: '数学建模', score: '88', term: '2026春季', level: '良好' } },
    { id: 15, fields: { student: '王若溪', course: '英语口语', score: '95', term: '2026春季', level: '优秀' } }
  ],
  users: [
    { id: 16, fields: { username: 'admin', role: '管理员', name: '系统管理员', status: '启用' } },
    { id: 17, fields: { username: 'teacher01', role: '教师', name: '林樱', status: '启用' } }
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
  { key: 'teachers', label: '教师', value: records.teachers.length, note: '覆盖 3 个教研组' },
  { key: 'students', label: '学生', value: records.students.length, note: '按班级统一管理' },
  { key: 'courses', label: '课程', value: records.courses.length, note: '课程状态清晰' },
  { key: 'scores', label: '成绩', value: records.scores.length, note: '支持快速录入' },
  { key: 'users', label: '账号', value: records.users.length, note: online.value ? '后端已连接' : '本地演示数据' }
])

const scoreBars = [
  { label: '优秀', value: 67 },
  { label: '良好', value: 25 },
  { label: '待提升', value: 8 }
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

