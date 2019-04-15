import functools
import pymysql
from . import api
from flask import request, g, session
from app.db import get_db
from werkzeug.security import generate_password_hash, check_password_hash
import json


def student_stat(student_id):
    db = get_db()
    cursor = db.cursor(pymysql.cursors.DictCursor)
    # student_id = g.user['student_id']
    cursor.execute(
        "SELECT student_term_start, student_term_end, student_name FROM students WHERE student_id = %s"%student_id
    )
    student = cursor.fetchone()
    terms = []
    term = int(student['student_term_end'])
    terms.append(term)
    while term != student['student_term_start']:
        if term % 2 == 1:
            term -= 99
        else:
            term -= 1
        terms.append(term)
    student_json = {
        "student_name": student['student_name'],
        "terms_amount": len(terms),
        "terms": terms
    }
    return student_json


@api.route('/login', methods=['GET', 'POST'])
def login():
    response_data = {
        'is_success': 0,
        'message': ''
    }
    if request.method == 'POST':
        data = json.loads(request.get_data(as_text=True))
        student_id = int(data['student_id'])
        password = pymysql.escape_string(data['student_password'])

        db = get_db()
        cursor = db.cursor(pymysql.cursors.DictCursor)
        error = None
        user = None


        cursor.execute(
            "SELECT * FROM students WHERE student_id = %s" % (student_id)
        )
        student = cursor.fetchone()

        if student is None:
            error = '该学号不存在。'
        elif not check_password_hash(student['student_password'], password):
            error = '密码错误。'

        if error is None:
            # login_user(user, True)
            session.clear()
            session['type'] = 'student'
            session['student_id'] = student_id
            response_data['is_success'] = 1
            error = '登陆成功!'
            response_data['data'] = student_stat(student_id)
        response_data['message'] = error
    return json.dumps(response_data, ensure_ascii=False)

@api.route('/register', methods=['GET', 'POST'])
def register():
    response_data = {
        'is_success': 0,
        'message': ''
    }
    if request.method == 'POST':
        data = json.loads(request.get_data(as_text=True))

        student_id = int(data['student_id'])
        password = pymysql.escape_string(data['student_password'])
        student_name = pymysql.escape_string(data['student_name'])
        term_start = data['term_start']
        term_end = data['term_end']
        db = get_db()
        cursor = db.cursor(pymysql.cursors.DictCursor)
        error = None
        student = None

        cursor.execute(
                "SELECT * FROM students WHERE student_id = %s" % (student_id)
            )
        student = cursor.fetchone()
        if student is not None:
            error = '该学号已存在'

        if error is None:
            cursor.execute(
                "INSERT INTO students(student_id, student_name, student_password, student_term_start, student_term_end)"
                " VALUES ('%s','%s','%s', '%s', '%s')" %
                (student_id, student_name, pymysql.escape_string(generate_password_hash(password)), term_start, term_end)
            )
            db.commit()
            response_data['is_success'] = 1
        response_data['message'] = error

    return json.dumps(response_data, ensure_ascii=False)


@api.route('/logout')
def logout():
    session.clear()
    response_data = {
        'is_success': 1,
        'message': '登出成功'
    }
    return json.dumps(response_data)


@api.before_app_request
def load_logged_in_user():
    student_id = session.get('student_id')
    if student_id is None:
        g.user = None
    else:
        cursor = get_db().cursor(pymysql.cursors.DictCursor)
        cursor.execute(
            "SELECT * FROM students WHERE student_id = %s" % student_id
        )
        g.user = cursor.fetchone()


def student_login_required(view):
    @functools.wraps(view)
    def wrapped_view(**kwargs):
        if g.user is None:
            response_data = {
                'is_success': 0,
                'message': '请登录'
            }
            return json.dumps(response_data)
        return view(**kwargs)
    return wrapped_view

