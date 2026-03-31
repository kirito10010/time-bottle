<template>
  <div class="performance-record">
    <div class="header-container">
      <h2>绩效记录</h2>
      <div class="action-buttons">
        <button class="btn btn-primary" @click="showWorkReport = true">工作汇报</button>
        <button class="btn btn-secondary" @click="showOvertimeReport = true">加班汇报</button>
        <button class="btn btn-secondary" @click="showSalaryRecord = true">工资记录</button>
        <button class="btn btn-secondary" @click="showProjectConfig = true">生产项目配置</button>
      </div>
    </div>
    
    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stat-card performance-card">
        <div class="stat-title">绩效总和</div>
        <div class="stat-value performance-value">{{ totalPerformanceDays.toFixed(5) }}</div>
      </div>
      <div class="stat-card attendance-card">
        <div class="stat-title">考勤天数</div>
        <div class="stat-value attendance-value">{{ attendanceDays }}</div>
      </div>
      <div class="stat-card average-card">
        <div class="stat-title">日均绩效</div>
        <div class="stat-value average-value">{{ dailyAveragePerformance.toFixed(5) }}</div>
      </div>
      <div class="stat-card overtime-card">
        <div class="stat-title">加班时长</div>
        <div class="stat-value overtime-value">{{ totalOvertimeHours.toFixed(1) }} 小时</div>
      </div>
      <div class="stat-card salary-card">
        <div class="stat-title">总工资</div>
        <div class="stat-value salary-value">待开发</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-section">
      <div class="chart-header">
        <h3>绩效趋势</h3>
        <div class="time-range-selector">
          <div class="time-buttons">
            <button class="time-btn" :class="{ active: chartTimeRange === 'week' }" @click="setChartTimeRange('week')">本周</button>
            <button class="time-btn" :class="{ active: chartTimeRange === 'month' }" @click="setChartTimeRange('month')">本月</button>
            <el-date-picker
              v-model="chartSelectedMonth"
              type="month"
              placeholder="选择月份"
              format="YYYY年MM月"
              value-format="YYYY-MM"
              class="chart-month-picker"
              :locale="zhCn"
              @change="handleMonthChange"
            />
          </div>
          <div class="time-selectors">
            <el-date-picker
              v-model="chartDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="chart-date-picker"
              :locale="zhCn"
              @change="handleChartDateChange"
            />
          </div>
        </div>
      </div>
      <div id="performanceChart" ref="chartRef" class="chart-container"></div>
    </div>
  </div>

  <Teleport to="body">
    <div v-if="showWorkReport" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, () => showWorkReport = false)">
      <div class="modal work-report-modal" @click.stop>
        <div class="modal-header">
          <h3>工作汇报</h3>
          <div class="modal-header-actions">
            <button class="btn btn-primary" @click="openAddPerformance">汇报工作</button>
            <button class="btn btn-danger" @click="batchDeletePerformances" :disabled="selectedPerformances.length === 0">批量删除</button>
          </div>
        </div>
        
        <div class="filter-section">
          <div class="filter-item">
            <label>日期范围</label>
            <el-date-picker
              v-model="filterDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              class="filter-date-picker"
              :locale="zhCn"
              @change="handleFilterChange"
            />
          </div>
          <div class="filter-item">
            <label>项目</label>
            <el-select v-model="filterProjectId" placeholder="全部项目" clearable filterable class="filter-select" @change="handleFilterChange">
              <el-option v-for="project in projects" :key="project.id" :label="project.projectName" :value="project.id"></el-option>
            </el-select>
          </div>
          <div class="filter-item">
            <label>绩效人天</label>
            <el-select v-model="filterManDays" placeholder="全部" clearable class="filter-select" @change="handleFilterChange">
              <el-option label="小于1" value="lt1"></el-option>
              <el-option label="大于等于1" value="gte1"></el-option>
            </el-select>
          </div>
          <button class="btn btn-secondary btn-reset" @click="resetFilters">重置</button>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th><input type="checkbox" v-model="selectAllPerformances" @change="toggleSelectAllPerformances"></th>
                <th>记录日期</th>
                <th>项目名称</th>
                <th>工序类型</th>
                <th>额定效率</th>
                <th>实际工作量</th>
                <th>绩效人天</th>
                <th style="width: 140px;">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="performance in performances" :key="performance.id">
                <td><input type="checkbox" v-model="selectedPerformances" :value="performance.id"></td>
                <td>{{ performance.recordDate }}</td>
                <td>{{ getProjectName(performance.projectId) }}</td>
                <td>{{ performance.processType }}</td>
                <td>{{ performance.quotaEfficiency }}</td>
                <td>{{ performance.actualWorkload }}</td>
                <td>{{ performance.performanceManDays ? Number(performance.performanceManDays).toFixed(5) : '0.00000' }}</td>
                <td>
                  <button class="btn btn-sm btn-secondary" @click="openEditPerformance(performance)">编辑</button>
                  <button class="btn btn-sm btn-danger" @click="deletePerformance(performance.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="performances.length === 0" class="no-data">暂无工作汇报记录</div>
        </div>
        
        <div class="pagination-container" v-if="totalPerformances > 0">
          <div class="pagination-info">共 {{ totalPerformances }} 条记录</div>
          <div class="pagination-controls">
            <button class="page-btn" :class="{ disabled: performancePage === 1 }" @click="performancePage > 1 && goToPage(performancePage - 1)">上一页</button>
            <div class="page-numbers">
              <button v-for="page in visiblePerformancePages" :key="page" class="page-number" :class="{ active: page === performancePage }" @click="goToPage(page)">{{ page }}</button>
            </div>
            <button class="page-btn" :class="{ disabled: performancePage >= totalPerformancePages }" @click="performancePage < totalPerformancePages && goToPage(performancePage + 1)">下一页</button>
          </div>
          <div class="page-size-selector">
            <label>每页显示:</label>
            <select v-model="performancePageSize" @change="performancePage = 1; applyFilters()" class="page-size-select">
              <option value="10">10条</option>
              <option value="20">20条</option>
              <option value="50">50条</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showAddPerformance || showEditPerformance" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, closePerformanceModal)">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="savePerformance">
          <div class="modal-header">
            <h3>{{ showEditPerformance ? '编辑工作汇报' : '汇报工作' }}</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>记录日期</label>
              <el-date-picker
                class="custom-date-picker"
                v-model="performanceForm.recordDate"
                type="date"
                placeholder="请选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :locale="zhCn"
                required
              />
            </div>
            
            <div class="form-group">
              <label>项目</label>
              <el-select v-model="performanceForm.projectId" @change="onProjectChange" placeholder="请选择项目" required class="full-width" filterable clearable>
                <el-option v-for="project in projects" :key="project.id" :label="project.projectName" :value="project.id"></el-option>
              </el-select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>工序类型</label>
              <el-select v-model="performanceForm.processType" @change="onProcessTypeChange" placeholder="请选择工序类型" required class="full-width">
                <el-option label="作业" value="作业"></el-option>
                <el-option label="质检" value="质检"></el-option>
              </el-select>
            </div>
            
            <div class="form-group">
              <label>额定效率</label>
              <input type="number" v-model.number="performanceForm.quotaEfficiency" step="0.01" min="0" placeholder="自动填充" readonly class="readonly-input">
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>实际工作量</label>
              <input type="number" v-model.number="performanceForm.actualWorkload" step="0.01" min="0" placeholder="请输入实际工作量" required>
            </div>
            
            <div class="form-group">
              <label>绩效人天</label>
              <input type="text" :value="calculatedManDays" readonly class="readonly-input">
            </div>
          </div>
        </form>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showProjectConfig" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, () => showProjectConfig = false)">
      <div class="modal project-config-modal" @click.stop>
        <div class="modal-header">
          <h3>生产项目配置</h3>
          <div class="modal-header-actions">
            <button class="btn btn-primary" @click="openAddProject">添加项目</button>
            <button class="btn btn-danger" @click="batchDeleteProjects" :disabled="selectedProjects.length === 0">批量删除</button>
          </div>
        </div>

        <div class="filter-section">
          <div class="filter-item">
            <label>项目名称</label>
            <el-input v-model="projectNameSearch" placeholder="请输入项目名称搜索" clearable class="filter-input" @input="handleProjectSearch">
              <template #prefix>
                <span class="search-icon">🔍</span>
              </template>
            </el-input>
          </div>
        </div>
        
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th><input type="checkbox" v-model="selectAllProjects" @change="toggleSelectAllProjects"></th>
                <th>项目名称</th>
                <th>作业定额</th>
                <th>质检定额</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="project in projects" :key="project.id">
                <td><input type="checkbox" v-model="selectedProjects" :value="project.id"></td>
                <td>{{ project.projectName }}</td>
                <td>{{ project.operationQuota }}</td>
                <td>{{ project.qualityQuota }}</td>
                <td>{{ formatDate(project.createdAt) }}</td>
                <td>
                  <button class="btn btn-sm btn-secondary" @click="openEditProject(project)">编辑</button>
                  <button class="btn btn-sm btn-danger" @click="deleteProject(project.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="projects.length === 0" class="no-data">暂无项目配置</div>
        </div>
        
        <div class="pagination-container" v-if="totalProjects > 0">
          <div class="pagination-info">共 {{ totalProjects }} 条记录</div>
          <div class="pagination-controls">
            <button class="page-btn" :class="{ disabled: projectPage === 1 }" @click="projectPage > 1 && goToProjectPage(projectPage - 1)">上一页</button>
            <div class="page-numbers">
              <button v-for="page in visibleProjectPages" :key="page" class="page-number" :class="{ active: page === projectPage }" @click="goToProjectPage(page)">{{ page }}</button>
            </div>
            <button class="page-btn" :class="{ disabled: projectPage >= totalProjectPages }" @click="projectPage < totalProjectPages && goToProjectPage(projectPage + 1)">下一页</button>
          </div>
          <div class="page-size-selector">
            <label>每页显示:</label>
            <select v-model="projectPageSize" @change="projectPage = 1; applyProjectFilters()" class="page-size-select">
              <option value="10">10条</option>
              <option value="20">20条</option>
              <option value="50">50条</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showAddProject || showEditProject" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, closeProjectModal)">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="saveProject">
          <div class="modal-header">
            <h3>{{ showEditProject ? '编辑项目' : '添加项目' }}</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          
          <div class="form-group">
            <label>项目名称</label>
            <input type="text" v-model="projectForm.projectName" required maxlength="100" placeholder="请输入项目名称">
          </div>
          
          <div class="form-group">
            <label>作业定额（单位工作量/人天）</label>
            <input type="number" v-model.number="projectForm.operationQuota" step="0.01" min="0" placeholder="请输入作业定额" required>
          </div>
          
          <div class="form-group">
            <label>质检定额（单位工作量/人天）</label>
            <input type="number" v-model.number="projectForm.qualityQuota" step="0.01" min="0" placeholder="请输入质检定额" required>
          </div>
        </form>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showOvertimeReport" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, () => showOvertimeReport = false)">
      <div class="modal work-report-modal" @click.stop>
        <div class="modal-header">
          <h3>加班汇报</h3>
          <div class="modal-header-actions">
            <button class="btn btn-primary" @click="openAddOvertime">添加汇报</button>
            <button class="btn btn-danger" @click="batchDeleteOvertimes" :disabled="selectedOvertimes.length === 0">批量删除</button>
          </div>
        </div>

        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th><input type="checkbox" v-model="selectAllOvertimes" @change="toggleSelectAllOvertimes"></th>
                <th>记录日期</th>
                <th>项目名称</th>
                <th>加班时长(小时)</th>
                <th>描述</th>
                <th style="width: 140px;">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="overtime in overtimes" :key="overtime.id">
                <td><input type="checkbox" v-model="selectedOvertimes" :value="overtime.id"></td>
                <td>{{ overtime.recordDate }}</td>
                <td>{{ getProjectName(overtime.projectId) }}</td>
                <td>{{ overtime.overtimeHours }}</td>
                <td>{{ overtime.description || '-' }}</td>
                <td>
                  <button class="btn btn-sm btn-secondary" @click="openEditOvertime(overtime)">编辑</button>
                  <button class="btn btn-sm btn-danger" @click="deleteOvertime(overtime.id)">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div v-if="overtimes.length === 0" class="no-data">暂无加班汇报记录</div>
        </div>
        
        <div class="pagination-container" v-if="totalOvertimes > 0">
          <div class="pagination-info">共 {{ totalOvertimes }} 条记录</div>
          <div class="pagination-controls">
            <button class="page-btn" :class="{ disabled: overtimePage === 1 }" @click="overtimePage > 1 && goToOvertimePage(overtimePage - 1)">上一页</button>
            <div class="page-numbers">
              <button v-for="page in visibleOvertimePages" :key="page" class="page-number" :class="{ active: page === overtimePage }" @click="goToOvertimePage(page)">{{ page }}</button>
            </div>
            <button class="page-btn" :class="{ disabled: overtimePage >= totalOvertimePages }" @click="overtimePage < totalOvertimePages && goToOvertimePage(overtimePage + 1)">下一页</button>
          </div>
          <div class="page-size-selector">
            <label>每页显示:</label>
            <select v-model="overtimePageSize" @change="overtimePage = 1; applyOvertimeFilters()" class="page-size-select">
              <option value="10">10条</option>
              <option value="20">20条</option>
              <option value="50">50条</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showAddOvertime || showEditOvertime" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, closeOvertimeModal)">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="saveOvertime">
          <div class="modal-header">
            <h3>{{ showEditOvertime ? '编辑加班汇报' : '添加加班汇报' }}</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>记录日期</label>
              <el-date-picker
                class="custom-date-picker"
                v-model="overtimeForm.recordDate"
                type="date"
                placeholder="请选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :locale="zhCn"
                required
              />
            </div>
            
            <div class="form-group">
              <label>项目</label>
              <el-select v-model="overtimeForm.projectId" placeholder="请选择项目" required class="full-width" filterable clearable>
                <el-option v-for="project in projects" :key="project.id" :label="project.projectName" :value="project.id"></el-option>
              </el-select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>加班时长(小时)</label>
              <input type="number" v-model.number="overtimeForm.overtimeHours" step="0.5" min="0" placeholder="请输入加班时长" required>
            </div>
            
            <div class="form-group">
              <label>描述</label>
              <input type="text" v-model="overtimeForm.description" placeholder="请输入加班描述（可选）">
            </div>
          </div>
        </form>
      </div>
    </div>
  </Teleport>

  <Teleport to="body">
    <div v-if="showSalaryRecord" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, () => showSalaryRecord = false)">
      <div class="modal work-report-modal" @click.stop>
        <div class="modal-header">
          <h3>工资记录</h3>
          <div class="modal-header-actions">
            <button class="btn btn-primary" @click="openAddSalary">记录工资</button>
            <button class="btn btn-secondary" @click="openSalaryConfig">工资配置</button>
            <button class="btn btn-danger" @click="batchDeleteSalaries" :disabled="selectedSalaries.length === 0">批量删除</button>
          </div>
        </div>
        
        <div class="table-container">
          <table class="data-table">
            <thead>
              <tr>
                <th><input type="checkbox" v-model="selectAllSalaries" @change="toggleSelectAllSalaries"></th>
                <th>月份</th>
                <th>出勤天数</th>
                <th>基本薪资</th>
                <th>绩效薪资</th>
                <th>加班薪资</th>
                <th>应发总额</th>
                <th>扣除总额</th>
                <th>实发薪资</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="salary in paginatedSalaries" :key="salary.id">
                <td><input type="checkbox" v-model="selectedSalaries" :value="salary.id"></td>
                <td>{{ salary.month }}</td>
                <td>{{ Number(salary.attendanceDays).toFixed(2) }}</td>
                <td>{{ formatMoney(salary.basicSalary) }}</td>
                <td>{{ formatMoney(salary.performanceSalary) }}</td>
                <td>{{ formatMoney(salary.overtimeSalary) }}</td>
                <td class="payable">{{ formatMoney(salary.totalPayable) }}</td>
                <td class="deduction">{{ formatMoney(salary.totalDeduction) }}</td>
                <td class="net-salary">{{ formatMoney(salary.netSalary) }}</td>
                <td>
                  <button class="btn btn-sm btn-secondary" @click="openEditSalary(salary)">编辑</button>
                  <button class="btn btn-sm btn-danger" @click="deleteSalary(salary.id)">删除</button>
                </td>
              </tr>
              <tr v-if="salaries.length === 0">
                <td colspan="10" class="empty-row">暂无工资记录</td>
              </tr>
            </tbody>
          </table>
        </div>
        
        <div class="pagination-container" v-if="salaries.length > 0">
          <div class="pagination-info">共 {{ salaries.length }} 条记录</div>
          <div class="pagination-controls">
            <button class="page-btn" :class="{ disabled: salaryCurrentPage === 1 }" @click="salaryCurrentPage > 1 && salaryCurrentPage--">上一页</button>
            <div class="page-numbers">
              <button v-for="page in visibleSalaryPages" :key="page" class="page-number" :class="{ active: page === salaryCurrentPage }" @click="salaryCurrentPage = page">{{ page }}</button>
            </div>
            <button class="page-btn" :class="{ disabled: salaryCurrentPage >= salaryTotalPages }" @click="salaryCurrentPage < salaryTotalPages && salaryCurrentPage++">下一页</button>
          </div>
        </div>
      </div>
    </div>
  </Teleport>

  <!-- 添加/编辑工资弹窗 -->
  <Teleport to="body">
    <div v-if="showAddSalary || showEditSalary" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, closeSalaryModal)">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="saveSalary">
          <div class="modal-header">
            <h3>{{ showEditSalary ? '编辑工资' : '记录工资' }}</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>月份</label>
              <el-date-picker
                v-model="salaryForm.month"
                type="month"
                placeholder="选择月份"
                value-format="YYYY-MM"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>出勤天数</label>
              <el-input-number
                v-model="salaryForm.attendanceDays"
                :min="0"
                :step="0.01"
                :precision="2"
                placeholder="自动计算"
                style="width: 100%"
                disabled
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>周期开始日期</label>
              <el-date-picker
                v-model="salaryForm.periodStartDate"
                type="date"
                placeholder="自动计算"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                disabled
              />
            </div>
            <div class="form-group">
              <label>周期结束日期</label>
              <el-date-picker
                v-model="salaryForm.periodEndDate"
                type="date"
                placeholder="自动计算"
                value-format="YYYY-MM-DD"
                style="width: 100%"
                disabled
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>基本薪资</label>
              <el-input-number
                v-model="salaryForm.basicSalary"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="基本薪资"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>绩效总和</label>
              <el-input-number
                v-model="salaryForm.performanceCoefficient"
                :min="0"
                :step="0.00001"
                :precision="5"
                placeholder="自动计算"
                style="width: 100%"
                disabled
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>绩效薪资</label>
              <el-input-number
                v-model="salaryForm.performanceSalary"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="自动计算"
                style="width: 100%"
                disabled
              />
            </div>
            <div class="form-group">
              <label>岗位绩效</label>
              <el-input-number
                v-model="salaryForm.positionPerformance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="岗位绩效"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>餐补</label>
              <el-input-number
                v-model="salaryForm.mealAllowance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="餐补"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>房补</label>
              <el-input-number
                v-model="salaryForm.housingAllowance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="房补"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>全勤奖</label>
              <el-input-number
                v-model="salaryForm.fullAttendanceBonus"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="全勤奖"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>其他奖金</label>
              <el-input-number
                v-model="salaryForm.otherBonus"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="其他奖金"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>加班时长(小时)</label>
              <el-input-number
                v-model="salaryForm.overtimeHours"
                :min="0"
                :step="0.01"
                :precision="2"
                placeholder="自动计算"
                style="width: 100%"
                disabled
              />
            </div>
            <div class="form-group">
              <label>加班薪资</label>
              <el-input-number
                v-model="salaryForm.overtimeSalary"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="自动计算"
                style="width: 100%"
                disabled
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>养老保险</label>
              <el-input-number
                v-model="salaryForm.pensionInsurance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="养老保险"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>医疗保险</label>
              <el-input-number
                v-model="salaryForm.medicalInsurance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="医疗保险"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>失业保险</label>
              <el-input-number
                v-model="salaryForm.unemploymentInsurance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="失业保险"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>迟到扣款</label>
              <el-input-number
                v-model="salaryForm.lateDeduction"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="迟到扣款"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-summary">
            <div class="summary-item">
              <span>应发总额：</span>
              <span class="payable">{{ formatMoney(calculatedTotalPayable) }}</span>
            </div>
            <div class="summary-item">
              <span>扣除总额：</span>
              <span class="deduction">{{ formatMoney(calculatedTotalDeduction) }}</span>
            </div>
            <div class="summary-item net">
              <span>实发薪资：</span>
              <span class="net-salary">{{ formatMoney(calculatedNetSalary) }}</span>
            </div>
          </div>
        </form>
      </div>
    </div>
  </Teleport>

  <!-- 工资配置弹窗 -->
  <Teleport to="body">
    <div v-if="showSalaryConfig" class="modal-overlay" 
      @mousedown="handleOverlayMouseDown" 
      @mouseup="(e) => handleOverlayMouseUp(e, () => showSalaryConfig = false)">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="saveSalaryConfig">
          <div class="modal-header">
            <h3>工资配置</h3>
            <div class="modal-header-actions">
              <button type="button" class="btn btn-secondary" @click="resetSalaryConfig">重置默认</button>
              <button type="submit" class="btn btn-primary">保存配置</button>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>基本薪资</label>
              <el-input-number
                v-model="tempSalaryConfig.basicSalary"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="基本薪资"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>岗位绩效</label>
              <el-input-number
                v-model="tempSalaryConfig.positionPerformance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="岗位绩效"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>餐补</label>
              <el-input-number
                v-model="tempSalaryConfig.mealAllowance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="餐补"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>房补</label>
              <el-input-number
                v-model="tempSalaryConfig.housingAllowance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="房补"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>全勤奖</label>
              <el-input-number
                v-model="tempSalaryConfig.fullAttendanceBonus"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="全勤奖"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>其他奖金</label>
              <el-input-number
                v-model="tempSalaryConfig.otherBonus"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="其他奖金"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>养老保险</label>
              <el-input-number
                v-model="tempSalaryConfig.pensionInsurance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="养老保险"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>医疗保险</label>
              <el-input-number
                v-model="tempSalaryConfig.medicalInsurance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="医疗保险"
                style="width: 100%"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>失业保险</label>
              <el-input-number
                v-model="tempSalaryConfig.unemploymentInsurance"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="失业保险"
                style="width: 100%"
              />
            </div>
            <div class="form-group">
              <label>迟到扣款</label>
              <el-input-number
                v-model="tempSalaryConfig.lateDeduction"
                :min="0"
                :step="0.01"
                :precision="5"
                placeholder="迟到扣款"
                style="width: 100%"
              />
            </div>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted, markRaw, nextTick, onActivated } from 'vue';
