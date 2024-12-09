// 드래그할 요소
const chatBox = document.querySelector('.chat-box');
const chatHeader = document.querySelector('.chat-header');

let isDragging = false; // 드래그 여부
let offsetX, offsetY; // 마우스 클릭 위치에서 채팅창의 상대 위치

// 이전에 저장된 위치가 있는지 확인하고, 있다면 채팅창 위치를 설정
window.addEventListener('load', () => {
  const savedLeft = localStorage.getItem('chatBoxLeft');
  const savedTop = localStorage.getItem('chatBoxTop');

  if (savedLeft && savedTop) {
    chatBox.style.left = savedLeft + 'px';
    chatBox.style.top = savedTop + 'px';
  } else {
    chatBox.style.left = '350px'; // 기본값
    chatBox.style.top = '200px';  // 기본값
  }
});

// 마우스 클릭 시 드래그 시작
chatHeader.addEventListener('mousedown', (e) => {
  isDragging = true; // 드래그 시작
  offsetX = e.clientX - chatBox.offsetLeft; // 마우스의 X 좌표와 채팅창의 왼쪽 위치 차이
  offsetY = e.clientY - chatBox.offsetTop;  // 마우스의 Y 좌표와 채팅창의 위쪽 위치 차이

  // 마우스 커서를 '그랩'처럼 보이게 설정
  chatBox.style.cursor = 'grabbing';
});

// 마우스 이동 시 드래그한 채팅창 이동
document.addEventListener('mousemove', (e) => {
  if (!isDragging) return; // 드래그 중이 아니면 아무 작업도 하지 않음

  // 마우스의 위치에 맞춰 채팅창 이동
  const newX = e.clientX - offsetX;
  const newY = e.clientY - offsetY;

  chatBox.style.left = newX + 'px'; // 새로운 X 위치 설정
  chatBox.style.top = newY + 'px';  // 새로운 Y 위치 설정
});

// 마우스를 놓으면 드래그 종료 및 위치 저장
document.addEventListener('mouseup', () => {
  isDragging = false; // 드래그 종료
  chatBox.style.cursor = 'move'; // 마우스 커서를 원래대로 복원

  // 이동 후 위치를 로컬 스토리지에 저장
  localStorage.setItem('chatBoxLeft', chatBox.offsetLeft);
  localStorage.setItem('chatBoxTop', chatBox.offsetTop);
});
