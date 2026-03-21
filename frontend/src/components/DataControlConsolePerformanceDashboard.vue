<template>
  <div class="performance-dashboard">
    <div class="header-container">
      <h2>绩效看板</h2>
      <div class="action-buttons">
        <div class="filter-item" v-if="hasData">
          <label>考勤项目</label>
          <el-select v-model="selectedProject" placeholder="全部项目" clearable filterable style="width: 180px;">
            <el-option v-for="project in projectList" :key="project" :label="project" :value="project"></el-option>
          </el-select>
        </div>
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="false"
          accept=".xlsx,.xls"
          :on-change="handleFileChange"
        >
          <template #trigger>
            <button class="btn btn-primary">导入绩效文件</button>
          </template>
        </el-upload>
        <button class="btn btn-secondary" @click="clearData" :disabled="!hasData">清空数据</button>
      </div>
    </div>

    <div v-if="!hasData" class="placeholder">
      <div class="placeholder-card card wallet">
        <div class="overlay"></div>
        <div class="circle">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="23 29 78 60" height="60px" width="78px">
            <g transform="translate(23.000000, 29.500000)" fill-rule="evenodd" fill="none" stroke-width="1" stroke="none">
              <rect rx="4.70247832" height="21.8788565" width="9.40495664" y="26.0333433" x="67.8357511" fill="#AC8BE9"></rect>
              <rect rx="4.70247832" height="10.962961" width="9.40495664" y="38.776399" x="67.8357511" fill="#6A5297"></rect>
              <polygon points="57.3086772 0 67.1649301 26.3776902 14.4413177 45.0699507 4.58506484 18.6922605" fill="#6A5297"></polygon>
              <path fill="#8B6FC0" d="M0,19.6104296 C0,16.2921718 2.68622235,13.6021923 5.99495032,13.6021923 L67.6438591,13.6021923 C70.9547788,13.6021923 73.6388095,16.2865506 73.6388095,19.6104296 L73.6388095,52.6639057 C73.6388095,55.9821635 70.9525871,58.672143 67.6438591,58.672143 L5.99495032,58.672143 C2.68403068,58.672143 0,55.9877847 0,52.6639057 L0,19.6104296 Z"></path>
              <path fill="#F6F1FF" d="M47.5173769,27.0835169 C45.0052827,24.5377699 40.9347162,24.5377699 38.422622,27.0835169 L36.9065677,28.6198808 L35.3905134,27.0835169 C32.8799903,24.5377699 28.8078527,24.5377699 26.2957585,27.0835169 C23.7852354,29.6292639 23.7852354,33.7559532 26.2957585,36.3001081 L36.9065677,47.0530632 L47.5173769,36.3001081 C50.029471,33.7559532 50.029471,29.6292639 47.5173769,27.0835169"></path>
              <rect height="12.863158" width="15.6082259" y="26.1162588" x="58.0305835" fill="#AC8BE9"></rect>
              <ellipse ry="2.23319575" rx="2.20116007" cy="33.0919007" cx="65.8346965" fill="#FFFFFF"></ellipse>
            </g>
          </svg>
        </div>
        <p>绩效看板</p>
        <p class="card-hint">请导入绩效汇总文件（.xlsx）</p>
      </div>
    </div>

    <div v-else class="dashboard-content">
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-title">总人数</div>
          <div class="stat-value">{{ stats.totalPeople }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">项目数量</div>
          <div class="stat-value">{{ stats.totalProjects }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">绩效总和</div>
          <div class="stat-value">{{ stats.totalPerformance.toFixed(2) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">平均绩效</div>
          <div class="stat-value">{{ stats.avgPerformance.toFixed(2) }}</div>
        </div>
      </div>

      <div class="chart-grid">
        <div class="chart-card">
          <div class="chart-header">
            <h3>个人绩效排名</h3>
            <el-select v-model="chart1Sort" size="small" style="width: 100px;">
              <el-option label="降序" value="desc"></el-option>
              <el-option label="升序" value="asc"></el-option>
            </el-select>
          </div>
          <div ref="chart1Ref" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>项目绩效总和</h3>
            <el-select v-model="chart2Sort" size="small" style="width: 100px;">
              <el-option label="降序" value="desc"></el-option>
              <el-option label="升序" value="asc"></el-option>
            </el-select>
          </div>
          <div ref="chart2Ref" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>项目平均绩效</h3>
            <el-select v-model="chart3Sort" size="small" style="width: 100px;">
              <el-option label="降序" value="desc"></el-option>
              <el-option label="升序" value="asc"></el-option>
            </el-select>
          </div>
          <div ref="chart3Ref" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>绩效分布情况</h3>
          </div>
          <div ref="chart4Ref" class="chart-container"></div>
        </div>
      </div>

      <div class="data-table-section">
        <div class="table-header">
          <h3>详细数据</h3>
          <div class="table-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索姓名/项目"
              clearable
              style="width: 200px;"
            >
              <template #prefix>
                <span>🔍</span>
              </template>
            </el-input>
          </div>
        </div>
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th>月份</th>
                <th>姓名</th>
                <th>考勤项目</th>
                <th>绩效人天合计</th>
                <th>额外结算人天</th>
                <th>人员类型</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(row, index) in filteredTableData" :key="index">
                <td>{{ row.month }}</td>
                <td>{{ row.name }}</td>
                <td>{{ row.project }}</td>
                <td>{{ row.performanceTotal?.toFixed(2) || '-' }}</td>
                <td>{{ row.extraPerformance?.toFixed(2) || '-' }}</td>
                <td>{{ row.personType || '-' }}</td>
              </tr>
            </tbody>
          </table>
          <div v-if="filteredTableData.length === 0" class="no-data">暂无匹配数据</div>
        </div>
      </div>
    </div>
  </div>

  <!-- 图表详情弹窗 -->
  <Teleport to="body">
    <div v-if="showChartModal" class="modal-overlay" @click.self="closeChartModal">
      <div class="modal-container chart-modal">
        <div class="modal-header">
          <h3>{{ chartModalTitle }}</h3>
          <button class="close-btn" @click="closeChartModal">×</button>
        </div>
        <div class="modal-body">
          <div ref="modalChartRef" class="modal-chart-container"></div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, markRaw, nextTick } from 'vue';
import { ElMessage, ElUpload, ElSelect, ElOption, ElInput } from 'element-plus';
import 'element-plus/dist/index.css';
import * as echarts from 'echarts';
import * as XLSX from 'xlsx';

const uploadRef = ref(null);
const hasData = ref(false);
const rawData = ref([]);
const selectedProject = ref('');
const chart1Sort = ref('desc');
const chart2Sort = ref('desc');
const chart3Sort = ref('desc');
const searchKeyword = ref('');

const chart1Ref = ref(null);
const chart2Ref = ref(null);
const chart3Ref = ref(null);
const chart4Ref = ref(null);
const modalChartRef = ref(null);

let chart1Instance = null;
let chart2Instance = null;
let chart3Instance = null;
let chart4Instance = null;
let modalChartInstance = null;

const showChartModal = ref(false);
const chartModalTitle = ref('');
const currentChartType = ref('');

const projectList = computed(() => {
  const projects = new Set(rawData.value.map(r => r.project).filter(Boolean));
  return Array.from(projects).sort();
});

const filteredData = computed(() => {
  if (!selectedProject.value) return rawData.value;
  return rawData.value.filter(r => r.project === selectedProject.value);
});

const stats = computed(() => {
  if (!hasData.value || filteredData.value.length === 0) {
    return { totalPeople: 0, totalProjects: 0, totalPerformance: 0, avgPerformance: 0 };
  }
  
  const people = new Set(filteredData.value.map(r => r.name));
  const projects = new Set(filteredData.value.map(r => r.project));
  const total = filteredData.value.reduce((sum, r) => sum + (r.extraPerformance || 0), 0);
  
  return {
    totalPeople: people.size,
    totalProjects: projects.size,
    totalPerformance: total,
    avgPerformance: people.size > 0 ? total / people.size : 0
  };
});

const filteredTableData = computed(() => {
  let data = selectedProject.value ? filteredData.value : rawData.value;
  if (!searchKeyword.value) return data;
  const keyword = searchKeyword.value.toLowerCase();
  return data.filter(r => 
    r.name?.toLowerCase().includes(keyword) || 
    r.project?.toLowerCase().includes(keyword)
  );
});

const handleFileChange = (uploadFile) => {
  const file = uploadFile.raw;
  if (!file) return;
  
  const reader = new FileReader();
  reader.onload = (e) => {
    try {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, { type: 'array' });
      const firstSheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(firstSheet);
      
      const parsedData = jsonData.map(row => ({
        month: String(row['月份'] || ''),
        name: row['姓名'] || '',
        project: row['考勤项目'] || '',
        performanceTotal: parseFloat(row['绩效人天合计']) || 0,
        extraPerformance: parseFloat(row['额外结算人天']) || 0,
        personType: row['人员类型'] || '',
        status: row['状态'] || ''
      })).filter(r => r.name && r.status !== '离职');
      
      rawData.value = [...rawData.value, ...parsedData];
      hasData.value = true;
      
      ElMessage.success(`成功导入 ${parsedData.length} 条数据`);
      
      nextTick(() => {
        initCharts();
        updateCharts();
      });
    } catch (error) {
      console.error('解析文件失败:', error);
      ElMessage.error('文件解析失败，请检查文件格式');
    }
  };
  reader.readAsArrayBuffer(file);
};

const clearData = () => {
  rawData.value = [];
  hasData.value = false;
  destroyCharts();
  ElMessage.success('数据已清空');
};

const initCharts = () => {
  if (chart1Ref.value) {
    if (chart1Instance) chart1Instance.dispose();
    chart1Instance = markRaw(echarts.init(chart1Ref.value));
    chart1Instance.on('click', () => openChartModal('chart1'));
  }
  if (chart2Ref.value) {
    if (chart2Instance) chart2Instance.dispose();
    chart2Instance = markRaw(echarts.init(chart2Ref.value));
    chart2Instance.on('click', () => openChartModal('chart2'));
  }
  if (chart3Ref.value) {
    if (chart3Instance) chart3Instance.dispose();
    chart3Instance = markRaw(echarts.init(chart3Ref.value));
    chart3Instance.on('click', () => openChartModal('chart3'));
  }
  if (chart4Ref.value) {
    if (chart4Instance) chart4Instance.dispose();
    chart4Instance = markRaw(echarts.init(chart4Ref.value));
    chart4Instance.on('click', () => openChartModal('chart4'));
  }
};

const destroyCharts = () => {
  if (chart1Instance) { chart1Instance.dispose(); chart1Instance = null; }
  if (chart2Instance) { chart2Instance.dispose(); chart2Instance = null; }
  if (chart3Instance) { chart3Instance.dispose(); chart3Instance = null; }
  if (chart4Instance) { chart4Instance.dispose(); chart4Instance = null; }
  if (modalChartInstance) { modalChartInstance.dispose(); modalChartInstance = null; }
};

const openChartModal = (chartType) => {
  currentChartType.value = chartType;
  const titles = {
    chart1: '个人绩效排名（详细）',
    chart2: '项目绩效总和（详细）',
    chart3: '项目平均绩效（详细）',
    chart4: '绩效分布情况（详细）'
  };
  chartModalTitle.value = titles[chartType];
  showChartModal.value = true;
  
  nextTick(() => {
    initModalChart();
    updateModalChart();
  });
};

const closeChartModal = () => {
  showChartModal.value = false;
  if (modalChartInstance) {
    modalChartInstance.dispose();
    modalChartInstance = null;
  }
};

const initModalChart = () => {
  if (modalChartRef.value) {
    if (modalChartInstance) modalChartInstance.dispose();
    modalChartInstance = markRaw(echarts.init(modalChartRef.value));
  }
};

const updateModalChart = () => {
  if (!modalChartInstance) return;
  
  switch (currentChartType.value) {
    case 'chart1':
      updateChart1Modal();
      break;
    case 'chart2':
      updateChart2Modal();
      break;
    case 'chart3':
      updateChart3Modal();
      break;
    case 'chart4':
      updateChart4Modal();
      break;
  }
};

const updateCharts = () => {
  updateChart1();
  updateChart2();
  updateChart3();
  updateChart4();
};

const updateChart1 = () => {
  if (!chart1Instance || filteredData.value.length === 0) return;
  
  const personPerformance = {};
  filteredData.value.forEach(r => {
    if (r.name) {
      personPerformance[r.name] = (personPerformance[r.name] || 0) + (r.extraPerformance || 0);
    }
  });
  
  let data = Object.entries(personPerformance)
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => chart1Sort.value === 'desc' ? b.value - a.value : a.value - b.value);
  
  if (data.length > 20) data = data.slice(0, 20);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0];
        return `${p.name}<br/>绩效: ${p.value.toFixed(2)} 人天`;
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.name),
      axisLabel: { rotate: 45, fontSize: 10, color: '#718096' },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '绩效(人天)',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
      axisLabel: { color: '#718096', fontSize: 11 }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#79bbff' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: { color: '#66b1ff' }
      }
    }]
  };
  
  chart1Instance.setOption(option);
};

