-- 격리된 실습 DB 전용 / 실제 개인정보 없음
-- password_hash는 로그인 불가 표시. 로그인 검증용 BCrypt 계정은 앱 구현 시 별도 제공
-- 미디어 key는 가상 경로이며 실제 S3 객체가 없으므로 재생되지 않음
USE ott;
START TRANSACTION;
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원가','test_member01','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member01');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원나','test_member02','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member02');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원다','test_member03','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member03');
INSERT INTO video(title,thumbnail_key,genre,summary,actor,video_key,duration_seconds,status)
SELECT '[TEST] 바다 산책','test/thumbnails/ocean.jpg','NATURE','복제 테스트용 가상 영상',NULL,'test/videos/ocean.mp4',45,'PUBLIC'
WHERE NOT EXISTS(SELECT 1 FROM video WHERE video_key='test/videos/ocean.mp4');
INSERT INTO video(title,thumbnail_key,genre,summary,actor,video_key,duration_seconds,status)
SELECT '[TEST] 도시의 하루','test/thumbnails/city.jpg','LIFE','백업 테스트용 가상 영상','가상배우','test/videos/city.mp4',50,'PUBLIC'
WHERE NOT EXISTS(SELECT 1 FROM video WHERE video_key='test/videos/city.mp4');
INSERT INTO video(title,thumbnail_key,genre,summary,actor,video_key,duration_seconds,status)
SELECT '[TEST] 비공개 영상','test/thumbnails/private.jpg',NULL,'공개 상태 구분 테스트',NULL,'test/videos/private.mp4',30,'PRIVATE'
WHERE NOT EXISTS(SELECT 1 FROM video WHERE video_key='test/videos/private.mp4');
INSERT INTO subtitle(video_id,language,subtitle_key)
SELECT v.video_id,'ko','test/subtitles/ocean.ko.vtt' FROM video v
WHERE video_key='test/videos/ocean.mp4'
AND NOT EXISTS(SELECT 1 FROM subtitle s WHERE s.video_id=v.video_id AND s.language='ko');
INSERT INTO subtitle(video_id,language,subtitle_key)
SELECT v.video_id,'en','test/subtitles/ocean.en.vtt' FROM video v
WHERE video_key='test/videos/ocean.mp4'
AND NOT EXISTS(SELECT 1 FROM subtitle s WHERE s.video_id=v.video_id AND s.language='en');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원04','test_member04','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member04');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원05','test_member05','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member05');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원06','test_member06','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member06');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원07','test_member07','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member07');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원08','test_member08','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member08');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원09','test_member09','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member09');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원10','test_member10','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member10');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원11','test_member11','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member11');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원12','test_member12','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member12');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원13','test_member13','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member13');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원14','test_member14','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member14');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원15','test_member15','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member15');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원16','test_member16','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member16');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원17','test_member17','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member17');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원18','test_member18','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member18');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원19','test_member19','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member19');
INSERT INTO member(name,login_id,password_hash)
SELECT '가상회원20','test_member20','!TEST_ONLY_NO_LOGIN'
WHERE NOT EXISTS(SELECT 1 FROM member WHERE login_id='test_member20');
COMMIT;
SELECT COUNT(*) AS test_member_count FROM member WHERE login_id REGEXP '^test_member(0[1-9]|1[0-9]|20)
;