import { ElMessage, ElDatePicker, ElSelect, ElOption, ElMessageBox, ElInput, ElInputNumber } from 'element-plus';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import * as echarts from 'echarts';

const API_BASE = '/api';

let mouseDownTarget = null;

const handleOverlayMouseDown = (event) => {
  mouseDownTarget = event.target;
};

const handleOverlayMouseUp = (event, closeCallback) => {
  if (mouseDownTarget === event.target && event.target.classList.contains('modal-overlay')) {
    closeCallback();
  }
  mouseDownTarget = null;
};

const showWorkReport = ref(false);
const showOvertimeReport = ref(false);
const showSalaryRecord = ref(false);
const showProjectConfig = ref(false);
const showAddPerformance = ref(false);
const showEditPerformance = ref(false);
const showAddProject = ref(false);
const showEditProject = ref(false);
const showAddOvertime = ref(false);
const showEditOvertime = ref(false);
const showAddSalary = ref(false);
const showEditSalary = ref(false);
const showSalaryConfig = ref(false);

const DEFAULT_SALARY_CONFIG = {
  basicSalary: 2000,
  positionPerformance: 500,
  mealAllowance: 200,
  housingAllowance: 300,
  fullAttendanceBonus: 300,
  otherBonus: 100,
  pensionInsurance: 360.32,
  medicalInsurance: 90.08,
  unemploymentInsurance: 13.51,
  lateDeduction: 0
};

