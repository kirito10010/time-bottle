<template>
  <el-config-provider :locale="zhCn">
    <div class="financial-ledger">
      <div class="header-container">
        <h2>财务台账</h2>
        <div class="action-buttons">
          <button class="btn btn-primary" @click="showBillManagement = true">账单管理</button>
          <button class="btn btn-secondary" @click="showCategoryManagement = true">分类管理</button>
          <button class="btn btn-secondary" @click="showDataVisualization = true">数据可视化</button>
          <button class="btn btn-secondary" @click="showImportExport = true">导入导出</button>
        </div>
      </div>
      
      <!-- 统计卡片区域 -->
      <div class="stats-section">
        <div class="stat-card income-card">
          <div class="stat-title">总收入</div>
          <div class="stat-value income-value">¥{{ totalIncome.toFixed(2) }}</div>
        </div>
        <div class="stat-card expense-card">
          <div class="stat-title">总支出</div>
          <div class="stat-value expense-value">¥{{ totalExpense.toFixed(2) }}</div>
        </div>
        <div class="stat-card balance-card">
          <div class="stat-title">结余</div>
          <div class="stat-value balance-value">¥{{ balance.toFixed(2) }}</div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-section">
        <div class="chart-header">
          <h3>财务趋势</h3>
          <div class="time-range-selector">
            <div class="time-buttons">
              <button class="time-btn" :class="{ active: timeRange === 'week' }" @click="setTimeRange('week')">本周</button>
              <button class="time-btn" :class="{ active: timeRange === 'month' }" @click="setTimeRange('month')">本月</button>
            </div>
            <div class="time-selectors">
              <el-date-picker
                v-model="selectedMonthDate"
                type="month"
                placeholder="选择月份"
                format="YYYY年MM月"
                value-format="YYYY-MM"
                class="month-picker"
                style="width: 150px"
                @change="handleMonthChange"
              />
              <div class="date-range-container">
                <el-date-picker
                  v-model="customDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  class="date-picker"
                  style="width: 280px"
                  :locale="zhCn"
                  @change="handleDateRangeChange"
                />
              </div>
            </div>
          </div>
        </div>
        <div id="financialChart" ref="chartRef" class="chart-container"></div>
      </div>
    </div>
    
    <!-- 添加/编辑分类弹窗 -->
    <Teleport to="body">
      <div v-if="showAddCategory || showEditCategory" class="modal-overlay" @click="showAddCategory = showEditCategory = false; showCategoryManagement = true">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="saveCategory">
          <div class="modal-header">
            <h3>{{ showEditCategory ? '编辑分类' : '添加分类' }}</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          <div class="form-group">
            <label>分类名称</label>
            <input type="text" v-model="categoryName" required maxlength="30" placeholder="请输入分类名称（最多30个字符）">
          </div>
          
          <div class="form-group">
            <label>分类类型</label>
            <div class="type-selector">
              <button type="button" 
                      :class="['type-btn', { active: categoryType === 0 }]"
                      @click="categoryType = 0">
                支出
              </button>
              <button type="button" 
                      :class="['type-btn', { active: categoryType === 1 }]"
                      @click="categoryType = 1">
                收入
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label>排序值</label>
            <input type="number" v-model.number="categorySort" min="0" max="255" placeholder="数字越小越靠前，默认0">
            <div class="form-hint">排序值用于控制分类显示顺序，数字越小排在越前面。例如：0排在1前面，1排在2前面。</div>
          </div>
          
          <div class="form-group" v-if="currentUserRole === '2'">
            <label class="checkbox-label">
              <input type="checkbox" v-model="categoryIsDefault">
              设为系统默认分类（所有用户可见）
            </label>
          </div>
        </form>
      </div>
    </div>
    </Teleport>
    
    <!-- 账单管理弹窗 -->
    <Teleport to="body">
      <div v-if="showBillManagement" class="modal-overlay" @click="showBillManagement = false">
      <div class="modal bill-management-modal" @click.stop>
        <div class="modal-header">
          <h3>账单管理</h3>
          <div class="modal-header-actions">
            <button class="btn btn-primary" @click="showAddBill = true; showBillManagement = false">添加账单</button>
          </div>
        </div>
        
        <div class="bill-management-content">
          <!-- 账单筛选 -->
          <div class="bill-filter">
            <div class="filter-item">
              <label>日期</label>
              <el-date-picker
                v-model="filterDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="date-picker"
                style="width: 150px"
              />
            </div>
            <div class="filter-item">
              <label>类型</label>
              <select v-model="filterType" class="filter-select">
                <option value="">全部</option>
                <option value="0">支出</option>
                <option value="1">收入</option>
              </select>
            </div>
            <div class="filter-item">
              <label>分类</label>
              <select v-model="filterCategory" class="filter-select">
                <option value="">全部</option>
                <option v-for="category in filteredCategories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>
            <div class="filter-item">
              <label>账户</label>
              <select v-model="filterAccount" class="filter-select">
                <option value="">全部</option>
                <option value="现金">现金</option>
                <option value="微信">微信</option>
                <option value="支付宝">支付宝</option>
                <option value="银行卡">银行卡</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="filter-item amount-range">
              <label>金额范围</label>
              <div class="amount-inputs">
                <input type="number" v-model="filterMinAmount" step="0.01" placeholder="最小" class="amount-input">
                <span class="amount-separator">-</span>
                <input type="number" v-model="filterMaxAmount" step="0.01" placeholder="最大" class="amount-input">
              </div>
            </div>
            <div class="filter-item">
              <label>备注</label>
              <input type="text" v-model="filterRemark" placeholder="模糊搜索" class="filter-input">
            </div>
            <div class="filter-actions">
              <button class="btn btn-secondary" @click="resetFilters">重置</button>
              <button class="btn btn-primary" @click="fetchBills">查询</button>
            </div>
          </div>
          
          <!-- 账单列表 -->
          <div class="bill-table-container">
            <table v-if="bills.length > 0" class="bill-table">
              <thead>
                <tr>
                  <th>日期</th>
                  <th>时间</th>
                  <th>类型</th>
                  <th>分类</th>
                  <th>账户</th>
                  <th>金额</th>
                  <th>备注</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bill in bills" :key="bill.id">
                  <td>{{ bill.billDate }}</td>
                  <td>{{ bill.billTime || '-' }}</td>
                  <td :class="bill.type === '0' ? 'expense' : 'income'">
                    {{ bill.type === '0' ? '支出' : '收入' }}
                  </td>
                  <td>{{ getCategoryName(bill.categoryId) }}</td>
                  <td>{{ bill.account }}</td>
                  <td :class="bill.type === '0' ? 'expense' : 'income'">
                    {{ bill.type === '0' ? '-' : '+' }}{{ bill.amount.toFixed(2) }}
                  </td>
                  <td>{{ bill.remark || '-' }}</td>
                  <td>
                    <button class="btn btn-sm btn-secondary" @click="editBill(bill); showBillManagement = false">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteBill(bill.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div v-if="bills.length === 0" class="no-data">暂无账单数据</div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination-container" v-if="totalBills > 0">
            <div class="pagination-info">
              <span>共 {{ totalBills }} 条记录</span>
            </div>
            <div class="pagination-controls">
              <button class="page-btn" :class="{ disabled: currentPage === 1 }" @click="currentPage > 1 && (currentPage--, fetchBills())">
                <span class="page-icon">‹</span>
                <span class="page-text">上一页</span>
              </button>
              
              <!-- 页码按钮 -->
              <div class="page-numbers">
                <button 
                  v-for="page in visiblePages" 
                  :key="page"
                  class="page-number" 
                  :class="{ active: page === currentPage }"
                  @click="currentPage = page; fetchBills()"
                >
                  {{ page }}
                </button>
              </div>
              
              <button class="page-btn" :class="{ disabled: currentPage >= totalPages }" @click="currentPage < totalPages && (currentPage++, fetchBills())">
                <span class="page-text">下一页</span>
                <span class="page-icon">›</span>
              </button>
            </div>
            <div class="page-size-selector">
              <label>每页显示:</label>
              <select v-model="pageSize" @change="currentPage = 1; fetchBills()" class="page-size-select">
                <option value="10">10条</option>
                <option value="20">20条</option>
                <option value="50">50条</option>
                <option value="100">100条</option>
              </select>
            </div>
          </div>
        </div>
      </div>
    </div>
    </Teleport>
    
    <!-- 添加账单弹窗 -->
    <Teleport to="body">
      <div v-if="showAddBill" class="modal-overlay" @click="showAddBill = false; showBillManagement = true">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="addBill">
          <div class="modal-header">
            <h3>添加账单</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>类型</label>
              <div class="type-selector">
                <button type="button" 
                        :class="['type-btn', { active: billType === 0 }]"
                        @click="billType = 0; billCategory = ''">
                  支出
                </button>
                <button type="button" 
                        :class="['type-btn', { active: billType === 1 }]"
                        @click="billType = 1; billCategory = ''">
                  收入
                </button>
              </div>
            </div>
            
            <div class="form-group">
              <label>金额</label>
              <input type="number" v-model.number="billAmount" step="0.01" min="0" placeholder="请输入金额" required>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>分类</label>
              <select v-model="billCategory" required>
                <option value="">请选择分类</option>
                <option v-for="category in categories.filter(cat => cat.type === String(billType) && cat.isDeleted === '0')" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>账户</label>
              <select v-model="billAccount" required>
                <option value="现金">现金</option>
                <option value="微信">微信</option>
                <option value="支付宝">支付宝</option>
                <option value="银行卡">银行卡</option>
                <option value="其他">其他</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>日期</label>
              <el-date-picker
                v-model="billDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="date-picker"
                style="width: 150px"
                required
              />
            </div>
            
            <div class="form-group">
              <label>时间</label>
              <el-time-picker
                v-model="billTime"
                placeholder="选择时间"
                format="HH:mm"
                value-format="HH:mm"
                class="time-picker"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label>备注</label>
            <input type="text" v-model="billRemark" placeholder="请输入备注">
          </div>
        </form>
      </div>
    </div>
    </Teleport>
    
    <!-- 分类管理弹窗 -->
    <Teleport to="body">
      <div v-if="showCategoryManagement" class="modal-overlay" @click="showCategoryManagement = false">
      <div class="modal category-management-modal" @click.stop>
        <div class="modal-header">
          <h3>分类管理</h3>
          <div class="modal-header-actions">
            <button class="btn btn-primary" @click="showAddCategory = true; showCategoryManagement = false">添加分类</button>
          </div>
        </div>
        
        <!-- 分类列表 -->
        <div class="category-list">
          <div class="category-section">
            <div class="section-header">
              <div class="section-icon expense-icon">
                <i class="icon">💰</i>
              </div>
              <h4>支出分类</h4>
            </div>
            <div class="category-items">
              <div v-for="category in expenseCategories" :key="category.id" class="category-item">
                <div class="category-info">
                  <div class="category-name">{{ category.name }}</div>
                  <div class="category-meta" v-if="category.isDefault === '1'">默认分类</div>
                </div>
                <div class="category-actions">
                  <button class="btn btn-sm btn-secondary" @click="editCategory(category); showCategoryManagement = false" v-if="category.isDefault !== '1' || currentUserRole === '2'">
                    <i class="action-icon">✏️</i> 编辑
                  </button>
                  <button class="btn btn-sm btn-danger" @click="deleteCategory(category.id)" v-if="category.isDefault !== '1' || currentUserRole === '2'">
                    <i class="action-icon">🗑️</i> 删除
                  </button>
                </div>
              </div>
              <div v-if="expenseCategories.length === 0" class="no-data">
                <div class="no-data-icon">📭</div>
                <div class="no-data-text">暂无支出分类</div>
                <div class="no-data-hint">点击上方"添加分类"按钮创建</div>
              </div>
            </div>
          </div>
          
          <div class="category-section">
            <div class="section-header">
              <div class="section-icon income-icon">
                <i class="icon">📈</i>
              </div>
              <h4>收入分类</h4>
            </div>
            <div class="category-items">
              <div v-for="category in incomeCategories" :key="category.id" class="category-item">
                <div class="category-info">
                  <div class="category-name">{{ category.name }}</div>
                  <div class="category-meta" v-if="category.isDefault === '1'">默认分类</div>
                </div>
                <div class="category-actions">
                  <button class="btn btn-sm btn-secondary" @click="editCategory(category); showCategoryManagement = false" v-if="category.isDefault !== '1' || currentUserRole === '2'">
                    <i class="action-icon">✏️</i> 编辑
                  </button>
                  <button class="btn btn-sm btn-danger" @click="deleteCategory(category.id)" v-if="category.isDefault !== '1' || currentUserRole === '2'">
                    <i class="action-icon">🗑️</i> 删除
                  </button>
                </div>
              </div>
              <div v-if="incomeCategories.length === 0" class="no-data">
                <div class="no-data-icon">📭</div>
                <div class="no-data-text">暂无收入分类</div>
                <div class="no-data-hint">点击上方"添加分类"按钮创建</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    </Teleport>
    
    <!-- 编辑账单弹窗 -->
    <Teleport to="body">
      <div v-if="showEditBill" class="modal-overlay" @click="showEditBill = false; showBillManagement = true">
      <div class="modal modal-form" @click.stop>
        <form @submit.prevent="updateBill">
          <div class="modal-header">
            <h3>编辑账单</h3>
            <div class="modal-header-actions">
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>类型</label>
              <div class="type-selector">
                <button type="button" 
                        :class="['type-btn', { active: editBillData.type === 0 }]"
                        @click="editBillData.type = 0; editBillData.categoryId = ''">
                  支出
                </button>
                <button type="button" 
                        :class="['type-btn', { active: editBillData.type === 1 }]"
                        @click="editBillData.type = 1; editBillData.categoryId = ''">
                  收入
                </button>
              </div>
            </div>
            
            <div class="form-group">
              <label>金额</label>
              <input type="number" v-model.number="editBillData.amount" step="0.01" placeholder="请输入金额" required>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>分类</label>
              <select v-model="editBillData.categoryId" required>
                <option value="">请选择分类</option>
                <option v-for="category in categories.filter(cat => cat.type === String(editBillData.type) && cat.isDeleted === '0')" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
            </div>
            
            <div class="form-group">
              <label>账户</label>
              <select v-model="editBillData.account" required>
                <option value="现金">现金</option>
                <option value="微信">微信</option>
                <option value="支付宝">支付宝</option>
                <option value="银行卡">银行卡</option>
                <option value="其他">其他</option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>日期</label>
              <el-date-picker
                v-model="editBillData.billDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="date-picker"
                style="width: 150px"
                required
              />
            </div>
            
            <div class="form-group">
              <label>时间</label>
              <el-time-picker
                v-model="editBillData.billTime"
                placeholder="选择时间"
                format="HH:mm"
                value-format="HH:mm"
                class="time-picker"
              />
            </div>
          </div>
          
          <div class="form-group">
            <label>备注</label>
            <input type="text" v-model="editBillData.remark" placeholder="请输入备注">
          </div>
        </form>
      </div>
    </div>
    </Teleport>
    
    <!-- 数据可视化弹窗 -->
    <Teleport to="body">
      <div v-if="showDataVisualization" class="modal-overlay" @click="showDataVisualization = false">
      <div class="modal data-visualization-modal" @click.stop>
        <div class="modal-header">
          <h3>数据可视化</h3>
        </div>
        <div class="chart-types">
          <button class="btn btn-secondary" :class="{ active: currentChartType === 'pie' }" @click="switchChartType('pie')">饼图（分类占比）</button>
          <button class="btn btn-secondary" :class="{ active: currentChartType === 'bar' }" @click="switchChartType('bar')">柱状图（月度对比）</button>
          <button class="btn btn-secondary" :class="{ active: currentChartType === 'heatmap' }" @click="switchChartType('heatmap')">热力图（年度分布）</button>
        </div>
        
        <!-- 饼图月份选择器 -->
        <div v-if="currentChartType === 'pie'" class="year-selector">
          <el-date-picker
            v-model="pieMonthDate"
            type="month"
            placeholder="选择月份"
            format="YYYY年MM月"
            value-format="YYYY-MM"
            @change="onPieMonthChange"
            class="month-picker"
            style="width: 150px"
          />
        </div>
        
        <!-- 柱状图年份选择器 -->
        <div v-if="currentChartType === 'bar'" class="year-selector">
          <el-date-picker
            v-model="selectedYearDate"
            type="year"
            placeholder="选择年份"
            format="YYYY年"
            value-format="YYYY"
            @change="onYearChange"
            class="year-picker"
            style="width: 150px"
          />
        </div>
        
        <!-- 热力图年份选择器和收支切换 -->
        <div v-if="currentChartType === 'heatmap'" class="heatmap-controls">
          <el-date-picker
            v-model="heatmapYearDate"
            type="year"
            placeholder="选择年份"
            format="YYYY年"
            value-format="YYYY"
            @change="onHeatmapYearChange"
            class="year-picker"
            style="width: 150px"
          />
          <div class="heatmap-type-buttons">
            <button class="btn btn-sm" :class="{ active: heatmapType === 'all' }" @click="heatmapType = 'all'; updateVisualizationChart()">全部</button>
            <button class="btn btn-sm" :class="{ active: heatmapType === 'income' }" @click="heatmapType = 'income'; updateVisualizationChart()">收入</button>
            <button class="btn btn-sm" :class="{ active: heatmapType === 'expense' }" @click="heatmapType = 'expense'; updateVisualizationChart()">支出</button>
          </div>
        </div>
        
        <div id="visualizationChart" ref="visualizationChartRef" class="chart-container"></div>
      </div>
    </div>
    </Teleport>
    
    <!-- 导入导出弹窗 -->
    <Teleport to="body">
      <div v-if="showImportExport" class="modal-overlay" @click="showImportExport = false">
      <div class="modal import-export-modal" @click.stop>
        <div class="modal-header">
          <h3>数据导入导出</h3>
        </div>
        <div class="import-export-tabs">
          <button class="tab-btn" :class="{ active: importExportTab === 'export' }" @click="importExportTab = 'export'">导出</button>
          <button class="tab-btn" :class="{ active: importExportTab === 'import' }" @click="importExportTab = 'import'">导入</button>
        </div>
        <div class="import-export-content">
          <div v-if="importExportTab === 'export'" class="export-section">
            <div class="form-group">
              <label class="form-label">导出格式</label>
              <select v-model="exportFormat" class="form-select">
                <option value="csv">CSV</option>
                <option value="excel">Excel</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">导出范围</label>
              <el-date-picker
                v-model="exportDateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                class="date-picker w-full"
              />
            </div>
            <div class="form-actions">
              <button class="btn btn-primary w-full" @click="exportData">导出数据</button>
            </div>
          </div>
          <div v-if="importExportTab === 'import'" class="import-section">
            <div class="form-group">
              <label class="form-label">选择文件</label>
              <div class="file-upload-container">
                <input type="file" @change="handleFileUpload" accept=".csv,.xlsx,.xls" class="file-input">
                <div class="file-upload-info">
                  <span v-if="selectedFile" class="file-name">{{ selectedFile.name }}</span>
                  <span v-else class="file-placeholder">点击或拖拽文件到此处</span>
                </div>
              </div>
              <div class="file-format-hint">支持 CSV、Excel 格式文件</div>
            </div>
            <div class="form-actions">
              <button class="btn btn-primary w-full" @click="importData">导入数据</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    </Teleport>
  </el-config-provider>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, shallowRef, markRaw } from 'vue';