const updateChart2 = () => {
  if (!chart2Instance || filteredData.value.length === 0) return;
  
  const projectPerformance = {};
  filteredData.value.forEach(r => {
    if (r.project) {
      projectPerformance[r.project] = (projectPerformance[r.project] || 0) + (r.extraPerformance || 0);
    }
  });
  
  let data = Object.entries(projectPerformance)
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => chart2Sort.value === 'desc' ? b.value - a.value : a.value - b.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0];
        return `${p.name}<br/>绩效总和: ${p.value.toFixed(2)} 人天`;
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.name),
      axisLabel: { rotate: 30, fontSize: 10, color: '#718096' },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '绩效总和(人天)',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
      axisLabel: { color: '#718096', fontSize: 11 }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: { color: '#79bbff' }
      }
    }]
  };
  
  chart2Instance.setOption(option);
};

const updateChart3 = () => {
  if (!chart3Instance || filteredData.value.length === 0) return;
  
  const projectData = {};
  filteredData.value.forEach(r => {
    if (r.project) {
      if (!projectData[r.project]) {
        projectData[r.project] = { total: 0, count: 0 };
      }
      projectData[r.project].total += (r.extraPerformance || 0);
      projectData[r.project].count += 1;
    }
  });
  
  let data = Object.entries(projectData)
    .map(([name, { total, count }]) => ({ name, value: count > 0 ? total / count : 0 }))
    .sort((a, b) => chart3Sort.value === 'desc' ? b.value - a.value : a.value - b.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0];
        return `${p.name}<br/>平均绩效: ${p.value.toFixed(2)} 人天`;
      }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.name),
      axisLabel: { rotate: 30, fontSize: 10, color: '#718096' },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '平均绩效(人天)',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
      axisLabel: { color: '#718096', fontSize: 11 }
    },
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#e6a23c' },
          { offset: 1, color: '#eebe77' }
        ]),
        borderRadius: [4, 4, 0, 0]
      },
      emphasis: {
        itemStyle: { color: '#f5d442' }
      }
    }]
  };
  
  chart3Instance.setOption(option);
};