const salaryConfig = ref({ ...DEFAULT_SALARY_CONFIG });
const tempSalaryConfig = ref({ ...DEFAULT_SALARY_CONFIG });

const loadSalaryConfig = () => {
  const saved = localStorage.getItem('salaryConfig');
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      salaryConfig.value = { ...DEFAULT_SALARY_CONFIG, ...parsed };
    } catch (e) {
      salaryConfig.value = { ...DEFAULT_SALARY_CONFIG };
    }
  }
};

const openSalaryConfig = () => {
  tempSalaryConfig.value = { ...salaryConfig.value };
  showSalaryConfig.value = true;
};

const saveSalaryConfig = () => {
  salaryConfig.value = { ...tempSalaryConfig.value };
  localStorage.setItem('salaryConfig', JSON.stringify(salaryConfig.value));
  ElMessage.success('配置已保存');
  showSalaryConfig.value = false;
};

const resetSalaryConfig = () => {
  tempSalaryConfig.value = { ...DEFAULT_SALARY_CONFIG };
  ElMessage.success('已重置为默认配置');
};

const performances = ref([]);
const projects = ref([]);
const overtimes = ref([]);
const salaries = ref([]);
const selectedPerformances = ref([]);
const selectedProjects = ref([]);
const selectedOvertimes = ref([]);
const selectedSalaries = ref([]);
const selectAllPerformances = ref(false);
const selectAllProjects = ref(false);
const selectAllOvertimes = ref(false);
const selectAllSalaries = ref(false);

