
document.getElementById("signupForm").addEventListener("submit", async function(e) {
  e.preventDefault();

  // 선택된 카테고리 추출
  const categories = [...document.querySelectorAll(".category-tag")]
    .map(tag => tag.firstChild.textContent.trim());

  const dto = {
    username: document.getElementById("username").value,
    userId: document.getElementById("userId").value,
    password: document.getElementById("password").value,
    confirmPassword: document.getElementById("confirm").value,
    email: document.getElementById("email").value,
    interestCategories: categories // 리스트로 보냄
  };

  try {
    const response = await fetch("http://localhost:8080/api/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(dto)
    });

    if (response.ok) {
      alert("회원가입 성공!");
      window.location.href = "/login.html";
    } else {
      alert("회원가입 실패");
    }
  } catch (err) {
    console.error("에러 발생:", err);
    alert("서버 오류");
  }
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