const updateChart4 = () => {
  if (!chart4Instance || filteredData.value.length === 0) return;
  
  const personPerformance = {};
  filteredData.value.forEach(r => {
    if (r.name) {
      personPerformance[r.name] = (personPerformance[r.name] || 0) + (r.extraPerformance || 0);
    }
  });
  
  const values = Object.values(personPerformance);
  
  const ranges = [
    { name: '0-5', min: 0, max: 5 },
    { name: '5-10', min: 5, max: 10 },
    { name: '10-15', min: 10, max: 15 },
    { name: '15-20', min: 15, max: 20 },
    { name: '20以上', min: 20, max: Infinity }
  ];
  
  const distribution = ranges.map(range => {
    const count = values.filter(v => v >= range.min && v < range.max).length;
    return { name: range.name, value: count };
  });
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        return `${params.name}<br/>人数: ${params.value} (${params.percent}%)`;
      }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: '#4a5568', fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold',
          formatter: '{b}\n{c}人'
        }
      },
      labelLine: { show: false },
      data: distribution,
      color: ['#409EFF', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
    }]
  };
  
  chart4Instance.setOption(option);
};

const updateChart1Modal = () => {
  if (!modalChartInstance || filteredData.value.length === 0) return;
  
  const personPerformance = {};
  filteredData.value.forEach(r => {
    if (r.name) {
      personPerformance[r.name] = (personPerformance[r.name] || 0) + (r.extraPerformance || 0);
    }
  });
  
  let data = Object.entries(personPerformance)
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => chart1Sort.value === 'desc' ? b.value - a.value : a.value - b.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0];
        return `${p.name}<br/>绩效: ${p.value.toFixed(2)} 人天`;
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.name),
      axisLabel: { rotate: 45, fontSize: 11, color: '#718096' },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '绩效(人天)',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
      axisLabel: { color: '#718096', fontSize: 11 }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100, height: 20, bottom: 5 }
    ],
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#79bbff' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  };
  
  modalChartInstance.setOption(option);
};