import * as echarts from 'echarts';

import { ElDatePicker, ElTimePicker, ElConfigProvider, ElMessageBox } from 'element-plus';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/es/locale/lang/zh-cn';

// 用户信息缓存
let cachedUser = null;
let cachedUserId = null;

// 防抖函数
const debounce = (fn, delay) => {
  let timer = null;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn.apply(this, args), delay);
  };
};

// 获取当前用户角色
const currentUserRole = computed(() => {
  if (!cachedUser) {
    const savedUser = localStorage.getItem('user');
    cachedUser = savedUser ? JSON.parse(savedUser) : null;
  }
  return cachedUser?.role || '0';
});

// 获取用户ID（使用缓存）
const getUserId = () => {
  if (!cachedUser) {
    const savedUser = localStorage.getItem('user');
    cachedUser = savedUser ? JSON.parse(savedUser) : null;
  }
  if (cachedUserId === null) {
    cachedUserId = cachedUser?.id || 0;
  }
  return cachedUserId;
};

// 账单相关数据
const billType = ref(0); // 0=支出, 1=收入
const billAmount = ref(null);
const billCategory = ref('');
const billAccount = ref('现金');
const getCurrentDate = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const dateStr = `${year}-${month}-${day}`;
  return dateStr;
};
const billDate = ref(getCurrentDate());
const billTime = ref('');
const billRemark = ref('');

