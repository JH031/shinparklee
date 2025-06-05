const container = document.getElementById('newsDetailContainer');
const params = new URLSearchParams(window.location.search);
const newsId = params.get('newsId');

if (!newsId) {
  container.innerHTML = '<p>뉴스 ID가 없습니다.</p>';
} else {
  fetch('http://localhost:8080/api/summary/basic')
    .then(res => res.json())
    .then(data => {
      const news = data.find(n => n.newsId === newsId);
      if (!news) {
        container.innerHTML = '<p>뉴스를 찾을 수 없습니다.</p>';
        return;
      }

      const { title, url, summaries } = news;
      const content = summaries?.additionalProp1 || '(요약 없음)';
      const imageUrl = "assets/default-news.jpg"; // 이미지 없음 처리

      container.innerHTML = `
        <img src="${imageUrl}" alt="뉴스 이미지" style="width:100%; border-radius:10px;" />
        <h2>${title}</h2>
        <p>${content}</p>
        <a href="${url}" target="_blank" class="detail-btn">원문 보기</a>
      `;
    })
    .catch(err => {
      container.innerHTML = '<p>뉴스를 불러오지 못했습니다.</p>';
      console.error(err);
    });
}
