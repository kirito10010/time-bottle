<template>
  <div class="introduction-page">
    <!-- 头部导航 -->
    <header class="header">
      <div class="container">
        <div class="header-content">
          <a href="#" class="logo">拾光瓶</a>
          <nav class="nav">
            <a href="#features" class="nav-link">核心功能</a>
            <a href="#resource" class="nav-link">项目资源</a>
            <a href="#comments" class="nav-link">用户评价</a>
            <a href="#price" class="nav-link">版本/部署</a>
            <a href="#contact" class="nav-link">联系我们</a>
          </nav>
          <div class="header-actions">
            <button class="btn btn-outline-primary">文档</button>
            <button class="btn btn-primary" @click="handleStartUse">立即使用</button>
          </div>
        </div>
      </div>
    </header>

    <!-- 核心卖点区 -->
    <section class="hero">
      <div id="particles-js"></div>
      <div class="container">
        <div class="hero-content">
          <div class="hero-text fade-in">
            <h1>一站式<span>拾光瓶</span>解决方案<br>高效完成个人管理需求</h1>
            <p>整合财务台账、绩效记录、绩效看板核心能力，一套系统解决传统个人管理中的多工具切换、数据不同步、效率低下等问题，零学习成本，快速上手，助力个人高效管理。</p>
            <div class="hero-actions">
              <button class="btn btn-primary btn-lg" @click="handleStartUse">立即体验</button>
              <button class="btn btn-outline-primary btn-lg" @click="scrollToSection('features')">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                  <path d="M6.271 5.055a.5.5 0 0 1 .52.038l3.5 2.5a.5.5 0 0 1 0 .814l-3.5 2.5A.5.5 0 0 1 6 10.5v-5a.5.5 0 0 1 .271-.445z"/>
                </svg>
                查看演示
              </button>
            </div>
          </div>
          <div class="hero-image fade-in">
            <img src="https://picsum.photos/800/500" alt="项目主图" class="img-fluid">
          </div>
        </div>
      </div>
    </section>

    <!-- 核心功能介绍 -->
    <section class="features" id="features">
      <div class="container text-center">
        <h2 class="section-title fade-in">核心功能</h2>
        <p class="section-desc fade-in">覆盖个人管理全生命周期，从财务记录、绩效管理到数据分析，一站式解决所有核心需求</p>
        <div class="features-grid">
          <div class="feature-card card fade-in">
            <div class="feature-icon">💰</div>
            <h3>财务台账</h3>
            <p>个人收支管理、账单记录、分类统计，支持数据可视化和导入导出，轻松掌握财务状况。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">📈</div>
            <h3>绩效记录</h3>
            <p>每日绩效记录、加班管理、薪资计算，自动统计绩效数据，一键生成月度报表。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">📊</div>
            <h3>绩效看板</h3>
            <p>导入绩效数据，多维度图表分析，个人绩效排名、项目绩效对比一目了然。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">🔒</div>
            <h3>安全可靠</h3>
            <p>BCrypt密码加密，数据安全传输，支持私有化部署，保障个人数据安全。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">📱</div>
            <h3>响应式设计</h3>
            <p>完美适配PC端和移动端，随时随地记录数据，数据云端同步，多端无缝切换。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">🎨</div>
            <h3>精美界面</h3>
            <p>现代化UI设计，毛玻璃效果、渐变色、动画过渡，带来极致的视觉体验。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">📤</div>
            <h3>数据导入导出</h3>
            <p>支持CSV/Excel格式数据导入导出，兼容主流工具生态，无缝迁移现有数据。</p>
          </div>
          <div class="feature-card card fade-in">
            <div class="feature-icon">⚙️</div>
            <h3>灵活配置</h3>
            <p>自定义薪资参数、项目配置，满足不同场景需求，配置持久化保存。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 项目资源/案例区 -->
    <section class="resource" id="resource">
      <div class="container text-center">
        <h2 class="section-title fade-in">项目资源&相关案例</h2>
        <p class="section-desc fade-in">丰富的配套资源，结合实际业务案例，快速落地使用</p>
        <div class="resource-grid">
          <div class="resource-card card fade-in">
            <h3>项目在线文档</h3>
            <p>详细的使用教程、接口说明、配置指南，零基础快速上手</p>
            <a href="#" class="resource-link">查看文档 →</a>
          </div>
          <div class="resource-card card fade-in">
            <h3>演示项目</h3>
            <p>基于真实业务的演示案例，直观展示项目核心能力与使用效果</p>
            <a href="#" class="resource-link" @click.prevent="handleStartUse">在线体验 →</a>
          </div>
          <div class="resource-card card fade-in">
            <h3>开发工具集</h3>
            <p>配套的开发工具、脚本工具，无缝集成到现有开发流程，提升效率</p>
            <a href="#" class="resource-link">下载使用 →</a>
          </div>
        </div>
      </div>
    </section>

    <!-- 用户评价区 -->
    <section class="comments" id="comments">
      <div class="container text-center">
        <h2 class="section-title fade-in">用户真实评价</h2>
        <p class="section-desc fade-in">来自真实用户的使用反馈</p>
        <div class="comments-grid">
          <div class="comment-item fade-in">
            <p>这个项目真的解决了我的财务管理问题，之前用Excel记账太麻烦，现在一站式搞定，数据可视化也很直观，上手速度特别快！</p>
            <div class="comment-user">
              <img src="https://picsum.photos/100/100?random=1" alt="用户头像">
              <div class="user-info">
                <h5>张先生</h5>
                <p>软件工程师</p>
              </div>
            </div>
          </div>
          <div class="comment-item fade-in">
            <p>作为自由职业者，最看重的是易用性，这个项目的界面设计太友好了，绩效记录和薪资计算功能特别实用，省了我很多时间！</p>
            <div class="comment-user">
              <img src="https://picsum.photos/100/100?random=2" alt="用户头像">
              <div class="user-info">
                <h5>李女士</h5>
                <p>自由职业者</p>
              </div>
            </div>
          </div>
          <div class="comment-item fade-in">
            <p>数据安全有保障，而且支持私有化部署，技术栈也很现代化，Vue3 + SpringBoot3，代码质量很高，值得学习！</p>
            <div class="comment-user">
              <img src="https://picsum.photos/100/100?random=3" alt="用户头像">
              <div class="user-info">
                <h5>王先生</h5>
                <p>技术负责人</p>
              </div>
            </div>
          </div>
          <div class="comment-item fade-in">
            <p>零学习成本真的太香了，界面美观，功能实用，而且支持多格式数据导入，我之前的老数据无缝迁移，一点都不麻烦。</p>
            <div class="comment-user">
              <img src="https://picsum.photos/100/100?random=4" alt="用户头像">
              <div class="user-info">
                <h5>赵女士</h5>
                <p>产品经理</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 版本/部署区 -->
    <section class="price" id="price">
      <div class="container text-center">
        <h2 class="section-title fade-in">版本选择&部署方式</h2>
        <p class="section-desc fade-in">根据个人需求，选择适合的版本，灵活部署</p>
        <div class="price-grid">
          <div class="price-card card fade-in">
            <h3>免费公网版</h3>
            <p>适合个人用户，快速体验核心功能</p>
            <ul>
              <li>✓ 完整核心功能使用</li>
              <li>✓ 云端数据同步</li>
              <li>✓ 基础技术支持</li>
              <li>✓ 在线文档/演示案例</li>
              <li>✓ 数据导入导出</li>
            </ul>
            <button class="btn btn-outline-primary w-100" @click="handleStartUse">立即使用</button>
          </div>
          <div class="price-card card featured fade-in">
            <div class="featured-badge">推荐</div>
            <h3>私有化部署版</h3>
            <p>适合企业/团队，专属服务，数据安全可控</p>
            <ul>
              <li>✓ 包含免费版所有功能</li>
              <li>✓ 本地/私有服务器部署</li>
              <li>✓ 精细化权限管理</li>
              <li>✓ 专属技术支持</li>
              <li>✓ 定制化功能开发</li>
            </ul>
            <button class="btn btn-primary w-100">立即咨询</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 底部信息 -->
    <footer class="footer" id="contact">
      <div class="container">
        <div class="footer-grid">
          <div class="footer-links">
            <h5>拾光瓶</h5>
            <ul>
              <li>一站式个人管理解决方案</li>
              <li>高效 · 便捷 · 安全</li>
              <li>开源免费</li>
            </ul>
          </div>
          <div class="footer-links">
            <h5>核心功能</h5>
            <ul>
              <li><a href="#features">财务台账</a></li>
              <li><a href="#features">绩效记录</a></li>
              <li><a href="#features">绩效看板</a></li>
              <li><a href="#features">数据管理</a></li>
            </ul>
          </div>
          <div class="footer-links">
            <h5>相关资源</h5>
            <ul>
              <li><a href="#">在线文档</a></li>
              <li><a href="#">演示案例</a></li>
              <li><a href="#">开发工具</a></li>
              <li><a href="#">常见问题</a></li>
            </ul>
          </div>
          <div class="footer-links">
            <h5>联系我们</h5>
            <ul>
              <li>📧 a3025863517@163.com</li>
              <li><a href="https://github.com/kirito10010/time-bottle" target="_blank">🌐 GitHub/time-bottle</a></li>
              <li>📱 198-6395-0869</li>
            </ul>
          </div>
          <div class="footer-links">
            <h5>扫码关注</h5>
            <img src="../assets/images/saomaguanzhu.PNG" alt="公众号二维码" class="qrcode-img">
            <p class="qrcode-tip">获取最新更新&专属福利</p>
          </div>
        </div>
        <div class="copyright">
          <p>© 2025 拾光瓶 Time Bottle 版权所有 | MIT License</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const handleStartUse = () => {
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    router.push('/financial-ledger');
  } else {
    router.push('/login');
  }
};