// 分类相关数据
const categories = ref([]);
const showAddCategory = ref(false);
const showEditCategory = ref(false);
const categoryName = ref('');
const categoryType = ref(0);
const categorySort = ref(0);
const categoryIsDefault = ref(false);
const editingCategoryId = ref(null);

// 账单列表相关数据
const bills = ref([]);
const filterDate = ref('');
// 筛选相关数据
const filterType = ref(''); // 类型：空=全部， 0=支出, 1=收入
const filterCategory = ref(''); // 分类ID
const filterAccount = ref(''); // 账户
const filterMinAmount = ref(''); // 最小金额
const filterMaxAmount = ref(''); // 最大金额
const filterRemark = ref(''); // 备注搜索

// 账单管理相关数据
const showBillManagement = ref(false);
const showAddBill = ref(false);

// 分类管理相关数据
const showCategoryManagement = ref(false);

// 编辑账单相关数据
const showEditBill = ref(false);

// 新功能弹窗状态
const showDataVisualization = ref(false);
const showImportExport = ref(false);



// 导入导出相关数据
const importExportTab = ref('export');
const exportFormat = ref('csv');
const exportDateRange = ref([]);

// 数据可视化相关数据
const visualizationChartRef = ref(null);
const visualizationChart = shallowRef(null);
const currentChartType = ref('pie');
const heatmapType = ref('all'); // 'all' 全部, 'income' 收入, 'expense' 支出
const selectedYear = ref('recent12'); // 'recent12' 最近12个月, 'all' 全部, 或具体年份如 '2026'
const heatmapYear = ref(String(new Date().getFullYear())); // 热力图年份，默认今年
const pieMonth = ref((() => {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
})()); // 饼图月份，默认本月
// Element Plus 日期选择器绑定值
const pieMonthDate = ref(new Date()); // 饼图月份选择器
const selectedYearDate = ref(new Date()); // 柱状图年份选择器
const heatmapYearDate = ref(new Date()); // 热力图年份选择器
// 月份选择器变化处理
const onPieMonthChange = (val) => {
  if (val) {
    pieMonth.value = val;
    updateVisualizationChart();
  }
};

// 年份选择器变化处理
const onYearChange = (val) => {
  if (val) {
    selectedYear.value = val;
    updateVisualizationChart();
  }
};

// 热力图年份选择器变化处理
const onHeatmapYearChange = (val) => {
  if (val) {
    heatmapYear.value = val;
    updateVisualizationChart();
  }
};

// 饼图月份选项（动态生成）
const pieMonthOptions = computed(() => {
  const options = [];
  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth() + 1;
  
  // 从当前月份往前推24个月
  for (let i = 0; i < 24; i++) {
    let year = currentYear;
    let month = currentMonth - i;
    
    while (month <= 0) {
      year--;
      month += 12;
    }
    
    const monthStr = `${year}-${String(month).padStart(2, '0')}`;
    options.push({
      value: monthStr,
      label: `${year}年${month}月`
    });
  }
  
  return options;
});

// 动态生成年份选项
const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear();
  const options = [
    { value: 'recent12', label: '最近12个月' }
  ];
  
  // 从当前年份开始，往前推3年
  for (let i = 0; i < 3; i++) {
    const year = currentYear - i;
    options.push({ value: String(year), label: `${year}年` });
  }
  
  options.push({ value: 'all', label: '全部年份' });
  
  return options;
});

// 热力图年份选项（不包含"最近12个月"）
const heatmapYearOptions = computed(() => {
  const currentYear = new Date().getFullYear();
  const options = [];
  
  // 从当前年份开始，往前推5年
  for (let i = 0; i < 5; i++) {
    const year = currentYear - i;
    options.push({ value: String(year), label: `${year}年` });
  }
  
  return options;
});

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);
const totalBills = ref(0);

// 计算属性：总页数
const totalPages = computed(() => {
  return Math.ceil(totalBills.value / pageSize.value) || 1;
});

// 计算属性：可见页码
const visiblePages = computed(() => {
  const pages = [];
  const total = totalPages.value;
  const current = currentPage.value;
  
  // 显示当前页附近的页码，最多显示5个页码
  let start = Math.max(1, current - 2);
  let end = Math.min(total, start + 4);
  
  // 调整起始位置，确保显示5个页码
  if (end - start < 4) {
    start = Math.max(1, end - 4);
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  
  return pages;
});
const editBillData = ref({
  id: null,
  type: 0,
  amount: '',
  categoryId: '',
  account: '现金',
  billDate: '',
  billTime: '',
  remark: ''
});

// 图表相关数据
const chartRef = ref(null);
const chart = shallowRef(null);
const timeRange = ref('week');
const selectedMonth = ref('');
const selectedMonthDate = ref('');
const customDateRange = ref([]);
const customStartDate = ref('');
const customEndDate = ref('');
const chartData = ref({ dates: [], income: [], expense: [] });

// 统计数据
const totalIncome = ref(0);
const totalExpense = ref(0);
const balance = ref(0);

// 月份选项
const generateMonths = () => {
  const monthsList = [];
  const currentYear = new Date().getFullYear();
  const lastYear = currentYear - 1;
  
  // 去年的所有月份
  for (let month = 1; month <= 12; month++) {
    const monthStr = String(month).padStart(2, '0');
    monthsList.push({
      value: `${lastYear}-${monthStr}`,
      label: `${lastYear}年${month}月`
    });
  }
  
  // 今年的所有月份
  for (let month = 1; month <= 12; month++) {
    const monthStr = String(month).padStart(2, '0');
    monthsList.push({
      value: `${currentYear}-${monthStr}`,
      label: `${currentYear}年${month}月`
    });
  }
  
  return monthsList;
};

const months = ref(generateMonths());

// 计算属性：所有分类
const filteredCategories = computed(() => {
  return categories.value.filter(cat => cat.isDeleted === '0');
});

// 计算属性：所有分类（编辑账单时使用）
const filteredEditCategories = computed(() => {
  return categories.value.filter(cat => cat.isDeleted === '0');
});

// 计算属性：支出分类
const expenseCategories = computed(() => {
  return categories.value.filter(cat => cat.type === '0' && cat.isDeleted === '0');
});

// 计算属性：收入分类
const incomeCategories = computed(() => {
  return categories.value.filter(cat => cat.type === '1' && cat.isDeleted === '0');
});

// 分类名称缓存 Map
const categoryMap = computed(() => {
  const map = new Map();
  categories.value.forEach(cat => {
    if (cat.isDeleted === '0') {
      map.set(cat.id, cat.name);
    }
  });
  return map;
});

// 获取分类名称（使用 Map 优化查找性能）
const getCategoryName = (categoryId) => {
  return categoryMap.value.get(categoryId) || '未知分类';
};

// 加载分类数据
const fetchCategories = async () => {
  try {
    const userId = getUserId();
    
    const response = await fetch(`http://localhost:8080/api/categories?userId=${userId}`);
    if (response.ok) {
      const data = await response.json();
      categories.value = data.categories;
    }
  } catch (error) {
  }
};

// 加载账单数据（分页）
const fetchBills = async () => {
  try {
    const userId = getUserId();
    
    const params = new URLSearchParams({
      page: currentPage.value.toString(),
      pageSize: pageSize.value.toString(),
      userId: userId.toString()
    });
    
    const endpoint = 'bills';
    
    if (filterDate.value) {
      params.append('date', filterDate.value);
    }
    if (filterType.value !== '') {
      params.append('type', filterType.value);
    }
    if (filterCategory.value) {
      params.append('categoryId', filterCategory.value);
    }
    if (filterAccount.value) {
      params.append('account', filterAccount.value);
    }
    if (filterMinAmount.value) {
      params.append('minAmount', filterMinAmount.value);
    }
    if (filterMaxAmount.value) {
      params.append('maxAmount', filterMaxAmount.value);
    }
    if (filterRemark.value) {
      params.append('remark', filterRemark.value);
    }
    
    const url = `http://localhost:8080/api/${endpoint}?${params.toString()}`;
    
    const response = await fetch(url);
    
    if (response.ok) {
      const data = await response.json();
      bills.value = data.bills;
      totalBills.value = data.total || 0;
    } else {
      const errorData = await response.json();
      alert(errorData.message || '获取账单失败');
    }
  } catch (error) {
    console.error('获取账单出错:', error);
  }
};

// 重置筛选条件
const resetFilters = () => {
  filterDate.value = '';
  filterType.value = '';
  filterCategory.value = '';
  filterAccount.value = '';
  filterMinAmount.value = '';
  filterMaxAmount.value = '';
  filterRemark.value = '';
  currentPage.value = 1;
  fetchBills();
};

// 加载所有账单数据（不分页，用于图表)
const fetchAllBills = async () => {
  try {
    const userId = getUserId();
    
    const url = `http://localhost:8080/api/bills/all?page=1&pageSize=1000&userId=${userId}`;
    
    const response = await fetch(url);
    
    if (response.ok) {
      const data = await response.json();
      return data.bills;
    } else {
      const errorData = await response.json();
      return [];
    }
  } catch (error) {
    return [];
  }
};

// 添加账单
const addBill = async () => {
  try {
    const userId = getUserId();
    
    const response = await fetch('http://localhost:8080/api/bills', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        type: billType.value,
        amount: billAmount.value,
        category_id: billCategory.value,
        account: billAccount.value,
        bill_date: billDate.value,
        bill_time: billTime.value,
        remark: billRemark.value,
        userId: userId
      })
    });
    
    if (response.ok) {
      // 重置表单
      billAmount.value = null;
      billCategory.value = '';
      billRemark.value = '';
      billType.value = 0;
      billAccount.value = '现金';
      billDate.value = getCurrentDate();
      billTime.value = '';
      // 关闭弹窗
      showAddBill.value = false;
      // 打开账单管理弹窗
      showBillManagement.value = true;
      // 重新加载账单
      await fetchBills();
      // 更新图表
      await updateChart();
      // 更新数据可视化图表
      await updateVisualizationChart();
    } else {
      const errorData = await response.json();
      alert(errorData.message || '添加账单失败');
    }
  } catch (error) {
    console.error('添加账单出错:', error);
    alert('添加账单失败，请重试');
  }
};