const updateChart2Modal = () => {
  if (!modalChartInstance || filteredData.value.length === 0) return;
  
  const projectPerformance = {};
  filteredData.value.forEach(r => {
    if (r.project) {
      projectPerformance[r.project] = (projectPerformance[r.project] || 0) + (r.extraPerformance || 0);
    }
  });
  
  let data = Object.entries(projectPerformance)
    .map(([name, value]) => ({ name, value }))
    .sort((a, b) => chart2Sort.value === 'desc' ? b.value - a.value : a.value - b.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0];
        return `${p.name}<br/>绩效总和: ${p.value.toFixed(2)} 人天`;
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.name),
      axisLabel: { rotate: 30, fontSize: 11, color: '#718096' },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '绩效总和(人天)',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
      axisLabel: { color: '#718096', fontSize: 11 }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100, height: 20, bottom: 5 }
    ],
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  };
  
  modalChartInstance.setOption(option);
};

const updateChart3Modal = () => {
  if (!modalChartInstance || filteredData.value.length === 0) return;
  
  const projectData = {};
  filteredData.value.forEach(r => {
    if (r.project) {
      if (!projectData[r.project]) {
        projectData[r.project] = { total: 0, count: 0 };
      }
      projectData[r.project].total += (r.extraPerformance || 0);
      projectData[r.project].count += 1;
    }
  });
  
  let data = Object.entries(projectData)
    .map(([name, { total, count }]) => ({ name, value: count > 0 ? total / count : 0 }))
    .sort((a, b) => chart3Sort.value === 'desc' ? b.value - a.value : a.value - b.value);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0];
        return `${p.name}<br/>平均绩效: ${p.value.toFixed(2)} 人天`;
      }
    },
    grid: { left: '3%', right: '4%', bottom: '15%', top: '5%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.name),
      axisLabel: { rotate: 30, fontSize: 11, color: '#718096' },
      axisLine: { lineStyle: { color: '#e2e8f0' } }
    },
    yAxis: {
      type: 'value',
      name: '平均绩效(人天)',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
      axisLabel: { color: '#718096', fontSize: 11 }
    },
    dataZoom: [
      { type: 'inside', start: 0, end: 100 },
      { type: 'slider', start: 0, end: 100, height: 20, bottom: 5 }
    ],
    series: [{
      type: 'bar',
      data: data.map(d => d.value),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#e6a23c' },
          { offset: 1, color: '#eebe77' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  };
  
  modalChartInstance.setOption(option);
};

const updateChart4Modal = () => {
  if (!modalChartInstance || filteredData.value.length === 0) return;
  
  const personPerformance = {};
  filteredData.value.forEach(r => {
    if (r.name) {
      personPerformance[r.name] = (personPerformance[r.name] || 0) + (r.extraPerformance || 0);
    }
  });
  
  const values = Object.values(personPerformance);
  
  const ranges = [
    { name: '0-5', min: 0, max: 5 },
    { name: '5-10', min: 5, max: 10 },
    { name: '10-15', min: 10, max: 15 },
    { name: '15-20', min: 15, max: 20 },
    { name: '20以上', min: 20, max: Infinity }
  ];
  
  const distribution = ranges.map(range => {
    const count = values.filter(v => v >= range.min && v < range.max).length;
    return { name: range.name, value: count };
  });
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        return `${params.name}<br/>人数: ${params.value} (${params.percent}%)`;
      }
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: { color: '#4a5568', fontSize: 14 }
    },
    series: [{
      type: 'pie',
      radius: ['35%', '60%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}\n{c}人',
        fontSize: 14
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold'
        }
      },
      labelLine: { show: true },
      data: distribution,
      color: ['#409EFF', '#67c23a', '#e6a23c', '#f56c6c', '#909399']
    }]
  };
  
  modalChartInstance.setOption(option);
};

