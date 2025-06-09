document.getElementById("loginForm").addEventListener("submit", function(e) {
  e.preventDefault();

  const userId = document.getElementById("userId").value;
  const password = document.getElementById("password").value;

  fetch("http://localhost:8080/api/login", {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify({
    userId: userId,
    password: password
  })
})
.then(async res => {
  const result = await res.json(); // <-- 토큰 있는 JSON 응답 받기
  if (!res.ok) {
    throw new Error(result.message || "로그인 실패");
  }

  const token = result.token;
  console.log("✅ 토큰:", token);
  localStorage.setItem('token', token); // 저장
  localStorage.setItem('isLoggedIn', 'true');
  localStorage.setItem('userId', userId);

  window.location.href = "Home.html";
})
.catch(err => {
  alert("❌ 로그인 실패: " + err.message);
});
});