// 编辑账单
const editBill = (bill) => {
  editBillData.value = {
    id: bill.id,
    type: parseInt(bill.type) || 0,
    amount: bill.amount,
    categoryId: bill.categoryId,
    account: bill.account,
    billDate: bill.billDate,
    billTime: bill.billTime,
    remark: bill.remark
  };
  showEditBill.value = true;
};

// 更新账单
const updateBill = async () => {
  try {
    const response = await fetch(`http://localhost:8080/api/bills/${editBillData.value.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        type: editBillData.value.type,
        category_id: editBillData.value.categoryId,
        account: editBillData.value.account,
        amount: editBillData.value.amount,
        bill_date: editBillData.value.billDate,
        bill_time: editBillData.value.billTime,
        remark: editBillData.value.remark
      })
    });
    
    if (response.ok) {
      showEditBill.value = false;
      showBillManagement.value = true;
      // 重新加载账单
      await fetchBills();
      // 更新图表
      await updateChart();
      // 更新数据可视化图表
      await updateVisualizationChart();
    } else {
      const errorData = await response.json();
      alert(errorData.message || '更新账单失败');
    }
  } catch (error) {
    console.error('更新账单出错:', error);
    alert('更新账单失败，请重试');
  }
};

// 删除账单
const deleteBill = async (billId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条账单吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await fetch(`http://localhost:8080/api/bills/${billId}`, {
      method: 'DELETE'
    });
    
    if (response.ok) {
      await fetchBills();
      await updateChart();
      await updateVisualizationChart();
    } else {
      const errorData = await response.json();
      alert(errorData.message || '删除账单失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除账单出错:', error);
      alert('删除账单失败，请重试');
    }
  }
};

// 编辑分类
const editCategory = (category) => {
  categoryName.value = category.name;
  categoryType.value = parseInt(category.type) || 0;
  categorySort.value = category.sort || 0;
  categoryIsDefault.value = category.isDefault === '1';
  editingCategoryId.value = category.id;
  showEditCategory.value = true;
};

// 保存分类
const saveCategory = async () => {
  try {
    const userId = getUserId();
    const userRole = currentUserRole.value;
    
    const url = editingCategoryId.value 
      ? `http://localhost:8080/api/categories/${editingCategoryId.value}` 
      : 'http://localhost:8080/api/categories';
    
    const method = editingCategoryId.value ? 'PUT' : 'POST';
    
    const requestBody = {
      name: categoryName.value,
      type: categoryType.value,
      userId: userId,
      sort: categorySort.value || 0,
      isDefault: false,
      userRole: userRole
    };
    
    if (userRole === '2' && categoryIsDefault.value) {
      requestBody.userId = 0;
      requestBody.isDefault = true;
    }
    
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestBody)
    });
    
    if (response.ok) {
      categoryName.value = '';
      categoryType.value = 0;
      categorySort.value = 0;
      categoryIsDefault.value = false;
      editingCategoryId.value = null;
      showAddCategory.value = showEditCategory.value = false;
      showCategoryManagement.value = true;
      // 重新加载分类
      await fetchCategories();
    } else {
      console.error('保存分类失败');
    }
  } catch (error) {
    console.error('保存分类出错:', error);
  }
};

// 删除分类
const deleteCategory = async (categoryId) => {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const userRole = currentUserRole.value;
    
    const response = await fetch(`http://localhost:8080/api/categories/${categoryId}?userRole=${userRole}`, {
      method: 'DELETE'
    });
    
    if (response.ok) {
      await fetchCategories();
    } else {
      const errorData = await response.json();
      alert(errorData.message || '删除分类失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除分类出错:', error);
    }
  }
};

// 初始化图表
const initChart = async () => {
  if (chartRef.value) {
    // 如果已存在图表实例，先销毁
    if (chart.value) {
      chart.value.dispose();
      chart.value = null;
    }
    
    // 使用 markRaw 防止 Vue 代理 echarts 实例
    chart.value = markRaw(echarts.init(chartRef.value));
    
    // 等待数据加载
    await updateChart();
  }
};

// 加载图表数据
const loadChartData = async () => {
  try {
    // 使用本地时间
    const today = new Date();
    let startDate;
    let endDate;
    
    // 直接根据timeRange的值计算时间范围，不考虑其他变量
    switch (timeRange.value) {
      case 'week':
        const dayOfWeek = today.getDay();
        const daysToMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
        startDate = new Date(today);
        startDate.setDate(today.getDate() - daysToMonday);
        endDate = new Date(startDate);
        endDate.setDate(startDate.getDate() + 6);
        break;
      case 'month':
        // 计算本月：从1号到月末
        startDate = new Date(today.getFullYear(), today.getMonth(), 1);
        endDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
        break;
      case 'custom':
        if (selectedMonth.value) {
          // 选择月份：从该月1号到月末
          const [year, month] = selectedMonth.value.split('-');
          startDate = new Date(parseInt(year), parseInt(month) - 1, 1);
          endDate = new Date(parseInt(year), parseInt(month), 0);
        } else if (customStartDate.value && customEndDate.value) {
          startDate = new Date(customStartDate.value);
          endDate = new Date(customEndDate.value);
        } else {
          // 默认显示本周
          const dayOfWeek = today.getDay();
          const daysToMonday = dayOfWeek === 0 ? 6 : dayOfWeek - 1;
          startDate = new Date(today);
          startDate.setDate(today.getDate() - daysToMonday);
          endDate = new Date(startDate);
          endDate.setDate(startDate.getDate() + 6);
        }
        break;
      default:
        // 默认显示本月
        startDate = new Date(today.getFullYear(), today.getMonth(), 1);
        endDate = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    }
    
    // 验证日期范围
    const formatDate = (date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    };
    
    // 生成日期数组
    const dates = [];
    const current = new Date(startDate);
    while (current <= endDate) {
      dates.push(formatDate(current));
      current.setDate(current.getDate() + 1);
    }
    
    // 获取所有账单数据（不分页）
    const allBills = await fetchAllBills();
    
    // 从bills数组中提取真实数据
    const billMap = {};
    
    // 初始化每天的数据
    dates.forEach(date => {
      billMap[date] = { income: 0, expense: 0 };
    });
    
    // 遍历bills数组，按日期和类型统计数据
    allBills.forEach(bill => {
      const billDate = bill.billDate;
      if (dates.includes(billDate)) {
        if (bill.type === '1') {
          billMap[billDate].income += bill.amount;
        } else if (bill.type === '0') {
          billMap[billDate].expense += bill.amount;
        }
      }
    });
    
    // 生成收入和支出数据数组
    const incomeData = dates.map(date => billMap[date].income);
    const expenseData = dates.map(date => billMap[date].expense);
    
    // 计算总收入、总支出和结余
    const totalIncomeValue = incomeData.reduce((sum, value) => sum + value, 0);
    const totalExpenseValue = expenseData.reduce((sum, value) => sum + value, 0);
    const balanceValue = totalIncomeValue - totalExpenseValue;
    
    totalIncome.value = totalIncomeValue;
    totalExpense.value = totalExpenseValue;
    balance.value = balanceValue;
    
    chartData.value = {
      dates,
      income: incomeData,
      expense: expenseData
    };
  } catch (error) {
  }
};

// 更新图表（防抖）
const debouncedUpdateChart = debounce(async () => {
  await loadChartData();
  
  if (chart.value && chartData.value.dates && chartData.value.dates.length > 0) {
    const option = {
      color: ['#38a169', '#e53e3e'],
      
      tooltip: {
        show: true,
        trigger: 'axis',
        confine: true,
        enterable: false,
        transitionDuration: 0,
        throttle: 50,
        axisPointer: {
          axis: 'y'
        },
        backgroundColor: 'rgba(255, 255, 255, 0.98)',
        borderColor: 'rgba(37, 99, 235, 0.15)',
        borderWidth: 1,
        padding: [14, 18],
        borderRadius: 12,
        textStyle: {
          color: '#2d3748',
          fontSize: 13
        },
        extraCssText: 'box-shadow: 0 4px 20px rgba(37, 99, 235, 0.12), 0 1px 3px rgba(0, 0, 0, 0.08); backdrop-filter: blur(8px);',
        axisPointer: {
          type: 'line',
          lineStyle: {
            color: 'rgba(37, 99, 235, 0.3)',
            width: 1,
            type: 'dashed'
          }
        },
        formatter: function(params) {
          if (!params || params.length === 0) return '';
          const date = params[0].axisValue;
          let income = 0;
          let expense = 0;
          
          params.forEach(item => {
            if (item.seriesName === '收入' && item.value != null) {
              income = item.value;
            } else if (item.seriesName === '支出' && item.value != null) {
              expense = item.value;
            }
          });
          
          const balance = income - expense;
          const balanceColor = balance >= 0 ? '#059669' : '#dc2626';
          const balanceText = balance >= 0 ? '+' + balance.toFixed(2) : balance.toFixed(2);
          
          return `<div style="font-size:14px;font-weight:600;color:#1e40af;margin-bottom:12px;padding-bottom:10px;border-bottom:1px solid rgba(37,99,235,0.15);">${date}</div><div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;"><span style="color:#64748b;">收入</span><span style="color:#059669;font-weight:600;font-size:14px;">¥${income.toFixed(2)}</span></div><div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:8px;"><span style="color:#64748b;">支出</span><span style="color:#dc2626;font-weight:600;font-size:14px;">¥${expense.toFixed(2)}</span></div><div style="display:flex;justify-content:space-between;align-items:center;padding-top:10px;border-top:1px solid rgba(37,99,235,0.15);"><span style="color:#64748b;">结余</span><span style="color:${balanceColor};font-weight:700;font-size:15px;">¥${balanceText}</span></div>`;
        }
      },
      
      legend: {
        data: ['收入', '支出'],
        textStyle: {
          color: '#4a5568'
        },
        top: 0,
        right: 10
      },
      
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        top: '15%',
        containLabel: true
      },
      
      xAxis: [{
        type: 'category',
        boundaryGap: false,
        data: chartData.value.dates,
        axisLine: {
          show: true,
          lineStyle: {
            color: '#cbd5e0',
            width: 1.5
          }
        },
        axisLabel: {
          color: '#718096',
          fontSize: 12,
          rotate: 45
        },
        axisTick: {
          show: true,
          lineStyle: {
            color: '#cbd5e0'
          }
        }
      }],
      
      yAxis: [{
        type: 'value',
        axisLine: {
          show: true,
          lineStyle: {
            color: '#cbd5e0',
            width: 1.5
          }
        },
        axisLabel: {
          color: '#718096',
          fontSize: 12,
          formatter: '¥{value}'
        },
        axisTick: {
          show: true,
          lineStyle: {
            color: '#cbd5e0'
          }
        },
        splitLine: {
          lineStyle: {
            color: '#e2e8f0',
            type: 'dashed',
            width: 1
          }
        }
      }],
      
      series: [
        {
          name: '收入',
          type: 'line',
          xAxisIndex: 0,
          yAxisIndex: 0,
          silent: false,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 3,
            shadowColor: 'rgba(56, 161, 105, 0.3)',
            shadowBlur: 10,
            shadowOffsetY: 5
          },
          itemStyle: {
            color: '#38a169',
            borderColor: '#ffffff',
            borderWidth: 2
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(56, 161, 105, 0.5)' },
              { offset: 1, color: 'rgba(56, 161, 105, 0.05)' }
            ])
          },
          data: chartData.value.income
        },
        {
          name: '支出',
          type: 'line',
          xAxisIndex: 0,
          yAxisIndex: 0,
          silent: false,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: {
            width: 3,
            shadowColor: 'rgba(229, 62, 62, 0.3)',
            shadowBlur: 10,
            shadowOffsetY: 5
          },
          itemStyle: {
            color: '#e53e3e',
            borderColor: '#ffffff',
            borderWidth: 2
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(229, 62, 62, 0.5)' },
              { offset: 1, color: 'rgba(229, 62, 62, 0.05)' }
            ])
          },
          data: chartData.value.expense
        }
      ]
    };
    
    chart.value.setOption(option, { notMerge: true });
  }
}, 200);