const salaryCurrentPage = ref(1);
const salaryPageSize = 10;

const filterDateRange = ref(null);
const filterProjectId = ref(null);
const filterManDays = ref(null);
const allPerformances = ref([]);
const allOvertimes = ref([]);

const projectNameSearch = ref('');
const allProjects = ref([]);

const performancePage = ref(1);
const performancePageSize = ref(10);
const totalPerformances = ref(0);

const overtimePage = ref(1);
const overtimePageSize = ref(10);
const totalOvertimes = ref(0);

const projectPage = ref(1);
const projectPageSize = ref(10);
const totalProjects = ref(0);

const performanceForm = ref({
  recordDate: '',
  projectId: null,
  processType: '',
  quotaEfficiency: 0,
  actualWorkload: 0
});
const editingPerformanceId = ref(null);

const overtimeForm = ref({
  recordDate: '',
  projectId: null,
  overtimeHours: 0,
  description: ''
});
const editingOvertimeId = ref(null);

const salaryForm = ref({
  id: null,
  month: '',
  periodStartDate: '',
  periodEndDate: '',
  attendanceDays: 0,
  basicSalary: DEFAULT_SALARY_CONFIG.basicSalary,
  performanceCoefficient: 1,
  performanceSalary: 0,
  positionPerformance: DEFAULT_SALARY_CONFIG.positionPerformance,
  mealAllowance: DEFAULT_SALARY_CONFIG.mealAllowance,
  housingAllowance: DEFAULT_SALARY_CONFIG.housingAllowance,
  fullAttendanceBonus: DEFAULT_SALARY_CONFIG.fullAttendanceBonus,
  otherBonus: DEFAULT_SALARY_CONFIG.otherBonus,
  pensionInsurance: DEFAULT_SALARY_CONFIG.pensionInsurance,
  medicalInsurance: DEFAULT_SALARY_CONFIG.medicalInsurance,
  unemploymentInsurance: DEFAULT_SALARY_CONFIG.unemploymentInsurance,
  lateDeduction: DEFAULT_SALARY_CONFIG.lateDeduction,
  overtimeHours: 0,
  overtimeSalary: 0
});
const editingSalaryId = ref(null);

watch(() => salaryForm.value.month, (newMonth) => {
  if (newMonth) {
    const [year, month] = newMonth.split('-').map(Number);
    const periodStart = new Date(year, month - 2, 26);
    const periodEnd = new Date(year, month - 1, 25);
    
    const formatDate = (date) => {
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, '0');
      const d = String(date.getDate()).padStart(2, '0');
      return `${y}-${m}-${d}`;
    };
    
    salaryForm.value.periodStartDate = formatDate(periodStart);
    salaryForm.value.periodEndDate = formatDate(periodEnd);
    
    const filteredPerformances = allPerformances.value.filter(p => {
      const recordDate = p.recordDate;
      return recordDate >= salaryForm.value.periodStartDate && recordDate <= salaryForm.value.periodEndDate;
    });
    
    const filteredOvertimes = allOvertimes.value.filter(o => {
      const recordDate = o.recordDate;
      return recordDate >= salaryForm.value.periodStartDate && recordDate <= salaryForm.value.periodEndDate;
    });
    
    if (filteredPerformances.length > 0) {
      const performanceSum = filteredPerformances.reduce((sum, p) => {
        return sum + (Number(p.performanceManDays) || 0);
      }, 0);
      
      const uniqueDates = new Set(filteredPerformances.map(p => p.recordDate));
      const attendanceDays = uniqueDates.size;
      const baseDeduction = attendanceDays;
      
      const overtimeDeduction = filteredOvertimes.reduce((sum, o) => {
        const hours = Number(o.overtimeHours) || 0;
        return sum + (hours * 0.125);
      }, 0);
      
      const totalPerformanceDays = performanceSum - baseDeduction - overtimeDeduction;
      const dailyAverage = totalPerformanceDays / attendanceDays;
      
      const overtimeHours = filteredOvertimes.reduce((sum, o) => {
        return sum + (Number(o.overtimeHours) || 0);
      }, 0);
      
      salaryForm.value.attendanceDays = attendanceDays;
      salaryForm.value.performanceCoefficient = Number(totalPerformanceDays.toFixed(5));
      salaryForm.value.performanceSalary = Number((totalPerformanceDays * 170 * 0.94).toFixed(2));
      salaryForm.value.overtimeHours = overtimeHours;
      salaryForm.value.overtimeSalary = Number((overtimeHours * 17).toFixed(2));
    } else {
      salaryForm.value.attendanceDays = 0;
      salaryForm.value.performanceCoefficient = 0;
      salaryForm.value.performanceSalary = 0;
      salaryForm.value.overtimeHours = 0;
      salaryForm.value.overtimeSalary = 0;
    }
  }
});

const projectForm = ref({
  id: null,
  projectName: '',
  operationQuota: 0,
  qualityQuota: 0
});

const currentUser = ref(null);

const totalPerformancePages = computed(() => Math.ceil(totalPerformances.value / performancePageSize.value) || 1);
const totalProjectPages = computed(() => Math.ceil(totalProjects.value / projectPageSize.value) || 1);

const visiblePerformancePages = computed(() => {
  const pages = [];
  const start = Math.max(1, performancePage.value - 2);
  const end = Math.min(totalPerformancePages.value, performancePage.value + 2);
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});

const totalOvertimePages = computed(() => Math.ceil(totalOvertimes.value / overtimePageSize.value) || 1);

const visibleOvertimePages = computed(() => {
  const pages = [];
  const start = Math.max(1, overtimePage.value - 2);
  const end = Math.min(totalOvertimePages.value, overtimePage.value + 2);
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});

const visibleProjectPages = computed(() => {
  const pages = [];
  const start = Math.max(1, projectPage.value - 2);
  const end = Math.min(totalProjectPages.value, projectPage.value + 2);
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});

const calculatedManDays = computed(() => {
  if (performanceForm.value.quotaEfficiency > 0 && performanceForm.value.actualWorkload > 0) {
    return (performanceForm.value.actualWorkload / performanceForm.value.quotaEfficiency).toFixed(5);
  }
  return '0.00000';
});

const formatDateToLocal = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const getChartDateRange = () => {
  const today = new Date();
  let startDate, endDate;
  
  if (chartTimeRange.value === 'week') {
    const dayOfWeek = today.getDay();
    const daysToMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    startDate = new Date(today);
    startDate.setDate(today.getDate() - daysToMonday);
    endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + 6);
  } else if (chartTimeRange.value === 'month') {
    if (today.getDate() >= 26) {
      startDate = new Date(today.getFullYear(), today.getMonth(), 26);
      endDate = new Date(today.getFullYear(), today.getMonth() + 1, 25);
    } else {
      startDate = new Date(today.getFullYear(), today.getMonth() - 1, 26);
      endDate = new Date(today.getFullYear(), today.getMonth(), 25);
    }
  } else if (chartSelectedMonth.value) {
    const [year, month] = chartSelectedMonth.value.split('-');
    const monthNum = parseInt(month);
    startDate = new Date(parseInt(year), monthNum - 2, 26);
    endDate = new Date(parseInt(year), monthNum - 1, 25);
  } else if (chartDateRange.value && chartDateRange.value.length === 2) {
    startDate = new Date(chartDateRange.value[0]);
    endDate = new Date(chartDateRange.value[1]);
  } else {
    if (today.getDate() >= 26) {
      startDate = new Date(today.getFullYear(), today.getMonth(), 26);
      endDate = new Date(today.getFullYear(), today.getMonth() + 1, 25);
    } else {
      startDate = new Date(today.getFullYear(), today.getMonth() - 1, 26);
      endDate = new Date(today.getFullYear(), today.getMonth(), 25);
    }
  }
  
  return {
    startDateStr: formatDateToLocal(startDate),
    endDateStr: formatDateToLocal(endDate)
  };
};

