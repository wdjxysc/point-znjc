-- 学生作答
select *
from (
	SELECT e.exam_id as examId, e.creator_id as personId, DATE_FORMAT(e.create_time, '%Y-%m-%d %H:%i:%s') as createTime,  COUNT(1) as answerNum
	FROM `ps_exam` e
	inner join ps_examinee_sheet es on e.exam_id = es.exam_id
	WHERE e.platform_code = 350000
	and e.create_time > '2020-03-01'
	and e.create_time < '2020-06-01'
	and es.answer_time < DATE_ADD(e.create_time, INTERVAL 30 day)
	GROUP BY e.exam_id
	ORDER BY e.create_time ASC
) t WHERE t.answerNum > 9

-- 老师批阅
select * FROM(
	SELECT *, COUNT(1) as markSheetNum  FROM
	(
		SELECT e.exam_id as examId, sm.submit_marker_id, m.person_id as personId, DATE_FORMAT(e.create_time, '%Y-%m-%d %H:%i:%s') as createTime
		from ps_exam e
		inner join ps_scoring_mark sm on e.exam_id = sm.exam_id
		left join ps_marker m on sm.submit_marker_id = m.marker_id
		WHERE e.platform_code = 350000
		and e.create_time > '2020-03-01'
		and e.create_time < '2020-06-01'
		and sm.submit_time < DATE_ADD(e.create_time, INTERVAL 30 day)
		and sm.submit_marker_id is not null
		and m.person_id is not null
		GROUP BY sm.sheet_id
		ORDER BY e.create_time ASC
	) t
	GROUP BY submit_marker_id
	ORDER BY createTime ASC
) y
WHERE markSheetNum > 9