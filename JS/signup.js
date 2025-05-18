
document.getElementById("signupForm").addEventListener("submit", function(e) {
  e.preventDefault();
  alert("회원가입 시도 중...");
});
document.addEventListener("DOMContentLoaded", () => {
  const categorySelect = document.getElementById("category");
  const selectedDiv = document.getElementById("selectedCategories");

  const selectedSet = new Set();

  categorySelect.addEventListener("change", () => {
    const selectedValue = categorySelect.value;

    // 이미 선택된 항목이면 추가하지 않음
    if (selectedSet.has(selectedValue)) return;

    selectedSet.add(selectedValue);

    const tag = document.createElement("div");
    tag.className = "category-tag";
    tag.innerHTML = `
      ${selectedValue}
      <button class="remove-btn" type="button">✕</button>
    `;

    // X버튼 클릭 시 삭제
    tag.querySelector(".remove-btn").addEventListener("click", () => {
      selectedDiv.removeChild(tag);
      selectedSet.delete(selectedValue);
    });

    selectedDiv.appendChild(tag);

    // 다시 초기 상태로 변경 (옵션 텍스트로)
    categorySelect.selectedIndex = 0;
  });
});