const getFilteredPerformances = computed(() => {
  const { startDateStr, endDateStr } = getChartDateRange();
  return allPerformances.value.filter(p => {
    return p.recordDate >= startDateStr && p.recordDate <= endDateStr;
  });
});

const getFilteredOvertimes = computed(() => {
  const { startDateStr, endDateStr } = getChartDateRange();
  return allOvertimes.value.filter(o => {
    return o.recordDate >= startDateStr && o.recordDate <= endDateStr;
  });
});

const totalPerformanceDays = computed(() => {
  const performanceSum = getFilteredPerformances.value.reduce((sum, p) => {
    const dailyPerformance = Number(p.performanceManDays) || 0;
    return sum + dailyPerformance;
  }, 0);
  
  const uniqueDates = new Set(getFilteredPerformances.value.map(p => p.recordDate));
  const baseDeduction = uniqueDates.size;
  
  const overtimeDeduction = getFilteredOvertimes.value.reduce((sum, o) => {
    const hours = Number(o.overtimeHours) || 0;
    return sum + (hours * 0.125);
  }, 0);
  
  return performanceSum - baseDeduction - overtimeDeduction;
});

const attendanceDays = computed(() => {
  const uniqueDates = new Set(getFilteredPerformances.value.map(p => p.recordDate));
  return uniqueDates.size;
});

const dailyAveragePerformance = computed(() => {
  if (attendanceDays.value === 0) return 0;
  return totalPerformanceDays.value / attendanceDays.value;
});

const totalOvertimeHours = computed(() => {
  return getFilteredOvertimes.value.reduce((sum, o) => {
    const hours = Number(o.overtimeHours) || 0;
    return sum + hours;
  }, 0);
});

const salaryTotalPages = computed(() => Math.ceil(salaries.value.length / salaryPageSize) || 1);

const visibleSalaryPages = computed(() => {
  const pages = [];
  const start = Math.max(1, salaryCurrentPage.value - 2);
  const end = Math.min(salaryTotalPages.value, salaryCurrentPage.value + 2);
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});

const paginatedSalaries = computed(() => {
  const start = (salaryCurrentPage.value - 1) * salaryPageSize;
  const end = start + salaryPageSize;
  return salaries.value.slice(start, end);
});

const calculatedTotalPayable = computed(() => {
  return (salaryForm.value.basicSalary || 0) +
         (salaryForm.value.performanceSalary || 0) +
         (salaryForm.value.positionPerformance || 0) +
         (salaryForm.value.mealAllowance || 0) +
         (salaryForm.value.housingAllowance || 0) +
         (salaryForm.value.fullAttendanceBonus || 0) +
         (salaryForm.value.otherBonus || 0) +
         (salaryForm.value.overtimeSalary || 0);
});

const calculatedTotalDeduction = computed(() => {
  return (salaryForm.value.pensionInsurance || 0) +
         (salaryForm.value.medicalInsurance || 0) +
         (salaryForm.value.unemploymentInsurance || 0) +
         (salaryForm.value.lateDeduction || 0);
});

const calculatedNetSalary = computed(() => {
  return calculatedTotalPayable.value - calculatedTotalDeduction.value;
});

const formatMoney = (value) => {
  if (value === null || value === undefined) return '¥0.00';
  return '¥' + Number(value).toFixed(2);
};

const chartTimeRange = ref('month');
const chartDateRange = ref(null);
const chartSelectedMonth = ref(null);
const chartRef = ref(null);
let chartInstance = null;

const getUid = () => {
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    const user = JSON.parse(savedUser);
    currentUser.value = user;
    return String(user.id);
  }
  return null;
};

const getProjectName = (projectId) => {
  const project = projects.value.find(p => p.id === projectId);
  return project ? project.projectName : '未知项目';
};

const formatDate = (dateStr) => {
  if (!dateStr) return '-';
  return dateStr.replace('T', ' ').substring(0, 19);
};

const fetchProjects = async () => {
  const uid = getUid();
  if (!uid) return;
  
  try {
    const response = await fetch(`${API_BASE}/project-configs?uid=${uid}&page=1&pageSize=10000`);
    const data = await response.json();
    allProjects.value = data.configs || [];
    applyProjectFilters();
  } catch (error) {
    console.error('获取项目配置失败:', error);
    ElMessage.error('获取项目配置失败');
  }
};

const applyProjectFilters = () => {
  let filtered = [...allProjects.value];
  
  if (projectNameSearch.value) {
    const searchTerm = projectNameSearch.value.toLowerCase();
    filtered = filtered.filter(p => 
      p.projectName.toLowerCase().includes(searchTerm)
    );
  }
  
  totalProjects.value = filtered.length;
  
  const start = (projectPage.value - 1) * projectPageSize.value;
  const end = start + projectPageSize.value;
  projects.value = filtered.slice(start, end);
};

const handleProjectSearch = () => {
  projectPage.value = 1;
  applyProjectFilters();
};

const goToProjectPage = (page) => {
  projectPage.value = page;
  applyProjectFilters();
};

const fetchPerformances = async () => {
  const uid = getUid();
  if (!uid) return;
  
  try {
    const response = await fetch(`${API_BASE}/daily-performances?uid=${uid}&page=1&pageSize=10000`);
    const data = await response.json();
    allPerformances.value = data.performances || [];
    applyFilters();
  } catch (error) {
    console.error('获取工作汇报失败:', error);
    ElMessage.error('获取工作汇报失败');
  }
};

const applyFilters = () => {
  let filtered = [...allPerformances.value];
  
  if (filterDateRange.value && filterDateRange.value.length === 2) {
    const [startDate, endDate] = filterDateRange.value;
    filtered = filtered.filter(p => {
      const recordDate = p.recordDate;
      return recordDate >= startDate && recordDate <= endDate;
    });
  }
  
  if (filterProjectId.value) {
    filtered = filtered.filter(p => p.projectId === filterProjectId.value);
  }
  
  if (filterManDays.value) {
    filtered = filtered.filter(p => {
      const manDays = Number(p.performanceManDays) || 0;
      if (filterManDays.value === 'lt1') {
        return manDays < 1;
      } else if (filterManDays.value === 'gte1') {
        return manDays >= 1;
      }
      return true;
    });
  }
  
  totalPerformances.value = filtered.length;
  
  const start = (performancePage.value - 1) * performancePageSize.value;
  const end = start + performancePageSize.value;
  performances.value = filtered.slice(start, end);
};

const goToPage = (page) => {
  performancePage.value = page;
  applyFilters();
};

const handleFilterChange = () => {
  performancePage.value = 1;
  applyFilters();
};

const resetFilters = () => {
  filterDateRange.value = null;
  filterProjectId.value = null;
  filterManDays.value = null;
  applyFilters();
};

const onProjectChange = () => {
  const project = projects.value.find(p => p.id === performanceForm.value.projectId);
  if (project && performanceForm.value.processType) {
    onProcessTypeChange();
  }
};

const onProcessTypeChange = () => {
  const project = projects.value.find(p => p.id === performanceForm.value.projectId);
  if (project) {
    if (performanceForm.value.processType === '作业') {
      performanceForm.value.quotaEfficiency = project.operationQuota;
    } else if (performanceForm.value.processType === '质检') {
      performanceForm.value.quotaEfficiency = project.qualityQuota;
    }
  }
};

const openAddPerformance = () => {
  performanceForm.value = {
    id: null,
    recordDate: new Date().toISOString().split('T')[0],
    projectId: '',
    processType: '',
    quotaEfficiency: 0,
    actualWorkload: 0
  };
  showAddPerformance.value = true;
  showWorkReport.value = false;
};

