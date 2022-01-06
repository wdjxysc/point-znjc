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


select ex.exam_id, ee.person_id, ee.name, count(1) as num
from ps_examinee_sheet es
    inner join ps_examinee ee on es.exam_id = ee.exam_id and es.exam_number = ee.exam_number
    left join ps_exam ex on ex.exam_id = es.exam_id
where ex.platform_code = '420100' and ex.create_time > '2021-03-01' and ex.create_time < '2021-04-01' and es.is_del = 0
  and ex.creator_org_id not in ('bc633fea764147c999a9280a7b85b215', '971ca231eb8141318db87109b16f34b2')
  and ex.exam_id not in ( 'EXAM5227e683dfc64dd3890a16d571bc7515', 'EXAM5e65521757db493e8d99baaf722cf872','EXAM579fc50932d44c66922ae98c77dbdb03','EXAMae3446cb0be844fd8329e8e92bfb1ae3','EXAM173abd9303744fe3ab41407ee927f31f')
GROUP BY ee.exam_number