const showDocTip = () => {
  alert('文档功能开发中，敬请期待！');
};

const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
};

const initParticles = () => {
  if (window.particlesJS) {
    window.particlesJS('particles-js', {
      particles: {
        number: { value: 80, density: { enable: true, value_area: 800 } },
        color: { value: '#165DFF' },
        shape: { type: 'circle' },
        opacity: { value: 0.1, random: true },
        size: { value: 3, random: true },
        line_linked: { enable: true, distance: 150, color: '#165DFF', opacity: 0.1, width: 1 },
        move: { enable: true, speed: 1, direction: 'none', random: true, straight: false, out_mode: 'out', bounce: false }
      },
      interactivity: {
        detect_on: 'canvas',
        events: { onhover: { enable: true, mode: 'grab' }, onclick: { enable: true, mode: 'push' }, resize: true },
        modes: { grab: { distance: 140, line_linked: { opacity: 0.2 } }, push: { particles_nb: 4 } }
      },
      retina_detect: true
    });
  }
};

const handleScroll = () => {
  const fadeItems = document.querySelectorAll('.fade-in');
  fadeItems.forEach(item => {
    const itemTop = item.getBoundingClientRect().top;
    const windowHeight = window.innerHeight;
    if (itemTop < windowHeight * 0.8) {
      item.classList.add('active');
    }
  });
};

