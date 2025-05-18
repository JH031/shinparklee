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
  .then(res => {
    if (!res.ok) throw new Error("로그인 실패: 상태 코드 " + res.status);
    return res.text(); // 또는 .json() 백엔드가 어떻게 응답하느냐에 따라
  })
  .then(msg => {
    alert("로그인 성공! 응답: " + msg);
    // 예: 토큰 저장 후 메인 페이지로 이동
    // localStorage.setItem("token", msg);  // JWT일 경우
    // window.location.href = "main.html";
  })
  .catch(err => {
    alert("에러 발생: " + err.message);
  });
});
