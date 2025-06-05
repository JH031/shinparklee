import { createBriefingCarousel } from '../components/BriefingCardComponent.js';

document.addEventListener("DOMContentLoaded", () => {
  fetch("http://localhost:8080/api/summary/hot")
    .then(res => res.json())
    .then(data => {
      createBriefingCarousel('briefingContainer', data); // 이미지 대체는 내부에서 처리
    })
    .catch(err => {
      console.error("🔥 핫토픽 뉴스 불러오기 실패:", err);
      alert("뉴스를 불러오지 못했습니다.");
    });
});