const openEditPerformance = (performance) => {
  performanceForm.value = {
    id: performance.id,
    recordDate: performance.recordDate,
    projectId: performance.projectId,
    processType: performance.processType,
    quotaEfficiency: performance.quotaEfficiency,
    actualWorkload: performance.actualWorkload
  };
  showEditPerformance.value = true;
  showWorkReport.value = false;
};

const closePerformanceModal = () => {
  showAddPerformance.value = false;
  showEditPerformance.value = false;
  showWorkReport.value = true;
};

const savePerformance = async () => {
  const uid = getUid();
  if (!uid) {
    ElMessage.error('请先登录');
    return;
  }
  
  if (!performanceForm.value.recordDate) {
    ElMessage.warning('请选择记录日期');
    return;
  }
  if (!performanceForm.value.projectId) {
    ElMessage.warning('请选择项目');
    return;
  }
  if (!performanceForm.value.processType) {
    ElMessage.warning('请选择工序类型');
    return;
  }
  if (!performanceForm.value.actualWorkload || performanceForm.value.actualWorkload <= 0) {
    ElMessage.warning('请输入实际工作量');
    return;
  }
  
  try {
    const url = showEditPerformance.value 
      ? `${API_BASE}/daily-performances/${performanceForm.value.id}`
      : `${API_BASE}/daily-performances`;
    
    const response = await fetch(url, {
      method: showEditPerformance.value ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        uid,
        recordDate: performanceForm.value.recordDate,
        projectId: performanceForm.value.projectId,
        processType: performanceForm.value.processType,
        quotaEfficiency: performanceForm.value.quotaEfficiency,
        actualWorkload: performanceForm.value.actualWorkload
      })
    });
    
    const data = await response.json();
    if (response.ok) {
      ElMessage.success(showEditPerformance.value ? '更新成功' : '汇报成功');
      closePerformanceModal();
      await fetchPerformances();
      updateChart();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

const deletePerformance = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/daily-performances/${id}`, { method: 'DELETE' });
    if (response.ok) {
      ElMessage.success('删除成功');
      await fetchPerformances();
      updateChart();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDeletePerformances = async () => {
  if (selectedPerformances.value.length === 0) return;
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedPerformances.value.length} 条记录吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/daily-performances/batch`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: selectedPerformances.value })
    });
    if (response.ok) {
      ElMessage.success('批量删除成功');
      selectedPerformances.value = [];
      selectAllPerformances.value = false;
      await fetchPerformances();
      updateChart();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
};

const toggleSelectAllPerformances = () => {
  if (selectAllPerformances.value) {
    selectedPerformances.value = performances.value.map(p => p.id);
  } else {
    selectedPerformances.value = [];
  }
};

// 加班汇报相关方法
const fetchOvertimes = async () => {
  const uid = getUid();
  if (!uid) return;
  
  try {
    const response = await fetch(`${API_BASE}/overtime-records?uid=${uid}&page=1&pageSize=10000`);
    const data = await response.json();
    allOvertimes.value = data.records || [];
    applyOvertimeFilters();
  } catch (error) {
    console.error('获取加班汇报失败:', error);
    ElMessage.error('获取加班汇报失败');
  }
};

const applyOvertimeFilters = () => {
  let filtered = [...allOvertimes.value];
  
  totalOvertimes.value = filtered.length;
  
  const start = (overtimePage.value - 1) * overtimePageSize.value;
  const end = start + overtimePageSize.value;
  overtimes.value = filtered.slice(start, end);
};

const goToOvertimePage = (page) => {
  overtimePage.value = page;
  applyOvertimeFilters();
};

const openAddOvertime = () => {
  overtimeForm.value = {
    recordDate: '',
    projectId: null,
    overtimeHours: 0,
    description: ''
  };
  showAddOvertime.value = true;
  showOvertimeReport.value = false;
};

const openEditOvertime = (overtime) => {
  overtimeForm.value = {
    recordDate: overtime.recordDate,
    projectId: overtime.projectId,
    overtimeHours: overtime.overtimeHours,
    description: overtime.description || ''
  };
  editingOvertimeId.value = overtime.id;
  showEditOvertime.value = true;
  showOvertimeReport.value = false;
};

const closeOvertimeModal = () => {
  showAddOvertime.value = false;
  showEditOvertime.value = false;
  showOvertimeReport.value = true;
};

const saveOvertime = async () => {
  const uid = getUid();
  if (!uid) {
    ElMessage.error('请先登录');
    return;
  }
  
  if (!overtimeForm.value.recordDate) {
    ElMessage.warning('请选择记录日期');
    return;
  }
  if (!overtimeForm.value.projectId) {
    ElMessage.warning('请选择项目');
    return;
  }
  if (!overtimeForm.value.overtimeHours || overtimeForm.value.overtimeHours <= 0) {
    ElMessage.warning('请输入加班时长');
    return;
  }
  
  try {
    const url = showEditOvertime.value 
      ? `${API_BASE}/overtime-records/${editingOvertimeId.value}`
      : `${API_BASE}/overtime-records`;
    
    const method = showEditOvertime.value ? 'PUT' : 'POST';
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        uid,
        recordDate: overtimeForm.value.recordDate,
        projectId: overtimeForm.value.projectId,
        overtimeHours: overtimeForm.value.overtimeHours,
        description: overtimeForm.value.description
      })
    });
    
    const data = await response.json();
    if (response.ok) {
      ElMessage.success(showEditOvertime.value ? '更新成功' : '添加成功');
      closeOvertimeModal();
      await fetchOvertimes();
      updateChart();
    } else {
      ElMessage.error(data.message || '保存失败');
    }
  } catch (error) {
    console.error('保存加班记录失败:', error);
    ElMessage.error('保存失败');
  }
};

const deleteOvertime = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/overtime-records/${id}`, { method: 'DELETE' });
    if (response.ok) {
      ElMessage.success('删除成功');
      await fetchOvertimes();
      updateChart();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDeleteOvertimes = async () => {
  if (selectedOvertimes.value.length === 0) return;
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedOvertimes.value.length} 条记录吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/overtime-records/batch`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: selectedOvertimes.value })
    });
    if (response.ok) {
      ElMessage.success('批量删除成功');
      selectedOvertimes.value = [];
      selectAllOvertimes.value = false;
      await fetchOvertimes();
      updateChart();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
};

// 工资记录相关函数
const fetchSalaries = async () => {
  const uid = getUid();
  if (!uid) return;
  
  try {
    const response = await fetch(`${API_BASE}/salary-records?uid=${uid}`);
    const data = await response.json();
    salaries.value = data.records || [];
  } catch (error) {
    console.error('获取工资记录失败:', error);
  }
};

const openAddSalary = () => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, '0');
  
  salaryForm.value = {
    id: null,
    month: `${year}-${month}`,
    periodStartDate: '',
    periodEndDate: '',
    attendanceDays: 0,
    basicSalary: salaryConfig.value.basicSalary,
    performanceCoefficient: 1,
    performanceSalary: 0,
    positionPerformance: salaryConfig.value.positionPerformance,
    mealAllowance: salaryConfig.value.mealAllowance,
    housingAllowance: salaryConfig.value.housingAllowance,
    fullAttendanceBonus: salaryConfig.value.fullAttendanceBonus,
    otherBonus: salaryConfig.value.otherBonus,
    pensionInsurance: salaryConfig.value.pensionInsurance,
    medicalInsurance: salaryConfig.value.medicalInsurance,
    unemploymentInsurance: salaryConfig.value.unemploymentInsurance,
    lateDeduction: salaryConfig.value.lateDeduction,
    overtimeHours: 0,
    overtimeSalary: 0
  };
  showAddSalary.value = true;
  showSalaryRecord.value = false;
};

const openEditSalary = (salary) => {
  salaryForm.value = {
    id: salary.id,
    month: salary.month,
    periodStartDate: salary.periodStartDate,
    periodEndDate: salary.periodEndDate,
    attendanceDays: salary.attendanceDays,
    basicSalary: salary.basicSalary,
    performanceCoefficient: salary.performanceCoefficient,
    performanceSalary: salary.performanceSalary,
    positionPerformance: salary.positionPerformance,
    mealAllowance: salary.mealAllowance,
    housingAllowance: salary.housingAllowance,
    fullAttendanceBonus: salary.fullAttendanceBonus,
    otherBonus: salary.otherBonus,
    pensionInsurance: salary.pensionInsurance,
    medicalInsurance: salary.medicalInsurance,
    unemploymentInsurance: salary.unemploymentInsurance,
    lateDeduction: salary.lateDeduction,
    overtimeHours: salary.overtimeHours,
    overtimeSalary: salary.overtimeSalary
  };
  editingSalaryId.value = salary.id;
  showEditSalary.value = true;
  showSalaryRecord.value = false;
};

