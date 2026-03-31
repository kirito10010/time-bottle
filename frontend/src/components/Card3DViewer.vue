<template>
  <div class="card-3d-viewer" ref="containerRef">
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>
    
    <button class="close-btn" @click="$emit('close')">
      <span class="close-icon">×</span>
    </button>
    
    <div class="card-info-panel" v-if="card">
      <div class="info-row">
        <span class="info-label">名称</span>
        <span class="info-value card-name">{{ card.name }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">系列</span>
        <span class="info-value">{{ card.seriesName }}</span>
      </div>
      <div class="info-row">
        <span class="info-label">稀有度</span>
        <span class="info-value">
          <span class="card-type" :class="rarityClass">{{ card.type }}</span>
        </span>
      </div>
      <div class="info-row">
        <span class="info-label">数量</span>
        <span class="info-value quantity">x{{ card.quantity }}</span>
      </div>
    </div>
    
    <div class="control-hints">
      <span>🖱️ 右键拖拽旋转</span>
      <span>🔍 滚轮缩放</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue';
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';

const props = defineProps({
  card: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close']);

const containerRef = ref(null);
const loading = ref(true);

const rarityClass = computed(() => {
  if (!props.card?.rarityLevel) return 'common';
  switch (props.card.rarityLevel) {
    case 5: return 'legendary';
    case 4: return 'epic';
    case 3: return 'rare';
    case 2: return 'uncommon';
    default: return 'common';
  }
});

const rarityColors = {
  legendary: { primary: 0xffd700, secondary: 0xfbbf24, glow: 0xffffff, edge: 0xffd700, particle: 0.12, particleCount: 200, particleSize: 0.05, outerCount: 120, outerSize: 0.035, glowOpacity: 0.3, edgeEmissive: 0.15, backGlow: 0x2a2a2a, backGlowOpacity: 0.15, spriteColor: 0xffffff, spriteOpacity: 0.15, spriteScale: 1.2 },
  epic: { primary: 0xa855f7, secondary: 0x8b5cf6, glow: 0xffffff, edge: 0xa855f7, particle: 0.12, particleCount: 170, particleSize: 0.045, outerCount: 100, outerSize: 0.03, glowOpacity: 0.25, edgeEmissive: 0.12, backGlow: 0x2a2a2a, backGlowOpacity: 0.12, spriteColor: 0xffffff, spriteOpacity: 0.12, spriteScale: 1.0 },
  rare: { primary: 0x3b82f6, secondary: 0x60a5fa, glow: 0xffffff, edge: 0x3b82f6, particle: 0.12, particleCount: 140, particleSize: 0.04, outerCount: 80, outerSize: 0.025, glowOpacity: 0.2, edgeEmissive: 0.1, backGlow: 0x2a2a2a, backGlowOpacity: 0.1, spriteColor: 0xffffff, spriteOpacity: 0.1, spriteScale: 0.9 },
  uncommon: { primary: 0x22c55e, secondary: 0x10b981, glow: 0xffffff, edge: 0x22c55e, particle: 0.12, particleCount: 110, particleSize: 0.035, outerCount: 60, outerSize: 0.02, glowOpacity: 0.15, edgeEmissive: 0.08, backGlow: 0x2a2a2a, backGlowOpacity: 0.08, spriteColor: 0xffffff, spriteOpacity: 0.08, spriteScale: 0.8 },
  common: { primary: 0x9ca3af, secondary: 0x6b7280, glow: 0xffffff, edge: 0xc9b28b, particle: 0.12, particleCount: 80, particleSize: 0.03, outerCount: 40, outerSize: 0.015, glowOpacity: 0.1, edgeEmissive: 0.05, backGlow: 0x2a2a2a, backGlowOpacity: 0.06, spriteColor: 0xffffff, spriteOpacity: 0.06, spriteScale: 0.7 }
};

let scene, camera, renderer, controls, cardMesh, cardGroup;
let animationId;
let particleRing, outerRing;
let time = 0;

const initScene = () => {
  if (!containerRef.value) return;
  
  const width = containerRef.value.clientWidth;
  const height = containerRef.value.clientHeight;
  
  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x050b1a);
  scene.fog = new THREE.FogExp2(0x050b1a, 0.008);
  
  camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);
  camera.position.set(1.8, 1.2, 5.2);
  camera.lookAt(0, 0, 0);
  
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
  renderer.setSize(width, height);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.shadowMap.enabled = true;
  renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  renderer.toneMapping = THREE.NoToneMapping;
  containerRef.value.appendChild(renderer.domElement);
  
  controls = new OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.rotateSpeed = 1.2;
  controls.enableZoom = true;
  controls.enablePan = true;
  controls.minDistance = 4;
  controls.maxDistance = 12;
  controls.target.set(0, 0, 0);
  controls.mouseButtons = {
    LEFT: null,
    MIDDLE: null,
    RIGHT: THREE.MOUSE.ROTATE
  };
  
  addLights();
  addStars();
  createCard();
  
  animate();
};

let floatingLight = null;
let backLightRef = null;
let fillLightRef = null;

const addLights = () => {
  const ambientLight = new THREE.AmbientLight(0x404060, 0.65);
  scene.add(ambientLight);
  
  const dirLight = new THREE.DirectionalLight(0xffffff, 1.2);
  dirLight.position.set(2, 5, 3);
  dirLight.castShadow = true;
  dirLight.shadow.mapSize.width = 1024;
  dirLight.shadow.mapSize.height = 1024;
  dirLight.shadow.camera.near = 0.5;
  dirLight.shadow.camera.far = 8;
  dirLight.shadow.camera.left = -5;
  dirLight.shadow.camera.right = 5;
  dirLight.shadow.camera.top = 5;
  dirLight.shadow.camera.bottom = -5;
  scene.add(dirLight);
  
  const colors = rarityColors[rarityClass.value];
  
  backLightRef = new THREE.PointLight(0xffaa66, 0.5);
  backLightRef.position.set(-2, 1, -3);
  scene.add(backLightRef);
  
  fillLightRef = new THREE.PointLight(0x88aaff, 0.4);
  fillLightRef.position.set(1.5, 2, 2);
  scene.add(fillLightRef);
  
  const rimLight = new THREE.PointLight(0xaa88ff, 0.3);
  rimLight.position.set(0, -2, 0.5);
  scene.add(rimLight);
  
  floatingLight = new THREE.PointLight(colors.glow, 0.6);
  floatingLight.position.set(1, 1.2, 1);
  scene.add(floatingLight);
};

let starsRef = null;

const addStars = () => {
  const starGeometry = new THREE.BufferGeometry();
  const starCount = 1200;
  const positions = new Float32Array(starCount * 3);
  
  for (let i = 0; i < starCount; i++) {
    positions[i * 3] = (Math.random() - 0.5) * 200;
    positions[i * 3 + 1] = (Math.random() - 0.5) * 100;
    positions[i * 3 + 2] = (Math.random() - 0.5) * 80 - 40;
  }
  
  starGeometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  const starMaterial = new THREE.PointsMaterial({
    color: 0xffffff,
    size: 0.12,
    transparent: true,
    opacity: 0.6,
    blending: THREE.AdditiveBlending
  });
  
  starsRef = new THREE.Points(starGeometry, starMaterial);
  scene.add(starsRef);
};

const createCard = () => {
  cardGroup = new THREE.Group();
  scene.add(cardGroup);
  
  const textureLoader = new THREE.TextureLoader();
  const imageUrl = props.card?.imageUrl || '';
  
  textureLoader.load(
    imageUrl,
    (texture) => {
      texture.anisotropy = 16;
      texture.minFilter = THREE.LinearFilter;
      texture.magFilter = THREE.LinearFilter;
      texture.wrapS = THREE.RepeatWrapping;
      texture.wrapT = THREE.RepeatWrapping;
      
      const imgWidth = texture.image.width;
      const imgHeight = texture.image.height;
      const aspect = imgWidth / imgHeight;
      
      let cardWidth, cardHeight;
      const maxSide = 3.2;
      if (aspect >= 1) {
        cardWidth = maxSide;
        cardHeight = maxSide / aspect;
      } else {
        cardHeight = maxSide;
        cardWidth = maxSide * aspect;
      }
      
      const depth = Math.min(0.22, cardWidth * 0.08);
      
      const colors = rarityColors[rarityClass.value];
      
      const frontMaterial = new THREE.MeshStandardMaterial({
        map: texture,
        roughness: 0.28,
        metalness: 0.35,
        emissive: 0x000000,
        emissiveIntensity: 0,
        side: THREE.DoubleSide
      });
      
      const backMaterial = new THREE.MeshStandardMaterial({
        color: 0x3a2a2a,
        roughness: 0.55,
        metalness: 0.1,
        emissive: 0x1a0e0a,
        emissiveIntensity: 0.1
      });
      
      const edgeMaterial = new THREE.MeshStandardMaterial({
        color: colors.edge,
        roughness: 0.4,
        metalness: 0.65,
        emissive: colors.glow,
        emissiveIntensity: colors.edgeEmissive
      });
      
      const materials = [
        edgeMaterial, edgeMaterial,
        edgeMaterial, edgeMaterial,
        frontMaterial, backMaterial
      ];
      
      const geometry = new THREE.BoxGeometry(cardWidth, cardHeight, depth);
      cardMesh = new THREE.Mesh(geometry, materials);
      cardMesh.castShadow = true;
      cardMesh.receiveShadow = false;
      cardGroup.add(cardMesh);
      
      const edgesGeo = new THREE.EdgesGeometry(geometry);
      const edgesMat = new THREE.LineBasicMaterial({ 
        color: colors.glow, 
        transparent: true,
        opacity: 0.9,
        linewidth: 1 
      });
      const wireframe = new THREE.LineSegments(edgesGeo, edgesMat);
      cardMesh.add(wireframe);
      
      createParticleRings(cardWidth, cardHeight);
      createGlowSprite();
      createBackGlow(cardWidth, cardHeight);
      
      loading.value = false;
    },
    undefined,
    (error) => {
      console.error('图片加载失败:', error);
      loading.value = false;
    }
  );
};

const createBackGlow = (width, height) => {
  const colors = rarityColors[rarityClass.value];
  const backGlowMat = new THREE.MeshBasicMaterial({ 
    color: colors.backGlow, 
    transparent: true, 
    opacity: colors.backGlowOpacity, 
    side: THREE.BackSide 
  });
  const glowPlane = new THREE.Mesh(
    new THREE.PlaneGeometry(width * 1.3, height * 1.3), 
    backGlowMat
  );
  glowPlane.position.set(0, 0, -0.5);
  cardGroup.add(glowPlane);
};

const createParticleRings = (width, height) => {
  const colors = rarityColors[rarityClass.value];
  const radius = Math.max(width, height) * 0.65;
  
  const ringCount = colors.particleCount;
  const ringGeometry = new THREE.BufferGeometry();
  const positions = new Float32Array(ringCount * 3);
  const colorAttr = new Float32Array(ringCount * 3);
  
  for (let i = 0; i < ringCount; i++) {
    const angle = (i / ringCount) * Math.PI * 2;
    positions[i * 3] = Math.cos(angle) * radius;
    positions[i * 3 + 1] = Math.sin(angle * 2) * 0.15;
    positions[i * 3 + 2] = Math.sin(angle) * radius;
    
    const mixColor = new THREE.Color().setHSL(colors.particle + Math.sin(angle) * 0.05, 0.9, 0.6);
    colorAttr[i * 3] = mixColor.r;
    colorAttr[i * 3 + 1] = mixColor.g;
    colorAttr[i * 3 + 2] = mixColor.b;
  }
  
  ringGeometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
  ringGeometry.setAttribute('color', new THREE.BufferAttribute(colorAttr, 3));
  
  const ringMaterial = new THREE.PointsMaterial({
    size: colors.particleSize,
    vertexColors: true,
    transparent: true,
    opacity: 0.7,
    blending: THREE.AdditiveBlending
  });
  
  particleRing = new THREE.Points(ringGeometry, ringMaterial);
  cardGroup.add(particleRing);
  
  const outerCount = colors.outerCount;
  const outerGeo = new THREE.BufferGeometry();
  const outerPos = new Float32Array(outerCount * 3);
  
  for (let i = 0; i < outerCount; i++) {
    const angle = (i / outerCount) * Math.PI * 2;
    const r = radius * 1.18;
    outerPos[i * 3] = Math.cos(angle) * r;
    outerPos[i * 3 + 1] = Math.cos(angle * 3) * 0.08;
    outerPos[i * 3 + 2] = Math.sin(angle) * r;
  }
  
  outerGeo.setAttribute('position', new THREE.BufferAttribute(outerPos, 3));
  const outerMat = new THREE.PointsMaterial({
    color: colors.secondary,
    size: colors.outerSize,
    transparent: true,
    opacity: 0.5,
    blending: THREE.AdditiveBlending
  });
  
  outerRing = new THREE.Points(outerGeo, outerMat);
  cardGroup.add(outerRing);
};

const createGlowSprite = () => {
  const colors = rarityColors[rarityClass.value];
  
  const canvas = document.createElement('canvas');
  canvas.width = 32;
  canvas.height = 32;
  const ctx = canvas.getContext('2d');
  
  ctx.fillStyle = 'rgba(0,0,0,0)';
  ctx.fillRect(0, 0, 32, 32);
  ctx.beginPath();
  ctx.arc(16, 16, 12, 0, 2 * Math.PI);
  ctx.fillStyle = '#ffcc88';
  ctx.fill();
  ctx.globalCompositeOperation = 'source-over';
  ctx.beginPath();
  ctx.arc(16, 16, 6, 0, 2 * Math.PI);
  ctx.fillStyle = '#ffaa66';
  ctx.fill();
  
  const texture = new THREE.CanvasTexture(canvas);
  const spriteMat = new THREE.SpriteMaterial({
    map: texture,
    color: colors.spriteColor,
    blending: THREE.AdditiveBlending,
    transparent: true,
    opacity: colors.spriteOpacity
  });
  
  const sprite = new THREE.Sprite(spriteMat);
  sprite.scale.set(colors.spriteScale, colors.spriteScale, 1);
  sprite.position.set(0.5, 0.8, 1.2);
  cardGroup.add(sprite);
};

const animate = () => {
  animationId = requestAnimationFrame(animate);
  
  time += 0.012;
  
  if (cardGroup) {
    cardGroup.position.y = Math.sin(time * 1.2) * 0.03;
  }
  
  if (floatingLight) {
    floatingLight.position.x = 1 + Math.sin(time * 0.9) * 0.3;
    floatingLight.position.y = 1.2 + Math.cos(time * 1.1) * 0.2;
    floatingLight.intensity = 0.55 + Math.sin(time * 1.5) * 0.1;
  }
  
  if (backLightRef) {
    backLightRef.intensity = 0.5 + Math.sin(time * 0.7) * 0.1;
  }
  
  if (fillLightRef) {
    fillLightRef.intensity = 0.45 + Math.cos(time * 0.9) * 0.08;
  }
  
  if (starsRef) {
    starsRef.rotation.y += 0.0003;
    starsRef.rotation.x += 0.0002;
  }
  
  if (particleRing) {
    particleRing.rotation.y += 0.005;
    particleRing.rotation.x = Math.sin(time * 0.5) * 0.1;
  }
  
  if (outerRing) {
    outerRing.rotation.y -= 0.002;
  }
  
  if (controls) {
    controls.update();
  }
  
  if (renderer && scene && camera) {
    renderer.render(scene, camera);
  }
};

const handleResize = () => {
  if (!containerRef.value || !camera || !renderer) return;
  
  const width = containerRef.value.clientWidth;
  const height = containerRef.value.clientHeight;
  
  camera.aspect = width / height;
  camera.updateProjectionMatrix();
  renderer.setSize(width, height);
};

onMounted(() => {
  initScene();
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  
  if (renderer) {
    renderer.dispose();
    if (containerRef.value && renderer.domElement) {
      containerRef.value.removeChild(renderer.domElement);
    }
  }
  
  if (scene) {
    scene.traverse((object) => {
      if (object.geometry) object.geometry.dispose();
      if (object.material) {
        if (Array.isArray(object.material)) {
          object.material.forEach(m => m.dispose());
        } else {
          object.material.dispose();
        }
      }
    });
  }
});

watch(() => props.card, () => {
  if (scene) {
    while (scene.children.length > 0) {
      const child = scene.children[0];
      scene.remove(child);
    }
    
    if (cardGroup) {
      cardGroup.clear();
    }
    
    loading.value = true;
    addLights();
    addStars();
    createCard();
  }
}, { deep: true });
</script>

<style scoped>
.card-3d-viewer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #050b1a;
  z-index: 10000;
}

.loading-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  color: white;
  font-size: 16px;
  z-index: 10;
  background: rgba(0, 0, 0, 0.7);
  padding: 12px 24px;
  border-radius: 40px;
  backdrop-filter: blur(8px);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid rgba(255, 255, 255, 0.2);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.15);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 10;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: rotate(90deg);
}

.close-icon {
  font-size: 26px;
  line-height: 1;
}

.card-info-panel {
  position: absolute;
  top: 20px;
  right: 80px;
  background: rgba(13, 17, 23, 0.75);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 16px 20px;
  min-width: 160px;
  z-index: 10;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-right: 16px;
}

.info-value {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.card-name {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.card-type {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
}

.card-type.legendary {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: #78350f;
}

.card-type.epic {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  color: #4c1d95;
}

.card-type.rare {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: #1e3a8a;
}

.card-type.uncommon {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: #064e3b;
}

.card-type.common {
  background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%);
  color: #1f2937;
}

.quantity {
  color: #fbbf24;
  font-weight: 700;
}

.control-hints {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 24px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  z-index: 10;
}
</style>
