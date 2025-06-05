export function createBriefingCarousel(containerId, data, onBackClick = null) {
  let currentIndex = 0;

  const container = document.getElementById(containerId);
  container.innerHTML = `
    <div class="header-row">
      <button class="back-button" id="backBtn">←</button>
      <h2>6월 4일 브리핑</h2>
    </div>
    <div class="progress-indicator" id="progressIndicator"></div>
    <div class="carousel">
      <div class="card" id="cardContent"></div>
      <div class="navigation-buttons">
        <button id="prevBtn">◀</button>
        <button id="nextBtn">▶</button>
      </div>
    </div>
  `;

  const card = container.querySelector("#cardContent");
  const prevBtn = container.querySelector("#prevBtn");
  const nextBtn = container.querySelector("#nextBtn");
  const backBtn = container.querySelector("#backBtn");
  const progress = container.querySelector("#progressIndicator");

  function renderProgress() {
    progress.innerHTML = '';
    data.forEach((_, i) => {
      const dot = document.createElement('div');
      dot.className = 'dot';
      if (i <= currentIndex) dot.classList.add('active');
      progress.appendChild(dot);
    });
  }

 function renderCard(index) {
  const { title, summaries, url } = data[index];
  const content = summaries?.additionalProp1 || '(요약 없음)';
  const image = "assets/news1.png"; // 고정 이미지로 대체

  card.innerHTML = `
    <img src="${image}" alt="뉴스 이미지" style="width:100%; border-radius:10px;" />
    <h3>${title}</h3>
    <p>${content}</p>
    <a class="detail-btn" href="${url}" target="_blank">자세히 보기</a>
  `;
  renderProgress();
}


  prevBtn.onclick = () => {
    if (currentIndex > 0) {
      currentIndex--;
      renderCard(currentIndex);
    }
  };

  nextBtn.onclick = () => {
    if (currentIndex < data.length - 1) {
      currentIndex++;
      renderCard(currentIndex);
    }
  };

  backBtn.onclick = () => {
    if (onBackClick) {
      onBackClick();
    } else {
      location.href = 'Home.html';
    }
  };

  renderCard(currentIndex);
}