// 更新图表（包装函数）
const updateChart = () => {
  debouncedUpdateChart();
};

// 处理日期范围变化
const handleDateRangeChange = (val) => {
  if (val && val.length === 2) {
    customStartDate.value = val[0];
    customEndDate.value = val[1];
    // 清空选择的月份，确保日期范围优先级更高
    selectedMonth.value = '';
    setTimeRange('custom');
  } else {
    customStartDate.value = '';
    customEndDate.value = '';
  }
};

// 设置时间范围
const setTimeRange = async (range) => {
  timeRange.value = range;
  // 当选择本周或本月时，清空选择的月份和自定义日期范围
  if (range === 'week' || range === 'month') {
    selectedMonth.value = '';
    selectedMonthDate.value = '';
    customDateRange.value = [];
    customStartDate.value = '';
    customEndDate.value = '';
  } else if (range === 'custom' && selectedMonth.value) {
    // 当选择月份时，清空日期范围
    customDateRange.value = [];
    customStartDate.value = '';
    customEndDate.value = '';
  }
  await updateChart();
};

// 月份选择器变化处理
const handleMonthChange = async (val) => {
  if (val) {
    selectedMonth.value = val;
    await setTimeRange('custom');
  }
};

// 监听窗口大小变化
const handleResize = () => {
  try {
    if (chart.value && chartData.value.dates && chartData.value.dates.length > 0) {
      chart.value.resize();
    }
    if (visualizationChart.value) {
      visualizationChart.value.resize();
    }
  } catch (e) {
    // 忽略 resize 错误
  }
};

// ResizeObserver 用于监听容器大小变化（如侧边栏切换）
let resizeObserver = null;

// 导入导出相关数据
const selectedFile = ref(null);

// 导入导出相关方法
const exportData = async () => {
  try {
    const allBills = await fetchAllBills();
    
    let filteredBills = allBills;
    if (exportDateRange.value && exportDateRange.value.length === 2) {
      const startDate = new Date(exportDateRange.value[0]);
      const endDate = new Date(exportDateRange.value[1]);
      filteredBills = allBills.filter(bill => {
        const billDate = new Date(bill.billDate);
        return billDate >= startDate && billDate <= endDate;
      });
    }

    if (filteredBills.length === 0) {
      alert('没有数据可导出');
      return;
    }

    if (exportFormat.value === 'csv') {
      exportToCSV(filteredBills);
    } else if (exportFormat.value === 'excel') {
      exportToExcel(filteredBills);
    }
  } catch (error) {
    console.error('导出数据出错:', error);
    alert('导出失败，请重试');
  }
};

const exportToCSV = (data) => {
  // 生成CSV内容
  const headers = ['日期', '时间', '类型', '分类', '账户', '金额', '备注'];
  const rows = data.map(bill => {
    return [
      bill.billDate,
      bill.billTime || '',
      bill.type === '1' ? '收入' : '支出',
      getCategoryName(bill.categoryId),
      bill.account,
      bill.amount,
      bill.remark || ''
    ];
  });

  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.join(','))
  ].join('\n');

  // 添加BOM (Byte Order Mark) 确保Excel能正确识别UTF-8编码
  const bom = new Uint8Array([0xEF, 0xBB, 0xBF]);
  const blob = new Blob([bom, csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', `账单数据_${new Date().toISOString().split('T')[0]}.csv`);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

const exportToExcel = (data) => {
  // 生成Excel内容（简化版，实际项目中可使用xlsx库）
  const headers = ['日期', '时间', '类型', '分类', '账户', '金额', '备注'];
  const rows = data.map(bill => {
    return [
      bill.billDate,
      bill.billTime || '',
      bill.type === '1' ? '收入' : '支出',
      getCategoryName(bill.categoryId),
      bill.account,
      bill.amount,
      bill.remark || ''
    ];
  });

  const csvContent = [
    headers.join('\t'),
    ...rows.map(row => row.join('\t'))
  ].join('\n');

  // 添加BOM (Byte Order Mark) 确保Excel能正确识别UTF-8编码
  const bom = new Uint8Array([0xEF, 0xBB, 0xBF]);
  const blob = new Blob([bom, csvContent], { type: 'application/vnd.ms-excel' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', `账单数据_${new Date().toISOString().split('T')[0]}.xls`);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
  }
};

const importData = async () => {
  try {
    if (!selectedFile.value) {
      alert('请先选择文件');
      return;
    }

    const file = selectedFile.value;
    const fileExtension = file.name.split('.').pop().toLowerCase();

    if (fileExtension === 'csv') {
      await importFromCSV(file);
    } else if (fileExtension === 'xlsx' || fileExtension === 'xls') {
      await importFromExcel(file);
    } else {
      alert('不支持的文件格式，请选择CSV或Excel文件');
      return;
    }
  } catch (error) {
    console.error('导入数据出错:', error);
    alert('导入失败，请重试');
  }
};

const importFromCSV = async (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = async (e) => {
      try {
        const content = e.target.result;
        const lines = content.split('\n').filter(line => line.trim() !== '');
        if (lines.length < 2) {
          alert('文件内容为空或格式不正确');
          resolve();
          return;
        }

        // 跳过表头
        const dataLines = lines.slice(1);
        const importedBills = [];

        for (const line of dataLines) {
          const values = line.split(',');
          if (values.length >= 6) {
            const [billDate, billTime, typeStr, categoryName, account, amountStr, remark] = values;
            const type = typeStr === '收入' ? 1 : 0;
            const amount = parseFloat(amountStr);

            // 查找分类ID
            let categoryId = '';
            const category = categories.value.find(cat => cat.name === categoryName && String(cat.type) === String(type));
            if (category) {
              categoryId = category.id;
            } else {
              // 如果分类不存在，创建新分类
              const newCategory = await createCategory(categoryName, type);
              if (newCategory) {
                categoryId = newCategory.id;
              }
            }

            if (categoryId && !isNaN(amount)) {
              importedBills.push({
                type,
                amount,
                categoryId,
                account,
                billDate,
                billTime: billTime || '',
                remark: remark || ''
              });
            }
          }
        }

        if (importedBills.length > 0) {
          await saveImportedBills(importedBills);
          alert(`成功导入 ${importedBills.length} 条数据`);
          // 重新加载数据
          await fetchBills();
          await updateChart();
          await updateVisualizationChart();
        } else {
          alert('没有可导入的数据');
        }

        resolve();
      } catch (error) {
        reject(error);
      }
    };
    reader.onerror = reject;
    reader.readAsText(file);
  });
};

const importFromExcel = async (file) => {
  // 简化版Excel导入，实际项目中可使用xlsx库
  alert('Excel导入功能开发中，请使用CSV格式导入');
};

const createCategory = async (name, type) => {
  try {
    const userId = getUserId();

    const response = await fetch('http://localhost:8080/api/categories', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name,
        type,
        userId
      })
    });

    if (response.ok) {
      const data = await response.json();
      await fetchCategories();
      return data.category;
    }
    return null;
  } catch (error) {
    return null;
  }
};

const saveImportedBills = async (bills) => {
  const userId = getUserId();
  
  for (const bill of bills) {
    try {
      await fetch('http://localhost:8080/api/bills', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          type: bill.type,
          amount: bill.amount,
          category_id: bill.categoryId,
          account: bill.account,
          bill_date: bill.billDate,
          bill_time: bill.billTime,
          remark: bill.remark,
          userId: userId
        })
      });
    } catch (error) {
    }
  }
};

// 数据可视化相关方法
const switchChartType = (type) => {
  currentChartType.value = type;
  // 确保图表实例存在且已初始化
  if (visualizationChart.value) {
    updateVisualizationChart();
  } else {
    initVisualizationChart();
  }
};