const loadParticlesScript = () => {
  return new Promise((resolve) => {
    if (window.particlesJS) {
      resolve();
      return;
    }
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js';
    script.onload = resolve;
    document.head.appendChild(script);
  });
};

onMounted(async () => {
  await loadParticlesScript();
  initParticles();
  window.addEventListener('scroll', handleScroll);
  handleScroll();
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "Microsoft YaHei", "PingFang SC", sans-serif;
}

.introduction-page {
  --primary: #165DFF;
  --primary-light: #4080FF;
  --primary-dark: #0E42D2;
  --light: #F5F7FA;
  --dark: #1D2129;
  --gray: #86909C;
  --border: #E5E6EB;
  --shadow-sm: 0 2px 8px rgba(22, 93, 255, 0.05);
  --shadow-md: 0 4px 16px rgba(22, 93, 255, 0.08);
  --shadow-lg: 0 8px 24px rgba(22, 93, 255, 0.12);
  min-height: 100vh;
  background: #fff;
  color: #1D2129;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Header */
.header {
  padding: 1rem 0;
  border-bottom: 1px solid rgba(229, 230, 235, 0.5);
  position: sticky;
  top: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 999;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.05);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #0E42D2, #4080FF) !important;
  -webkit-background-clip: text !important;
  -webkit-text-fill-color: transparent !important;
  background-clip: text !important;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: #1D2129;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: #165DFF;
}

.header-actions {
  display: flex;
  gap: 0.5rem;
}

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  text-decoration: none;
  gap: 0.5rem;
  position: relative;
  overflow: hidden;
}

