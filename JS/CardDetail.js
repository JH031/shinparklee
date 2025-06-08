document.addEventListener("DOMContentLoaded", async function () {
  const container = document.getElementById("newsDetailContainer");
  const newsId = localStorage.getItem("selectedNewsId");
  console.log("newsId:", newsId); // 디버깅용 로그
  const token = localStorage.getItem("token");

  if (!newsId || newsId === "undefined") {
    container.innerHTML = "<p>선택된 뉴스가 없습니다.</p>";
    return;
  }

  let item = null;
  let styledSummaries = {};

  try {
    // 기본 요약 불러오기
    //const res = await fetch("http://localhost:8080/api/summary/basic");
    //const data = await res.json();
    //item = data.find(news => news.newsId === newsId);

    // 아래처럼 대체
    const res = await fetch(`http://localhost:8080/api/summary/detail/${newsId}`, {
      headers: token ? { "Authorization": `Bearer ${token}` } : {}
    });

    if (!res.ok) {
      container.innerHTML = "<p>뉴스 정보를 불러오는 데 실패했습니다.</p>";
      return;
    }
    item = await res.json();



    if (!item) {
      container.innerHTML = "<p>뉴스 정보를 찾을 수 없습니다.</p>";
      return;
    }

    const { title, url, imageUrl, createdAt, summaries = {} } = item;
    styledSummaries["DEFAULT"] = summaries["DEFAULT"] || "(요약 없음)";

    // 스크랩 상태 확인
    let isScrapped = false;
    let starIcon = "assets/emptystar.png";

    if (token) {
      try {
        const withScrapRes = await fetch("http://localhost:8080/api/news/with-scrap", {
          headers: { "Authorization": `Bearer ${token}` }
        });
        if (withScrapRes.ok) {
          const allNews = await withScrapRes.json();
          const matchedNews = allNews.find(n => n.newsId === newsId);
          isScrapped = matchedNews?.scrapped === true;
          starIcon = isScrapped ? "assets/fillstar.png" : "assets/emptystar.png";
        }
      } catch (err) {
        console.warn("스크랩 확인 실패:", err);
      }
    }

    // 상세 내용 출력
    container.innerHTML = `
    <div class="news-detail-card">
      <img src="${imageUrl || 'assets/news1.png'}" alt="뉴스 이미지" class="news-detail-image" />
      <div class="title-line">
        <h2 class="news-detail-title">${title}</h2>
        <div class="right-controls" style="display: flex; align-items: center; gap: 20px;">
          <img id="scrapIcon" src="${starIcon}" alt="스크랩 아이콘" class="scrap-icon-inline" />
          <label for="styleSelect" style="white-space: nowrap;">말투 선택:</label>
          <select id="styleSelect">
            <option value="DEFAULT">기본</option>
            <option value="FUNNY">재미있게</option>
            <option value="SIMPLE">쉽게</option>
            <option value="FRIENDLY">친근하게</option>
          </select>
        </div>
      </div>
      <p id="summaryText" class="news-detail-summary">${styledSummaries["DEFAULT"]}</p>
      <a href="${url}" target="_blank" class="news-detail-link">원문 보기</a>
      <button id="backButton" class="back-button">← 뒤로가기</button>
    </div>
  `;
  document.getElementById("backButton").addEventListener("click", () => {
  window.history.back();
});


    // 스크랩 기능
    document.getElementById("scrapIcon").addEventListener("click", async () => {
      if (!token) return alert("로그인 후 스크랩할 수 있습니다.");

      try {
        const res = await fetch(`http://localhost:8080/api/scrap/${newsId}`, {
          method: "POST",
          headers: { "Authorization": `Bearer ${token}` }
        });

        if (res.ok) {
          alert("스크랩 완료!");
          document.getElementById("scrapIcon").src = "assets/fillstar.png";
        } else {
          alert("스크랩 실패");
        }
      } catch (err) {
        console.error("스크랩 실패:", err);
      }
    });

    // 드롭다운 변경 시 자동 변환
    document.getElementById("styleSelect").addEventListener("change", async (e) => {
      const selectedStyle = e.target.value;
      const summaryTextElem = document.getElementById("summaryText");

      if (styledSummaries[selectedStyle]) {
        summaryTextElem.textContent = styledSummaries[selectedStyle];
        return;
      }

      try {
        const res = await fetch(`http://localhost:8080/api/summary/style?style=${selectedStyle}`, {
          headers: token ? { "Authorization": `Bearer ${token}` } : {}
        });
        const styleData = await res.json();
        const styledItem = styleData.find(n => n.newsId === newsId);
        const summary = styledItem?.summaries?.[selectedStyle] || "(요약 없음)";
        styledSummaries[selectedStyle] = summary;
        summaryTextElem.textContent = summary;
      } catch (err) {
        console.error("스타일 요약 불러오기 실패:", err);
        summaryTextElem.textContent = "(요약 변환 실패)";
      }
    });

  } catch (err) {
    console.error("뉴스 상세 불러오기 실패:", err);
    container.innerHTML = "<p>뉴스 정보를 불러오는 중 오류가 발생했습니다.</p>";
  }
});