const initVisualizationChart = () => {
  if (visualizationChartRef.value) {
    // 销毁已存在的图表实例，避免多个图表叠加
    if (visualizationChart.value) {
      visualizationChart.value.dispose();
    }
    // 重新初始化图表，使用 markRaw 防止 Vue 代理
    visualizationChart.value = markRaw(echarts.init(visualizationChartRef.value));
    
    // 先设置一个完整的初始配置
    visualizationChart.value.setOption({
      color: ['#38a169', '#e53e3e'],
      tooltip: {
        trigger: 'item'
      },
      legend: {
        data: []
      },
      xAxis: [{
        type: 'category',
        data: []
      }],
      yAxis: [{
        type: 'value'
      }],
      series: [
        {
          name: '数据',
          type: 'pie',
          data: []
        }
      ]
    });
    
    updateVisualizationChart();
  }
};

// 更新数据可视化图表（防抖）
const debouncedUpdateVisualizationChart = debounce(async () => {
  if (visualizationChart.value) {
    // 获取所有账单数据（不分页）
    const allBills = await fetchAllBills();
    
    let option;
    
    switch (currentChartType.value) {
      case 'pie':
        // 饼图 - 分类占比
        // 根据选择的月份过滤数据
        const filteredBillsForPie = allBills.filter(bill => bill.billDate.startsWith(pieMonth.value));
        
        // 从过滤后的账单中提取分类数据
        const categoryMap = {};
        filteredBillsForPie.forEach(bill => {
          const categoryName = getCategoryName(bill.categoryId);
          if (!categoryMap[categoryName]) {
            categoryMap[categoryName] = 0;
          }
          categoryMap[categoryName] += bill.amount;
        });
        
        const pieData = Object.entries(categoryMap).map(([name, value]) => ({
          name,
          value
        }));
        
        // 解析月份显示
        const [year, month] = pieMonth.value.split('-');
        
        option = {
          title: {
            text: `分类占比 - ${year}年${parseInt(month)}月`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            textStyle: {
              color: '#4a5568'
            }
          },
          series: [
            {
              name: '分类占比',
              type: 'pie',
              radius: '50%',
              data: pieData.length > 0 ? pieData : [{ value: 1, name: '暂无数据' }],
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              itemStyle: {
                borderRadius: 4,
                borderColor: '#fff',
                borderWidth: 2
              }
            }
          ]
        };
        break;
      case 'bar':
        // 柱状图 - 月度对比
        // 根据选择的年份过滤数据
        let filteredBillsForBar = allBills;
        
        if (selectedYear.value === 'recent12') {
          // 最近12个月
          const now = new Date();
          const twelveMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 11, 1);
          const startDateStr = twelveMonthsAgo.toISOString().substring(0, 10);
          filteredBillsForBar = allBills.filter(bill => bill.billDate >= startDateStr);
        } else if (selectedYear.value !== 'all') {
          // 特定年份
          filteredBillsForBar = allBills.filter(bill => bill.billDate.startsWith(selectedYear.value));
        }
        
        // 从过滤后的账单中提取月度数据
        const monthlyData = {};
        filteredBillsForBar.forEach(bill => {
          const month = bill.billDate.substring(0, 7);
          if (!monthlyData[month]) {
            monthlyData[month] = { income: 0, expense: 0 };
          }
          if (bill.type === '1') {
            monthlyData[month].income += bill.amount;
          } else {
            monthlyData[month].expense += bill.amount;
          }
        });
        
        // 按月份排序
        const sortedMonths = Object.keys(monthlyData).sort();
        const monthsLabels = sortedMonths.map(month => {
          const [year, monthNum] = month.split('-');
          return `${year}年${monthNum}月`;
        });
        const incomeData = sortedMonths.map(month => monthlyData[month].income);
        const expenseData = sortedMonths.map(month => monthlyData[month].expense);
        
        option = {
          title: {
            text: '月度收支对比',
            left: 'center',
            top: 10,
            textStyle: {
              color: '#2d3748',
              fontSize: 16
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            formatter: function(params) {
              if (!params || params.length === 0) return '';
              let result = params[0].axisValue + '<br/>';
              params.forEach(item => {
                if (item.value !== null && item.value !== undefined) {
                  const color = item.color;
                  const name = item.seriesName;
                  const value = typeof item.value === 'number' ? item.value.toFixed(2) : item.value;
                  result += `<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${color};"></span>${name}: ¥${value}<br/>`;
                }
              });
              return result;
            }
          },
          legend: {
            data: ['收入', '支出'],
            top: 50,
            left: 'center',
            textStyle: {
              color: '#4a5568',
              fontSize: 12
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '15%',
            top: '25%',
            containLabel: true
          },
          dataZoom: monthsLabels.length > 12 ? [
            {
              type: 'slider',
              show: true,
              xAxisIndex: [0],
              start: Math.max(0, (monthsLabels.length - 12) / monthsLabels.length * 100),
              end: 100,
              height: 20,
              bottom: 5,
              borderColor: 'transparent',
              backgroundColor: '#f7fafc',
              fillerColor: 'rgba(37, 99, 235, 0.15)',
              handleStyle: {
                color: '#2563eb',
                borderColor: '#2563eb'
              },
              textStyle: {
                color: '#64748b'
              }
            }
          ] : [],
          xAxis: [{
            type: 'category',
            data: monthsLabels.length > 0 ? monthsLabels : ['暂无数据'],
            axisLine: {
              lineStyle: {
                color: '#cbd5e0'
              }
            },
            axisLabel: {
              color: '#718096',
              rotate: 45
            }
          }],
          yAxis: [{
            type: 'value',
            axisLabel: {
              formatter: '¥{value}',
              color: '#718096'
            },
            axisLine: {
              lineStyle: {
                color: '#cbd5e0'
              }
            },
            splitLine: {
              lineStyle: {
                color: '#e2e8f0',
                type: 'dashed'
              }
            }
          }],
          series: [
            {
              name: '收入',
              type: 'bar',
              xAxisIndex: 0,
              yAxisIndex: 0,
              data: incomeData.length > 0 ? incomeData : [0],
              itemStyle: {
                color: '#38a169'
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(56, 161, 105, 0.3)'
                }
              }
            },
            {
              name: '支出',
              type: 'bar',
              xAxisIndex: 0,
              yAxisIndex: 0,
              data: expenseData.length > 0 ? expenseData : [0],
              itemStyle: {
                color: '#e53e3e'
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowColor: 'rgba(229, 62, 62, 0.3)'
                }
              }
            }
          ]
        };
        break;
      case 'heatmap':
        // 热力图 - 年度分布
        // 根据选择的年份过滤数据
        const filteredBillsByYear = allBills.filter(bill => bill.billDate.startsWith(heatmapYear.value));
        
        // 从bills数组中提取热力图数据
        const heatmapData = [];
        const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
        const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
        
        // 根据heatmapType筛选数据
        const filteredBills = heatmapType.value === 'all' 
          ? filteredBillsByYear 
          : filteredBillsByYear.filter(bill => 
              heatmapType.value === 'income' ? bill.type === '1' : bill.type === '0'
            );
        
        filteredBills.forEach(bill => {
          const date = new Date(bill.billDate);
          const monthIndex = date.getMonth(); // 0-11
          const dayOfWeek = date.getDay(); // 0-6 (0=周日)
          
          // 检查是否已存在该位置的数据
          const existingIndex = heatmapData.findIndex(item => item[0] === monthIndex && item[1] === dayOfWeek);
          if (existingIndex >= 0) {
            heatmapData[existingIndex][2] += bill.amount;
          } else {
            heatmapData.push([monthIndex, dayOfWeek, bill.amount]);
          }
        });
        
        // 计算最大值用于视觉映射
        const maxAmount = Math.max(...heatmapData.map(item => item[2]), 1000);
        
        option = {
          title: {
            text: (heatmapType.value === 'all' ? '年度收支分布' : 
                  heatmapType.value === 'income' ? '年度收入分布' : '年度支出分布') + ` - ${heatmapYear.value}年`,
            left: 'center',
            top: 5,
            textStyle: {
              color: '#2d3748',
              fontSize: 14
            }
          },
          tooltip: {
            position: 'top',
            formatter: function(params) {
              if (!params || !params.value) return '';
              return `${months[params.value[0]]} ${weekDays[params.value[1]]}: ¥${params.value[2].toFixed(2)}`;
            }
          },
          grid: {
            height: '55%',
            top: '15%',
            left: '8%',
            right: '8%',
            bottom: '22%'
          },
          xAxis: [{
            type: 'category',
            data: months,
            splitArea: {
              show: true
            }
          }],
          yAxis: [{
            type: 'category',
            data: weekDays,
            splitArea: {
              show: true
            }
          }],
          visualMap: {
            type: 'continuous',
            min: 0,
            max: maxAmount,
            calculable: true,
            orient: 'horizontal',
            left: 'center',
            bottom: '5%',
            width: '60%',
            height: 20,
            inRange: {
              color: ['#e0f2fe', '#bae6fd', '#7dd3fc', '#38bdf8', '#0ea5e9']
            }
          },
          series: [
            {
              name: '收支金额',
              type: 'heatmap',
              xAxisIndex: 0,
              yAxisIndex: 0,
              data: heatmapData.length > 0 ? heatmapData : [[0, 0, 0]],
              label: {
                show: true
              }
            }
          ]
        };
        break;
    }
    
    if (option) {
      visualizationChart.value.setOption(option, { notMerge: true });
    }
  }
}, 200);

// 更新数据可视化图表（包装函数）
const updateVisualizationChart = () => {
  debouncedUpdateVisualizationChart();
};

// 初始化数据
onMounted(async () => {
  await fetchCategories();
  await fetchBills();
  await initChart();
  window.addEventListener('resize', handleResize);
  
  // 使用 ResizeObserver 监听图表容器大小变化
  if (chartRef.value && typeof ResizeObserver !== 'undefined') {
    resizeObserver = new ResizeObserver(() => {
      handleResize();
    });
    resizeObserver.observe(chartRef.value);
  }
});

// 组件卸载时清理资源
onUnmounted(() => {
  // 移除 resize 事件监听器
  window.removeEventListener('resize', handleResize);
  
  // 断开 ResizeObserver
  if (resizeObserver) {
    resizeObserver.disconnect();
    resizeObserver = null;
  }
  
  // 销毁图表实例
  if (chart.value) {
    chart.value.dispose();
    chart.value = null;
  }
  
  if (visualizationChart.value) {
    visualizationChart.value.dispose();
    visualizationChart.value = null;
  }
});

