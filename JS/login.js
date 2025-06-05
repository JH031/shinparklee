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
    const text = await res.text(); // 백엔드에서 응답으로 주는 메시지를 읽음
    if (!res.ok) {
      throw new Error(text);  // 구체적인 에러 메시지 전파
    }
    return text;
  })
  .then(msg => {
     // ✅ 로그인 성공 시 상태 저장
    localStorage.setItem('isLoggedIn', 'true');
    localStorage.setItem('userId', userId);
    alert("✅ 로그인 성공!");
    // ✅ 로그인 성공 시 Home.html로 이동
    window.location.href = "Home.html";
  })
  .catch(err => {
    alert("❌ 로그인 실패: " + err.message);
  });
});