const closeSalaryModal = () => {
  showAddSalary.value = false;
  showEditSalary.value = false;
  showSalaryRecord.value = true;
};

const saveSalary = async () => {
  const uid = getUid();
  if (!uid) {
    ElMessage.error('请先登录');
    return;
  }
  
  if (!salaryForm.value.month) {
    ElMessage.warning('请选择月份');
    return;
  }
  if (!salaryForm.value.periodStartDate || !salaryForm.value.periodEndDate) {
    ElMessage.warning('请选择周期日期');
    return;
  }
  
  try {
    const url = showEditSalary.value 
      ? `${API_BASE}/salary-records/${editingSalaryId.value}`
      : `${API_BASE}/salary-records`;
    
    const response = await fetch(url, {
      method: showEditSalary.value ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        uid,
        ...salaryForm.value
      })
    });
    
    const data = await response.json();
    if (response.ok) {
      ElMessage.success(showEditSalary.value ? '更新成功' : '记录成功');
      closeSalaryModal();
      fetchSalaries();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('保存工资记录失败:', error);
    ElMessage.error('保存失败');
  }
};

const deleteSalary = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/salary-records/${id}`, { method: 'DELETE' });
    if (response.ok) {
      ElMessage.success('删除成功');
      fetchSalaries();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDeleteSalaries = async () => {
  if (selectedSalaries.value.length === 0) return;
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedSalaries.value.length} 条记录吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/salary-records/batch`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: selectedSalaries.value })
    });
    if (response.ok) {
      ElMessage.success('批量删除成功');
      selectedSalaries.value = [];
      selectAllSalaries.value = false;
      fetchSalaries();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
};

const toggleSelectAllSalaries = () => {
  if (selectAllSalaries.value) {
    selectedSalaries.value = paginatedSalaries.value.map(s => s.id);
  } else {
    selectedSalaries.value = [];
  }
};

const toggleSelectAllOvertimes = () => {
  if (selectAllOvertimes.value) {
    selectedOvertimes.value = overtimes.value.map(o => o.id);
  } else {
    selectedOvertimes.value = [];
  }
};

const openAddProject = () => {
  projectForm.value = {
    id: null,
    projectName: '',
    operationQuota: 0,
    qualityQuota: 0
  };
  showAddProject.value = true;
  showProjectConfig.value = false;
};

const openEditProject = (project) => {
  projectForm.value = {
    id: project.id,
    projectName: project.projectName,
    operationQuota: project.operationQuota,
    qualityQuota: project.qualityQuota
  };
  showEditProject.value = true;
  showProjectConfig.value = false;
};

const closeProjectModal = () => {
  showAddProject.value = false;
  showEditProject.value = false;
  showProjectConfig.value = true;
};

const saveProject = async () => {
  const uid = getUid();
  if (!uid) {
    ElMessage.error('请先登录');
    return;
  }
  
  try {
    const url = showEditProject.value 
      ? `${API_BASE}/project-configs/${projectForm.value.id}`
      : `${API_BASE}/project-configs`;
    
    const response = await fetch(url, {
      method: showEditProject.value ? 'PUT' : 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        uid,
        projectName: projectForm.value.projectName,
        operationQuota: projectForm.value.operationQuota,
        qualityQuota: projectForm.value.qualityQuota
      })
    });
    
    const data = await response.json();
    if (response.ok) {
      ElMessage.success(showEditProject.value ? '更新成功' : '添加成功');
      closeProjectModal();
      fetchProjects();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('保存失败');
  }
};