// 监听数据可视化弹窗显示状态
watch(showDataVisualization, (newValue) => {
  if (newValue) {
    // 弹窗打开后初始化图表
    setTimeout(() => {
      initVisualizationChart();
      // 确保图表正确渲染
      setTimeout(() => {
        if (visualizationChart.value) {
          visualizationChart.value.resize();
        }
      }, 100);
    }, 100); // 等待DOM更新
  } else {
    // 弹窗关闭时销毁图表实例，避免内存泄漏和叠加层问题
    if (visualizationChart.value) {
      visualizationChart.value.dispose();
      visualizationChart.value = null;
    }
  }
});
</script>

<style scoped>
.financial-ledger {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 
    0 4px 16px rgba(37, 99, 235, 0.06),
    0 1px 2px rgba(0, 0, 0, 0.03),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  padding: 24px;
}

.financial-ledger:hover {
  box-shadow: 
    0 8px 24px rgba(37, 99, 235, 0.1),
    0 2px 4px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 1);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.financial-ledger h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.financial-ledger h3 {
  font-size: 16px;
  font-weight: 600;
  color: #4a5568;
  margin-bottom: 16px;
  margin-top: 32px;
}

/* 统计卡片区域 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 24px;
}

.stat-card {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: box-shadow 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-title {
  font-size: 14px;
  font-weight: 500;
  color: #718096;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.income-value {
  color: #38a169;
}

.expense-value {
  color: #e53e3e;
}

.balance-value {
  color: #4299e1;
}

/* 图表区域 */
.chart-section {
  margin-top: 32px;
  padding: 20px;
  background-color: #f7fafc;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #4a5568;
}

.time-range-selector {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.time-buttons {
  display: flex;
  gap: 8px;
  background-color: #f7fafc;
  padding: 4px;
  border-radius: 8px;
}

.time-btn {
  padding: 6px 16px;
  border: none;
  background: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #718096;
  cursor: pointer;
}

.time-btn:hover {
  color: #4299e1;
}

.time-btn.active {
  background-color: #4299e1;
  color: white;
  box-shadow: 0 2px 4px rgba(66, 153, 225, 0.3);
}

.time-selectors {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.time-selectors > * {
  flex-shrink: 0;
}

.month-picker {
  width: 180px;
  max-width: 180px;
}

.month-picker :deep(.el-date-editor) {
  width: 180px;
  max-width: 180px;
}

.month-picker :deep(.el-input__wrapper) {
  width: 180px;
  max-width: 180px;
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: none;
}

.month-picker :deep(.el-input__wrapper:hover) {
  border-color: #4299e1;
}

.month-picker :deep(.el-input__wrapper.is-focus) {
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
}

.month-picker :deep(.el-input__inner) {
  color: #2d3748;
  font-size: 14px;
}

.date-range-container {
  min-width: 240px;
  max-width: 320px;
  flex: 1;
}

.date-picker {
  width: 100%;
  max-width: 320px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .time-range-selector {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .time-buttons {
    justify-content: center;
  }
  
  .time-selectors {
    flex-direction: column;
    align-items: stretch;
  }
  
  .month-picker {
    width: 100%;
  }
  
  .date-range-container {
    width: 100%;
  }
}

.form-select {
  padding: 6px 12px;
  border: 1px solid #e0e6ed;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.form-select:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.custom-date-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-picker {
  width: 100%;
  min-width: 280px;
}

/* 自定义Element Plus日期选择器样式 */
:deep(.el-date-picker) {
  border-radius: 8px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15) !important;
}

:deep(.el-date-picker__header) {
  background-color: #ffffff !important;
  border-bottom: 1px solid #f0f0f0 !important;
}

:deep(.el-date-picker__header-label) {
  font-size: 14px !important;
  font-weight: 600 !important;
  color: #2d3748 !important;
}

:deep(.el-date-picker__nav-btn) {
  color: #4a5568 !important;
}

:deep(.el-date-picker__nav-btn:hover) {
  color: #409EFF !important;
}

:deep(.el-date-table th) {
  font-size: 12px !important;
  font-weight: 500 !important;
  color: #718096 !important;
}

:deep(.el-date-table td) {
  padding: 6px !important;
}

:deep(.el-date-table__cell) {
  border-radius: 6px !important;
}

:deep(.el-date-table__cell:hover) {
  background-color: #f7fafc !important;
}

:deep(.el-date-table__cell.is-selected) {
  background-color: #409EFF !important;
  color: #ffffff !important;
}

:deep(.el-date-table__cell.is-today) {
  color: #409EFF !important;
}

:deep(.el-date-range-picker__time-header) {
  background-color: #ffffff !important;
  border-bottom: 1px solid #f0f0f0 !important;
}

:deep(.el-date-range-picker__range-separator) {
  color: #94a3b8 !important;
  font-weight: 500 !important;
}

.chart-container {
  width: 100%;
  height: 400px;
  min-height: 400px;
  position: relative;
}

.chart-container > div {
  width: 100% !important;
}

.chart-container canvas {
  width: 100% !important;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 12px;
}

/* 表单样式 */
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.form-group {
  flex: 1;
  min-width: 0;
}

.form-group:has(.el-date-editor) {
  min-width: 0;
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e0e6ed;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.form-hint {
  font-size: 12px;
  color: #718096;
  margin-top: 4px;
  line-height: 1.5;
}

.form-group label.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  color: #4a5568;
  font-size: 14px;
  line-height: 1.5;
  user-select: none;
  margin-bottom: 0;
}

.form-group label.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #409EFF;
  flex-shrink: 0;
  margin: 0;
  vertical-align: middle;
}

.type-selector {
  display: flex;
  gap: 10px;
}

.type-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid #e0e6ed;
  border-radius: 6px;
  background-color: #f7fafc;
  color: #4a5568;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  outline: none;
}

.type-btn:hover {
  background-color: #edf2f7;
  border-color: #cbd5e0;
}

.type-btn.active {
  background-color: #409EFF;
  color: #ffffff;
  border-color: #409EFF;
}

/* 表单操作按钮 */
.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

/* 分类管理 */
.category-management {
  margin-top: 32px;
}

.category-list {
  display: flex;
  gap: 24px;
  margin-top: 24px;
  padding: 0 24px;
  flex: 1;
  overflow: hidden;
  min-height: 0;
}

.category-section {
  flex: 1;
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f4f8;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f4f8;
  flex-shrink: 0;
}

.section-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.expense-icon {
  background: #fff5f5;
  color: #e53e3e;
}

.income-icon {
  background: #f0fff4;
  color: #38a169;
}

.section-header h4 {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.category-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  padding-right: 8px;
}

.category-items::-webkit-scrollbar {
  width: 6px;
}

.category-items::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.category-items::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.category-items::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  background-color: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  position: relative;
  overflow: hidden;
}

.category-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  border-radius: 8px 0 0 8px;
  background: #e2e8f0;
}

.category-item:hover {
  background-color: #edf2f7;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.category-item:hover::before {
  background: #cbd5e0;
}

.category-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 12px;
}

