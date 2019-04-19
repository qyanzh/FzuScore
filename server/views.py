from . import api
from flask import render_template, redirect, request, flash, url_for, g, session
from ..db import get_db
import pymysql
import decimal
import json


# 获取课程号
def _acquire_course_id(course_name):
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    cursor.execute('SELECT course_id FROM courses WHERE course_name = "%s"' % course_name)
    id = cursor.fetchone()
    if id is None:
        return None
    else:
        return id['course_id']


# 获取某学期的总成绩排行
def _get_rank_list(term):
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)

    cursor.execute("""
        SELECT r.* ,s.student_name FROM student_rank r,students s
        WHERE term = %s AND s.student_id = r.student_id
        ORDER BY rank
    """ % term)
    return cursor.fetchall()

# 浮点数处理
class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, decimal.Decimal):
            return float(o)
        super(DecimalEncoder, self).default(o)


# 返回数据1
def _response_data(is_success=0, message=None, data=None):
    response_data = {
        "is_success": is_success,
        "message": message,
        "data": data
    }
    return json.dumps(response_data, cls=DecimalEncoder, ensure_ascii=False)


# 返回数据2
def _data(data=None):
    return json.dumps(data, cls=DecimalEncoder, ensure_ascii=False)


# 获取单个学生的所有成绩
@api.route('/score', methods=["POST"])
def score():
    data = json.loads(request.get_data(as_text=True))
    student_id = int(data['student_id'])
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)

    terms = [201802, 201801, 201702, 201701]
    totals = []
    for term in terms:
        # 总成绩
        cursor.execute("""
            SELECT rank, average_score FROM student_rank
            WHERE term = %s AND student_id = %s
        """ % (term, student_id))
        _total_score = cursor.fetchone()
        try:
            average_score = _total_score['average_score']
            rank = _total_score['rank']
        except TypeError:
            return _response_data(message="学号错误！")
        # 每科成绩
        cursor.execute("""
                    SELECT course_score, c.course_name, course_rank, s.course_id
                    FROM course_select s inner join courses c on s.course_id=c.course_id
                    WHERE student_id = %s and term = %s
                    """%(student_id, term)
                       )
        students_score = cursor.fetchall()

        subjects = []
        for i in students_score:
            cursor.execute("""
                           SELECT course_average, course_min, course_max, course_number, course_perfect, course_pass
                           FROM course_stat 
                           WHERE course_id = %s
                           """ % (i['course_id'])
                           )
            course_stat = cursor.fetchone()
            subject_dict = {
                "subject_name": i["course_name"],
                "subject_score": i["course_score"],
                "subject_rank": i["course_rank"],
                "subject_averscore": course_stat['course_average'],
                "subject_min": course_stat['course_min'],
                "subject_max": course_stat['course_max'],
                "subject_amount": course_stat['course_number'],
                "subject_perfect": course_stat['course_perfect'],
                "subject_pass": course_stat['course_pass']
            }
            subjects.append(subject_dict)
        total = {
            "score": average_score,
            "rank": rank,
            "term": term,
            "subjects": subjects
        }
        totals.append(total)
    return _data(totals)


# 获取某学科全部数据
@api.route('/subject', methods=["POST"])
def subject():
    data = json.loads(request.get_data(as_text=True))
    course_name = data['subject_name']
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    course_id = _acquire_course_id(course_name)

    cursor.execute("""
        SELECT c.*, s.student_name FROM course_select c, students s
        WHERE course_id = %s AND s.student_id = c.student_id
        ORDER BY course_score DESC 
    """ % course_id)
    students_dict = cursor.fetchall()

    cursor.execute("""
        SELECT * FROM course_stat
        WHERE course_id = %s
    """ % course_id)
    course_stat = cursor.fetchone()
    students = []
    for student_dict in students_dict:
        student = {
            "student_name": student_dict["student_name"],
            "student_id": student_dict["student_id"],
            "term": student_dict["term"],
            "rank": student_dict["course_rank"],
            "subject_score": student_dict["course_score"]
        }
        students.append(student)
    subject_stat = {
        "subject_min": course_stat["course_min"],
        "subject_average": course_stat["course_average"],
        "subject_max": course_stat["course_max"],
        "subject_perfect": course_stat["course_perfect"],
        "subject_pass": course_stat["course_pass"],
        "subject_amount": course_stat["course_number"],
        "students": students
    }
    return _data(subject_stat)


@api.route('/rank_list', methods=['POST'])
def rank_list():
    data = json.loads(request.get_data(as_text=True))
    term = data['term']

    rank_list = _get_rank_list(term)
    for rank in rank_list:
        rank['score'] = rank.pop("average_score")
        del rank['term']

    return json.dumps(rank_list, ensure_ascii=False, cls=DecimalEncoder)