const deleteProject = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个项目吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/project-configs/${id}`, { method: 'DELETE' });
    const data = await response.json();
    if (response.ok) {
      ElMessage.success('删除成功');
      fetchProjects();
    } else {
      ElMessage.error(data.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDeleteProjects = async () => {
  if (selectedProjects.value.length === 0) return;
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedProjects.value.length} 个项目吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`${API_BASE}/project-configs/batch`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: selectedProjects.value })
    });
    if (response.ok) {
      ElMessage.success('批量删除成功');
      selectedProjects.value = [];
      selectAllProjects.value = false;
      fetchProjects();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
};

const toggleSelectAllProjects = () => {
  if (selectAllProjects.value) {
    selectedProjects.value = projects.value.map(p => p.id);
  } else {
    selectedProjects.value = [];
  }
};

watch(showWorkReport, (val) => {
  if (val) fetchPerformances();
});

watch(showProjectConfig, (val) => {
  if (val) fetchProjects();
});

watch(showOvertimeReport, (val) => {
  if (val) fetchOvertimes();
});

// 图表相关方法
const initChart = () => {
  if (!chartRef.value) {
    setTimeout(() => initChart(), 100);
    return;
  }
  
  const rect = chartRef.value.getBoundingClientRect();
  if (rect.width === 0 || rect.height === 0) {
    setTimeout(() => initChart(), 100);
    return;
  }
  
  if (chartInstance) {
    chartInstance.dispose();
  }
  chartInstance = markRaw(echarts.init(chartRef.value));
  updateChart();
};

const updateChart = () => {
  if (!chartInstance) return;
  
  const today = new Date();
  let startDate, endDate;
  
  if (chartTimeRange.value === 'week') {
    const dayOfWeek = today.getDay();
    const daysToMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
    startDate = new Date(today);
    startDate.setDate(today.getDate() - daysToMonday);
    endDate = new Date(startDate);
    endDate.setDate(startDate.getDate() + 6);
  } else if (chartTimeRange.value === 'month') {
    if (today.getDate() >= 26) {
      startDate = new Date(today.getFullYear(), today.getMonth(), 26);
      endDate = new Date(today.getFullYear(), today.getMonth() + 1, 25);
    } else {
      startDate = new Date(today.getFullYear(), today.getMonth() - 1, 26);
      endDate = new Date(today.getFullYear(), today.getMonth(), 25);
    }
  } else if (chartSelectedMonth.value) {
    const [year, month] = chartSelectedMonth.value.split('-');
    const monthNum = parseInt(month);
    startDate = new Date(parseInt(year), monthNum - 2, 26);
    endDate = new Date(parseInt(year), monthNum - 1, 25);
  } else if (chartDateRange.value && chartDateRange.value.length === 2) {
    startDate = new Date(chartDateRange.value[0]);
    endDate = new Date(chartDateRange.value[1]);
  } else {
    if (today.getDate() >= 26) {
      startDate = new Date(today.getFullYear(), today.getMonth(), 26);
      endDate = new Date(today.getFullYear(), today.getMonth() + 1, 25);
    } else {
      startDate = new Date(today.getFullYear(), today.getMonth() - 1, 26);
      endDate = new Date(today.getFullYear(), today.getMonth(), 25);
    }
  }
  
  const startDateStr = formatDateToLocal(startDate);
  const endDateStr = formatDateToLocal(endDate);
  
  const filteredData = allPerformances.value.filter(p => {
    return p.recordDate >= startDateStr && p.recordDate <= endDateStr;
  });
  
  const dateMap = new Map();
  const currentDate = new Date(startDate);
  while (currentDate <= endDate) {
    const dateStr = formatDateToLocal(currentDate);
    dateMap.set(dateStr, 0);
    currentDate.setDate(currentDate.getDate() + 1);
  }
  
  filteredData.forEach(p => {
    const currentValue = dateMap.get(p.recordDate) || 0;
    dateMap.set(p.recordDate, currentValue + (Number(p.performanceManDays) || 0));
  });
  
  const dates = Array.from(dateMap.keys());
  const values = Array.from(dateMap.values());
  
  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: 'rgba(37, 99, 235, 0.15)',
      borderWidth: 1,
      padding: [14, 18],
      borderRadius: 12,
      textStyle: {
        color: '#2d3748',
        fontSize: 13
      },
      formatter: function(params) {
        if (!params || params.length === 0) return '';
        const date = params[0].axisValue;
        const value = params[0].value || 0;
        return `<div style="font-size:14px;font-weight:600;color:#1e40af;margin-bottom:8px;">${date}</div><div style="display:flex;justify-content:space-between;align-items:center;"><span style="color:#64748b;">绩效人天</span><span style="color:#409EFF;font-weight:600;font-size:14px;">${value.toFixed(5)} 天</span></div>`;
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: [{
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#e2e8f0'
        }
      },
      axisLabel: {
        color: '#718096',
        fontSize: 11,
        rotate: 45
      }
    }],
    yAxis: [{
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#718096',
        fontSize: 11
      },
      splitLine: {
        lineStyle: {
          color: '#e2e8f0',
          type: 'dashed'
        }
      }
    }],
    series: [{
      name: '绩效人天',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: {
        width: 2,
        color: '#409EFF'
      },
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0,
            color: 'rgba(66, 153, 225, 0.3)'
          }, {
            offset: 1,
            color: 'rgba(66, 153, 225, 0.05)'
          }]
        }
      },
      data: values,
      markLine: {
        silent: true,
        symbol: 'none',
        label: {
          show: true,
          position: 'end',
          formatter: '底量线',
          color: '#E6A23C',
          fontSize: 12
        },
        lineStyle: {
          color: '#E6A23C',
          type: 'dashed',
          width: 2
        },
        data: [{
          yAxis: 1
        }]
      }
    }]
  };
  
  chartInstance.setOption(option);
};

const setChartTimeRange = (range) => {
  chartTimeRange.value = range;
  if (range === 'week' || range === 'month') {
    chartDateRange.value = null;
    chartSelectedMonth.value = null;
  }
  updateChart();
};

const handleMonthChange = () => {
  if (chartSelectedMonth.value) {
    chartTimeRange.value = 'custom';
    chartDateRange.value = null;
    updateChart();
  }
};

const handleChartDateChange = () => {
  if (chartDateRange.value && chartDateRange.value.length === 2) {
    chartTimeRange.value = 'custom';
    chartSelectedMonth.value = null;
  }
  updateChart();
};

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

let resizeObserver = null;

onMounted(async () => {
  loadSalaryConfig();
  await Promise.all([
    fetchProjects(),
    fetchPerformances(),
    fetchOvertimes(),
    fetchSalaries()
  ]);
  
  window.addEventListener('resize', handleResize);
  
  await nextTick();
  
  if (chartRef.value) {
    resizeObserver = new ResizeObserver(() => {
      handleResize();
    });
    resizeObserver.observe(chartRef.value);
  }
  
  setTimeout(() => {
    initChart();
  }, 200);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  }
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
});

onActivated(() => {
  setTimeout(() => {
    if (chartInstance) {
      chartInstance.resize();
      updateChart();
    } else {
      initChart();
    }
  }, 100);
});
</script>

<style scoped>
.performance-record {
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
  gap: 12px;
}

.stats-section {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
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
}

.performance-card {
  border-left: 4px solid #409EFF;
}

.performance-value {
  color: #409EFF;
}

.attendance-card {
  border-left: 4px solid #48bb78;
}

.attendance-value {
  color: #48bb78;
}

.average-card {
  border-left: 4px solid #ed8936;
}

.average-value {
  color: #ed8936;
}

.overtime-card {
  border-left: 4px solid #9f7aea;
}

.overtime-value {
  color: #9f7aea;
}

.salary-card {
  border-left: 4px solid #f56565;
}

.salary-value {
  color: #f56565;
}

.form-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #4a5568;
  margin: 16px 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;
}

.form-summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  margin-top: 20px;
  padding: 16px 16px 0 16px;
  border-top: 2px solid #e2e8f0;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.summary-item.net {
  font-size: 16px;
  font-weight: 600;
}

.payable {
  color: #38a169;
  font-weight: 600;
}

.deduction {
  color: #e53e3e;
  font-weight: 600;
}

.net-salary {
  color: #3182ce;
  font-weight: 700;
}

.chart-section {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
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

.time-range-selector {
  display: flex;
  align-items: center;
  gap: 16px;
}

.time-buttons {
  display: flex;
  gap: 8px;
}

.time-btn {
  padding: 6px 16px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 6px;
  font-size: 13px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s;
  height: 34px;
}

.time-btn:hover {
  background: #f7fafc;
}

.time-btn.active {
  background: #409EFF;
  border-color: #409EFF;
  color: #ffffff;
}

.time-selectors {
  display: flex;
  gap: 12px;
}

:deep(.chart-date-picker) {
  width: 280px;
}

:deep(.chart-date-picker) {
  height: 34px;
}

:deep(.chart-month-picker) {
  width: 150px;
}

:deep(.chart-month-picker > .el-input__wrapper) {
  height: 34px;
}

.chart-container {
  width: 100%;
  height: 350px;
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

.btn-danger {
  background: linear-gradient(135deg, #fc8181 0%, #f56565 100%);
  color: #ffffff;
}

.btn-danger:hover {
  background: linear-gradient(135deg, #f56565 0%, #e53e3e 100%);
}

.btn-danger:disabled {
  background: #e2e8f0;
  color: #a0aec0;
  cursor: not-allowed;
}

.btn-sm {
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 6px;
}

.btn-sm + .btn-sm {
  margin-left: 8px;
}

.placeholder {
  height: calc(100% - 80px);
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(248, 250, 252, 0.5);
  border-radius: 12px;
  border: 1px dashed rgba(203, 213, 224, 0.5);
}

.placeholder p {
  font-size: 16px;
  color: #a0aec0;
  margin: 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  height: 80vh;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.work-report-modal,
.project-config-modal,
.placeholder-modal {
  width: 80vw;
  max-width: 80vw;
  height: 80vh;
  max-height: 80vh;
}

.modal-form {
  width: 80vw;
  max-width: 80vw;
  height: 80vh;
  max-height: 80vh;
  padding: 0;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2d3748;
}

.modal-header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  display: flex;
  align-items: flex-end;
  gap: 16px;
  padding: 16px 24px;
  background: #f7fafc;
  border-bottom: 1px solid #e2e8f0;
  flex-wrap: wrap;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-item label {
  font-size: 13px;
  font-weight: 500;
  color: #4a5568;
}

:deep(.filter-date-picker) {
  height: 34px;
}

:deep(.filter-select > .el-select__wrapper) {
  height: 34px;
  width: 160px;
}

:deep(.filter-input > .el-input__wrapper) {
  height: 34px;
}

.btn-reset {
  width: 80px;
  height: 34px;
}

.search-icon {
  margin-right: 4px;
}

.table-container {
  flex: 1;
  overflow: auto;
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
}

.data-table tbody tr:hover {
  background: #f7fafc;
}

.data-table input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.no-data {
  text-align: center;
  padding: 34px;
  color: #a0aec0;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #f7fafc;
}

.pagination-info {
  font-size: 14px;
  color: #718096;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  padding: 6px 12px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #4a5568;
  transition: all 0.2s;
}

.page-btn:hover:not(.disabled) {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.page-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number {
  width: 32px;
  height: 34px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #4a5568;
  transition: all 0.2s;
}

.page-number:hover {
  background: #edf2f7;
}

.page-number.active {
  background: #409EFF;
  border-color: #409EFF;
  color: #ffffff;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #718096;
}

.page-size-select {
  padding: 4px 8px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
}

.form-group {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin: 0 2%;
}

.form-row .form-group {
  flex: 1;
}

.form-group label {
  white-space: nowrap;
  font-weight: 500;
  color: #4a5568;
  min-width: 100px;
  width: 100px;
}

.form-group input,
.form-group select {
  flex: 1;
  padding: 0 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  line-height: 34px;
  height: 34px;
  box-sizing: border-box;
}

:deep(.custom-date-picker) {
  flex: 1;
  height: 34px;
}

:deep(.full-width .el-select__wrapper) {
  height: 34px;
  min-height: auto; 
}

:deep(.el-input-number) {
  flex: 1;
}

:deep(.el-date-editor) {
  flex: 1;
}

:deep(.el-input__wrapper) {
  height: 34px;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
  outline: none;
}

.readonly-input {
  background: #f7fafc !important;
  color: #718096 !important;
  cursor: not-allowed;
  height: 34px !important;
  line-height: 38px !important;
}

.placeholder-content {
  padding: 60px 24px;
  text-align: center;
  color: #a0aec0;
  font-size: 16px;
}

.data-table th > input,.data-table td > input {
  margin-left: 20%;
}
</style>
