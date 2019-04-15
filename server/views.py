from . import api
from flask import render_template, redirect, request, flash, url_for, g, session
from app.db import get_db
import json
from ..db import get_db
from .student_auth import student_login_required
import pymysql

import decimal

class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, decimal.Decimal):
            return float(o)
        super(DecimalEncoder, self).default(o)

@api.route('/', methods=['GET', 'POST'])
def api_main():
    if request.method == 'POST':
        data = str(type(request.get_data()))
        return request.get_data()
    return "Hello!"

@api.route('/input', methods=['POST'])
def student_input():
    response_data = {
        'is_success': 0,
        'message': '',
    }
    data = json.loads(request.get_data(as_text=True))
    db = get_db()
    cursor = db.cursor()

    # 添加新课程
    if data['type'] == 1:
        cursor.execute(
            "INSERT INTO courses(course_name) VALUES ('%s')" %
            (data['course_name'])
        )
        response_data['is_success'] = 1
    # 添加新选课
    if data['type'] == 2:
        cursor.execute(
            "INSERT IGNORE INTO course_select(course_id, student_id, term, course_score) VALUES (%s, %s, '%s', %s)" %
            (data['course_id'], data['student_id'], data['term'], data['score'])
        )
        response_data['is_success'] = 1
    if data['type'] == 3:
        cursor.execute(
            "UPDATE course_select SET course_score = %s WHERE course_id = %s AND student_id = %s " %
            (data['course_score'], data['course_id'], data['student_id'])
        )
        response_data['is_success'] = 1
    if data['type'] == 4:
        cursor.execute(
            "UPDATE courses SET course_name = '%s' WHERE course_id = %s" %
            (data['course_name'], data['course_id'])
        )
        response_data['is_success'] = 1
    db.commit()
    return json.dumps(response_data, ensure_ascii=False)


@api.route('/score', methods=["POST"])
def score():
    data = json.loads(request.get_data(as_text=True))
    student_id = int(data['student_id'])
    term = data['term']
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    cursor.execute("""
                SELECT course_score, c.course_name, course_rank, s.course_id
                FROM course_select s inner join courses c on s.course_id=c.course_id
                WHERE student_id = %s and term = %s
                """%(student_id, term)
                   )
    score = cursor.fetchall()
    subjects_amount = len(score)
    subjects = []
    for i in score:
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
    subject_json = {
        "subjects_amount": subjects_amount,
        "subjects": subjects
    }
    return json.dumps(subject_json, ensure_ascii=False, cls=DecimalEncoder)

@api.route('/stat')
def stat():

    for i in range(0, 24):
        db = get_db()
        cursor = db.cursor()
        cursor.execute("""
            SELECT course_score FROM course_select WHERE course_id = %s
        """ % i)
        scores = cursor.fetchall()
        num = len(scores)
        sum = 0
        max = 0
        min = 100
        perfect_num = 0
        pass_num = 0
        for score2 in scores:
            score = score2[0]
            sum += score
            if score > max:
                max = score
            if score < min:
                min = score
            if score >= 90:
                perfect_num += 1
            if score >= 60:
                pass_num += 1
        average = float(sum / num)
        perfect_num = float(perfect_num / num)
        pass_num = float(pass_num / num)
        cursor.execute("""
            UPDATE course_stat set course_average=%s, course_max=%s, course_min=%s, 
            course_number=%s, course_perfect=%s, course_pass=%s
            WHERE course_id = %s
            """ % (average, max, min, num, perfect_num, pass_num, i))

    response_data = {
        'is_success': 1,
        'message': '',
    }
    db.commit()
    return json.dumps(response_data)


@api.route('/rank')
def rank():
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    cursor.execute("""
        SELECT * FROM course_select
    """)
    students = cursor.fetchall()
    for student in students:
        cursor.execute("""
            SELECT COUNT(*) FROM course_select
            WHERE course_id = %s and course_score > %s 
        """ % (student['course_id'], student['course_score']))
        rank = cursor.fetchone()['COUNT(*)'] + 1
        cursor.execute("""
                    UPDATE course_select SET course_rank = %s
                    WHERE course_id = %s AND student_id = %s
                """ % (rank, student['course_id'], student['student_id']))
    response_data = {
        'is_success': 1,
        'message': '',
    }
    db.commit()
    return json.dumps(response_data)


@api.route('/student_rank')
def student_rank():
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    cursor.execute("""
            SELECT student_id FROM students
        """)
    students = cursor.fetchall()
    terms = ["201701", "201702", "201801", "201802"]
    for student in students:
        for term in terms:
            cursor.execute("""
                SELECT course_score FROM course_select
                WHERE student_id = %s AND term = %s
            """ % (student['student_id'], term))
            scores = cursor.fetchall()
            sum = 0
            for score in scores:
                sum += score['course_score']
            sum = float(sum / len(scores))
            cursor.execute("UPDATE student_rank SET average_score = %s "
                           "WHERE student_id =%s AND term = %s" % (sum, student['student_id'], term))

    for term in terms:
        pass
    response_data = {
        'is_success': 1,
        'message': '',
    }
    db.commit()
    return json.dumps(response_data)