.category-name {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-meta {
  font-size: 11px;
  color: #718096;
  background: #edf2f7;
  padding: 2px 6px;
  border-radius: 8px;
  white-space: nowrap;
}

.category-actions {
  display: flex;
  gap: 6px;
  flex-shrink: 0;
}

.category-actions .btn {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 11px;
  padding: 5px 8px;
  border-radius: 6px;
  white-space: nowrap;
}

.category-actions .btn:hover {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 12px;
}

.category-divider {
  height: 1px;
  background: linear-gradient(to right, transparent, #e2e8f0, transparent);
  margin: 0 24px;
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  background: #f8fafc;
  border-radius: 8px;
  border: 2px dashed #e2e8f0;
}

.no-data-icon {
  font-size: 32px;
  margin-bottom: 12px;
  opacity: 0.6;
}

.no-data-text {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  margin-bottom: 8px;
}

.no-data-hint {
  font-size: 12px;
  color: #718096;
}

/* 账单列表 */
.bill-list {
  margin-top: 32px;
}

.bill-filter {
  margin-bottom: 16px;
  padding: 16px;
  background-color: #f8fafc;
  border-radius: 8px;
  display: flex;
  gap: 12px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.filter-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 160px;
}

.filter-item label {
  font-size: 12px;
  font-weight: 500;
  color: #4a5568;
  line-height: 1;
}

.filter-select,
.filter-input {
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 13px;
  color: #2d3748;
  background-color: white;
  transition: border-color 0.3s ease;
  height: 32px;
  width: 100%;
}

/* 统一日期选择器宽度 */
/* 筛选器输入框宽度 */
.filter-input :deep(.el-input__wrapper) {
  width: 100% !important;
}

.filter-input :deep(.el-input) {
  width: 100% !important;
}

.filter-select:focus,
.filter-input:focus,
.amount-input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.amount-range {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 200px;
}

.amount-inputs {
  display: flex;
  align-items: center;
  gap: 6px;
}

.amount-input {
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  font-size: 13px;
  color: #2d3748;
  background-color: white;
  transition: border-color 0.3s ease;
  height: 32px;
  width: 80px;
}

.amount-separator {
  color: #718096;
  font-weight: 500;
  font-size: 12px;
  line-height: 32px;
  width: 16px;
  text-align: center;
}

.filter-actions {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.filter-actions .btn {
  padding: 6px 12px;
  font-size: 13px;
  height: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-item,
  .amount-range {
    min-width: 100%;
  }
  
  .amount-inputs {
    flex-direction: row;
  }
  
  .filter-actions {
    flex-direction: column;
  }
  
  .filter-actions .btn {
    width: 100%;
  }
}

.bill-table {
  width: 100%;
  border-collapse: collapse;
  background-color: #ffffff;
}

.bill-table th, .bill-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #eef2f6;
}

.bill-table thead th {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: #f7f9fb;
  font-weight: 600;
  color: #4a5568;
  font-size: 13px;
  border-bottom: 2px solid #e2e8f0;
  white-space: nowrap;
}

.bill-table tbody tr:hover {
  background-color: #fafbfc;
}

.bill-table tr:last-child td {
  border-bottom: none;
}

.bill-table tr:last-child td:first-child {
  border-radius: 0 0 0 9px;
}

.bill-table tr:last-child td:last-child {
  border-radius: 0 0 9px 0;
}

.bill-table .expense {
  color: #c53030;
  font-weight: 500;
}

.bill-table .income {
  color: #2f855a;
  font-weight: 500;
}

.bill-table td .btn {
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 4px;
  margin-right: 8px;
  border: 1px solid transparent;
}

.bill-table td .btn:last-child {
  margin-right: 0;
}

.bill-table td .btn-secondary {
  background-color: #f7fafc;
  color: #4a5568;
  border-color: #e2e8f0;
}

.bill-table td .btn-secondary:hover {
  background-color: #edf2f7;
  border-color: #cbd5e0;
}

.bill-table td .btn-danger {
  background-color: #fff5f5;
  color: #c53030;
  border-color: #feb2b2;
}

.bill-table td .btn-danger:hover {
  background-color: #fed7d7;
  border-color: #fc8181;
}

.no-data {
  text-align: center;
  padding: 40px 20px;
  color: #718096;
  font-size: 14px;
  background-color: #f8fafc;
  border-radius: 10px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .bill-table th, .bill-table td {
    padding: 10px 12px;
    font-size: 13px;
  }
  
  .bill-table td .btn {
    font-size: 10px;
    padding: 3px 6px;
  }
}

/* 模态框 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.modal {
  background-color: #ffffff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* 添加账单弹窗样式 - 使用简单类名 */
.modal-form {
  width: 500px;
  max-width: 90vw;
}

.modal-form .form-row {
  gap: 16px;
}

.modal-form .form-group {
  margin-bottom: 0;
}

.modal-form .form-group label {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 6px;
}

.modal-form .form-group input,
.modal-form .form-group select {
  padding: 10px 14px;
  font-size: 14px;
  border-color: #e2e8f0;
  background-color: #f8fafc;
}

.modal-form .form-group input:focus,
.modal-form .form-group select:focus {
  background-color: #ffffff;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.modal-form .type-selector {
  background-color: #f8fafc;
  border-radius: 8px;
  padding: 4px;
}

.modal-form .type-btn {
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 6px;
  background-color: transparent;
  color: #64748b;
  border: 1px solid transparent;
}

.modal-form .type-btn:hover {
  background-color: #e2e8f0;
  color: #2d3748;
}

.modal-form .type-btn.active {
  background: #4299e1;
  color: #ffffff;
}

/* 日期时间选择器 - 仅在表单中使用 */
.modal-form .el-date-editor,
.modal-form .el-time-picker {
  width: 100%;
  min-width: 160px;
}

.modal-form .el-date-editor .el-input__wrapper,
.modal-form .el-time-picker .el-input__wrapper {
  width: 100%;
  min-width: 160px;
  background-color: #f8fafc;
  flex: none;
}

.modal-form .el-date-editor .el-input__inner,
.modal-form .el-time-picker .el-input__inner {
  min-width: 120px;
  background-color: #f8fafc;
}

/* 弹窗通用样式 */
.bill-management-modal,
.category-management-modal {
  width: 70vw;
  height: 70vh;
  max-width: 80vw;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.bill-management-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.bill-table-container {
  flex: 1;
  margin: 16px 0;
  overflow: auto;
  min-height: 0;
  border: 1px solid #e8ecf0;
  border-radius: 10px;
  background-color: #ffffff;
}

.bill-table-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.bill-table-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.bill-table-container::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.bill-table-container::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

.data-visualization-modal,
.import-export-modal {
  width: 70vw;
  height: 70vh;
  max-width: 80vw;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.data-visualization-modal .modal-header {
  flex-shrink: 0;
}

.data-visualization-modal .chart-types,
.data-visualization-modal .year-selector,
.data-visualization-modal .heatmap-controls {
  flex-shrink: 0;
}

.data-visualization-modal .chart-container {
  flex: 1;
  min-height: 250px;
  height: auto;
  overflow-y: auto;
  overflow-x: hidden;
}

.data-visualization-modal .chart-container::-webkit-scrollbar {
  width: 6px;
}

.data-visualization-modal .chart-container::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.data-visualization-modal .chart-container::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.data-visualization-modal .chart-container::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e0e6ed;
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

/* 导入导出相关样式 */
.import-export-modal {
  width: 500px;
  max-width: 90vw;
  height: auto;
  max-height: 80vh;
}

.import-export-tabs {
  display: flex;
  margin: 0 -30px;
  border-bottom: 1px solid #e0e6ed;
}

.tab-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  background: none;
  font-size: 14px;
  font-weight: 500;
  color: #718096;
  border-bottom: 2px solid transparent;
  cursor: pointer;
}

.tab-btn:hover {
  color: #4299e1;
}

.tab-btn.active {
  color: #4299e1;
  border-bottom-color: #4299e1;
  background-color: rgba(66, 153, 225, 0.05);
}

.import-export-content {
  padding: 24px 0;
  flex: 1;
  overflow: auto;
  min-height: 0;
}

.import-export-content::-webkit-scrollbar {
  width: 6px;
}

.import-export-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.import-export-content::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 3px;
}

.import-export-content::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
  font-size: 14px;
}

.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  color: #2d3748;
  background-color: #ffffff;
  transition: border-color 0.3s ease;
}

.form-select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.w-full {
  width: 100%;
}

.file-upload-container {
  position: relative;
  border: 2px dashed #e2e8f0;
  border-radius: 8px;
  padding: 32px 24px;
  text-align: center;
  background-color: #f8fafc;
}

.file-upload-container:hover {
  border-color: #4299e1;
  background-color: rgba(66, 153, 225, 0.05);
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.file-upload-info {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.file-name {
  font-size: 14px;
  color: #4a5568;
  margin-bottom: 8px;
  word-break: break-all;
  max-width: 100%;
}

.file-placeholder {
  font-size: 14px;
  color: #718096;
  margin-bottom: 8px;
}

.file-format-hint {
  font-size: 12px;
  color: #a0aec0;
  margin-top: 8px;
  text-align: center;
}

.form-actions {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.form-actions .btn {
  width: 100%;
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 6px;
}

.form-actions .btn-primary {
  background-color: #4299e1;
  border: none;
  color: white;
}

.form-actions .btn-primary:hover {
  background-color: #3182ce;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .import-export-modal {
    width: 95vw;
  }
  
  .import-export-content {
    padding: 16px 0;
  }
  
  .file-upload-container {
    padding: 24px 16px;
  }
}

.no-data {
  text-align: center;
  padding: 40px;
  color: #718096;
  font-style: italic;
}

/* 分页 */
.pagination-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 24px 0;
  flex-wrap: wrap;
  gap: 12px;
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
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  background-color: white;
  border-radius: 6px;
  font-size: 14px;
  color: #4a5568;
  cursor: pointer;
}

.page-btn:hover:not(.disabled) {
  background-color: #f7fafc;
  border-color: #cbd5e0;
  color: #2d3748;
}

.page-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-icon {
  font-size: 16px;
  font-weight: bold;
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-number {
  width: 36px;
  height: 36px;
  border: 1px solid #e2e8f0;
  background-color: white;
  border-radius: 6px;
  font-size: 14px;
  color: #4a5568;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-number:hover {
  border-color: #4299e1;
  color: #4299e1;
}

.page-number.active {
  background-color: #4299e1;
  border-color: #4299e1;
  color: white;
  font-weight: 500;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #718096;
}

.page-size-select {
  padding: 6px 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  color: #4a5568;
  background-color: white;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.page-size-select:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .pagination-container {
    flex-direction: column;
    align-items: center;
  }
  
  .pagination-controls {
    justify-content: center;
  }
  
  .page-size-selector {
    justify-content: center;
  }
}



/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  outline: none;
  text-decoration: none;
}

/* 图表类型选择按钮 */
.chart-types {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #f7fafc;
  border-radius: 8px;
  justify-content: center;
  flex-shrink: 0;
}

.chart-types .btn {
  flex: 1;
  min-width: 120px;
  max-width: 180px;
}

.chart-types .btn.active {
  background-color: #409EFF;
  color: #ffffff;
}

/* 柱状图年份选择器 */
.year-selector {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  padding: 12px;
  background-color: #f7fafc;
  border-radius: 8px;
  flex-shrink: 0;
  width: 240px;
  margin-left: auto;
  margin-right: auto;
}

.year-selector .month-picker,
.year-selector .year-picker {
  width: 220px;
  max-width: 220px;
}

.year-selector :deep(.el-input__wrapper) {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: none;
}

.year-selector :deep(.el-input__wrapper:hover) {
  border-color: #4299e1;
}

.year-selector :deep(.el-input__wrapper.is-focus) {
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
}

.year-selector :deep(.el-input__inner) {
  color: #2d3748;
  font-size: 14px;
}

.year-select {
  padding: 8px 16px;
  font-size: 14px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background-color: #ffffff;
  color: #2d3748;
  cursor: pointer;
  min-width: 150px;
}

.year-select:hover {
  border-color: #2563eb;
}

.year-select:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

/* 热力图控制区域 */
.heatmap-controls {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  justify-content: center;
  align-items: center;
  padding: 12px;
  background-color: #f7fafc;
  border-radius: 8px;
  flex-shrink: 0;
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
}

.heatmap-controls .year-picker {
  width: 180px;
}

.heatmap-controls :deep(.el-input__wrapper) {
  background-color: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  box-shadow: none;
}

.heatmap-controls :deep(.el-input__wrapper:hover) {
  border-color: #4299e1;
}

.heatmap-controls :deep(.el-input__wrapper.is-focus) {
  border-color: #4299e1;
  box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
}

.heatmap-controls :deep(.el-input__inner) {
  color: #2d3748;
  font-size: 14px;
}

.heatmap-type-buttons {
  display: flex;
  gap: 10px;
}

.heatmap-controls .btn {
  padding: 6px 12px;
  font-size: 12px;
}

.heatmap-controls .btn.active {
  background-color: #409EFF;
  color: #ffffff;
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
  background-color: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background-color: #cbd5e0;
  transform: translateY(-1px);
}

.btn-danger {
  background-color: #e53e3e;
  color: #ffffff;
}

.btn-danger:hover {
  background-color: #c53030;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(229, 62, 62, 0.3);
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
  }
  
  .category-list {
    grid-template-columns: 1fr;
  }
  
  .bill-filter {
    flex-direction: column;
  }
  
  .bill-table {
    font-size: 13px;
  }
  
  th, td {
    padding: 8px 12px;
  }
}
</style>




