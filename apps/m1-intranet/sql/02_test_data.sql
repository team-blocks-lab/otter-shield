-- 격리된 실습 DB 전용 / 비밀번호 값은 로그인 불가 표시이며 해시가 아님
USE intranet;
START TRANSACTION;
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST001','!TEST_ONLY_NO_LOGIN','테스트직원',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST001');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST002','!TEST_ONLY_NO_LOGIN',NULL,TRUE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST002');
INSERT INTO post(employee_id,category,title,content,anonymous)
SELECT employee_id,'PRAISE','[TEST] 동료 칭찬','복제·백업·복원 확인용 가상 게시글',TRUE
FROM employee e WHERE employee_number='TEST001'
AND NOT EXISTS(SELECT 1 FROM post WHERE title='[TEST] 동료 칭찬');
INSERT INTO post(employee_id,category,title,content,anonymous)
SELECT employee_id,'LUNCH','[TEST] 오늘의 점심','점심 게시판 가상 데이터',FALSE
FROM employee e WHERE employee_number='TEST001'
AND NOT EXISTS(SELECT 1 FROM post WHERE title='[TEST] 오늘의 점심');
COMMIT;
