document.addEventListener('DOMContentLoaded', async function () {
  const searchInput = document.querySelector('.search-box input');
  const searchBtn = document.querySelector('.search-box button:first-of-type');
  const loginBtn = document.getElementById('loginBtn');
  const welcomeText = document.getElementById('welcomeText');
  const categorySelect = document.getElementById('categorySelect');
  const newsContainer = document.getElementById('newsContainer');

  let newsTitles = [];

  // 로그인 확인
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
  const storedUserId = localStorage.getItem('userId');

  if (isLoggedIn && storedUserId) {
    loginBtn.style.display = 'none';
    welcomeText.style.display = 'inline-block';
    welcomeText.textContent = `${storedUserId}님 반갑습니다!`;
    welcomeText.style.fontWeight = 'bold';
  } else {
    loginBtn.style.display = 'inline-block';
    welcomeText.style.display = 'none';
    loginBtn.textContent = 'Login';
    loginBtn.onclick = () => location.href = 'login.html';
  }

  // 전체 뉴스 불러오기
  function loadAllNews() {
    fetch("http://localhost:8080/api/news/titles")
      .then(res => res.json())
      .then(data => {
        newsTitles = data;
        renderNews(newsTitles);
      })
      .catch(err => console.error('전체 뉴스 로딩 실패:', err));
  }

  // 뉴스 카드 렌더링
  function renderNews(titleList) {
    newsContainer.innerHTML = '';
    titleList.forEach(title => {
      const card = document.createElement('div');
      card.className = 'news-card';
      card.innerHTML = `
        <img src="assets/news1.png" alt="뉴스 썸네일" class="news-thumbnail" />
        <div class="news-text">
          <div class="news-title">${title}</div>
        </div>
      `;
      newsContainer.appendChild(card);
    });
  }

  // 검색 필터
  searchBtn.addEventListener('click', function () {
    const keyword = searchInput.value.trim();
    if (!keyword) return;
    const filtered = newsTitles.filter(title => title.includes(keyword));
    renderNews(filtered);
  });

  // 카테고리 셀렉트 구성
  async function buildCategoryOptions() {
    let categories = JSON.parse(localStorage.getItem('interestCategories') || '[]');

    if (categories.length === 0 && storedUserId) {
      categories = await fetchUserCategories(storedUserId);
    }

    categorySelect.innerHTML = '';

    const defaultOption = document.createElement('option');
    defaultOption.value = 'ALL';
    defaultOption.textContent = '카테고리 선택';
    categorySelect.appendChild(defaultOption);

    categories.forEach(cat => {
      const option = document.createElement('option');
      option.value = cat;
      option.textContent = translateCategory(cat);
      categorySelect.appendChild(option);
    });
  }

  // 셀렉트 이벤트
  categorySelect.addEventListener('change', () => {
    const selected = categorySelect.value;
    if (selected === 'ALL') {
      loadAllNews();
    } else {
      fetch(`http://localhost:8080/api/news?category=${selected}`)
        .then(res => res.json())
        .then(data => {
          const titlesOnly = data.map(n => n.title);
          newsTitles = titlesOnly;
          renderNews(newsTitles);
        })
        .catch(err => console.error('카테고리 뉴스 로딩 실패:', err));
    }
  });

  function translateCategory(code) {
    const map = {
      Politics: "정치",
      Economy: "경제",
      Society: "사회",
      LifestyleCulture: "생활/문화",
      Entertainment: "연예",
      IT: "IT/과학"
    };
    return map[code] || code;
  }

  async function fetchUserCategories(userId) {
    try {
      const res = await fetch(`http://localhost:8080/api/signup/preferences/${userId}`);
      return await res.json();
    } catch (err) {
      console.error("카테고리 불러오기 실패", err);
      return [];
    }
  }

  // 초기 실행
  await buildCategoryOptions();
  loadAllNews();
});
