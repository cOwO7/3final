CREATE DATABASE IF NOT EXISTS 3team; -- 데이터베이스 생성

use 3team; -- 데이터베이스 접속

CREATE TABLE IF NOT EXISTS member ( -- 테이블 생성
    power INT AUTO_INCREMENT PRIMARY KEY, -- 권한 부여 1번 마스터계정
    id VARCHAR(20) UNIQUE NOT NULL,  -- 로그인 ID 필드 추가
    name VARCHAR(10) NOT NULL,
    pass VARCHAR(100) NOT NULL,
    mobile VARCHAR(13) NOT NULL,
    reg_date TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
# 회원 정보 추가
INSERT INTO member VALUES(1,'admin', '관리자', 
'$2a$10$b3t8sn6QZGHYaRx3OS5KUuPxzWZdY5yHPRxlSdAgByQ7v0BlCLzrO', 
	'000-0001-0002', '2024-11-11 16:16:50');
select * from member;
commit;

-- 소셜 테이블
CREATE TABLE IF NOT EXISTS social_member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT,                    -- `member` 테이블과 1:1 관계
    provider VARCHAR(50) NOT NULL,     -- 소셜 로그인 제공자 (google, naver, kakao)
    provider_id VARCHAR(255) NOT NULL, -- 소셜 로그인 사용자 고유 ID (예: 구글 id, 네이버 id 등)
    FOREIGN KEY (member_id) REFERENCES member(power) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

select * from social_member;

-- 권한 부여

GRANT ALL PRIVILEGES ON 3team.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

ALTER USER 'root'@'localhost' IDENTIFIED BY '12345678';
FLUSH PRIVILEGES;


SHOW GRANTS FOR 'root'@'localhost';


-- 11/14 기상청 테이블 추가
CREATE TABLE weather_data (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 자동 증가하는 기본 키
    base_date DATE NOT NULL,            -- 날짜 (YYYYMMDD 형식)
    base_time TIME NOT NULL,           -- 시간 (HHMM 형식)
    category VARCHAR(10) NOT NULL,     -- 기상 항목 (예: PTY, REH 등)
    nx INT NOT NULL,                   -- 예보 지점 X 좌표
    ny INT NOT NULL,                   -- 예보 지점 Y 좌표
    obsr_value FLOAT NOT NULL,         -- 관측 값 (기온, 습도 등)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 데이터 입력 시간
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  -- 데이터 수정 시간
);

select * from weather_data;

commit;