# @api.route('/input', methods=['POST'])
# def student_input():
#     response_data = {
#         'is_success': 0,
#         'message': '',
#     }
#     data = json.loads(request.get_data(as_text=True))
#     db = get_db()
#     cursor = db.cursor()
#
#     # 添加新课程
#     if data['type'] == 1:
#         cursor.execute(
#             "INSERT INTO courses(course_name) VALUES ('%s')" %
#             (data['course_name'])
#         )
#         response_data['is_success'] = 1
#     # 添加新选课
#     if data['type'] == 2:
#         cursor.execute(
#             "INSERT IGNORE INTO course_select(course_id, student_id, term, course_score) VALUES (%s, %s, '%s', %s)" %
#             (data['course_id'], data['student_id'], data['term'], data['score'])
#         )
#         response_data['is_success'] = 1
#     if data['type'] == 3:
#         cursor.execute(
#             "UPDATE course_select SET course_score = %s WHERE course_id = %s AND student_id = %s " %
#             (data['course_score'], data['course_id'], data['student_id'])
#         )
#         response_data['is_success'] = 1
#     if data['type'] == 4:
#         cursor.execute(
#             "UPDATE courses SET course_name = '%s' WHERE course_id = %s" %
#             (data['course_name'], data['course_id'])
#         )
#         response_data['is_success'] = 1
#     db.commit()
#     return json.dumps(response_data, ensure_ascii=False)
# @api.route('/stat')
# def stat():
#
#     for i in range(0, 24):
#         db = get_db()
#         cursor = db.cursor()
#         cursor.execute("""
#             SELECT course_score FROM course_select WHERE course_id = %s
#         """ % i)
#         scores = cursor.fetchall()
#         num = len(scores)
#         sum = 0
#         max = 0
#         min = 100
#         perfect_num = 0
#         pass_num = 0
#         for score2 in scores:
#             score = score2[0]
#             sum += score
#             if score > max:
#                 max = score
#             if score < min:
#                 min = score
#             if score >= 90:
#                 perfect_num += 1
#             if score >= 60:
#                 pass_num += 1
#         average = float(sum / num)
#         perfect_num = float(perfect_num / num)
#         pass_num = float(pass_num / num)
#         cursor.execute("""
#             UPDATE course_stat set course_average=%s, course_max=%s, course_min=%s,
#             course_number=%s, course_perfect=%s, course_pass=%s
#             WHERE course_id = %s
#             """ % (average, max, min, num, perfect_num, pass_num, i))
#
#     response_data = {
#         'is_success': 1,
#         'message': '',
#     }
#     db.commit()
#     return json.dumps(response_data)


# @api.route('/rank')
# def rank():
#     db = get_db()
#     cursor = db.cursor(pymysql.cursors.DictCursor)
#     cursor.execute("""
#         SELECT * FROM course_select
#     """)
#     students = cursor.fetchall()
#     for student in students:
#         cursor.execute("""
#             SELECT COUNT(*) FROM course_select
#             WHERE course_id = %s and course_score > %s
#         """ % (student['course_id'], student['course_score']))
#         rank = cursor.fetchone()['COUNT(*)'] + 1
#         cursor.execute("""
#                     UPDATE course_select SET course_rank = %s
#                     WHERE course_id = %s AND student_id = %s
#                 """ % (rank, student['course_id'], student['student_id']))
#     response_data = {
#         'is_success': 1,
#         'message': '',
#     }
#     db.commit()
#     return json.dumps(response_data)
#
#
# @api.route('/student_rank')
# def student_rank():
#     db = get_db()
#     cursor = db.cursor(pymysql.cursors.DictCursor)
#
#     cursor.execute("""
#             SELECT student_id FROM students
#         """)
#     students = cursor.fetchall()
#
#     terms = ["201701", "201702", "201801", "201802"]
#     for student in students:
#         for term in terms:
#             cursor.execute("""
#                 SELECT course_score FROM course_select
#                 WHERE student_id = %s AND term = %s
#             """ % (student['student_id'], term))
#             scores = cursor.fetchall()
#             sum = 0
#             for score in scores:
#                 sum += score['course_score']
#             sum = float(sum / len(scores))
#             cursor.execute("UPDATE student_rank SET average_score = %s "
#                            "WHERE student_id =%s AND term = %s" % (sum, student['student_id'], term))
#
#     for term in terms:
#         cursor.execute("""
#             SELECT student_id, average_score FROM student_rank
#             WHERE term = %s ORDER BY average_score DESC
#         """ % term)
#         students = cursor.fetchall()
#         last_rank = 1
#         last_score = 100
#         for student, i in zip(students, range(1, 59)):
#             if student['average_score'] == last_score:
#                 rank = last_rank
#             else:
#                 rank = i
#             last_rank = rank
#             last_score = student['average_score']
#             cursor.execute("UPDATE student_rank SET rank= %s "
#                            "WHERE student_id =%s AND term = %s" % (rank, student['student_id'], term))
#     response_data = {
#         'is_success': 1,
#         'message': '',
#     }
#     db.commit()
#     return json.dumps(response_data)