.btn-primary {
  background: linear-gradient(135deg, #4080FF, #0E42D2) !important;
  color: #fff !important;
  box-shadow: 0 4px 16px rgba(22, 93, 255, 0.08);
  border: none !important;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(22, 93, 255, 0.12);
}

.btn-outline-primary {
  background: transparent !important;
  border: 1px solid #165DFF !important;
  color: #165DFF !important;
}

.btn-outline-primary:hover {
  background: linear-gradient(135deg, #4080FF, #0E42D2) !important;
  color: #fff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(22, 93, 255, 0.08);
}

.btn-lg {
  padding: 0.75rem 1.5rem;
  font-size: 16px;
}

/* Hero */
.hero {
  padding: 8rem 0;
  background: linear-gradient(135deg, #F0F5FF, #E8F3FF);
  position: relative;
  overflow: hidden;
}

#particles-js {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.hero .container {
  position: relative;
  z-index: 1;
}

.hero-content {
  display: flex;
  align-items: center;
  gap: 4rem;
}

.hero-text {
  flex: 1.2;
}

.hero-image {
  flex: 1;
}

.hero h1 {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1.5rem;
  line-height: 1.3;
}

.hero h1 span {
  background: linear-gradient(135deg, #0E42D2, #4080FF) !important;
  -webkit-background-clip: text !important;
  -webkit-text-fill-color: transparent !important;
  background-clip: text !important;
}

.hero p {
  font-size: 1.1rem;
  color: #86909C;
  margin-bottom: 2rem;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: 1rem;
}

.img-fluid {
  max-width: 100%;
  height: auto;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(22, 93, 255, 0.12);
}

/* Fade In Animation */
.fade-in {
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}

.fade-in.active {
  opacity: 1;
  transform: translateY(0);
}

/* Section Styles */
.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #0E42D2, #4080FF) !important;
  -webkit-background-clip: text !important;
  -webkit-text-fill-color: transparent !important;
  background-clip: text !important;
}

.section-desc {
  color: #86909C;
  font-size: 1.1rem;
  max-width: 800px;
  margin: 0 auto 3rem;
  line-height: 1.8;
}

/* Features */
.features {
  padding: 6rem 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
}

.feature-card {
  padding: 2rem;
  text-align: center;
  background: rgba(245, 247, 250, 0.8);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 24px rgba(22, 93, 255, 0.12);
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
}

.feature-card p {
  color: #86909C;
  font-size: 0.9rem;
  line-height: 1.6;
}

/* Resource */
.resource {
  padding: 6rem 0;
  background: linear-gradient(135deg, #F8FAFF, #F0F5FF);
}

.resource-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
}

.resource-card {
  padding: 2rem;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(22, 93, 255, 0.1);
}

.resource-card h3 {
  font-size: 1.2rem;
  font-weight: 700;
  margin-bottom: 0.75rem;
}

.resource-card p {
  color: #86909C;
  margin-bottom: 1rem;
}

.resource-link {
  color: #165DFF;
  text-decoration: none;
  font-weight: 500;
}

.resource-link:hover {
  text-decoration: underline;
}

/* Comments */
.comments {
  padding: 6rem 0;
}

.comments-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
  margin-top: 2rem;
}

.comment-item {
  padding: 2rem;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  border: 1px solid rgba(229, 230, 235, 0.8);
  position: relative;
}

.comment-item::before {
  content: '"';
  position: absolute;
  top: 1rem;
  left: 1.5rem;
  font-size: 4rem;
  color: rgba(22, 93, 255, 0.1);
  font-family: serif;
}

.comment-item p {
  color: #1D2129;
  line-height: 1.8;
  margin-bottom: 1.5rem;
  padding-left: 1rem;
  border-left: 3px solid #165DFF;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.comment-user img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 2px 8px rgba(22, 93, 255, 0.05);
}

.comment-user h5 {
  font-size: 1rem;
  margin-bottom: 0.25rem;
}

.comment-user p {
  font-size: 0.85rem;
  color: #86909C;
  margin: 0;
  padding: 0;
  border: none;
}

/* Price */
.price {
  padding: 6rem 0;
  background: linear-gradient(135deg, #F0F5FF, #E8F3FF);
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
  max-width: 800px;
  margin: 2rem auto 0;
}

.price-card {
  padding: 2.5rem;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  position: relative;
}

.price-card.featured {
  border: 2px solid #165DFF !important;
  transform: scale(1.02);
}

.featured-badge {
  position: absolute;
  top: -12px;
  right: 1.5rem;
  background: #165DFF;
  color: #fff;
  padding: 0.25rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.price-card h3 {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.price-card p {
  color: #86909C;
  margin-bottom: 1.5rem;
}

.price-card ul {
  list-style: none;
  text-align: left;
  margin-bottom: 1.5rem;
}

.price-card ul li {
  padding: 0.5rem 0;
  color: #1D2129;
}

.w-100 {
  width: 100%;
}

/* Footer */
.footer {
  padding: 6rem 0 2rem;
  border-top: 1px solid rgba(229, 230, 235, 0.5);
  background: linear-gradient(135deg, #F8FAFF, #F0F5FF);
}

.footer-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 2rem;
}

.footer-links h5 {
  font-weight: 700;
  margin-bottom: 1.8rem;
  color: #1D2129;
  position: relative;
  padding-bottom: 0.8rem;
}

.footer-links h5::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 40px;
  height: 2px;
  background: #165DFF;
  border-radius: 2px;
}

.footer-links ul {
  list-style: none;
}

.footer-links li {
  margin-bottom: 1.2rem;
  color: #86909C;
  transition: all 0.3s ease;
}

.footer-links li:hover {
  color: #165DFF;
  transform: translateX(5px);
}

.footer-links a {
  color: #86909C;
  text-decoration: none;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #165DFF;
}

.qrcode-img {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 0.5rem;
}

.qrcode-tip {
  color: #86909C;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.copyright {
  text-align: center;
  padding-top: 2.5rem;
  margin-top: 2.5rem;
  border-top: 1px solid rgba(229, 230, 235, 0.5);
  color: #86909C;
  font-size: 0.9rem;
}

/* Responsive */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-actions {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
    gap: 1rem;
  }
  
  .nav {
    display: none;
  }
  
  .hero h1 {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 1.8rem;
  }
  
  .features-grid,
  .resource-grid,
  .comments-grid,
  .price-grid,
  .footer-grid {
    grid-template-columns: 1fr;
  }
}
</style>
