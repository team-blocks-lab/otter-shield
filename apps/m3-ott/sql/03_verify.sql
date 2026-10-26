USE ott;
SELECT * FROM member ORDER BY member_id;
SELECT * FROM video ORDER BY video_id;
SELECT * FROM subtitle ORDER BY subtitle_id;
-- M3 원본에서만 실행(게시판은 해당 원본 DB). Replica에는 직접 쓰지 않음
-- 1) 테스트 계정 이름 변경 후 원본과 Replica에서 동일 SELECT 결과 비교
-- UPDATE member SET name='복제확인' WHERE login_id='test_member01';
-- 2) 복원은 별도 격리 인스턴스에 수행한 뒤 위 조회 결과·건수 비교