watch(chart1Sort, updateChart1);
watch(chart2Sort, updateChart2);
watch(chart3Sort, updateChart3);
watch(selectedProject, () => {
  updateChart1();
  updateChart2();
  updateChart3();
  updateChart4();
});

const handleResize = () => {
  chart1Instance?.resize();
  chart2Instance?.resize();
  chart3Instance?.resize();
  chart4Instance?.resize();
};

onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  destroyCharts();
});
</script>

<style scoped>
.performance-dashboard {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(37, 99, 235, 0.06), 0 1px 2px rgba(0, 0, 0, 0.03), inset 0 1px 0 rgba(255, 255, 255, 1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  padding: 24px;
  transition: all 0.3s ease;
  height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.header-container h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  white-space: nowrap;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  outline: none;
}

.btn-primary {
  background-color: #409EFF;
  color: #ffffff;
}

.btn-primary:hover {
  background-color: #66B1FF;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
}

.btn-secondary {
  background: linear-gradient(135deg, #edf2f7 0%, #e2e8f0 100%);
  color: #4a5568;
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e0 100%);
}

.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.placeholder {
  height: calc(100% - 80px);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(248, 250, 252, 0.5);
  border-radius: 12px;
}

.placeholder-card {
  --bg-color: #ceb2fc;
  --bg-color-light: #f0e7ff;
  --text-color-hover: #fff;
  --box-shadow-color: rgba(206, 178, 252, 0.48);
}

.card {
  width: 280px;
  height: 380px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  box-shadow: 0 14px 26px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease-out;
  text-decoration: none;
  cursor: pointer;
}

.card:hover {
  transform: translateY(-5px) scale(1.005) translateZ(0);
  box-shadow: 0 24px 36px rgba(0, 0, 0, 0.11), 0 24px 46px var(--box-shadow-color);
}

.card:hover .circle {
  border-color: var(--bg-color-light);
  background: var(--bg-color);
}

.card:hover .circle:after {
  background: var(--bg-color-light);
}

.card:hover p {
  color: var(--text-color-hover);
}

.card p {
  font-size: 17px;
  color: #4c5656;
  margin-top: 20px;
  z-index: 1000;
  transition: color 0.3s ease-out;
}

.card .card-hint {
  font-size: 13px;
  color: #a0aec0;
  margin-top: 8px;
  z-index: 1000;
  transition: color 0.3s ease-out;
}

.card:hover .card-hint {
  color: var(--text-color-hover);
}

.circle {
  width: 131px;
  height: 131px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid var(--bg-color);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 1;
  transition: all 0.3s ease-out;
  margin-bottom: 20px;
}

.circle:after {
  content: "";
  width: 118px;
  height: 118px;
  display: block;
  position: absolute;
  background: var(--bg-color);
  border-radius: 50%;
  top: 5px;
  left: 5px;
  transition: opacity 0.3s ease-out;
}

.circle svg {
  z-index: 10000;
  transform: translateZ(0);
}

.overlay {
  width: 118px;
  position: absolute;
  height: 118px;
  border-radius: 50%;
  background: var(--bg-color);
  top: 85px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 0;
  transition: transform 0.5s ease-out;
}

.card:hover .overlay {
  transform: translateX(-50%) scale(5.5);
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.stats-section {
  display: flex;
  gap: 20px;
}

.stat-card {
  flex: 1;
  background: linear-gradient(135deg, #ffffff 0%, #f7fafc 100%);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(224, 230, 237, 0.5);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-title {
  font-size: 14px;
  color: #718096;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(224, 230, 237, 0.5);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
}

.chart-container {
  width: 100%;
  height: 300px;
}

.data-table-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(224, 230, 237, 0.5);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
}

.table-container {
  max-height: 400px;
  overflow-y: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.data-table th {
  background: #f7fafc;
  font-weight: 600;
  color: #4a5568;
  position: sticky;
  top: 0;
  z-index: 1;
}

.data-table tbody tr:hover {
  background: #f7fafc;
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #a0aec0;
}

@media (max-width: 1200px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-section {
    flex-wrap: wrap;
  }
  
  .stat-card {
    flex: 1 1 calc(50% - 10px);
    min-width: 150px;
  }
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.chart-modal {
  width: 80vw;
  height: 80vh;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chart-modal .modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: #f7fafc;
}

.chart-modal .modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
}

.chart-modal .close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: #e2e8f0;
  border-radius: 8px;
  font-size: 20px;
  color: #4a5568;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.chart-modal .close-btn:hover {
  background: #cbd5e0;
  color: #2d3748;
}

.chart-modal .modal-body {
  flex: 1;
  padding: 20px;
  overflow: hidden;
}

.modal-chart-container {
  width: 100%;
  height: 100%;
}
</style>
