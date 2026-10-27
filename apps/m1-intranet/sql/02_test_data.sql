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
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST003','!TEST_ONLY_NO_LOGIN','가상직원03',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST003');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST004','!TEST_ONLY_NO_LOGIN','가상직원04',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST004');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST005','!TEST_ONLY_NO_LOGIN','가상직원05',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST005');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST006','!TEST_ONLY_NO_LOGIN','가상직원06',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST006');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST007','!TEST_ONLY_NO_LOGIN','가상직원07',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST007');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST008','!TEST_ONLY_NO_LOGIN','가상직원08',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST008');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST009','!TEST_ONLY_NO_LOGIN','가상직원09',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST009');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST010','!TEST_ONLY_NO_LOGIN','가상직원10',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST010');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST011','!TEST_ONLY_NO_LOGIN','가상직원11',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST011');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST012','!TEST_ONLY_NO_LOGIN','가상직원12',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST012');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST013','!TEST_ONLY_NO_LOGIN','가상직원13',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST013');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST014','!TEST_ONLY_NO_LOGIN','가상직원14',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST014');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST015','!TEST_ONLY_NO_LOGIN','가상직원15',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST015');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST016','!TEST_ONLY_NO_LOGIN','가상직원16',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST016');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST017','!TEST_ONLY_NO_LOGIN','가상직원17',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST017');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST018','!TEST_ONLY_NO_LOGIN','가상직원18',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST018');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST019','!TEST_ONLY_NO_LOGIN','가상직원19',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST019');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST020','!TEST_ONLY_NO_LOGIN','가상직원20',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST020');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST021','!TEST_ONLY_NO_LOGIN','가상직원21',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST021');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST022','!TEST_ONLY_NO_LOGIN','가상직원22',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST022');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST023','!TEST_ONLY_NO_LOGIN','가상직원23',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST023');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST024','!TEST_ONLY_NO_LOGIN','가상직원24',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST024');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST025','!TEST_ONLY_NO_LOGIN','가상직원25',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST025');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST026','!TEST_ONLY_NO_LOGIN','가상직원26',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST026');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST027','!TEST_ONLY_NO_LOGIN','가상직원27',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST027');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST028','!TEST_ONLY_NO_LOGIN','가상직원28',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST028');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST029','!TEST_ONLY_NO_LOGIN','가상직원29',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST029');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST030','!TEST_ONLY_NO_LOGIN','가상직원30',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST030');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST031','!TEST_ONLY_NO_LOGIN','가상직원31',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST031');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST032','!TEST_ONLY_NO_LOGIN','가상직원32',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST032');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST033','!TEST_ONLY_NO_LOGIN','가상직원33',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST033');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST034','!TEST_ONLY_NO_LOGIN','가상직원34',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST034');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST035','!TEST_ONLY_NO_LOGIN','가상직원35',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST035');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST036','!TEST_ONLY_NO_LOGIN','가상직원36',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST036');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST037','!TEST_ONLY_NO_LOGIN','가상직원37',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST037');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST038','!TEST_ONLY_NO_LOGIN','가상직원38',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST038');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST039','!TEST_ONLY_NO_LOGIN','가상직원39',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST039');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST040','!TEST_ONLY_NO_LOGIN','가상직원40',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST040');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST041','!TEST_ONLY_NO_LOGIN','가상직원41',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST041');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST042','!TEST_ONLY_NO_LOGIN','가상직원42',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST042');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST043','!TEST_ONLY_NO_LOGIN','가상직원43',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST043');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST044','!TEST_ONLY_NO_LOGIN','가상직원44',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST044');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST045','!TEST_ONLY_NO_LOGIN','가상직원45',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST045');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST046','!TEST_ONLY_NO_LOGIN','가상직원46',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST046');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST047','!TEST_ONLY_NO_LOGIN','가상직원47',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST047');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST048','!TEST_ONLY_NO_LOGIN','가상직원48',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST048');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST049','!TEST_ONLY_NO_LOGIN','가상직원49',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST049');
INSERT INTO employee(employee_number,password_hash,nickname,first_login)
SELECT 'TEST050','!TEST_ONLY_NO_LOGIN','가상직원50',FALSE
WHERE NOT EXISTS(SELECT 1 FROM employee WHERE employee_number='TEST050');
COMMIT;
SELECT COUNT(*) AS test_employee_count FROM employee WHERE employee_number REGEXP '^TEST(00[1-9]|0[1-4][0-9]|050)$';
